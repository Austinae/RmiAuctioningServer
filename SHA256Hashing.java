import java.security.MessageDigest;

public class SHA256Hashing{
	
	private String data;
	private MessageDigest messageDigest;
	private String hashedData;


	public SHA256Hashing(String aData){
		data = aData;
		try{
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(data.getBytes());
			hashedData = new String(messageDigest.digest());
		}catch(Exception e){
			e.printStackTrace();
        }
	}



	public String getHashedData(){
		return hashedData;
	}
}