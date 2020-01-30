
public class Die {
	private int faceValue;
	
	protected Die() {}
	
	public void roll() {
		faceValue = (int)(Math.random() * 6) + 1 ;
	}
	public int getFaceValue() {
		return faceValue;
	}
}
