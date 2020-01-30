
public class IncomeTaxSquare extends Square {

	
	protected IncomeTaxSquare(int squareLocation,String name) {
		super(squareLocation,name);
		// TODO Auto-generated constructor stub
	}
	
	public void setMoney(Player p) {
		p.getPlayerMoney().setMoney(-p.getPlayerMoney().getMoney() / 10 );
	}
}
