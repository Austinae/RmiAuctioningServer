import java.rmi.Naming;

public class AuctionServer{
	public AuctionServer(){
		try{
			AuctionInterface i = new AuctionInterfaceImpl();
			Naming.rebind("rmi://localhost/MyProgram", i);
		}
		catch(Exception e){
			System.out.println("Server error: "+ e);
		}
	}

	public static void main(String args[]){
		new AuctionServer();	
	}
}