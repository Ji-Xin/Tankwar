package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;

public class Client extends Game{
	Socket me;


	public Client() throws Exception{
		super("Tank War Client");
		frame.setLocation(600, 100);

		myTank = new Tank(100, 300, 1, this);

		String sIP = "127.0.0.1";
		me = new Socket(sIP, Server.serverPort);
		sender = new DataOutputStream(me.getOutputStream());
		receiver = new BufferedReader(new InputStreamReader(me.getInputStream()));

		// 0.2 initial information of myTank: {x,y,dir}
		sender.writeBytes(myTank.x+"\n");
		sender.writeBytes(myTank.y+"\n");
		sender.writeBytes(myTank.dir+"\n");
		
		int xx = Integer.parseInt(receiver.readLine()),
			yy = Integer.parseInt(receiver.readLine()),
			dd = Integer.parseInt(receiver.readLine());
		fTank = new Tank(xx, yy, dd, this);
		fTank.friend = true;

		// 0.3 initial information of enemyTanks: {x,y,dir}
		int enemy_count = Integer.parseInt(receiver.readLine());
		for (int i=0; i<enemy_count; i++)
		{
			xx = Integer.parseInt(receiver.readLine());
			yy = Integer.parseInt(receiver.readLine());
			dd = Integer.parseInt(receiver.readLine());
			enemies.add(new EnemyTank(xx, yy, dd, this, false));
		}


		ch.start();
		hi.start();
		mo.start();
	}


	public static void main(String [] args) throws Exception{
		Client c = new Client();
	}




	/*public class Chat extends Thread{
		public void run(){
			while (true)
			{
				try{
					if (hitting==true)
						wait();


					//1.1 myTank new information: {dir,moving}
					fTank.dir = Integer.parseInt(receiver.readLine());
					fTank.moving = Boolean.parseBoolean(receiver.readLine());
					sender.writeBytes(myTank.dir+"\n");
					sender.writeBytes(myTank.moving+"\n");


					//1.2 myBullet generation (only generate, no need to communicate after generated)
					
					


					//2.1 new dir of enemyTanks (client do the enemyBullet generation itself)
					for (int i=0; i<enemies.size(); i++)
						enemies.get(i).dir = Integer.parseInt(receiver.readLine());

					hitting = true;
					notify();
					Thread.sleep(delay);
				} catch(Exception e){}
			}
		}
	}*/
}