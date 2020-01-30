import java.util.Random;

public class Board {
	private Square[] squareArray;
	private LuckCard[] luckCardArray;
	
	protected Board() {
		squareArray = new Square[40];
		luckCardArray = new LuckCard[10];
		
		for(int i=0;i<40;i++) {
			if (i == 0) 
				squareArray[i] = new GoSquare(i,"GoSquare"); 
			else if(i == 4) 
				squareArray[i] = new IncomeTaxSquare(i,"IncomeTaxSquare");
			else if(i == 5)
				squareArray[i] = new RailRoad(i, "RailRoad1", 200);
			else if(i == 2 || i == 7 || i == 17 || i == 22 || i== 33 ||i == 36) 
				squareArray[i] = new LuckCardSquare(i,"LuckCardSquare");
			else if(i == 10) 
				squareArray[i] = new JailSquare(i,"JailSquare");
			else if(i == 15)
				squareArray[i] = new RailRoad(i, "RailRoad2", 200);
			else if(i == 12)
                squareArray[i] = new UtilitySquare(i, "ElectricUtility", 150);
			else if(i == 20) 
				squareArray[i] = new FreeParkingSquare(i,"FreeParkingSquare");
			else if(i == 28)
                squareArray[i] = new UtilitySquare(i, "WaterUtility", 150);
			else if(i == 25)
                squareArray[i] = new RailRoad(i, "RailRoad3", 200);
			else if(i == 30) 
				squareArray[i] = new GoToJailSquare(i,"GoToJailSquare");
			else if(i == 35)
				squareArray[i] = new RailRoad(i, "RailRoad4", 200);
			else if(i == 38) 
				squareArray[i] = new LuxuryTaxSquare(i,"LuxuryTaxSquare");
			else if(i == 1 )
				squareArray[i] = new Lot(i, "Square1", 60, 2);
			else if(i == 3 )
				squareArray[i] = new Lot(i, "Square3", 60, 4);
			else if(i == 6 )
				squareArray[i] = new Lot(i, "Square6", 100, 6);
			else if(i == 8 )
				squareArray[i] = new Lot(i, "Square8", 100, 6);
			else if(i == 9 )
				squareArray[i] = new Lot(i, "Square9", 120, 8);
			else if(i == 11 )
				squareArray[i] = new Lot(i, "Square11", 140, 10);
			else if(i == 13 )
				squareArray[i] = new Lot(i, "Square13", 140, 10);
			else if(i == 14 )
				squareArray[i] = new Lot(i, "Square14", 160, 12);
			else if(i == 16 )
				squareArray[i] = new Lot(i, "Square16", 180, 14);
			else if(i == 18 )
				squareArray[i] = new Lot(i, "Square18", 180, 14);
			else if(i == 19 )
				squareArray[i] = new Lot(i, "Square19", 200, 16);
			else if(i == 21 )
				squareArray[i] = new Lot(i, "Square21", 220, 18);
			else if(i == 23 )
				squareArray[i] = new Lot(i, "Square23", 220, 18);
			else if(i == 24 )
				squareArray[i] = new Lot(i, "Square24", 240, 20);
			else if(i == 26 )
				squareArray[i] = new Lot(i, "Square26", 260, 22);
			else if(i == 27 )
				squareArray[i] = new Lot(i, "Square27", 260, 22);
			else if(i == 29 )
				squareArray[i] = new Lot(i, "Square29", 280, 24);
			else if(i == 31 )
				squareArray[i] = new Lot(i, "Square31", 300, 26);
			else if(i == 32 )
				squareArray[i] = new Lot(i, "Square32", 300, 26);
			else if(i == 34 )
				squareArray[i] = new Lot(i, "Square34", 320, 28);
			else if(i == 37 )
				squareArray[i] = new Lot(i, "Square37", 350, 35);
			else if(i == 39 )
				squareArray[i] = new Lot(i, "Square39", 400, 50);

		}
		
		for(int i=0;i<10;i++) {
			if(i == 0)
				luckCardArray[i] = new LuckCard("GoToStart",i);				//Go to GoSquare
			else if(i == 1)	
				luckCardArray[i] = new LuckCard("GoBack3Squares",i);		//Go back 3 squares
			else if(i == 2)	
				luckCardArray[i] = new LuckCard("GoToJail",i);				//Go to jail directly
			else if(i == 3)
				luckCardArray[i] = new LuckCard("PayPoorTax",i);			//Pay 15$ to bank
			else if(i == 4)
				luckCardArray[i] = new LuckCard("ChairmanOfTheBoard",i);	//Pay 50$ to each player
			else if(i == 5)
				luckCardArray[i] = new LuckCard("CrosswordCompetition",i);	//Collect 100$
			else if(i == 6)
				luckCardArray[i] = new LuckCard("GoForward3Squares",i);		//Go forward 3 squares
			else if(i == 7)
				luckCardArray[i] = new LuckCard("GoBack3Squares",i);		//Go back 3 squares
			else if(i == 8)
				luckCardArray[i] = new LuckCard("Jackpot",i);				//Collect 300$
			else if(i == 9)
				luckCardArray[i] = new LuckCard("PayRichTax",i);			//Pay 50$ to bank
		}
		
		shuffleDeck();
			
	}
	
	//Shuffle luck cards
	public void shuffleDeck(){
	    int index;
	    LuckCard temp;
	    Random random = new Random();
	    for (int i = luckCardArray.length - 1; i > 0; i--){
	        index = random.nextInt(i + 1);
	        temp = luckCardArray[index];
	        luckCardArray[index] = luckCardArray[i];
	        luckCardArray[i] = temp;
	    }
	    for(int i = 0; i<luckCardArray.length;i++)
	    	luckCardArray[i].setLocation(i);
	}
	
	//Put the top card to bottom
	public void changeCardLocations() {
	    LuckCard temp = luckCardArray[0];
	    for (int i = 1; i < luckCardArray.length; i++){
	    	luckCardArray[i-1] = luckCardArray[i];
	    }
	    luckCardArray[9] = temp;
	    for(int i = 0; i<luckCardArray.length;i++)
	    	luckCardArray[i].setLocation(i);
	}
	public Square getSquare(int loc) {
		return squareArray[loc];
	}
	
	public LuckCard getLuckCard(int loc) {
		return luckCardArray[loc];
	}
	
}
