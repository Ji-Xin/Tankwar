package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class Bullet{
	int x,y;
	static int speed=5;
	int dir;
	boolean mine;

	public Bullet(int xx, int yy, int d, boolean m){
		x = xx;
		y = yy;
		dir = d;
		mine = m;
	}

	public void draw(Graphics g){
		if (mine)
			g.setColor(Color.black);
		else
			g.setColor(Color.magenta);
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

	public boolean out(){
		if (x<0 || x>Game.width || y<0 || y>Game.height)
			return true;
		return false;
	}
}