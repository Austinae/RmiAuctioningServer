import java.security.Key;
import javax.crypto.SealedObject;
import java.util.ArrayList;

public class RSAClient{
	private AuctionInterface i;
	private String authenticationData;
	private String username;
	private String type;
	private Key serverPub;

	public RSAClient(AuctionInterface aI, String aAuthenticationData, String aUsername, String aType){
		i = aI;
		authenticationData = aAuthenticationData;
		username = aUsername;
		type = aType;
	}

	public String[] authenticate(){
		try{
			// RSA
			// Get server's public key
			System.out.println("\nAwaiting server's public key");
			serverPub = i.getPublicKey();
			System.out.println("Server public key received");

			// Create public and private keys
        	System.out.println("\nCreating RSA key pair");
			RSAEncryption rsa = new RSAEncryption();
			rsa.createKeyPair();
        	System.out.println("Key pair created successfully");
			
			// Hash the authentication data using SHA-256
        	System.out.println("\nHashing authentication data...");
			SHA256Hashing sha = new SHA256Hashing(authenticationData);
			String hashedData = sha.getHashedData();
        	System.out.println("Hashing was successful");
			
			// Create signature using client's private key and hashed data
			System.out.println("\nCreating RSA signature...");
			SealedObject signature = rsa.encryptObject(hashedData, "pvt");
			System.out.println("Signature created Successfully");
			
			// Encrypt authentication data using server's public key
			System.out.println("\nEncrypting authentication data...");
			SealedObject sealedData = rsa.encryptObject(authenticationData, serverPub);
			System.out.println("Encryption successful");
			
			// Send & receive data for authentication
			System.out.println("\nSending signed encrypted data\nAwaiting authentication...");
			ArrayList<SealedObject> sealedMessage = i.authenticate(username, sealedData, signature, rsa.getPub(), type);
			
			// Checking if response is null, I could loop sending another request a few times before timing out but that would complicate things
			if (sealedMessage == null) {
				System.out.println("\nAuthentication failed or you're already logged in on another device");
				return null;
			}
			
			// Decrypt data using private key
			String receivedData = (String)sealedMessage.get(0).getObject(rsa.getPvt());
			
			// Hash data we've just decrypted
			SHA256Hashing anotherSHA = new SHA256Hashing(receivedData);
			String receivedDataHashed = anotherSHA.getHashedData();
			
			// Decrypt signature using server's public key
			String decryptedSignature = (String)sealedMessage.get(1).getObject(serverPub);

			// Compare hashed data and decrypted signature, it should be equal
			if(receivedDataHashed.equals(decryptedSignature)){
				// Return AES key and ID from response 
				String[] mashup = receivedData.split(" ");
				return mashup;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public Key getServerPub(){
		try{
			return serverPub;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}