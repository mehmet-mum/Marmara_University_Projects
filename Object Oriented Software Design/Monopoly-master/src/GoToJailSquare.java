
public class GoToJailSquare extends Square{

	protected GoToJailSquare(int squareLocation,String name) {
		super(squareLocation,name);
		// TODO Auto-generated constructor stub
	}
	
	public void goToJail(Player p) {
		p.getPlayerPiece().setLocation(10);
		p.setJail(true);
	}		
}
