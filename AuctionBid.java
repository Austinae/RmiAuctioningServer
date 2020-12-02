import java.io.Serializable;
import java.util.Scanner;


/*
*	This class provides a way to:
*	- store information about a bid
*/

public class AuctionBid implements Serializable{
	
	private int id;
	private float bid;
	private int itemId;
	private String name;
	private String email;

	public AuctionBid(int aId){
		id = aId;
		String informativeMessage = "\nThe option is not an operation for an auction, check your options and improve your caution";
		Boolean gate = true; 
		Scanner in = new Scanner(System.in);
		while(gate){
			System.out.println("\nPlease enter the item id of the auction:\n");
			try{itemId = in.nextInt(); gate = false;}catch(Exception e){System.out.println("\nBad integer input."+informativeMessage); in.nextLine();}
		}
		while(!gate){
			System.out.println("\nPlease enter your bid:\n");
			try{bid = in.nextInt(); in.nextLine(); gate = true;}catch(Exception e){System.out.println("\nBad integer input."+informativeMessage); in.nextLine();}
		}
		while(gate){
			System.out.println("\nPlease provide a name:\n");
			try{name = in.nextLine(); gate = false;}catch(Exception e){System.out.println("\nBad string input."+informativeMessage); in.nextLine();}
		}
		while(!gate){
			System.out.println("\nPlease provide an email:\n");
			try{email = in.nextLine(); gate = true;}catch(Exception e){System.out.println("\nBad string input."+informativeMessage); in.nextLine();}
		}
	}

	public int getId(){return id;}

	public float getBid(){return bid;}

	public int getItemId(){return itemId;}

	public String getName(){return name;}

	public String getEmail(){return email;}
}