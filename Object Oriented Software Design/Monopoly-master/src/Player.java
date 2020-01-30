
public class Player {
	private int playerID;
	private boolean bankruptcy;
	private boolean inJail;
	private int rolledDiceInJail;
	private Money money;
	private Piece piece;
	
	protected Player(int playerID, int cash) {
		piece = new Piece();
		money = new Money(cash);
		bankruptcy = false;
		inJail = false;
		rolledDiceInJail = 0;
		this.playerID = playerID ;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
	public void checkBankruptcy() {
		if(money.getMoney() <= 0) {
			bankruptcy = true;
			System.out.println("Player" + playerID + " bankrupted!");
		}
			
	}
	
	public boolean getBankruptcy() {
		return bankruptcy;
	}
	public void setJail(boolean jail) {
		inJail = jail;
	}
	
	public boolean getJail() {
		return inJail;
	}
	
	public void setRolledDiceInJail(int rolledDiceInJail) {
		this.rolledDiceInJail=rolledDiceInJail;
	}
	
	public int getRolledDiceInJail() {
		return rolledDiceInJail;
	}
	
	public Money getPlayerMoney() {
		return money;
	}
	
	public Piece getPlayerPiece() {
		return piece;
	}
}
