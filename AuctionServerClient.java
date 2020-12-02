import java.security.Key;

public class AuctionServerClient{
	private int id;
	private String username;
	private Key pub;
	private String aes;
	private Boolean isActive;
	private String typeOfClient;

	public AuctionServerClient(int aId, String aUsername, Key aPub, String aAes, String aTypeOfClient){
		id = aId;
		username = aUsername;
		pub = aPub;
		aes = aAes;
		typeOfClient = aTypeOfClient;
		isActive = true;
	}

	// GET functions

	public int getId(){return id;}

	public String getUsername(){return username;}

	public Key getPub(){return pub;}

	public String getAes(){return aes;}

	public Boolean getIsActive(){return isActive;}

	public String getTypeOfClient(){return typeOfClient;}

	// SET functions	

	public void setAes(String aAes){aes = aAes;}

	public void setIsActive(Boolean b){isActive = b;}

	public void setPub(Key k){pub = k;}
}