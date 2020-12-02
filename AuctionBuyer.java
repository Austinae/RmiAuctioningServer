import java.rmi.Naming;
import java.security.*;
import javax.crypto.*;
import java.security.MessageDigest;
import java.util.*;
import java.rmi.ConnectException;


/*
*	This class provides a way to:
*	- connect to an RMI server
*	- do a 5-stage challenge-response protocol where both parties verify each other
*	- view active auctions
*	- bid on active auctions
*/

public class AuctionBuyer{

	public static void main(String args[]){

		try{
			String type = "buyer";
			String username = "";
			// Get username from CLI
			if(args.length == 1){username = args[0];}else{System.out.println("\nNo input, please try: >>java AuctionBuyer \"YOUR_USERNAME\""); System.exit(0);}

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
            	ArrayList<AuctionItem> auctions = new ArrayList<AuctionItem>();
            	ArrayList<SealedObject> so = i.getActiveAuctions(encryptedID, encryptedUsername);
            	for(SealedObject sealedobject : so){
            		auctions.add((AuctionItem)sealedobject.getObject(theAESKey));
            	}

                // Provide insight into what auctions have been created and the options available to the user
                if(auctions.isEmpty()){System.out.println(delimeterTwo+"No auctions active at the moment"+delimeter);}
                else{
                	System.out.println(delimeterTwo+"Current Auctions:\n");
                	for(AuctionItem item:auctions){
            			System.out.println("Item ID: "+item.getId()+"\n"+"Title: "+item.getTitle()+"\n"+"Highest Bid: "+item.getHighestBid()+delimeter);
            		}
                }
                System.out.println("\nChoose one by typing the correct number and pressing [Enter]:\n1. Bid auction\n2. Refresh\n3. Exit program"+delimeterTwo);
                
                // Simple exception handling
                try{input = in.nextInt();}catch(Exception e){System.out.println(informativeMessage); in.nextLine(); continue;}
                

                // 1. Bid on an auction
                if (input==1){
                    
                    // Ask user which auction he wants to bid on
                    AuctionBid ab = new AuctionBid(id);
                	Boolean bidResponse =  i.bidAuction(rsaEncryption.encryptObject(ab, serverPub));
                	if (bidResponse){
                		System.out.println("\nYou successfully added bid to the auction "+ab.getItemId());
                	}
                	else{
                		System.out.println("\nThe bid was unsuccessful, pleaes make you bid higher than current highest bid\n");
                	}
                }


                // 2. Refresh
                else if (input==2){System.out.println("\nRefresh the page");continue;}


                // 3. Terminate program
                else if (input==3){
					Boolean b = i.closeSession(encryptedID);
					if (b){
                    	System.out.println("\nClose session was successful, goodbye.");
                    	System.exit(0);	
					}                    
					else{
						System.out.println("\nSomething went wrong, please try again");
					}
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
