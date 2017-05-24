package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;

public class Game extends JPanel{
	JFrame frame;
	public static final int width=800, height=600;
	public static final int delay=50;
	Color bground;
	Tank myTank, fTank; //friendTank
	Motion mo;
	Hit hi;
	ArrayList<Bullet> myBullets; //including friend bullets
	ArrayList<Bullet> enemyBullets;
	public ArrayList<EnemyTank> enemies;
	ArrayList<Wall> walls;
	boolean isServer;


	BufferedReader receiver;
	DataOutputStream sender;
	Chat ch;


	public Game(String title){
		bground = Color.black;

		frame = new JFrame(title);
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setVisible(true);

		frame.addKeyListener(new Listener());

		myBullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		enemies = new ArrayList<EnemyTank>();
		walls = new ArrayList<Wall>();

		for (int i=0; i<10; i++)
			walls.add(new Wall(150+30*i, 500, this));

		hi = new Hit();
		mo = new Motion();
		ch = new Chat();
	}


	public class Chat extends Thread{
		public void run(){
			try{
				while (true)
				{

					String s = receiver.readLine();
					//System.out.println(s);

					if (s.equals("$myTankMotion"))
					{
						String temp = receiver.readLine();
						fTank.dir = Integer.parseInt(temp);
						fTank.moving = true;
					}

					if (s.equals("$myTankStop"))
					{
						fTank.moving = false;
					}

					if (s.equals("$fire"))
					{
						String temp = receiver.readLine();
						String [] arr = temp.split(",");
						Bullet bul = new Bullet(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]),
							Integer.parseInt(arr[2]), Boolean.parseBoolean(arr[3]));
						if (arr[3].equals("true"))
							myBullets.add(bul);
						else
							enemyBullets.add(bul);
					}

					if (s.equals("$enemyMotion"))
					{
						String temp = receiver.readLine();
						String [] arr = temp.split(",");
						enemies.get(Integer.parseInt(arr[0])).dir = Integer.parseInt(arr[1]);
					}

					if (s.equals("$tankDead"))
					{
						fTank.alive = false;
					}

					/*if (s.equals("$myTankCollide"))
					{
						fTank.colliding = true;
					}

					if (s.equals("$myTankNotCollide"))
					{
						fTank.colliding = false;
					}*/


				}
			} catch(Exception e){System.err.println("[E]\t"+e);}
		}
	}


	public void paint(Graphics g){
		try{
			g.setColor(bground);
			g.fillRect(0, 0, width, height);

			g.setColor(Color.black);

			if (myTank.alive)
			{
				myTank.draw(g);
				//myTank.move();
			}

			for (int i=0; i<enemies.size(); i++)
			{
				EnemyTank e = enemies.get(i);
				e.draw(g);
				e.move();
			}

			for (int i=0; i<myBullets.size(); i++)
			{
				Bullet b = myBullets.get(i);
				b.draw(g);
				if (b.out())
					myBullets.remove(b);
			}

			for (int i=0; i<enemyBullets.size(); i++)
			{
				Bullet b = enemyBullets.get(i);
				b.draw(g);
				if (b.out())
					enemyBullets.remove(b);
			}

			for (int i=0; i<walls.size(); i++)
			{
				Wall w = walls.get(i);
				w.draw(g);
			}

			if (fTank.alive)
			{
				fTank.draw(g);
				fTank.move();
			}
		} catch(Exception e) {System.err.println("[E]\t"+e);}
	}

	public class Motion extends Thread{
		public void run(){
			while (true)
			{
				repaint();
				System.out.println(myTank.colliding);
				try{Thread.sleep(delay);}catch(Exception e){System.err.println("[E]\t"+e);}
			}
		}
	}

	public class Listener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			if (myTank.alive)
				myTank.pressed(e);
		}

		public void keyReleased(KeyEvent e){
			if (myTank.alive)
				myTank.released(e);
		}
	}

	public class Hit extends Thread{
		public void run(){
			while (true)
			{
				try{
					//{myTank, fTank, enemies, myBullets, enemyBullets, walls}
					//myBullets and enemyTanks
					for (int i=0; i<myBullets.size(); i++)
						for (int j=0; j<enemies.size(); j++)
							if (collide(myBullets.get(i), enemies.get(j)))
							{
								enemies.get(j).alive = false;
								enemies.remove(j);
								myBullets.remove(i);
							}

					//enemyBullets and myTank
					if (myTank.alive)
					for (int i=0; i<enemyBullets.size(); i++)
						if (collide(enemyBullets.get(i), myTank))
						{
							myTank.alive = false;
							enemyBullets.remove(i);
							synchronized(sender)
							{
								sender.writeBytes("$tankDead\n");
							}
						}

					//myBullets and walls
					for (int j=0; j<walls.size(); j++)
						for (int i=0; i<myBullets.size(); i++)
							if (collide(myBullets.get(i), walls.get(j)))
								myBullets.remove(i);

					//enemyBullets and walls
					for (int j=0; j<walls.size(); j++)
						for (int i=0; i<enemyBullets.size(); i++)
							if (collide(enemyBullets.get(i), walls.get(j)))
								enemyBullets.remove(i);

					//{myTank, fTank} and walls
					myTank.colliding = false;
					for (int i=0; i<walls.size(); i++)
					{
						if (collide(myTank, walls.get(i)))
							myTank.colliding = true;
					}
					fTank.colliding = false;
					for (int i=0; i<walls.size(); i++)
					{
						if (collide(fTank, walls.get(i)))
							fTank.colliding = true;
					}

					//enemyTank and walls
					for (int i=0; i<enemies.size(); i++)
					{
						EnemyTank eT = enemies.get(i);
						eT.colliding = false;
						for (int j=0; j<walls.size(); j++)
							if (collide(eT, walls.get(j)))
								eT.colliding = true;
					}

					//{myTank, fTank} and enemyTank
					if (myTank.alive)
						for (int i=0; i<enemies.size(); i++)
							if (collide(myTank, enemies.get(i)))
							{
								myTank.alive = false;
								enemies.get(i).alive = false;
								enemies.remove(i);
							}
					if (fTank.alive)
						for (int i=0; i<enemies.size(); i++)
							if (collide(fTank, enemies.get(i)))
							{
								fTank.alive = false;
								enemies.get(i).alive = false;
								enemies.remove(i);
							}

					//myTank and fTank
					if (myTank.alive && fTank.alive && collide(myTank, fTank))
					{
						myTank.colliding = true;

						
					}


					Thread.sleep(delay);
				} catch(Exception e){System.err.println("[E]\t"+e);}
			}
		}		
	}

	public boolean collide(Bullet b, Tank t){
		if (b.x+Tank.gun_size>=t.x && b.x-Tank.size<=t.x
			&&
			b.y+Tank.gun_size>=t.y && b.y-Tank.size<=t.y)
			return true;
		return false;
	}

	public boolean collide(Bullet b, Wall t){
		if (b.x+Tank.gun_size>=t.x && b.x-Tank.size<=t.x
			&&
			b.y+Tank.gun_size>=t.y && b.y-Tank.size<=t.y)
			return true;
		return false;
	}

	public boolean collide(Tank t, Wall w){
		int rest=5; // pixels between collision
		if (
		(t.dir==0 && Math.abs(t.y-w.y)<Tank.size && t.x<=w.x+Tank.size+rest && t.x>=w.x) ||
		(t.dir==1 && Math.abs(t.x-w.x)<Tank.size && t.y<=w.y+Tank.size+rest && t.y>=w.y) ||
		(t.dir==2 && Math.abs(t.y-w.y)<Tank.size && t.x<=w.x && t.x>=w.x-Tank.size-rest) ||
		(t.dir==3 && Math.abs(t.x-w.x)<Tank.size && t.y<=w.y && t.y>=w.y-Tank.size-rest) )
			return true;
		return false;
	}

	public boolean collide(Tank t, Tank e){
		if (Math.abs(t.x-e.x)<=Tank.size && Math.abs(t.y-e.y)<=Tank.size)
			return true;
		return false;
	}
}