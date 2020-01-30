import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class Ball{
	int x, y, width, height ;
	
	// a constructor for creating a ball by given variables
	public Ball(int x, int y, int width, int height){
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	// a method for drawing a ball
	public void drawBall(Graphics g){
		g.drawImage(new ImageIcon("img/ball.png").getImage(),x, y, width, height, null);
		
	}
	
	// a method for moving the ball in game field
	public void moveBall(int moveX, int moveY){
		x= x + moveX;
		y= y + moveY;
	}
	
	
}
