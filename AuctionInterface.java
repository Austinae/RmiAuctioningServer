import java.util.ArrayList;
import java.security.*;
import javax.crypto.*;

/*
*   This interface helps gather available functions for the client to use
*/
public interface AuctionInterface extends java.rmi.Remote {


    /** GET function that returns the public key
    * @return Key the public key
    */
	public Key getPublicKey() throws java.rmi.RemoteException;


    /** Authenticates user using RSA protocol
    * @param username the clients username
    * @param data the data sent by user
    * @param signature the RSA signature
    * @param clientPublicKey the client's public key
    * @param typeOfClient the client type
    * @return ArrayList<SealedObject>, the sealed AES key and clinet ID
    */
	public ArrayList<SealedObject> authenticate(String username, SealedObject data, SealedObject signature, Key clientPublicKey, String typeOfClient) throws java.rmi.RemoteException;


    /** retrieve seller auctions that belong to him/her
    * @param aId , the user's id
    * @param aUsername , the user's username
    * @return ArrayList<SealedObject>, the sealed auctions
    */
	public ArrayList<SealedObject> getMyAuctions(SealedObject aId, SealedObject aUsername) throws java.rmi.RemoteException;
    

    /** retrieve all active auctions for buyer
    * @param aId , the user's id
    * @param aUsername , the user's username
    * @return ArrayList<SealedObject>, the sealed auctions
    */
	public ArrayList<SealedObject> getActiveAuctions(SealedObject aId, SealedObject aUsername) throws java.rmi.RemoteException;

    /** Creates auction
    * @param so , a sealed AuctionClientRequest object
    * @param aUsername , athe client's username
    * @return SealedObject, the sealed unique auction ID
    */
	public SealedObject createAuction(SealedObject so, SealedObject aUsername) throws java.rmi.RemoteException;

	
    /** Close client session
    * @param clientId the sealed client id
    * @return Boolean, true if successful
    */
	public Boolean closeSession(SealedObject clientId) throws java.rmi.RemoteException;


    /** Close auction
    * @param auctionId , the auction id
    * @param aUsername , the client's username
    * @return SealedObject, a sealed AuctionBid with data about auction winner
    */
	public SealedObject closeAuction(SealedObject auctionId, SealedObject aUsername, SealedObject clientId) throws java.rmi.RemoteException;


    /** Bid on an auction
    * @param aucBid , the sealed auction bid
    * @return Boolean, true if operation was successful, false otherwise
    */
	public Boolean bidAuction(SealedObject aucBid) throws java.rmi.RemoteException;

}