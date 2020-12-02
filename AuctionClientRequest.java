import java.io.Serializable;
import java.util.Scanner;



/*
*	This AuctionClientRequest class helps capture and store information about the auctions
*	being created by the client	
*/
public class AuctionClientRequest implements Serializable{
	private int id;
	private String title;
	private String description;
	private String condition;
	private int minprice;
	private int reserveprice;

	public AuctionClientRequest(int aId){
		id = aId;
		Scanner in = new Scanner(System.in);
		Boolean gate = true; 
		String informativeMessage = "\nThe option is not an operation for an auction, check your options and improve your caution";
		
        while(gate){
            System.out.println("\nPlease enter a title for your auction:\n");
            try{title = in.nextLine(); gate = false;}catch(Exception e){System.out.println("\nBad string input."+informativeMessage); in.nextLine();}
        }
        while(!gate){
            System.out.println("\nPlease provide a description:\n");
            try{description = in.nextLine(); gate = true;}catch(Exception e){System.out.println("\nBad string input."+informativeMessage); in.nextLine();}
        }
        while(gate){
            System.out.println("\nPlease provide item's condition:\n");
            try{condition = in.nextLine(); gate = false;}catch(Exception e){System.out.println("\nBad string input."+informativeMessage); in.nextLine();}
        }
        while(!gate){
            System.out.println("\nPlease provide item's starting price:\n");
            try{minprice = in.nextInt(); gate = true;}catch(Exception e){System.out.println("\nBad integer input."+informativeMessage); in.nextLine();}
        }
        while(gate){
            System.out.println("\nPlease provide item's reserve price:\n");
            try{reserveprice = in.nextInt(); gate = false;}catch(Exception e){System.out.println("\nBad integer input."+informativeMessage); in.nextLine();}
        }
	}	

	public int getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public String getDescription(){
		return description;
	}

	public String getCondition(){
		return condition;
	}

	public int getMinPrice(){
		return minprice;
	}

	public int getReservePrice(){
		return reserveprice;
	}
}