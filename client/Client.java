package game.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import game.server.*;

// paint
// thread of repaint
// walls receive at beginning
// tanks and bullets receive instantly(only receive position, motion, objects remain on server)
// send key events


public class Client extends JPanel{
	JFrame frame;
	Motion mo;

	// the followings are only used for drawing.
	// their locations every moment are downloaded from the server
	Tank myTank;
	ArrayList<Tank> friends;
	ArrayList<EnemyTank> enemies;
	ArrayList<Bullet> myBullets;
	ArrayList<Bullet> enemyBullets;
	ArrayList<Wall> walls;

	// the followings are used for network connection
	Socket me;
	BufferedReader receiver;
	DataOutpurStream sender;


	public static void main(String [] args){
		Client c = new Client();
	}

	public Client(){
		frame = new JFrame("Tank War!");
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(500, 50);
		frame.setSize(Game.width, Game.height);
		frame.setResizable(false);
		frame.setVisible(true);
		//frame.addKeyListener(new Listener());

		friends = new ArrayList<Tank>();
		enemies = new ArrayList<EnemyTank>();
		myBullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		walls = new ArrayList<Wall>();

		String sIP = "127.0.0.1";
		me = new Socket(sIP, 2288);
		sender = new DataOutputStream(me.getOutputStream());
		receiver = new BufferedReader(new InputStreamReader(me.getInputStream()));


		mo = new Motion();
		mo.start();
	}

	private class Motion extends Thread{
		public void run(){
			while (true)
			{
				//receive motion
				repaint();
				try{Thread.sleep(50);}catch(Exception e){}
			}
		}
	}

	public void paint(Graphics g){
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.width, Game.height);

		if (myTank.alive)
			myTank.draw(g);

		for (int i=0; i<friends.size(); i++)
			friends.get(i).draw(g);

		for (int i=0; i<enemies.size(); i++)
			enemies.get(i).draw(g);

		for (int i=0; i<myBullets.size(); i++)
			myBullets.get(i).draw(g);

		for (int i=0; i<enemyBullets.size(); i++)
			enemyBullets.get(i).draw(g);

		for (int i=0; i<walls.size(); i++)
			walls.get(i).draw(g);
	}

	private class chat extends Thread{

	}
}