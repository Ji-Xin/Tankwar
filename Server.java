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
@	1.2 myBullet generation (only generate, no need to communicate after generated)

2 server to client only
@	2.1 new dir of enemyTanks (client do the enemyBullet generation itself)
*/

public class Server extends Game{
	ServerSocket server;
	static final int serverPort = 2288;
	Sync sy;

	TreeMap<String, Integer> users;

	public Server() throws Exception{
		super("Tank War Server");
		isServer = true;
		frame.setLocation(30, 50);

		users = new TreeMap<String, Integer>();
		Scanner scan = new Scanner(new File("game/users.txt"));
		while (scan.hasNext())
		{
			String user = scan.next();
			int score = scan.nextInt();
			users.put(user, score);
		}

		myTank = new Tank(100, 500, 1, this);

		for (int i=0; i<4; i++)
			enemies.add(new EnemyTank(500, 50+60*i, i%4, this, true, true));
		for (int i=4; i<8; i++)
			enemies.add(new EnemyTank(500, 50+60*i, i%4, this, true, false));



		System.out.println("Server starts.");


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
			sender.writeBytes(temp.strong+"\n");
		}
		for (int i=0; i<enemies.size(); i++)
		{
			enemies.get(i).auto_thread.start();
		}

		ch.start();
		hi.start();
		mo.start();
		sy = new Sync();
		sy.start();

		frame.setVisible(true);
	}
	

	public static void main(String [] args) throws Exception{
		Server s = new Server();
	}


	public class Sync extends Thread{
		public void run(){
			while (true)
				try{
					if (!paused)
						synchronized(sender)
						{
							sender.writeBytes("$sync\n");

							// myTank
							sender.writeBytes(myTank.x+","+myTank.y+","+myTank.dir+"\n");

							// enemies
							sender.writeBytes(enemies.size()+"\n");
							for (int i=0; i<enemies.size(); i++)
							{
								EnemyTank temp = enemies.get(i);
								sender.writeBytes(temp.x+","+temp.y+","+temp.dir+","+
									temp.health+","+temp.strong+"\n");
							}
						}
					Thread.sleep(2000);
				} catch(Exception ex){ex.printStackTrace();System.exit(0);}
		}
	}

	private void modify(String u, int s){
		int temp = 0;
		if (users.containsKey(u))
		{
			temp = users.get(u);
			users.remove(u);
		}
		users.put(u, s+temp);
	}

	public void record(){
		modify(myName, myPoint);
		modify(fName, fPoint);

		NavigableSet<Map.Entry<String, Integer>> set = 
			new TreeSet<Map.Entry<String, Integer>>(
			new Comparator<Map.Entry<String, Integer>>(){
				public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2){
					return -(e1.getValue().compareTo(e2.getValue()));
				}
			}
		);

		set.addAll(users.entrySet());

		try{
			File fout = new File("game/users.txt");
			PrintStream out = new PrintStream(fout);
			Iterator<Map.Entry<String, Integer>> iter = set.iterator();
			int count = 0;
			while (iter.hasNext())
			{
				Map.Entry<String, Integer> temp = iter.next();
				out.println(temp.getKey());
				out.println(temp.getValue());
				if (count<5)
				{
					history += "No."+(count+1)+"  "+temp.getKey()+"  "+temp.getValue()+"\n";
					count++;
				}
			}
			synchronized(sender)
			{
				sender.writeBytes("$history\n");
				sender.writeBytes(history);
			}
		} catch(Exception ex){ex.printStackTrace();System.exit(0);}
	}
}