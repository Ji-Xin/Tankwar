package game.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import game.server.*;
import java.io.*;

// paint
// thread of repaint
// walls receive at beginning
// tanks and bullets receive instantly(only receive position, motion, objects remain on server)
// send key events


public class Client extends JPanel{
	JFrame frame;
	Motion mo;
	Chat ch;

	// the followings are only used for drawing.
	// their locations every moment are downloaded from the server
	WalkingTank myTank;
	ArrayList<WalkingTank> friends;
	ArrayList<WalkingTank> enemies;
	ArrayList<Bullet> myBullets;
	ArrayList<Bullet> enemyBullets;
	ArrayList<Wall> walls;

	// the followings are used for network connection
	Socket me;
	BufferedReader receiver;
	DataOutputStream sender;


	public static void main(String [] args) throws Exception{
		Client c = new Client();
	}

	public Client() throws Exception{
		frame = new JFrame("Tank War!");
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(0, 0);//500, 50
		frame.setSize(Game.width, Game.height);
		frame.setResizable(false);
		frame.setVisible(true);
		//frame.addKeyListener(new Listener());

		myTank = new WalkingTank(100, 100, 2, true);
		friends = new ArrayList<WalkingTank>();
		enemies = new ArrayList<WalkingTank>();
		myBullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		walls = new ArrayList<Wall>();

		String sIP = "127.0.0.1";
		me = new Socket(sIP, 2288);
		sender = new DataOutputStream(me.getOutputStream());
		receiver = new BufferedReader(new InputStreamReader(me.getInputStream()));


		mo = new Motion();
		mo.start();

		ch = new Chat();
		ch.start();
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

		myTank.draw(g);

		/*for (int i=0; i<friends.size(); i++)
			friends.get(i).draw(g);*/

		for (int i=0; i<enemies.size(); i++)
			enemies.get(i).draw(g);

		/*for (int i=0; i<myBullets.size(); i++)
			myBullets.get(i).draw(g);*/

		for (int i=0; i<enemyBullets.size(); i++)
			enemyBullets.get(i).draw(g);

		for (int i=0; i<walls.size(); i++)
			walls.get(i).draw(g);
	}

	private class Chat extends Thread{
		public void run(){
			try{
				//init

				//walls
				int wall_count = Integer.parseInt(receiver.readLine());
				for (int i=0; i<wall_count; i++)
				{
					int xx, yy;
					xx = Integer.parseInt(receiver.readLine());
					yy = Integer.parseInt(receiver.readLine());
					walls.add(new Wall(xx, yy));
				}

				//myTank
				sender.writeBytes(String.valueOf(myTank.x)+"\n");
				sender.writeBytes(String.valueOf(myTank.y)+"\n");
				sender.writeBytes(String.valueOf(myTank.dir)+"\n");

				//enemyTanks
				int enemy_count = Integer.parseInt(receiver.readLine());
				for (int i=0; i<enemy_count; i++)
				{
					int xx, yy, dd;
					xx = Integer.parseInt(receiver.readLine());
					yy = Integer.parseInt(receiver.readLine());
					dd = Integer.parseInt(receiver.readLine());
					enemies.add(new WalkingTank(xx, yy, dd, false));
				}

				//regular communication
				while (true)
				{
					int xx, yy, dd;

					//myTank
					xx = Integer.parseInt(receiver.readLine());
					yy = Integer.parseInt(receiver.readLine());
					dd = Integer.parseInt(receiver.readLine());
					myTank.set(xx, yy, dd);

					//enemyTanks
					for (int i=0; i<enemies.size(); i++)
					{
						xx = Integer.parseInt(receiver.readLine());
						yy = Integer.parseInt(receiver.readLine());
						dd = Integer.parseInt(receiver.readLine());
						enemies.get(i).set(xx, yy, dd);
					}

					//enemyBullets
					enemyBullets.clear();
					int eb_count = Integer.parseInt(receiver.readLine());
					for (int i=0; i<eb_count; i++)
					{
						xx = Integer.parseInt(receiver.readLine());
						yy = Integer.parseInt(receiver.readLine());
						enemyBullets.add(new Bullet(xx, yy, 0, false));
					}

					Thread.sleep(50);
				}

			} catch(Exception ex){}
		}
	}

	/*private class Listener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			if (myTank.alive)
				myTank.pressed(e);
		}

		public void keyReleased(KeyEvent e){
			if (myTank.alive)
				myTank.released(e);
		}
	}*/
}