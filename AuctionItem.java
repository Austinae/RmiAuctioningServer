import java.io.Serializable;

public class AuctionItem implements Serializable{
	private int id;
	private int clientId;
	private String title;
	private String description;
	private String condition;
	private int minPrice;
	private int reservePrice;
	
	private Boolean isActive;
	private int highestBiderId;
	private float highestBid;
	private AuctionBid highestBidInfo;

	public AuctionItem(int aId, int aClientId, String aTitle, String aDescription, String aCondition, int aMinPrice, int aReservePrice){
		id = aId;
		clientId = aClientId;
		title = aTitle;
		description = aDescription;
		condition = aCondition;
		minPrice = aMinPrice;
		reservePrice = aReservePrice;
		highestBid = aMinPrice;
		isActive = true;
	}

	public int getId(){return id;}
	
	public int getClientId(){return clientId;}

	public String getTitle(){return title;}

	public String getDescription(){return description;}

	public String getCondition(){return condition;}

	public Boolean getIsActive(){return isActive;}

	public int getMinPrice(){return minPrice;}

	public int getReservePrice(){return reservePrice;}

	public int getHighestBiderId(){return highestBiderId;}

	public float getHighestBid(){return highestBid;}

	public AuctionBid getHighestBidInfo(){return highestBidInfo;}


	public void setIsActive(Boolean bool){isActive = bool;}
	
	public void setHighestBid(float bid){highestBid = bid;}	

	public void setHighestBidInfo(AuctionBid aucBid){highestBidInfo = aucBid;}

}