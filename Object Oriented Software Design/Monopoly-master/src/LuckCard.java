
public class LuckCard {
	private String type;	//type of the luck card
	private int location;	//location on the queue
	
	protected LuckCard(String type, int location) {
		this.type = type;
		this.location = location;
	}
	
	public String getType() {
		return type;
	}
	
	public int getLocation() {
		return location;
	}
	
	public void setLocation(int location) {
		this.location = location;
	}
}
