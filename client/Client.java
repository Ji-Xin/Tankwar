package game.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.net.*;

// paint
// thread of repaint
// walls receive at beginning
// tanks and bullets receive instantly(only receive position, motion, objects remain on server)
// send key events


public class Client extends JPanel{
	JFrame frame;
	Color background;
	Motion mo;

	Tank myTank;
	ArrayList<Tank> friends;
	ArrayList<EnemyTank> enemies;
	ArrayList<Bullet> myBullets;
	ArrayList<Bullet> enemyBullets;
	ArrayList<Wall> walls;

	public static void main(String [] args){
		System.out.println(1);
	}

	/*public Client(){
		bground = Color.black;
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

		for (int i=0; i<10; i++)
			walls.add(new Wall(150+30*i, 500, this));

		enemies.add(new EnemyTank(500, 200, 0, this));
		enemies.add(new EnemyTank(500, 300, 0, this));
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
	}*/
}