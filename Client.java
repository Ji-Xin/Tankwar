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
	DataOutputStream sender;
	BufferedReader receiver;


	public Client() throws Exception{
		myTank = new Tank(100, 300, 1, this);

		String sIP = "127.0.0.1";
		me = new Socket(sIP, Server.serverPort);
		sender = new DataOutputStream(me.getOutputStream());
		receiver = new BufferedReader(new InputStreamReader(me.getInputStream()));

		sender.writeBytes(myTank.x+"\n");
		sender.writeBytes(myTank.y+"\n");
		sender.writeBytes(myTank.dir+"\n");
		
		int xx = Integer.parseInt(receiver.readLine()),
			yy = Integer.parseInt(receiver.readLine()),
			dd = Integer.parseInt(receiver.readLine());
		fTank = new Tank(xx, yy, dd, this);
		fTank.friend = true;

		hi.start();
		mo.start();
	}


	public static void main(String [] args) throws Exception{
		Client c = new Client();
	}
}