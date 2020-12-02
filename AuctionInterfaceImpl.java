import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.security.*;
import javax.crypto.*;
import java.security.MessageDigest;

/*
*	This class provides the definitions for the interface functions
*/
public class AuctionInterfaceImpl extends java.rmi.server.UnicastRemoteObject implements AuctionInterface{

	private ArrayList<AuctionServerClient> users = new ArrayList<AuctionServerClient>();
	private ArrayList<AuctionItem> items = new ArrayList<AuctionItem>();
	private RSAEncryption rsa = new RSAEncryption();


	// Constructor
	public synchronized AuctionInterfaceImpl() throws java.rmi.RemoteException{
		super();
		try{
			System.out.println("\nCreating key pair for RSA authentication...");
			// Create both private (pvt) and public (pub) key
			rsa.createKeyPair();
			System.out.println("Key pair created successfully");
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		// items.add(new AuctionItem(0, 0, "Basketball hoop", "The description...", "bad condition", 23, 50));
	}

    /** GET function that returns the public key
    * @return Key the public key
    */
	public synchronized Key getPublicKey() throws java.rmi.RemoteException{return rsa.getPub();}


    /** retrieve seller auctions that belong to him/her
    * @param aId , the user's id
    * @param aUsername , the user's username
    * @return ArrayList<SealedObject>, the sealed auctions
    */
	public synchronized ArrayList<SealedObject> getMyAuctions(SealedObject aId, SealedObject aUsername) throws java.rmi.RemoteException{
		try{
			int id = (int)aId.getObject(rsa.getPvt());
			String username = (String)aUsername.getObject(rsa.getPvt());
			ArrayList<SealedObject> itemsToSend = new ArrayList<SealedObject>();
			AESKey aeskey = new AESKey(username, "ServerAESKEYS");
			aeskey.setAESKeyFromUsername();
			for(AuctionItem item : items){
				if (item.getClientId()==id){
					itemsToSend.add(aeskey.encryptObject(item));
				}
			}
			return itemsToSend;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


    /** retrieve all active auctions for buyer
    * @param aId , the user's id
    * @param aUsername , the user's username
    * @return ArrayList<SealedObject>, the sealed auctions
    */
	public synchronized ArrayList<SealedObject> getActiveAuctions(SealedObject aId, SealedObject aUsername) throws java.rmi.RemoteException{
		try{
			int id = (int)aId.getObject(rsa.getPvt());
			String username = (String)aUsername.getObject(rsa.getPvt());
			ArrayList<SealedObject> itemsToSend = new ArrayList<SealedObject>();
			AESKey aeskey = new AESKey(username, "ServerAESKEYS");
			aeskey.setAESKeyFromUsername();
			for(AuctionItem item : items){
				if (item.getIsActive()){
					itemsToSend.add(aeskey.encryptObject(item));
				}
			}
			return itemsToSend;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

    /** Authenticates user using RSA protocol
    * @param username the clients username
    * @param data the data sent by user
    * @param signature the RSA signature
    * @param clientPublicKey the client's public key
    * @param typeOfClient the client type
    * @return ArrayList<SealedObject>, the sealed AES key and clinet ID
    */
	public synchronized ArrayList<SealedObject> authenticate(String username, SealedObject data, SealedObject signature, Key clientPublicKey, String typeOfClient) throws java.rmi.RemoteException{
		try{
			// decrypt data using private key
			String receivedData = (String)data.getObject(rsa.getPvt());

			// hash data we've just decrypted
			SHA256Hashing sha = new SHA256Hashing(receivedData);
			String receivedDataHashed = sha.getHashedData();

			// decrypt signature using client's public key
			String decryptedSignature = (String)signature.getObject(clientPublicKey);

			// compare hashed data and decrypted signature, it should be equal
			if(receivedDataHashed.equals(decryptedSignature)){
				for (AuctionServerClient user : users){
					if (username.equals(user.getUsername())){
						// If user exists and is active then refuse access
						if (user.getIsActive()){System.out.println("\nUser "+username+" with id "+(users.size()-1)+" has been denied access because he is already marked as active.\n");return null;}
						// Deals with scenario where buyer logs in as seller (and the other ways)
						else if (!user.getTypeOfClient().equals(typeOfClient)){System.out.println("\nUser "+username+" with id "+user.getId()+" has been denied access for logging in as another account type.\n");return null;}
						/* If user exists and is inactive then use AES key already established for communication
						*  Encrypting AES key and the clients id one last time
						*/
						else {
							//Encrypting aes key and id for double verification
							ArrayList<SealedObject> returnData = new ArrayList<SealedObject>();

							user.setIsActive(true);
							user.setPub(clientPublicKey);
							
							String dataToSend = user.getAes() + " "+ Integer.toString(user.getId());

							// Hash the data using SHA-256
							sha = new SHA256Hashing(dataToSend);
							String hashedData = sha.getHashedData();

							// Create signature using server's private key and hashed data
							SealedObject signatureToSend = rsa.encryptObject(hashedData, "pvt");

							// Encrypt data using client's public key
							SealedObject sealedDataToSend = rsa.encryptObject(dataToSend, clientPublicKey);

							returnData.add(sealedDataToSend);
							returnData.add(signatureToSend);

							System.out.println("\nUser "+username+" with id "+user.getId()+" signed in.");
							return returnData;

						}
					}

				}
				/* User doesn't exist, create account 	
				*  The id won't work for replication in part 3 here but it works for now (1 server)
				*  If all communication had to be secure I'd create an AES key here
				*  Encrypting aes key and id one last time for double verification
				*/ 
				AESKey aeskey = new AESKey(username, "ServerAESKEYS");
				aeskey.createKey();
				aeskey.writeKey();


				users.add(new AuctionServerClient(users.size(), username, clientPublicKey, aeskey.getAESKeyString(), typeOfClient));

				// Simple data structure to return both the AES key and the ID
				ArrayList<SealedObject> returnData = new ArrayList<SealedObject>();
				

				String dataToSend = aeskey.getAESKeyString() + " " + Integer.toString(users.size()-1);

				// Hash the data using SHA-256
				sha = new SHA256Hashing(dataToSend);
				String hashedData = sha.getHashedData();

				// Create signature using server's private key and hashed data
				SealedObject signatureToSend = rsa.encryptObject(hashedData, "pvt");

				// Encrypt data using client's public key
				SealedObject sealedDataToSend = rsa.encryptObject(dataToSend, clientPublicKey);

				returnData.add(sealedDataToSend);
				returnData.add(signatureToSend);

				System.out.println("\nUser "+username+" with id "+(users.size()-1)+" registered and has been successfully added.\n");
				return returnData;
			}
			return null;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


    /** Close client session
    * @param clientId the sealed client id
    * @return Boolean, true if successful
    */
	public synchronized Boolean closeSession(SealedObject clientId) throws java.rmi.RemoteException{
		try{
			int id = (int)clientId.getObject(rsa.getPvt());
			for (AuctionServerClient user : users){
				if (id == user.getId()){
					user.setIsActive(false);
				}
			}
			System.out.println("\nclient with id = " + id  + " has signed out.");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


    /** Creates auction
    * @param so , a sealed AuctionClientRequest object
    * @param aUsername , athe client's username
    * @return SealedObject, the sealed unique auction ID
    */
	public synchronized SealedObject createAuction(SealedObject so, SealedObject aUsername) throws java.rmi.RemoteException{
		try{
			String username = (String)aUsername.getObject(rsa.getPvt());
			AuctionClientRequest acr = (AuctionClientRequest)so.getObject(rsa.getPvt());
			System.out.println("client with id = "+ acr.getId() +" made a request to create an auction with a unique ID = "+(items.size()+1));
			items.add(new AuctionItem(items.size()+1, acr.getId(), acr.getTitle(), acr.getDescription(), acr.getCondition(), acr.getMinPrice(), acr.getReservePrice()));
			AESKey aeskey = new AESKey(username, "ServerAESKEYS");
			aeskey.setAESKeyFromUsername();

			return aeskey.encryptObject(items.size());
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


    /** Close auction
    * @param auctionId , the auction id
    * @param aUsername , the client's username
    * @param aClientId , the client's id
    * @return SealedObject, a sealed AuctionBid with data about auction winner
    */
	public synchronized SealedObject closeAuction(SealedObject auctionId, SealedObject aUsername, SealedObject aClientId) throws java.rmi.RemoteException{
		try{
			int itemId = (int)auctionId.getObject(rsa.getPvt());
			int clientId = (int)aClientId.getObject(rsa.getPvt());
			String username = (String)aUsername.getObject(rsa.getPvt());
			for(AuctionItem item : items){
				if(itemId == item.getId() && clientId == item.getClientId()){
					item.setIsActive(false);
					AESKey aeskey = new AESKey(username, "ServerAESKEYS");
					aeskey.setAESKeyFromUsername();
					return aeskey.encryptObject(item.getHighestBidInfo());
				}
			}
			return null;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


    /** Bid on an auction
    * @param aucBid , the sealed auction bid
    * @return Boolean, true if operation was successful, false otherwise
    */
	public synchronized Boolean bidAuction(SealedObject aucBid) throws java.rmi.RemoteException {
		try{
			AuctionBid ab = (AuctionBid)aucBid.getObject(rsa.getPvt());
			for(AuctionItem item : items){
				if(item.getId() == ab.getItemId() && item.getIsActive()){
					if(ab.getBid() > item.getHighestBid()){
						item.setHighestBid(ab.getBid());
						item.setHighestBidInfo(ab);
						return true;
					}
				}
			}
			return false;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}