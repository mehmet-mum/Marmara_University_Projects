import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Space extends JFrame implements ActionListener{
	
	JFrame frame;
	Panel panel;
	
	
	MyPanel mainPanel = new MyPanel();
	JFrame mainFrame = new JFrame("BRICKS");
	
	JFrame howFrame;
	HowToPlayPanel howPanel;
	
	JButton newGame = new JButton("",new ImageIcon("img/newgame_150x29.png"));
	JButton highScore = new JButton("",new ImageIcon("img/highscore_155x29.png"));
	JButton about = new JButton("",new ImageIcon("img/howtoplay_192x29.png"));
	JButton exit = new JButton("",new ImageIcon("img/exit_59x29.png"));
	JButton easy = new JButton("",new ImageIcon("img/easy_69x29.png"));
	JButton normal = new JButton("",new ImageIcon("img/normal_110x29.png"));
	JButton hard = new JButton("",new ImageIcon("img/hard_69x29.png"));
	
	Icon dif = new ImageIcon("img/dif_150x26.png");
	Icon difeasy = new ImageIcon("img/difeasy_249x29.png");
	Icon difnormal = new ImageIcon("img/difnormal_290x29.png");
	Icon difhard = new ImageIcon("img/difhard_249x29.png");
	
	JLabel difficulty = new JLabel(dif);
	
	JLabel howToPlay1;
	JLabel howToPlay2;
	JLabel howToPlay3;
	JLabel howToPlay4;
	JLabel howToPlay5;
	JLabel howToPlay6;
	JLabel howToPlay7;
	JLabel howToPlay8;
	boolean bool = false;
	
	Font howFont;
	Font choronometerFont;
	
	
	Timer time;
	int hour;
	int minute;
	int second;
	JLabel choronometer = new JLabel("TIME : " + hour + " . " + minute + " . " + second);
	public Space(){
		time = new Timer(1000,this);
		time.start();
		
		frame = new JFrame("BRICKS");
		frame.setBounds(250, 100, 857, 579);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(false);
		frame.setResizable(false);
		
		panel = new Panel();
		frame.addKeyListener(panel);
		frame.addMouseListener(panel);
		frame.add(panel);
		panel.add(choronometer);
		
		choronometerFont = new Font(null,Font.BOLD,15);
		
		choronometer.setBounds(720, 530, 200, 15);
		choronometer.setForeground(Color.WHITE);
		choronometer.setFont(choronometerFont);
		
		newGame.addActionListener(this);
		highScore.addActionListener(this);
		about.addActionListener(this);
		exit.addActionListener(this);
		easy.addActionListener(this);
		normal.addActionListener(this);
		hard.addActionListener(this);
		
		
		mainFrame.setBounds(250, 100, 857, 579);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		
		mainFrame.add(mainPanel);
		
		mainPanel.setBounds(0,0,800,550);
		
		mainPanel.setLayout(null);
		mainPanel.add(newGame);
		mainPanel.add(highScore);
		mainPanel.add(about);
		mainPanel.add(exit);
		mainPanel.add(easy);
		mainPanel.add(normal);
		mainPanel.add(hard);
		mainPanel.add(difficulty);
		
		newGame.setBounds(225,250,150,50);
		highScore.setBounds(220,325,160,50);
		about.setBounds(200, 400, 200, 50);
		exit.setBounds(225, 475, 150, 50);
		easy.setBounds(525, 250, 150, 50);
		normal.setBounds(530, 325, 150, 50);
		hard.setBounds(525, 400, 150, 50);
		difficulty.setBounds(450, 475, 300, 50);
		
		newGame.setBackground(Color.CYAN);
		newGame.setOpaque(false);
		newGame.setBorderPainted(false);
		newGame.setFocusPainted(false);
		
		highScore.setBackground(Color.CYAN);
		highScore.setOpaque(false);
		highScore.setBorderPainted(false);
		highScore.setFocusPainted(false);
		
		about.setBackground(Color.CYAN);
		about.setOpaque(false);
		about.setBorderPainted(false);
		about.setFocusPainted(false);
		
		exit.setBackground(Color.CYAN);
		exit.setOpaque(false);
		exit.setBorderPainted(false);
		exit.setFocusPainted(false);
		
		easy.setBackground(Color.CYAN);
		easy.setOpaque(false);
		easy.setBorderPainted(false);
		easy.setFocusPainted(false);
		
		normal.setBackground(Color.CYAN);
		normal.setOpaque(false);
		normal.setBorderPainted(false);
		normal.setFocusPainted(false);
		
		hard.setBackground(Color.CYAN);
		hard.setOpaque(false);
		hard.setBorderPainted(false);
		hard.setFocusPainted(false);
		
		
		howFrame = new JFrame("HOW TO PLAY");
		howFrame.setBounds(325, 150, 707, 479);
		howFrame.setVisible(false);
		howFrame.setResizable(false);
		
		howPanel = new HowToPlayPanel();
		howPanel.setLayout(null);
		howFrame.add(howPanel);
		
		howFont = new Font(null, Font.BOLD,30);
		
		howToPlay1 = new JLabel("TRY TO HIT ALL BRICKS EACH LEVEL");
		howPanel.add(howToPlay1);
		howToPlay1.setFont(howFont);
		howToPlay1.setBounds(10,20,700,30);
		howToPlay1.setForeground(Color.YELLOW);
		
		howToPlay2 = new JLabel("MOVE YOUR SHIP BY PRESSING");
		howPanel.add(howToPlay2);
		howToPlay2.setFont(howFont);
		howToPlay2.setBounds(10,70,700,30);
		howToPlay2.setForeground(Color.YELLOW);
		
		howToPlay3 = new JLabel("LEFT AND RIGHT KEY");
		howPanel.add(howToPlay3);
		howToPlay3.setFont(howFont);
		howToPlay3.setBounds(10,120,700,30);
		howToPlay3.setForeground(Color.YELLOW);
		
		howToPlay4 = new JLabel("YOU CAN TURN ON THE MUSIC AND");
		howPanel.add(howToPlay4);
		howToPlay4.setFont(howFont);
		howToPlay4.setBounds(10,170,700,30);
		howToPlay4.setForeground(Color.YELLOW);
		
		howToPlay5 = new JLabel("TURN OFF THE MUSIC BY CLICKING");
		howPanel.add(howToPlay5);
		howToPlay5.setFont(howFont);
		howToPlay5.setBounds(10,220,700,30);
		howToPlay5.setForeground(Color.YELLOW);
		
		howToPlay6 = new JLabel("SOUND ICON");
		howPanel.add(howToPlay6);
		howToPlay6.setFont(howFont);
		howToPlay6.setBounds(10,270,700,30);
		howToPlay6.setForeground(Color.YELLOW);
		
		howToPlay7 = new JLabel("YOU CAN PAUSE GAME BY PRESSING ESC");
		howPanel.add(howToPlay7);
		howToPlay7.setFont(howFont);
		howToPlay7.setBounds(10,320,700,30);
		howToPlay7.setForeground(Color.YELLOW);
		
		howToPlay8 = new JLabel("IF YOU HAVE NO HEART GAME IS OVER");
		howPanel.add(howToPlay8);
		howToPlay8.setFont(howFont);
		howToPlay8.setBounds(10,370,700,30);
		howToPlay8.setForeground(Color.YELLOW);
	}
	
	
	public static void main(String[] args){
		Space space = new Space();
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == newGame){
			if(bool == false){
				JOptionPane.showMessageDialog(null, "CHOOSE A DIFFICULTY");
			}
			else if(bool == true){
				frame.setVisible(true);
				mainFrame.setVisible(false);
				panel.setStartTimer(true);
			}	
		}
		if(e.getSource() == highScore){
			try {
				JOptionPane.showMessageDialog(null, "High Score is " + readFile("HighScore/highScore.txt"),
						"HIGH SCORE",JOptionPane.INFORMATION_MESSAGE);
			} catch (HeadlessException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(e.getSource() == about){
			howFrame.setVisible(true);
		}
		if(e.getSource() == exit){
			System.exit(0);
		}
		
		if(e.getSource() == easy){
			panel.setSpeed(6);
			panel.setCoefficient(1);
			difficulty.setIcon(difeasy);
			bool = true;
		}
		else if(e.getSource() == normal){
			panel.setSpeed(5);
			panel.setCoefficient(2);
			difficulty.setIcon(difnormal);
			bool = true;
		}
		else if(e.getSource() == hard){
			panel.setSpeed(4);
			panel.setCoefficient(3);
			difficulty.setIcon(difhard);
			bool = true;
		}
		// choronometer
		if(minute == 59){
			minute = 0;
			hour++;
			choronometer.setText("TIME : " + hour + " . " + minute + " . " + second);
		}
		else if(second == 59){
			second = 0;
			minute++;
			choronometer.setText("TIME : " + hour + " . " + minute + " . " + second);
		}
		else{
			if(panel.getPause() == false)
			second++;
			choronometer.setText("TIME : " + hour + " . " + minute + " . " + second);
		}
		
			
	}
}

//new class for drawing backgrounds at main panel
class MyPanel extends JPanel{
	
	
	public MyPanel(){
		super();
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponents(g);
		g.drawImage(new ImageIcon("img/background.jpg").getImage(), 0, 0, 850, 550, null);
		g.drawImage(new ImageIcon("img/bricks.png").getImage(), 75, 0, 700, 250, null);
	}
	
}
//new class for drawing background at how to play panel
class HowToPlayPanel extends JPanel{
	public HowToPlayPanel(){
		super();
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponents(g);
		g.drawImage(new ImageIcon("img/howtoplayBack.png").getImage(), 0, 0, 700, 450, null);
	}
}
