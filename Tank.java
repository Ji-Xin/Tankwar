package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Tank{
	int x,y; //position
	int dir; //direction{left37, up38, right39, down40}
	public int old_dir;
	public boolean first_collide;
	boolean mine; //true for myTank and friendTank, false for enemyTank
	public boolean friend;
	public static final int size=30, gun_size=6, speed=3;
	Color army_green = new Color(77, 153, 0);
	Color dark_green = new Color(102, 51, 0);
	boolean moving;
	boolean alive;
	boolean colliding;//with walls or other tanks
	Game parent;
	boolean fire_ready;

	public Tank(int xx, int yy, int d, Game p){
		friend = false;
		mine = true;
		x = xx;
		y = yy;
		dir = d;
		old_dir = -1;
		first_collide = false;
		parent = p;
		moving = false;
		alive = true;
		fire_ready = true;
	}

	public void draw(Graphics g){
		if (mine)
			g.setColor(army_green);
		else
			g.setColor(Color.red);
		g.fillRect(x, y, size, size);

		if (mine)
		{
			g.setColor(dark_green);
			if (friend)
				g.setColor(Color.blue);
		}
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
		try{
			int key = e.getKeyCode();
			boolean flag = true;
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
					flag = false;
					break;
			}

			if (flag)
			{
				synchronized(parent.sender)
				{
					parent.sender.writeBytes("$myTankMotion\n");
					parent.sender.writeBytes(dir+"\n");
				}
			}
		} catch(Exception ex){System.err.println("[E]\t"+ex);}
	}

	public void fire(){
		if (fire_ready)
		{
			Bullet bul=null;
			switch(dir)
			{
				case 0:
					bul = new Bullet(
						x-Tank.size/2,
						y+Tank.size/2-Tank.gun_size/2,
						dir, mine);
					break;
				case 1:
					bul = new Bullet(
						x+Tank.size/2-Tank.gun_size/2,
						y-Tank.size/2,
						dir, mine);
					break;
				case 2:
					bul = new Bullet(
						x+Tank.size*3/2,
						y+Tank.size/2-Tank.gun_size/2,
						dir, mine);
					break;
				case 3:
					bul = new Bullet(
						x+Tank.size/2-Tank.gun_size/2,
						y+Tank.size*3/2,
						dir, mine);
					break;
			}
			if (mine)
				parent.myBullets.add(bul);
			else
				parent.enemyBullets.add(bul);
			if (parent.isServer || mine)
				synchronized(parent.sender)
				{
					try{
						parent.sender.writeBytes("$fire\n");
						parent.sender.writeBytes(bul.x+","+bul.y+","+bul.dir+","+bul.mine+"\n");
					} catch(Exception ex){System.err.println("[E]\t"+ex);}
				}
			(new FireWait()).start();
		}
	}

	private class FireWait extends Thread{
		public void run(){
			fire_ready = false;
			try{Thread.sleep(700);}catch(Exception e){System.err.println("[E]\t"+e);}
			fire_ready = true;
		}
	}

	public void released(KeyEvent e){
		try{
			int key = e.getKeyCode();
			if (37<=key && key<=40)
				synchronized(parent.sender)
				{
					parent.sender.writeBytes("$myTankStop\n");
					moving = false;
				}
		} catch(Exception ex){System.err.println("[E]\t"+ex);}
	}

	public void move(){
		if (moving && !this.out() && !colliding)//prevent it goes out or into the wall
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
		if ((x<0 && dir==0) ||
			(x>Game.width-Tank.size && dir==2) ||
			(y<0 && dir==1) ||
			(y>Game.height-Tank.size && dir==3))//prevent it gets stuck in walls
			return true;

		return false;
	}
}