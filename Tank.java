package code;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.*;

public class Tank{
	int x,y; //position
	int dir; //direction{left37, up38, right39, down40}
	public boolean first_collide;
	boolean mine; //true for myTank and friendTank, false for enemyTank
	public boolean friend;
	public static final int size=30, gun_size=6, speed=3;
	Color army_green = new Color(77, 153, 0);
	Color dark_green = new Color(102, 51, 0);
	boolean alive;
	int health;
	int colliding; // which directions is blocked, -1 for not blocked
	Game parent;
	boolean fire_ready;
	static File fire_sound;

	public Tank(int xx, int yy, int d, Game p){
		fire_sound = new File("code/source/Shot.wav");
		friend = false;
		mine = true;
		x = xx;
		y = yy;
		dir = d;
		first_collide = false;
		parent = p;
		alive = true;
		fire_ready = true;
		colliding = -1;
	}

	public void draw(Graphics g){
		g.setColor(army_green);
		g.fillRect(x, y, size, size);

		g.setColor(dark_green);
		if (friend)
			g.setColor(Color.blue);

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
			boolean flag_move = true;
			switch(key)
			{
				case 37:
					dir = 0;
					break;
				case 38:
					dir = 1;
					break;
				case 39:
					dir = 2;
					break;
				case 40:
					dir = 3;
					break;

				case 32://fire
					fire();
					flag_move = false;
					break;
			}

			if (flag_move)
			{
				move();
				synchronized(parent.sender)
				{
					parent.sender.writeBytes("$myTankMotion\n");
					parent.sender.writeBytes(dir+"\n");
				}
			}
		} catch(Exception ex){ex.printStackTrace();System.exit(0);}
	}

	public void fire() throws Exception{
		if (fire_ready)
		{
			Bullet bul=null;
			switch(dir)
			{
				case 0:
					bul = new Bullet(
						x-Tank.size/2,
						y+Tank.size/2-Tank.gun_size/2,
						dir, mine, true);
					break;
				case 1:
					bul = new Bullet(
						x+Tank.size/2-Tank.gun_size/2,
						y-Tank.size/2,
						dir, mine, true);
					break;
				case 2:
					bul = new Bullet(
						x+Tank.size*3/2,
						y+Tank.size/2-Tank.gun_size/2,
						dir, mine, true);
					break;
				case 3:
					bul = new Bullet(
						x+Tank.size/2-Tank.gun_size/2,
						y+Tank.size*3/2,
						dir, mine, true);
					break;
			}
			if (mine)
			{
				AudioInputStream audioIn = AudioSystem.getAudioInputStream(fire_sound);
				DataLine.Info info = new DataLine.Info(Clip.class, audioIn.getFormat());
				Clip clip = (Clip)AudioSystem.getLine(info);
				clip.open(audioIn);
				clip.start();

				parent.myBullets.add(bul);
			}
			else
				parent.enemyBullets.add(bul);
			if (parent.isServer || mine)
				synchronized(parent.sender)
				{
					try{
						parent.sender.writeBytes("$fire\n");
						parent.sender.writeBytes(bul.x+","+bul.y+","+bul.dir+","+bul.mine+"\n");
					} catch(Exception ex){ex.printStackTrace();System.exit(0);}
				}
			(new FireWait()).start();
		}
	}

	private class FireWait extends Thread{
		public void run(){
			fire_ready = false;
			try{Thread.sleep(700);}catch(Exception ex){ex.printStackTrace();System.exit(0);}
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
					//moving = false;
				}
		} catch(Exception ex){ex.printStackTrace();System.exit(0);}
	}

	public void move(){
		if (colliding!=dir && this.out()==-1)//prevent it goes out or into the wall
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

	public int out(){
		if ((x<0 && dir==0) ||
			(x>Game.width-Tank.size && dir==2) ||
			(y<0 && dir==1) ||
			(y>Game.height-Tank.size && dir==3))//prevent it gets stuck in walls
			return dir;

		return -1;
	}
}