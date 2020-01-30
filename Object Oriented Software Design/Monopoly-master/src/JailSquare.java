
public class JailSquare extends Square{
	Money bail; 

	protected JailSquare(int squareLocation,String name) {
		super(squareLocation,name);
		bail = new Money(50);
	}
	
	public void setMoney(Player p) {
		p.getPlayerMoney().setMoney(-this.bail.getMoney());
	}

}
