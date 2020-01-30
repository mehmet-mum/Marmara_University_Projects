
public class Lot extends Square {
	private int cost;
	private int rent;
	private int owner;
	public Lot(int squareLocation,String name,int cost,int rent) {
		super(squareLocation,name,cost,rent);
		this.cost = cost;
		this.rent = rent;
		this.owner = -1;
	}
	
}
