
public class LuxuryTaxSquare extends Square {
	Money luxuryTax;
	protected LuxuryTaxSquare(int squareLocation,String name) {
		super(squareLocation,name);
		luxuryTax = new Money(75);
	}
	
	public void setMoney(Player p) {
		p.getPlayerMoney().setMoney(-luxuryTax.getMoney());
	}
}
