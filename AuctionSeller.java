import java.rmi.Naming;
import java.security.*;
import javax.crypto.*;
import java.security.MessageDigest;
import java.util.*;
import java.rmi.ConnectException;
import java.lang.Thread;
import java.lang.Runtime;


/*
*	This class provides a way to:
*	- connect to an RMI server
*	- do a 5-stage challenge-response protocol where both parties verify each other
*	- view active auctions that belong to me, the seller
*	- create an auction
*	- view who won my auction
*/


public class AuctionSeller{

	public static void main(String args[]){

		try{
			String type = "seller";
			String username = "";
			// Get username from CLI
			if(args.length == 1){username = args[0];}else{System.out.println("\nNo input, please try: >>java AuctionSeller \"YOUR_USERNAME\""); System.exit(0);}

			// Connection setup 
			AuctionInterface i = (AuctionInterface)Naming.lookup("rmi://localhost/MyProgram");

			// Authentication data, surely this can be useful in real systems?
			String authenticationData = "Authentication data";

			// RSA, if cross verification fails don't go on
			RSAClient rsa = new RSAClient(i, authenticationData, username, type);
			String[] response = rsa.authenticate();
			if (response == null){
				System.exit(0);
			}


			System.out.println("\nAuthentication was successful");
			
			String aesKeyString = response[0];
			AESKey aesKey = new AESKey(username, "ClientAESKEYS");
			aesKey.setAESKey(aesKeyString);
			aesKey.writeKey();
			int id = Integer.parseInt(response[1]);




			// Authentification and secure communication established





			String informativeMessage = "\nThe option is not an operation for an auction, check your options and improve your caution";
			int input;
            Scanner in = new Scanner(System.in);
            String delimeter = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            String delimeterTwo = "\n---------------------------------\n";
            Key serverPub = rsa.getServerPub();
			RSAEncryption rsaEncryption = new RSAEncryption();
			SealedObject encryptedID = rsaEncryption.encryptObject(id, serverPub);
			SealedObject encryptedUsername = rsaEncryption.encryptObject(username, serverPub);
			SecretKey theAESKey = aesKey.getAESKey();
			
			// This deals with signing out user when he terminates the JVM 
			// create thread object
			AuctionSellerTerminate shutDownTask = new AuctionSellerTerminate(i, encryptedID);
			// add shutdown hook
			Runtime.getRuntime().addShutdownHook(shutDownTask);
			

            while (true){

            	// Get auctions from server which match client id 
            	ArrayList<AuctionItem> myAuctions = new ArrayList<AuctionItem>();
            	ArrayList<SealedObject> so = i.getMyAuctions(encryptedID, encryptedUsername);
            	for(SealedObject sealedobject : so){
            		myAuctions.add((AuctionItem)sealedobject.getObject(theAESKey));
            	}

                // Provide insight into what auctions have been created and the options available to the user
                if(myAuctions.isEmpty()){System.out.println(delimeterTwo+"No auctions active at the moment");}
                else{
                	System.out.println(delimeterTwo+"Current Auctions:\n");
                	for(AuctionItem item:myAuctions){
                		if (item.getIsActive()){
                			System.out.println("Item ID: "+item.getId()+"\n"+"Title: "+item.getTitle()+"\n"+"Highest Bid: "+item.getHighestBid()+delimeter);
                		}
                		else{
                			System.out.println("This auction is over.\n\nItem ID: "+item.getId()+"\n"+"Title: "+item.getTitle()+"\n"+"Highest Bid: "+item.getHighestBid());
							AuctionBid highestBidder = item.getHighestBidInfo();
							if (highestBidder == null){
								System.out.println("\nSadly, either no one placed a bid or the reserve price hasn't been reached.\nNo highest bidder information to display."+delimeter);
							}
							else{
								System.out.println("Name of highest bidder: "+highestBidder.getName());
								System.out.println("Highest bidder's name: "+highestBidder.getBid());
								System.out.println("Highest bidder's email: "+highestBidder.getEmail()+delimeter);
							}
                		}
                	}}
                System.out.println("\nChoose one by typing the correct number and pressing [Enter]:\n1. Create auction\n2. Close auction\n3. Refresh\n4. Exit program"+delimeterTwo);
                
                // Simple exception handling
                try{input = in.nextInt();}catch(Exception e){System.out.println(informativeMessage); in.nextLine(); continue;}
                

                // 1. Creating an auction
                if (input==1){
                    
                    // Ask user what auction he/she wants to create
                    AuctionClientRequest acr = new AuctionClientRequest(id);

                    // Create the auction server-side and keep track of auction ids
                    SealedObject aucIdSealed = i.createAuction(rsaEncryption.encryptObject(acr, serverPub), encryptedUsername);

                    System.out.println("\nYou created an auction with id:" + (int)aucIdSealed.getObject(theAESKey)+"\n");
                }


                // 2. Close an auction
                else if (input==2){
                    int closeAuctionID;
					while(true){
						System.out.println("\nPlease enter the id of the auction you want to close (or -1 to leave):\n");
						try{
							closeAuctionID = in.nextInt();
							if  (closeAuctionID == -1){
								break;
							}
							else{
								SealedObject highestBidderSealed = i.closeAuction(rsaEncryption.encryptObject(closeAuctionID, serverPub), encryptedUsername, encryptedID);
								AuctionBid highestBidder = (AuctionBid)highestBidderSealed.getObject(theAESKey);
								System.out.println("You have successfully closed the auction with " +  closeAuctionID);
								if (highestBidder == null){
									System.out.println("\nSadly, either no one placed a bid or the reserve price hasn't been reached.\nNo highest bidder information to display.");
								}
								else{
									System.out.println("Name of highest bidder: "+highestBidder.getName());
									System.out.println("Highest bidder's name: "+highestBidder.getBid());
									System.out.println("Highest bidder's email: "+highestBidder.getEmail());
								}
								break;
							}
						}
						catch(Exception e){System.out.println("\nBad string input or the auction you're trying to close doesn't belong to you!"); in.nextLine();}
					}
                }            
                

                // 3. Refresh
                else if (input==3){System.out.println("\nRefresh the page");continue;}


                // 4. Terminate program
                else if (input==4){
					System.exit(0);
                }

                // Wrong integer input
                else {System.out.println(informativeMessage);}
                System.out.println("\n\n\n");

            }

		}
		catch(ConnectException e){
			System.out.println("The server has crashed, exiting...");
			System.exit(0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}


	// Class that prevents user from exiting JVM without logging out
	private static class AuctionSellerTerminate extends Thread {

		private SealedObject id;
		private AuctionInterface i;

		private AuctionSellerTerminate(AuctionInterface inter, SealedObject aId){
			id = aId;
			i = inter;
		}

		@Override
		public void run() {
			// try{
				try{
					Boolean b = i.closeSession(id);
					System.out.println("\n\nSign out was successful");
				}
				catch(java.rmi.RemoteException e){
					e.printStackTrace();
				}
		}
	}

}
