
public class Piece {
	private int location;
	
	protected Piece () {
		this.location = 0;
	}
	
	
	public void setLocation(int location) {
		this.location = location % 40 ;
	}
	
	public int getLocation() {
		return location;
	}
}
