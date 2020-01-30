
public abstract class Square {
	private int squareLocation;
	private String name;
	private int owner;
	private int cost;
	private int rent;
	protected Square(int squareLocation, String name) {
		this.squareLocation = squareLocation;
		this.name = name;
		this.owner = -1;
	}
	
	protected Square(int squareLocation, String name, int cost) {
		this.squareLocation = squareLocation;
		this.name = name;
		this.owner = -1;
		this.cost = cost;
	}
	
	protected Square(int squareLocation, String name, int cost, int rent) {
		this.squareLocation = squareLocation;
		this.name = name;
		this.owner = -1;
		this.cost = cost;
		this.rent = rent;
	}
	
	public int getSquareLocation(){
		return this.squareLocation;
	}

	public void setMoney(Player p) {
		p.getPlayerMoney().setMoney(200);
	}
	
	public String getName() {
		return name;
	}
	
	public int getOwner() {
		return owner;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setOwner(int owner) {
		this.owner = owner;
	}
	
	public int getRent() {
		return rent;
	}
}
