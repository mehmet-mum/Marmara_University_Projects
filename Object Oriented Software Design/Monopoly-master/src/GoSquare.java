
public class GoSquare extends Square {
	Money goSquareMoney;
	protected GoSquare(int squareLocation,String name) {
		super(squareLocation,name);
		goSquareMoney = new Money(200);
	}
	
	public void setMoney(Player p) {
		p.getPlayerMoney().setMoney(goSquareMoney.getMoney());
	}
	
}
