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
		isServer = false;

		frame.setSize(width+extra_width, height);
		frame.setLocation(1200, 50);

		myTank = new Tank(100, 400, 1, this);



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
			EnemyTank temp = new EnemyTank(xx, yy, dd, this, false);
			enemies.add(temp);
		}
		for (int i=0; i<enemy_count; i++)
			enemies.get(i).auto_thread.start();


		ch.start();
		hi.start();
		mo.start();

		frame.setVisible(true);
	}


	public static void main(String [] args) throws Exception{
		Client c = new Client();
	}



}