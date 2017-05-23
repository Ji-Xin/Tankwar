package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;

/*
What to communicate?
0 beginning
@	0.1 walls (this part already written in Game.java)
@	0.2 initial information of myTank: {x,y,dir}
@	0.3 initial information of enemyTanks: {x,y,dir}

1 mutual
@	1.1 myTank new information: {dir,moving}
	1.2 myBullet generation (only generate, no need to communicate after generated)

2 server to client only
@	2.1 new dir of enemyTanks (client do the enemyBullet generation itself)
*/

public class Server extends Game{
	ServerSocket server;
	static final int serverPort = 2288;


	public Server() throws Exception{
		super("Tank War Server");
		frame.setLocation(10, 100);

		myTank = new Tank(100, 500, 1, this);

		for (int i=0; i<1; i++)
			enemies.add(new EnemyTank(500, 50+60*i, 0, this, true));

		server = new ServerSocket(serverPort);
		Socket client = server.accept();
		receiver = new BufferedReader(new InputStreamReader(client.getInputStream()));
		sender = new DataOutputStream(client.getOutputStream());



		// 0.2 initial information of myTank: {x,y,dir,moving}
		int xx = Integer.parseInt(receiver.readLine()),
			yy = Integer.parseInt(receiver.readLine()),
			dd = Integer.parseInt(receiver.readLine());
		fTank = new Tank(xx, yy, dd, this);
		fTank.friend = true;

		sender.writeBytes(myTank.x+"\n");
		sender.writeBytes(myTank.y+"\n");
		sender.writeBytes(myTank.dir+"\n");

		// 0.3 initial information of enemyTanks: {x,y,dir}
		sender.writeBytes(enemies.size()+"\n");
		for (int i=0; i<enemies.size(); i++)
		{
			EnemyTank temp = enemies.get(i);
			sender.writeBytes(temp.x+"\n");
			sender.writeBytes(temp.y+"\n");
			sender.writeBytes(temp.dir+"\n");
			temp.auto_thread.start();
		}

		ch.start();
		hi.start();
		mo.start();
	}
	

	public static void main(String [] args) throws Exception{
		Server s = new Server();
	}


	/*public class Chat extends Thread{
		public void run(){
			while (true)
			{
				try{
					if (hitting==true)
						wait();


					//1.1 myTank new information: {dir,moving}
					sender.writeBytes(myTank.dir+"\n");
					sender.writeBytes(myTank.moving+"\n");
					fTank.dir = Integer.parseInt(receiver.readLine());
					fTank.moving = Boolean.parseBoolean(receiver.readLine());


					//1.2 myBullet generation (only generate, no need to communicate after generated)
					


					//2.1 new dir of enemyTanks (client do the enemyBullet generation itself)
					
					for (int i=0; i<enemies.size(); i++)
						sender.writeBytes(enemies.get(i).dir+"\n");


					hitting = true;
					notify();
					Thread.sleep(delay);
				} catch(Exception e){}
			}
		}
	}*/
}