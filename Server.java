package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;

public class Server extends Game{
	ServerSocket server;
	BufferedReader receiver;
	DataOutputStream sender;
	static final int serverPort = 2288;


	public Server() throws Exception{
		super();

		myTank = new Tank(100, 500, 1, this);

		server = new ServerSocket(serverPort);
		Socket client = server.accept();
		receiver = new BufferedReader(new InputStreamReader(client.getInputStream()));
		sender = new DataOutputStream(client.getOutputStream());

		int xx = Integer.parseInt(receiver.readLine()),
			yy = Integer.parseInt(receiver.readLine()),
			dd = Integer.parseInt(receiver.readLine());
		fTank = new Tank(xx, yy, dd, this);
		fTank.friend = true;

		sender.writeBytes(myTank.x+"\n");
		sender.writeBytes(myTank.y+"\n");
		sender.writeBytes(myTank.dir+"\n");

		hi.start();
		mo.start();
	}
	

	public static void main(String [] args) throws Exception{
		Server s = new Server();
	}
}