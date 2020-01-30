import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;
import javax.swing.*;


public class Panel extends JPanel implements KeyListener, ActionListener, MouseListener{
	MyRectangle mainRectangle;   // this is our main rectangle
	
	MyRectangle[] rectangles1;
	MyRectangle[] rectangles2;
	MyRectangle[] rectangles3;    // this is our rectangles array for drawing rectangles which we are trying to hit
	MyRectangle[] rectangles4;
	MyRectangle[] rectangles5;
	MyRectangle[] rectangles6;
	MyRectangle[] rectangles7;
	
	Ball ball;                   // this is a ball of game
	
	int step; // speed of main rectangle moving
	
	int x = 25; 
	int y = 25;		
	int width = 60;   // rectangles array width
	int height = 20;  // rectangles array height
	
	// X position of ball for beginning of moving
	int ballX = 410;
			
	// Y position of ball for beginning of moving
	int ballY = 490;
	
	
	int mainRectangleX = 380; // main rectangle's x coordinate
	int mainRectangleY = 500; // main rectangle's y coordinate
	
	int moveBallX ; // speed of ball for moving at x coordinate
	int moveBallY ; // speed of ball for moving at y coordinate
	
	int score = 0; // score for determining
	int level = 1; 

	JLabel scoreLabel;
	JLabel levelLabel;
	JLabel start;
	JLabel sound;
	JLabel heart3;
	JLabel heart2;
	JLabel heart1;
	JLabel gameOverLabel;
	JLabel pause;
	JLabel congLabel;

	
	Icon soundIcon;		// sound icon
	Icon muteIcon;		// mute icon
	Icon heart;			// heart icon
	Icon gameOverIcon;	// game over icon
	Icon congIcon;
	int remainHeart = 3;
	boolean control = true;	
	
	boolean play = true; // for playing sounds
	
	boolean paused = false;
	int[] passLevel; // for passing levels 	
	Image image ;
	
	int coefficient; // for scores
	
	
	
	Clip clip;
	int speed; // speed of ball related timer
	Timer time ; // timer for moving ball
	Timer gameOverTimer; 
	int gameOver = 7; // duration of game over statement
	boolean createTimer = false; // if this boolean is true timer which is for moving ball will start at the beginning of game
	int controlMoveBallX = moveBallX;
	int controlMoveBallY = moveBallY;
	boolean exploded = false;
	
	// constructor
	public Panel(){
		super();
		
		
		setLayout(null);
		Font scoreAndLevel = new Font(null,Font.BOLD,15);
		// label for writing score on the top		
		scoreLabel = new JLabel("SCORE  " + score);
		add(scoreLabel);
		scoreLabel.setFont(scoreAndLevel);
		scoreLabel.setForeground(Color.WHITE);
		scoreLabel.setBounds(440,7, 250,15);
		// label for writing level on the top
		levelLabel = new JLabel("LEVEL  " + level);
		add(levelLabel);
		levelLabel.setFont(scoreAndLevel);
		levelLabel.setForeground(Color.WHITE);
		levelLabel.setBounds(340,7, 250,15);
		
		Font startFont = new Font(null,Font.BOLD,30);
		// label for warning user at the middle
		start = new JLabel("Press SPACE to start game!");
		add(start);
		start.setFont(startFont);
		start.setForeground(Color.WHITE);
		start.setBounds(250, 250, 400, 35);
		
		pause = new JLabel("");
		add(pause);
		pause.setFont(startFont);
		pause.setForeground(Color.WHITE);
		pause.setBounds(350, 250, 400, 35);
		
		soundIcon = new ImageIcon("img/sound_30x30.png");	// sound on icon
		muteIcon = new ImageIcon("img/mute_30x30.png");		// sound off icon
		gameOverIcon = new ImageIcon("img/Game_Over.gif");	// game over icon
		congIcon = new ImageIcon("img/cong.gif");
		
		sound = new JLabel(soundIcon);
		add(sound);
		sound.setBounds(0, 0, 30, 30);
		
		
		heart = new ImageIcon("img/heart_20x20.png");
		heart1 = new JLabel(heart);
		add(heart1);
		heart1.setBounds(820, 5, 20, 20);
		
		heart2 = new JLabel(heart);
		add(heart2);
		heart2.setBounds(795, 5, 20, 20);
		
		heart3 = new JLabel(heart);
		add(heart3);
		heart3.setBounds(770, 5, 20, 20);
		
		gameOverLabel = new JLabel(gameOverIcon);
		gameOverLabel.setIcon(null);
		add(gameOverLabel);
		gameOverLabel.setBounds(250, 120, 350, 275);
		
		congLabel = new JLabel(congIcon);
		congLabel.setIcon(null);
		add(congLabel);
		congLabel.setBounds(100, 120, 650, 275);
		
		addKeyListener(this);
		addMouseListener(this);
		// rectangle on the bottom
		mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
		// rectangles on the top
		rectangles1 = new MyRectangle[40];
		rectangles2 = new MyRectangle[35];
		rectangles3 = new MyRectangle[50];
		rectangles4 = new MyRectangle[26];
		rectangles5 = new MyRectangle[33];
		rectangles6 = new MyRectangle[34];
		rectangles7 = new MyRectangle[44];
		// the ball 
		ball = new Ball(ballX,ballY,10,10);
		
		passLevel = new int[7];
		
		level3();
		level2();
		level1();
		level4();
		level5();
		level6();
		level7();
		// background image at level1
		image = new ImageIcon("img/background4.png").getImage();
		
		// after game over program will count from 7 to 0 then new game will start
		gameOverTimer = new Timer(1000,this);
		
		// sound at level 1
		playSound("background.wav");
	}
	// level 1 rectangles
	public void level1(){
		rectangles1[0] = new MyRectangle(275, 30, width, height);
		rectangles1[1] = new MyRectangle(200, 30, width, height);
		rectangles1[2] = new MyRectangle(150, 55, width, height);
		rectangles1[3] = new MyRectangle(100, 80, width, height);
		rectangles1[4] = new MyRectangle(50, 105, width, height);
		rectangles1[5] = new MyRectangle(25, 130, width, height);
		rectangles1[6] = new MyRectangle(50, 155, width, height);
		rectangles1[7] = new MyRectangle(100, 180, width, height);
		rectangles1[8] = new MyRectangle(150, 205, width, height);
		rectangles1[9] = new MyRectangle(200, 230, width, height);
		rectangles1[10] = new MyRectangle(275, 230, width, height);
		rectangles1[11] = new MyRectangle(250, 205, width, height);
		rectangles1[12] = new MyRectangle(225, 180, width, height);
		rectangles1[13] = new MyRectangle(200, 155, width, height);
		rectangles1[14] = new MyRectangle(175, 130, width, height);
		rectangles1[15] = new MyRectangle(200, 105, width, height);
		rectangles1[16] = new MyRectangle(225, 80, width, height);
		rectangles1[17] = new MyRectangle(250, 55, width, height);
		rectangles1[18] = new MyRectangle(575, 5, width, height);
		rectangles1[19] = new MyRectangle(540, 30, width, height);
		rectangles1[20] = new MyRectangle(525, 55, width, height);
		rectangles1[21] = new MyRectangle(475, 80, width, height);
		rectangles1[22] = new MyRectangle(400, 105, width, height);
		rectangles1[23] = new MyRectangle(475, 130, width, height);
		rectangles1[24] = new MyRectangle(515, 155, width, height);
		rectangles1[25] = new MyRectangle(490, 180, width, height);
		rectangles1[26] = new MyRectangle(465, 205, width, height);
		rectangles1[27] = new MyRectangle(440, 230, width, height);
		rectangles1[28] = new MyRectangle(550, 225, width, height);
		rectangles1[29] = new MyRectangle(590, 200, width, height);
		rectangles1[30] = new MyRectangle(640, 225, width, height);
		rectangles1[31] = new MyRectangle(750, 230, width, height);
		rectangles1[32] = new MyRectangle(725, 205, width, height);
		rectangles1[33] = new MyRectangle(700, 180, width, height);
		rectangles1[34] = new MyRectangle(675, 155, width, height);
		rectangles1[35] = new MyRectangle(700, 130, width, height);
		rectangles1[36] = new MyRectangle(765, 105, width, height);
		rectangles1[37] = new MyRectangle(700, 80, width, height);
		rectangles1[38] = new MyRectangle(640, 55, width, height);
		rectangles1[39] = new MyRectangle(625, 30, width, height);
	}
	// level 2 rectangles
	public void level2(){
		rectangles2[0] = new MyRectangle(175,5,width,height);
		rectangles2[1] = new MyRectangle(250,5,width,height);
		rectangles2[2] = new MyRectangle(325,5,width,height);
		rectangles2[3] = new MyRectangle(150,30,width,height);
		rectangles2[4] = new MyRectangle(125,55,width,height);
		rectangles2[5] = new MyRectangle(100,80,width,height);
		rectangles2[6] = new MyRectangle(75,105,width,height);
		rectangles2[7] = new MyRectangle(75,130,width,height);
		rectangles2[8] = new MyRectangle(75,155,width,height);
		rectangles2[9] = new MyRectangle(100,180,width,height);
		rectangles2[10] = new MyRectangle(125,205,width,height);
		rectangles2[11] = new MyRectangle(150,230,width,height);
		rectangles2[12] = new MyRectangle(175,255,width,height);
		rectangles2[13] = new MyRectangle(250,255,width,height);
		rectangles2[14] = new MyRectangle(325,255,width,height);
		rectangles2[15] = new MyRectangle(325,230,width,height);
		rectangles2[16] = new MyRectangle(325,205,width,height);
		rectangles2[17] = new MyRectangle(325,180,width,height);
		rectangles2[18] = new MyRectangle(325,155,width,height);
		rectangles2[19] = new MyRectangle(325,130,width,height);
		rectangles2[20] = new MyRectangle(250,130,width,height);
		rectangles2[21] = new MyRectangle(175,130,width,height);
		rectangles2[22] = new MyRectangle(600,5,width,height);
		rectangles2[23] = new MyRectangle(525,5,width,height);
		rectangles2[24] = new MyRectangle(500,30,width,height);
		rectangles2[25] = new MyRectangle(475,55,width,height);
		rectangles2[26] = new MyRectangle(450,80,width,height);
		rectangles2[27] = new MyRectangle(475,105,width,height);
		rectangles2[28] = new MyRectangle(515,130,width,height);
		rectangles2[29] = new MyRectangle(560,155,width,height);
		rectangles2[30] = new MyRectangle(600,180,width,height);
		rectangles2[31] = new MyRectangle(575,205,width,height);
		rectangles2[32] = new MyRectangle(550,230,width,height);
		rectangles2[33] = new MyRectangle(525,255,width,height);
		rectangles2[34] = new MyRectangle(450,255,width,height);
	}
	// level 3 rectangles
	public void level3(){
		// for loop for drawing rectangles on the top for 1.line ( 10 rectangles )
				for(int i = 0; i<10; i++){	
					rectangles3[i] = new MyRectangle(x, y, width, height);
					x = x + 80;	
				}
				
				x = 25;
				y = 60;
				// for loop for drawing rectangles on the top for 2.line ( 10 rectangles ) 
				for(int i = 10; i<20; i++){	
					rectangles3[i] = new MyRectangle(x, y, width, height);
					x = x + 80;	
				}
				
				x = 25;
				y = 95;
				// for loop for drawing rectangles on the top for 3.line ( 10 rectangles )
				for(int i = 20; i<30; i++){	
					rectangles3[i] = new MyRectangle(x, y, width, height);
					x = x + 80;	
				}
				
				x = 25;
				y = 130;
				// for loop for drawing rectangles on the top for 3.line ( 10 rectangles )
				for(int i = 30; i<40; i++){	
					rectangles3[i] = new MyRectangle(x, y, width, height);
					x = x + 80;	
				}
				
				x = 25;
				y = 165;
				// for loop for drawing rectangles on the top for 3.line ( 10 rectangles )
				for(int i = 40; i<50; i++){	
					rectangles3[i] = new MyRectangle(x, y, width, height);
					x = x + 80;	
				}
	}
	// level 4 rectangles
	public void level4(){
		rectangles4[0] = new MyRectangle(375,105,width,height);
		rectangles4[1] = new MyRectangle(350,80,width,height);
		rectangles4[2] = new MyRectangle(325,55,width,height);
		rectangles4[3] = new MyRectangle(250,55,width,height);
		rectangles4[4] = new MyRectangle(200,80,width,height);
		
		int rec4X = 175;
		int rec4Y = 105;
		for(int i=5; i<14; i++){
			rectangles4[i] = new MyRectangle(rec4X,rec4Y,width,height);
			rec4X = rec4X + 25;
			rec4Y = rec4Y + 25;
		}
		rec4X = 415;
		rec4Y = 280;
		for(int i=14; i<22; i++){
			rectangles4[i] = new MyRectangle(rec4X,rec4Y,width,height);
			rec4X = rec4X + 25;
			rec4Y = rec4Y - 25;
		}
		
		rectangles4[22] = new MyRectangle(565,80,width,height);
		rectangles4[23] = new MyRectangle(515,55,width,height);
		rectangles4[24] = new MyRectangle(440,55,width,height);
		rectangles4[25] = new MyRectangle(415,80,width,height);
	}
	// level 5 rectangles
	public void level5(){
		rectangles5[0] = new MyRectangle(350,30,width,height);
		rectangles5[1] = new MyRectangle(300,55,width,height);
		int rec5X = 250;
		int rec5Y = 80;
		for(int i = 2; i<7; i++){
			rectangles5[i] = new MyRectangle(rec5X,rec5Y,width,height);
			rec5X = rec5X - 25;
			rec5Y = rec5Y + 25;
		}
		rec5X = 175;
		rec5Y = 205;
		for(int i = 7; i<12; i++){
			rectangles5[i] = new MyRectangle(rec5X,rec5Y,width,height);
			rec5X = rec5X + 25;
			rec5Y = rec5Y + 25;
		}
		rectangles5[11] = new MyRectangle(300,305,width,height);
		rectangles5[12] = new MyRectangle(350,330,width,height);
		rectangles5[13] = new MyRectangle(415,330,width,height);
		rectangles5[14] = new MyRectangle(465,305,width,height);
		rec5X = 515;
		rec5Y = 280;
		for(int i = 15; i<20; i++){
			rectangles5[i] = new MyRectangle(rec5X,rec5Y,width,height);
			rec5X = rec5X + 25;
			rec5Y = rec5Y - 25;
		}
		rec5X = 590;
		rec5Y = 155;
		for(int i = 20; i<24; i++){
			rectangles5[i] = new MyRectangle(rec5X,rec5Y,width,height);
			rec5X = rec5X - 25;
			rec5Y = rec5Y - 25;
		}
		rectangles5[24] = new MyRectangle(465,55,width,height);
		rectangles5[25] = new MyRectangle(415,30,width,height);
		rectangles5[26] = new MyRectangle(325,130,width,height);
		rectangles5[27] = new MyRectangle(440,130,width,height);
		rectangles5[28] = new MyRectangle(490,205,width,height);
		rectangles5[29] = new MyRectangle(440,230,width,height);
		rectangles5[30] = new MyRectangle(385,255,width,height);
		rectangles5[31] = new MyRectangle(325,230,width,height);
		rectangles5[32] = new MyRectangle(275,205,width,height);
	}
	// level 6 rectangles
	public void level6(){
		int rec6X = 25;
		int rec6Y = 30;
		for(int i = 0; i<7; i++){
			rectangles6[i] = new MyRectangle(rec6X,rec6Y,width,height);
			rec6X = rec6X + 50;
			rec6Y = rec6Y + 25;
		}
		rectangles6[7] = new MyRectangle(350,205,width,height);
		rectangles6[8] = new MyRectangle(440,205,width,height);
		rec6X = 465;
		rec6Y = 180;
		for(int i = 9; i<16; i++){
			rectangles6[i] = new MyRectangle(rec6X,rec6Y,width,height);
			rec6X = rec6X + 50;
			rec6Y = rec6Y - 25;
		}
		rec6X = 25;
		rec6Y = 230;
		for(int i = 16; i<27; i++){
			rectangles6[i] = new MyRectangle(rec6X,rec6Y,width,height);
			rec6X = rec6X + 75;
		}
		rectangles6[27] = new MyRectangle(275,255,width,height);
		rectangles6[28] = new MyRectangle(300,280,width,height);
		rectangles6[29] = new MyRectangle(350,305,width,height);
		rectangles6[30] = new MyRectangle(400,330,width,height);
		rectangles6[31] = new MyRectangle(450,305,width,height);
		rectangles6[32] = new MyRectangle(500,280,width,height);
		rectangles6[33] = new MyRectangle(550,255,width,height);
	}
	// level 7 rectangles
	public void level7(){
		rectangles7[0] = new MyRectangle(5,155,width,height);
		rectangles7[1] = new MyRectangle(5,180,width,height);
		rectangles7[2] = new MyRectangle(50,205,width,height);
		rectangles7[3] = new MyRectangle(100,180,width,height);
		int rec7X = 125;
		int rec7Y = 155;
		for(int i = 4; i<10; i++){
			rectangles7[i] = new MyRectangle(rec7X,rec7Y,width,height);
			rec7Y = rec7Y - 25;
		}
		rec7X = 200;
		rec7Y = 205;
		for(int i = 10; i<17; i = i+2){
			rectangles7[i] = new MyRectangle(rec7X,rec7Y,width,height);
			rec7X = rec7X + 25;
			rec7Y = rec7Y - 50;
		}
		rec7X = 215;
		rec7Y = 180;
		for(int i = 11; i<18; i = i+2){
			rectangles7[i] = new MyRectangle(rec7X,rec7Y,width,height);
			rec7X = rec7X + 25;
			rec7Y = rec7Y - 50;
		}
		rectangles7[18] = new MyRectangle(325,130,width,height);
		rec7X = 365;
		rec7Y = 30;
		for(int i = 19; i<26; i = i+2){
			rectangles7[i] = new MyRectangle(rec7X,rec7Y,width,height);
			rec7X = rec7X + 25;
			rec7Y = rec7Y + 50;
		}
		rec7X = 375;
		rec7Y = 55;
		for(int i = 20; i<27; i = i+2){
			rectangles7[i] = new MyRectangle(rec7X,rec7Y,width,height);
			rec7X = rec7X + 25;
			rec7Y = rec7Y + 50;
		}
		rec7X = 525;
		rec7Y = 205;
		for(int i = 27; i<34; i = i+2){
			rectangles7[i] = new MyRectangle(rec7X,rec7Y,width,height);
			rec7X = rec7X + 25;
			rec7Y = rec7Y - 50;
		}
		rec7X = 540;
		rec7Y = 180;
		for(int i = 28; i<35; i = i+2){
			rectangles7[i] = new MyRectangle(rec7X,rec7Y,width,height);
			rec7X = rec7X + 25;
			rec7Y = rec7Y - 50;
		}
		rec7X = 690;
		rec7Y = 30;
		for(int i = 35; i<42; i = i+2){
			rectangles7[i] = new MyRectangle(rec7X,rec7Y,width,height);
			rec7X = rec7X + 25;
			rec7Y = rec7Y + 50;
		}
		rec7X = 700;
		rec7Y = 55;
		for(int i = 36; i<43; i = i+2){
			rectangles7[i] = new MyRectangle(rec7X,rec7Y,width,height);
			rec7X = rec7X + 25;
			rec7Y = rec7Y + 50;
		}
		rectangles7[43] = new MyRectangle(650,130,width,height);
	}
	
	
	// a method for drawing rectangles - main rectangle - the ball
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.drawImage(image, 0, 0, 850, 550, null);
		ball.drawBall(g);
		
		if(level == 1){
			for(int i = 0; i<18; i++){
				rectangles1[i].drawRedBricks(g);
			}
			for(int i = 18; i<40; i++){
				rectangles1[i].drawWhiteBricks(g);
			}
			mainRectangle.draw(g,new ImageIcon("img/bluebackground.png").getImage());
		}
		if(level == 2){
			for(int i = 0; i<22; i++){
				rectangles2[i].drawYellowBricks(g);
			}
			for(int i = 22; i<35; i++){
				rectangles2[i].drawRedBricks(g);
			}
			mainRectangle.draw(g,new ImageIcon("img/yellowbackground.png").getImage());
		}
		if(level == 3){
			for(int i = 0; i<10; i++){
				rectangles3[i].drawYellowBricks(g);
			}
			for(int i = 10; i<20; i++){
				rectangles3[i].drawRedBricks(g);
			}
			for(int i = 20; i<30; i++){
				rectangles3[i].drawWhiteBricks(g);
			}
			for(int i = 30; i<40; i++){
				rectangles3[i].drawGreenBricks(g);
			}
			for(int i = 40; i<50; i++){
				rectangles3[i].drawBlueBricks(g);
			}
			mainRectangle.draw(g,new ImageIcon("img/yellowbackground.png").getImage());
			
		}
		if(level == 4){
			for(int i = 0; i<26; i++){
				rectangles4[i].drawRedBricks(g);
			}
			mainRectangle.draw(g,new ImageIcon("img/redbackground.png").getImage());
		}
		if(level == 5){
			for(int i = 0; i<26; i++){
				rectangles5[i].drawWhiteBricks(g);
			}
			for(int i = 26; i<28; i++){
				rectangles5[i].drawYellowBricks(g);
			}
			for(int i = 28; i<33; i++){
				rectangles5[i].drawGreenBricks(g);
			}
			mainRectangle.draw(g,new ImageIcon("img/yellowbackground.png").getImage());
		}
		if(level == 6){
			for(int i = 0; i<16; i++){
				rectangles6[i].drawGreenBricks(g);
			}
			for(int i = 16; i<34; i++){
				rectangles6[i].drawYellowBricks(g);
			}
			mainRectangle.draw(g,new ImageIcon("img/redbackground.png").getImage());
		}
		if(level == 7){
			for(int i = 0; i<10; i++){
				rectangles7[i].drawWhiteBricks(g);
			}
			for(int i = 10; i<19; i++){
				rectangles7[i].drawYellowBricks(g);
			}
			rectangles7[23].drawYellowBricks(g);
			for(int i = 19; i<23; i++){
				rectangles7[i].drawRedBricks(g);
			}
			for(int i = 24; i<30; i++){
				rectangles7[i].drawRedBricks(g);
			}
			for(int i = 31; i<35; i++){
				rectangles7[i].drawRedBricks(g);
			}
			rectangles7[30].drawYellowBricks(g);
			for(int i = 35; i<44; i++){
				rectangles7[i].drawYellowBricks(g);
			}
			mainRectangle.draw(g,new ImageIcon("img/yellowbackground.png").getImage());
		}
		repaint();
		
	}
	// method for setting coefficient 
	public void setCoefficient(int coefficient){
		this.coefficient = coefficient;
	}
	// method for setting speed
	public void setSpeed(int speed){
		this.speed = speed;
	}
	// method for starting timer 
	public void setStartTimer(boolean createTimer){
		this.createTimer = createTimer;
		if(createTimer == true){
			time = new Timer(speed , this);
			time.start();
		}
	}
	
	public boolean getPause(){
		return paused;
	}
	
	public void explosion(MyRectangle rectangles){
			// if ball hit a rectangle from top side then the ball reflect 45 degree and the rectangle is destroyed 
			if(ballX > rectangles.getX() - 10 && ballX < rectangles.getX() + 60 &&
					ballY == rectangles.getY() - 10 && rectangles.getX() != 0 && rectangles.getY() != 0){
				moveBallY = -1;
				score = score + (coefficient * 20);
				scoreLabel.setText("SCORE  " + score);
				exploded = true;
				bipSound("point.wav");
			}
			// if ball hit a rectangle from bottom side then the ball reflect 45 degree  and the rectangle is destroyed
			else if(ballX > rectangles.getX() - 10 && ballX < rectangles.getX() + 60 &&
					ballY == rectangles.getY() + 20 && rectangles.getX() != 0 && rectangles.getY() != 0){
				moveBallY = 1;
				score = score + (coefficient * 10);
				scoreLabel.setText("SCORE  " + score);
				exploded = true;
				bipSound("point.wav");
			}
			// if ball hit a rectangle from right side then the ball reflect 45 degree and the rectangle is destroyed
			else if(ballX == rectangles.getX() + 60 && ballY > rectangles.getY() -10 &&
					ballY < rectangles.getY() + 20 && rectangles.getX() != 0 && rectangles.getY() != 0 ||
					ballX == rectangles.getX() - 10 && ballY > rectangles.getY() -10 &&
					ballY < rectangles.getY() + 20 && rectangles.getX() != 0 && rectangles.getY() != 0){
				moveBallX = -moveBallX;
				score = score + (coefficient * 15);
				scoreLabel.setText("SCORE  " + score);
				exploded = true;
				bipSound("point.wav");				
			}
			else if(ballX == rectangles.getX() - 10 && ballY == rectangles.getY() -10 &&
					moveBallX > 0 && moveBallY > 0 || ballX == rectangles.getX() - 10 && 
					ballY == rectangles.getY() + 20 && moveBallX >0 && moveBallY < 0 ||
					ballX == rectangles.getX() + 60 && ballY == rectangles.getY() -10 &&
					moveBallX < 0 && moveBallY > 0 || ballX == rectangles.getX() + 60 &&
					ballY == rectangles.getY() + 20 && moveBallX < 0 && moveBallY < 0 ){
				moveBallX = -moveBallX;
				moveBallY = -moveBallY;
				score = score + (coefficient * 25);
				scoreLabel.setText("SCORE  " + score);
				exploded = true;
				bipSound("point.wav");				
			}
		
	}
	
	
	
	// method for playing background music
	public void playSound(String music){
		try {
	         // Open an audio input stream.
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(music));
	         // Get a sound clip resource.
	         clip = AudioSystem.getClip();
	         // Open audio clip and load samples from the audio input stream.
	         clip.open(audioIn);
	         clip.start();
		     clip.loop(Clip.LOOP_CONTINUOUSLY);
	      } catch (UnsupportedAudioFileException ex) {
	         ex.printStackTrace();
	      } catch (IOException ex) {
	         ex.printStackTrace();
	      } catch (LineUnavailableException ex) {
	         ex.printStackTrace();
	      }
		
			
		
	}
	// method for playing bip sound 
	public void bipSound(String hit){	
		if(play == true){
		try {
		
	         // Open an audio input stream.
			 AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(hit));
	         // Get a sound clip resource.
	         Clip clip = AudioSystem.getClip();
	         // Open audio clip and load samples from the audio input stream.
	         clip.open(audioIn);
	         clip.start();
	      } catch (UnsupportedAudioFileException ex) {
	         ex.printStackTrace();
	      } catch (IOException ex) {
	         ex.printStackTrace();
	      } catch (LineUnavailableException ex) {
	         ex.printStackTrace();
	      }	
		
			
		}
	}
	
	
	public String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        String line = br.readLine();
	       
	        return line;
	    }
	    finally {
	        br.close();
	    }
	}

	
	public void mouseClicked(MouseEvent e) {
		// if play is false stop all music
		if(e.getX() >= 0 && e.getX()<=30 && e.getY() >= 0 && e.getY() <=30 && play == true){
			play = false;
			
			sound.setIcon(muteIcon);
			clip.stop();
		}
		// if play is true start all music
		else if(e.getX() >= 0 && e.getX()<=30 && e.getY() >= 0 && e.getY() <=30 && play == false){
			play = true;
			
			sound.setIcon(soundIcon);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY); 
		}
			
	}
	
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	
	public void keyPressed(KeyEvent e) {
		
		// if user press right key do below
		if(e.getKeyCode() == e.VK_RIGHT && paused == false){
			step = 10;
			mainRectangle.move(step);
			// if main rectangle is at left border and user still press left key -+- don't change x coordinate of main rectangle
			if(mainRectangleX == 0 && step > 0){
				mainRectangleX = mainRectangleX + step; // new main rectanle's x coordinate
				if(control == true){
					ball.moveBall(step, 0);
					ballX = ballX + step;
				}
			}
			// if main rectangle is in game field let change x coordinate of main rectangle
			else if(mainRectangleX >0 && mainRectangleX<770){
				mainRectangleX = mainRectangleX + step; // new main rectangle's x coordinate
				if(control == true){
					ball.moveBall(step, 0);
					ballX = ballX + step;
				}
			}
		}
				
		
		else if(e.getKeyCode() == e.VK_LEFT && paused == false){
			step = -10;
			mainRectangle.move(step);
			
			// if main rectangle is at right border and user still press right key -+- don't change x coordinate of main rectangle
			if(mainRectangleX == 770 && step <0){
				mainRectangleX = mainRectangleX + step;
				if(control == true){
					ball.moveBall(step, 0);
					ballX = ballX + step;
				}
			}
			// if main rectangle is in game field let change x coordinate of main rectangle
			else if(mainRectangleX >0 && mainRectangleX<770){
				mainRectangleX = mainRectangleX + step;
				if(control == true){
					ball.moveBall(step, 0);
					ballX = ballX + step;
				}
			}
			
		}
	}

	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == e.VK_SPACE){
			
			// if ball is close to left border, the ball moves right in the beginning
			if (ballX < 420 && control == true){
				moveBallX = -1; 
				moveBallY = -1;
				start.setText("");
			}
			
			// if ball is close to right border, the ball moves left in the beginning
			else if (ballX >= 420 && control == true){
				moveBallX = 1;
				moveBallY = -1;
				start.setText("");
			}
			control = false;
		}
		if(moveBallX != 0 && moveBallY != 0){
			int controlMoveBallX = moveBallX;
			int controlMoveBallY = moveBallY;
		}
		if(e.getKeyCode() == e.VK_ESCAPE && paused == false && control == false){
			pause.setText("PAUSE");				
			moveBallX = 0;
			moveBallY = 0;
			paused = true;
		}
		else if(e.getKeyCode() == e.VK_ESCAPE && paused == true && control == false){
			pause.setText("");
			moveBallX = controlMoveBallX;
			moveBallY = controlMoveBallY;
			paused = false;
		}
	}
	public void keyTyped(KeyEvent e) {}
	
	
	public void actionPerformed(ActionEvent e) {
		if(moveBallX != 0 && moveBallY != 0){
			controlMoveBallX = moveBallX;
			controlMoveBallY = moveBallY;
		}
		// the ball is moving by this method
		ball.moveBall(moveBallX, moveBallY); 
		ballX = ballX + moveBallX; // new x coordinate of ball
		ballY = ballY + moveBallY; // new y coordinate of ball
		
		if(level==1){
			for(int i = 0; i<40; i++){
				explosion(rectangles1[i]);
				if(exploded == true){
					rectangles1[i] = new MyRectangle(1000,1000,0,0);
					passLevel[0]++;
					exploded = false;
				}
			}
		}
		// for level2's rectangles
		if(level == 2){
			for(int i = 0; i<35; i++){
				explosion(rectangles2[i]);
				if(exploded == true){
					rectangles2[i] = new MyRectangle(1000,1000,0,0);
					passLevel[1]++;
					exploded = false;
				}
			}
		}
		
		if(level==3){
			for(int i = 0; i<50; i++){
				explosion(rectangles3[i]);
				if(exploded == true){
					rectangles3[i] = new MyRectangle(1000,1000,0,0);
					passLevel[2]++;
					exploded = false;
				}
			}
		}
		// for level4's rectangles
		if(level==4){
			for(int i = 0; i<26; i++){
				explosion(rectangles4[i]);
				if(exploded == true){
					rectangles4[i] = new MyRectangle(1000,1000,0,0);
					passLevel[3]++;
					exploded = false;
				}
			}
		}
		// for level5's rectangles
		if(level==5){
			for(int i = 0; i<33; i++){
				explosion(rectangles5[i]);
				if(exploded == true){
					rectangles5[i] = new MyRectangle(1000,1000,0,0);
					passLevel[4]++;
					exploded = false;
				}
			}
		}
		// for level6's rectangles
		if(level==6){
			for(int i = 0; i<34; i++){
				explosion(rectangles6[i]);
				if(exploded == true){
					rectangles6[i] = new MyRectangle(1000,1000,0,0);
					passLevel[5]++;
					exploded = false;
				}
			}
		}
		// for level7's rectangles
		if(level==7){
			for(int i = 0; i<44; i++){
				explosion(rectangles7[i]);
				if(exploded == true){
					rectangles7[i] = new MyRectangle(1000,1000,0,0);
					passLevel[6]++;
					exploded = false;
				}
			}
		}
		
		// if ball hit main rectangle from top side then the ball reflect
		if(ballX > mainRectangleX -10 && ballX < mainRectangleX + 80 && 
				ballY == mainRectangleY -10 ){
			moveBallY = -moveBallY;
			if(control == false){
				bipSound("hit.wav");
			}	
		}
		// if ball hit main rectangle from right side then the ball reflect
		else if(ballX == mainRectangleX + 80 && ballY > mainRectangleY - 10 &&
				ballY < mainRectangleY + 5){
			moveBallX = 1;
		}
		// if ball hit main rectangle from left side then the ball reflect
		else if(ballX == mainRectangleX - 10 && ballY > mainRectangleY -10 &&
				ballY < mainRectangleY + 5){
			moveBallX = -1;
			bipSound("hit.wav");
		}
		else if(ballX == mainRectangleX - 10 && ballY == mainRectangleY -10 
				&& moveBallX > 0 && moveBallY > 0){
			moveBallX = -moveBallX;
			moveBallY = -moveBallY;
			bipSound("hit.wav");
		}
		
		else if(ballX == mainRectangleX + 80 && ballY == mainRectangleY -10 
				&& moveBallX < 0 && moveBallY > 0){
			moveBallX = -moveBallX;
			moveBallY = -moveBallY;
			bipSound("hit.wav");
		}
		
		// if ball hit game field from right side then the ball reflect
		if(ballX + 10 == 850){
			moveBallX = -moveBallX;
			bipSound("hit.wav");
		}
		// if ball hit main rectangle from left side then the ball reflect
		if(ballY  == 0){
			moveBallY = -moveBallY;
			bipSound("hit.wav");
		}
		// if ball hit main rectangle from top side then the ball reflect
		if(ballX  == 0){
			moveBallX = -moveBallX;
			bipSound("hit.wav");
		}
		if(ballY == 550){
			// if ball drop and user have 3 heart   one heart will be cleaned from top and user can continue to play 
			if(remainHeart == 3){
				moveBallX = 0;
				moveBallY = 0;
				ballX = 415;
				ballY = 490;
				
				x = 25; 
				y = 25;		
				width = 60;   
				height = 20;
				
				mainRectangleX = 380; 
				mainRectangleY = 500;
				
				mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
				ball = new Ball(ballX,ballY,10,10);
				
				scoreLabel.setText("SCORE  " + score);
				levelLabel.setText("LEVEL  " + level);
				start.setText("Press SPACE to start game!");
				control = true;
				heart3.setIcon(null);
				remainHeart = 2;
			}
			// if ball drop and user have 2 heart   one heart will be cleaned from top and user can continue to play 
			else if(remainHeart == 2){
				moveBallX = 0;
				moveBallY = 0;
				ballX = 415;
				ballY = 490;
				
				x = 25; 
				y = 25;		
				width = 60;   
				height = 20;
				
				mainRectangleX = 380; 
				mainRectangleY = 500;
				
				mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
				ball = new Ball(ballX,ballY,10,10);
				
				scoreLabel.setText("SCORE  " + score);
				levelLabel.setText("LEVEL  " + level);
				start.setText("Press SPACE to start game!");
				control = true;
				heart2.setIcon(null);
				remainHeart = 1;
			}
			/* if ball drop and user have 3 heart   one heart will be cleaned from top and 
			user can not continue to play and game will over */
			else if (remainHeart ==1){
				remainHeart = 0;
			}
		// if user don't have any heart game is over  timer will stop and game over timer will start after 7 seconds game restarts
		}
		if(remainHeart == 0){
			time.stop();
			gameOverTimer.start();
			heart1.setIcon(null);
			
			String textOfFile = null;
			
			try{
			 textOfFile = readFile("HighScore/highScore.txt");
			 if(textOfFile == null || Integer.parseInt(textOfFile) < score){
				 BufferedWriter writer = null;
			        try {
			            
			           File myFile = new File("HighScore/highScore.txt");
			           writer = new BufferedWriter(new FileWriter(myFile));
			           
			            writer.write(Integer.toString(score));
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        } finally {
			            try {
			                // Close the writer
			                writer.close();
			            } catch (Exception e1) {
			            }
			        }
			}
			}
			catch(IOException e1){
			}
			
			if(gameOver == 0){
				
				
				
				
				
				moveBallX = 0;
				moveBallY = 0;
				ballX = 415;
				ballY = 490;
				
				x = 25; 
				y = 25;		
				width = 60; 
				height = 20;
				
				mainRectangleX = 380; 
				mainRectangleY = 500;
				
				mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
			
				
				ball = new Ball(ballX,ballY,10,10);
				
				score = 0;
				level = 1;
				level3();
				level2();
				level1();
				level4();
				level5();
				level6();
				level7();

				scoreLabel.setText("SCORE  " + score);
				levelLabel.setText("LEVEL  " + level);
				start.setText("Press SPACE to start game!");
				control = true;
				
				passLevel[0] = 0;
				passLevel[1] = 0;
				passLevel[2] = 0;
				passLevel[3] = 0;
				passLevel[4] = 0;
				passLevel[5] = 0;
				passLevel[6] = 0;
				
				image = new ImageIcon("img/background4.png").getImage();
				heart1.setIcon(heart);
				heart2.setIcon(heart);
				heart3.setIcon(heart);
				remainHeart = 3;
				time.start();
				gameOverTimer.stop();
				gameOver = 7;
				gameOverLabel.setIcon(null);
				clip.stop();
				playSound("background.wav");
				clip.start();
			}
			else{
				gameOverLabel.setIcon(gameOverIcon);
				gameOver--;
			}
		}
		
		// if user can hit all rectangles at level 1, user can pass level 2
		if(passLevel[0] == 40){
			level = 2;
			levelLabel.setText("LEVEL  " + level);
		}
		if(level == 2 && passLevel[0] == 40){
			
			ballX = 410;
			ballY = 490;
			
			ball = new Ball(ballX,ballY,10,10);
			
			mainRectangleX = 380;
			mainRectangleY = 500;
			
			mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
			
			moveBallX = 0;
			moveBallY = 0;
			
			control = true;
			start.setText("Press SPACE to start game!");
			passLevel[0]++;
			clip.stop();
			playSound("background2.wav");
			clip.start();
			image = new ImageIcon("img/background5.png").getImage();
		}
		// if user can hit all rectangles at level 2, user can pass level 3
		if(passLevel[1] == 35){
			level = 3;
			levelLabel.setText("LEVEL  " + level);
		}
		if(level == 3 && passLevel[1] == 35){
			
			ballX = 410;
			ballY = 490;
			
			ball = new Ball(ballX,ballY,10,10);
			
			mainRectangleX = 380;
			mainRectangleY = 500;
			
			mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
			
			moveBallX = 0;
			moveBallY = 0;
			
			control = true;
			start.setText("Press SPACE to start game!");
			passLevel[1]++;
			clip.stop();
			playSound("background3.wav");
			clip.start();
			image = new ImageIcon("img/background2.png").getImage();
		}
		// if user can hit all rectangles at level 3, user can pass level 4
		if(passLevel[2] == 50){
			level = 4;
			levelLabel.setText("LEVEL  " + level);
		}
		if(level == 4 && passLevel[2] == 50){
			ballX = 410;
			ballY = 490;
			
			ball = new Ball(ballX,ballY,10,10);
			
			mainRectangleX = 380;
			mainRectangleY = 500;
			
			mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
			
			moveBallX = 0;
			moveBallY = 0;
			
			control = true;
			start.setText("Press SPACE to start game!");
			passLevel[2]++;
			clip.stop();
			playSound("background4.wav");
			clip.start();
			image = new ImageIcon("img/background3.png").getImage();
		}
		// if user can hit all rectangles at level 3, user can pass level 4
		if(passLevel[3] == 26){
			level = 5;
			levelLabel.setText("LEVEL  " + level);
		}
		if(level == 5 && passLevel[3] == 26){
			ballX = 410;
			ballY = 490;
			
			ball = new Ball(ballX,ballY,10,10);
			
			mainRectangleX = 380;
			mainRectangleY = 500;
			
			mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
			
			moveBallX = 0;
			moveBallY = 0;
			
			control = true;
			start.setText("Press SPACE to start game!");
			passLevel[3]++;
			clip.stop();
			playSound("background5.wav");
			clip.start();
			image = new ImageIcon("img/background6.png").getImage();
		}
		if(passLevel[4] == 33){
			level = 6;
			levelLabel.setText("LEVEL  " + level);
		}
		if(level == 6 && passLevel[4] == 33){
			ballX = 410;
			ballY = 490;
			
			ball = new Ball(ballX,ballY,10,10);
			
			mainRectangleX = 380;
			mainRectangleY = 500;
			
			mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
			
			moveBallX = 0;
			moveBallY = 0;
			
			control = true;
			start.setText("Press SPACE to start game!");
			passLevel[4]++;
			clip.stop();
			playSound("background6.wav");
			clip.start();
			image = new ImageIcon("img/background7.png").getImage();
		}
		if(passLevel[5] == 34){
			level = 7;
			levelLabel.setText("LEVEL  " + level);
		}
		if(level == 7 && passLevel[5] == 34){
			ballX = 410;
			ballY = 490;
			
			ball = new Ball(ballX,ballY,10,10);
			
			mainRectangleX = 380;
			mainRectangleY = 500;
			
			mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
			
			moveBallX = 0;
			moveBallY = 0;
			
			control = true;
			start.setText("Press SPACE to start game!");
			passLevel[5]++;
			clip.stop();
			playSound("background7.wav");
			clip.start();
			image = new ImageIcon("img/background8.png").getImage();
		}
		if(passLevel[6] == 44){
			level = 8;
			moveBallX = 1000;
			moveBallY = 1000;
		}
		if(level == 8 && passLevel[6] == 44){
			
			String textOfFile = null;
			
			try{
			 textOfFile = readFile("HighScore/highScore.txt");
			 if(textOfFile == null || Integer.parseInt(textOfFile) < score){
				 BufferedWriter writer = null;
			        try {
			           
			           File myFile = new File("HighScore/highScore.txt");
			           writer = new BufferedWriter(new FileWriter(myFile));
			          
			            writer.write(Integer.toString(score));
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        } finally {
			            try {
			                // Close the writer regardless of what happens...
			                writer.close();
			            } catch (Exception e1) {
			            }
			        }
			}
			}
			catch(IOException e1){
			}
			
			time.stop();
			gameOverTimer.start();
			
						
			if(gameOver == 0){
				
								
				moveBallX = 0;
				moveBallY = 0;
				ballX = 415;
				ballY = 490;
				
				x = 25; 
				y = 25;		
				width = 60; 
				height = 20;
				
				mainRectangleX = 380; 
				mainRectangleY = 500;
				
				mainRectangle = new MyRectangle(mainRectangleX, mainRectangleY, 80, 5);
			
				
				ball = new Ball(ballX,ballY,10,10);
				
				score = 0;
				level = 1;
				level3();
				level2();
				level1();
				level4();
				level5();
				level6();
				level7();

				scoreLabel.setText("SCORE  " + score);
				levelLabel.setText("LEVEL  " + level);
				start.setText("Press SPACE to start game!");
				control = true;
				
				passLevel[0] = 0;
				passLevel[1] = 0;
				passLevel[2] = 0;
				passLevel[3] = 0;
				passLevel[4] = 0;
				passLevel[5] = 0;
				passLevel[6] = 0;
				
				image = new ImageIcon("img/background4.png").getImage();
				heart1.setIcon(heart);
				heart2.setIcon(heart);
				heart3.setIcon(heart);
				remainHeart = 3;
				time.start();
				gameOverTimer.stop();
				gameOver = 7;
				congLabel.setIcon(null);
				clip.stop();
				playSound("background.wav");
				clip.start();
			}
			else{
				congLabel.setIcon(congIcon);
				gameOver--;
			}
		}
	}
}
