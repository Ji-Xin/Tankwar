package game.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class WalkingTank{
	public int x,y; //position
	public int dir; //direction{left37, up38, right39, down40}
	boolean mine; //my tank or enemy
	public static final int size=30, gun_size=6, speed=3;
	Color army_green = new Color(77, 153, 0);
	Color dark_green = new Color(102, 51, 0);

	public WalkingTank(int xx, int yy, int d, boolean m){
		mine = m;
		x = xx;
		y = yy;
		dir = d;
	}
	
	public void set(int xx, int yy, int d){
		x = xx;
		y = yy;
		dir = d;
	}

	public void draw(Graphics g){
		if (mine)
			g.setColor(army_green);
		else
			g.setColor(Color.red);
		g.fillRect(x, y, size, size);

		if (mine)
			g.setColor(dark_green);
		else
			g.setColor(Color.orange);
		switch(dir)
		{
			case 0:				
				g.fillRect(x-size/2, y-gun_size/2+size/2, size, gun_size);
				break;
			case 1:
				g.fillRect(x+size/2-gun_size/2, y-size/2, gun_size, size);
				break;
			case 2:
				g.fillRect(x+size/2, y-gun_size/2+size/2, size, gun_size);
				break;
			case 3:
				g.fillRect(x+size/2-gun_size/2, y+size/2, gun_size, size);
				break;
		}
	}
}