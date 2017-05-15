package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Game extends JPanel{
	JFrame frame;
	public static final int width=800, height=600;
	Color bground;
	Tank myTank;
	Motion mo;
	Hit hi;
	ArrayList<Bullet> myBullets;
	ArrayList<Bullet> enemyBullets;
	ArrayList<EnemyTank> enemies;
	ArrayList<Wall> walls;


	public Game(){
		bground = Color.white;
		myTank = new Tank(100, 500, 1, this);

		frame = new JFrame("Tank War!");
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(500, 50);
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setVisible(true);

		frame.addKeyListener(new Listener());

		myBullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		enemies = new ArrayList<EnemyTank>();
		walls = new ArrayList<Wall>();

		for (int i=0; i<5; i++)
			walls.add(new Wall(500+30*i, 500, this));

		enemies.add(new EnemyTank(500, 200, 0, this));
		enemies.add(new EnemyTank(500, 400, 0, this));
		enemies.add(new EnemyTank(500, 500, 0, this));


		hi = new Hit();
		hi.start();

		mo = new Motion();
		mo.start();
	}

	public void paint(Graphics g){
		g.setColor(bground);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.black);

		if (myTank.alive)
		{
			myTank.draw(g);
			myTank.move();
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
	}

	public static void main(String [] args) throws Exception{
		Game game = new Game();
	}

	private class Motion extends Thread{
		public void run(){
			while (true)
			{
				repaint();
				try{Thread.sleep(50);}catch(Exception e){}
			}
		}
	}

	private class Listener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			if (myTank.alive)
				myTank.pressed(e);
		}

		public void keyReleased(KeyEvent e){
			if (myTank.alive)
				myTank.released(e);
		}
	}

	private class Hit extends Thread{
		public void run(){
			while (true)
			{
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

				try{Thread.sleep(50);}catch(Exception e){}
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
}