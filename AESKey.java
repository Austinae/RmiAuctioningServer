import java.io.File;
import java.io.FileWriter;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.spec.*;
import javax.crypto.SealedObject;
import java.util.ArrayList;


/*
*	This class provides a way to:
* 	- create an AES key
*	- store an AES key
*	- retrieve an AES key from a file
*	- encrypt an object using an AES key
*/

public class AESKey{

	private String filename;
	private String dir;
	private SecretKey aesKey;

	public AESKey(String aFilename, String aDir){
		filename = aFilename;
		dir = aDir;
	}

	// Method that creates the AES key 
	public void createKey(){
		try{
			// Generate key
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			aesKey = kgen.generateKey();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

    /** Method that writes the AES key to a file
    * @return Boolean, true if operation was a success
    */
	public Boolean writeKey(){
		try{
			// FileWriter object to write to 'filename'
			FileWriter myWriter = new FileWriter(dir+"\\"+filename);

			// Get encoded bytes to write to 'filename' as output
			byte[] encoded = aesKey.getEncoded();
			String output = Base64.getEncoder().withoutPadding().encodeToString(encoded);
			
			// Writing the output to a file  
			myWriter.write(output);
			myWriter.close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

	}
    
    /** Method that reads first line of a file
    * @return the first line of the file
    */
	public String readKey(){
		try{
			File myObj = new File(dir+"\\"+filename);
			Scanner myReader = new Scanner(myObj);
			String data = myReader.nextLine();
			myReader.close();
			return data;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}	

    /** Method that encrypts a string using the AES keys
    * @param s the string that's being encrypted
    * @return SealedObject the encrypted object
    */
    public SealedObject encryptObject(String s){
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            SealedObject sealedObject = new SealedObject(s, cipher);
            return sealedObject;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /** Method that encrypts an item
    * @param s the item
    * @return SealedObject the encrypted item
    */
    public SealedObject encryptObject(AuctionItem s){
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            SealedObject sealedObject = new SealedObject(s, cipher);
            return sealedObject;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /** Method that encrypts an int
    * @param s the int 
    * @return SealedObject the encrypted int
    */
    public SealedObject encryptObject(int s){
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            SealedObject sealedObject = new SealedObject(s, cipher);
            return sealedObject;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /** Method that encrypts an auction bid
    * @param s the bid object 
    * @return SealedObject the encrypted bid
    */
    public SealedObject encryptObject(AuctionBid s){
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            SealedObject sealedObject = new SealedObject(s, cipher);
            return sealedObject;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


	// GET SET Methods	



	public String getAESKeyString(){
		try{
			byte[] encoded = aesKey.getEncoded();
			return Base64.getEncoder().withoutPadding().encodeToString(encoded);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public SecretKey getAESKey(){return aesKey;}
	
	public SecretKey getAESKeyFromUsername(){setAESKey(readKey());return aesKey;}

	public void setAESKeyFromUsername(){setAESKey(readKey());}

	public void setAESKey(SecretKey sk){aesKey = sk;}
	
	public void setAESKey(String s){byte[] b = Base64.getDecoder().decode(s);aesKey = new SecretKeySpec(b, "AES");}
}