import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class MyRectangle extends Rectangle{
	Rectangle r;
	int x,y,width,height;
	
	// a constructor for creating a rectangle by given variables
	public MyRectangle(int x, int y, int width, int height){	
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		r = new Rectangle(x, y, width, height);
		
	}
	
	
	// a method for drawing other rectangles which we are trying to hit
	public void drawYellowBricks(Graphics g){
		g.drawImage(new ImageIcon("img/yellow.png").getImage(),(int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight(), null);
	}
	
	public void drawWhiteBricks(Graphics g){
		g.drawImage(new ImageIcon("img/white.png").getImage(),(int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight(), null);
	}
	
	public void drawRedBricks(Graphics g){
		g.drawImage(new ImageIcon("img/red.png").getImage(),(int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight(), null);
	}
	
	public void drawGreenBricks(Graphics g){
		g.drawImage(new ImageIcon("img/green.png").getImage(),(int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight(), null);
	}
	
	public void drawBlueBricks(Graphics g){
		g.drawImage(new ImageIcon("img/blue.png").getImage(),(int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight(), null);
	}
	
	// a method for drawing main rectangle
	public void draw(Graphics g, Image img){
		g.drawImage(img,(int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight(), null);
	}
	
	// a method for moving the main rectangle
	public void move(int moveX){
		// if main rectangle is at left border it can only move left side
		if(r.getX() == 0 && moveX >0){
			r.setLocation((int)r.getX() + moveX, (int)r.getY());
		}
		
		// if main rectangle is at right border it can only move right side
		else if(r.getX() == 770 && moveX <0){
			r.setLocation((int)r.getX() + moveX, (int)r.getY());
		}
			
		// if main rectangle is in game field it can move both right or left side
		else if(r.getX() >0 && r.getX()<770){
			r.setLocation((int)r.getX() + moveX, (int)r.getY());
		}
		
	}
	
	
	// get method for x and y
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	
	

	
	
}