import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SealedObject;
import javax.crypto.Cipher;
import java.io.Serializable;

public class RSAEncryption{

	private Key pub;
    private Key pvt;
    private Cipher cipher;

    public void createKeyPair(){
        try{
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            pub = kp.getPublic();
            pvt = kp.getPrivate();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    /** Method that encrypts a string
    * @param s the string that's being encrypted
    * @return SealedObject the encrypted object
    */
    public SealedObject encryptObject(String s, String type){
        try{
            cipher = Cipher.getInstance("RSA");
            if (type == "pub"){
                cipher.init(Cipher.ENCRYPT_MODE, pub);
            } 
            else if (type == "pvt") { 
                cipher.init(Cipher.ENCRYPT_MODE, pvt);
            }
            return new SealedObject(s, cipher);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /** Method that encrypts a string using given key
    * @param s the string that's being encrypted
    * @return SealedObject the encrypted object
    */
    public SealedObject encryptObject(String s, Key key){
        try{
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new SealedObject(s, cipher);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /** Method that encrypts an int using given key
    * @param s the int that's being encrypted
    * @return SealedObject the encrypted object
    */
    public SealedObject encryptObject(int s, Key key){
        try{
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new SealedObject(s, cipher);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /** Method that encrypts an ACR
    * @param s the ACR
    * @return SealedObject the encrypted ACR
    */
    public SealedObject encryptObject(AuctionClientRequest s, Key key){
        try{
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new SealedObject(s, cipher);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /** Method that encrypts an AuctionBid
    * @param s the AuctionBid
    * @return SealedObject the encrypted AuctionBid
    */
    public SealedObject encryptObject(AuctionBid s, Key key){
        try{
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new SealedObject(s, cipher);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Key getPvt(){return pvt;}

    public Key getPub(){return pub;}

    public void setPvt(Key k){pvt = k;}

    public void setPub(Key k){pub = k;}
}