package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class Bullet{
	int x,y;
	static int speed=5;
	int dir;

	public Bullet(int xx, int yy, int d){
		x = xx;
		y = yy;
		dir = d;
	}

	public void draw(Graphics g){
		g.setColor(Color.black);
		g.fillRect(x, y, Tank.gun_size, Tank.gun_size);
		this.move();
	}

	public void move(){
		switch(dir)
		{
			case 0:
				x-=speed;
				break;
			case 1:
				y-=speed;
				break;
			case 2:
				x+=speed;
				break;
			case 3:
				y+=speed;
				break;
		}
	}
}