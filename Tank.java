package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Tank{
	int x,y; //position
	int dir; //direction{left37, up38, right39, down40}
	boolean mine; //my tank or enemy
	public static final int size=30, gun_size=6, speed=3;
	Color army_green = new Color(77, 153, 0);
	Color dark_green = new Color(102, 51, 0);
	boolean moving;
	Game parent;

	public Tank(int xx, int yy, int d, Game p){
		mine = true;
		x = xx;
		y = yy;
		dir = d;
		parent = p;
		moving = false;
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

	public void pressed(KeyEvent e){
		int key = e.getKeyCode();
		switch(key)
		{
			case 37:
				dir = 0;
				moving = true;
				break;
			case 38:
				dir = 1;
				moving = true;
				break;
			case 39:
				dir = 2;
				moving = true;
				break;
			case 40:
				dir = 3;
				moving = true;
				break;

			case 32://fire
				fire();
				break;
		}
	}

	public void fire(){
		Bullet bul=null;
		switch(dir)
		{
			case 0:
				bul = new Bullet(
					x-Tank.size/2,
					y+Tank.size/2-Tank.gun_size/2,
					dir);
				break;
			case 1:
				bul = new Bullet(
					x+Tank.size/2-Tank.gun_size/2,
					y-Tank.size/2,
					dir);
				break;
			case 2:
				bul = new Bullet(
					x+Tank.size*3/2,
					y+Tank.size/2-Tank.gun_size/2,
					dir);
				break;
			case 3:
				bul = new Bullet(
					x+Tank.size/2-Tank.gun_size/2,
					y+Tank.size*3/2,
					dir);
				break;
		}
		parent.bullets.add(bul);
	}

	public void released(KeyEvent e){
		int key = e.getKeyCode();
		if (37<=key && key<=40)
			moving = false;
	}

	public void move(){
		if (moving)
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