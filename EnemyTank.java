package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class EnemyTank extends Tank{
	boolean auto;
	public Auto auto_thread;
	Game parent;

	public EnemyTank(int xx, int yy, int d, Game p, boolean a){
		super(xx, yy, d, p);
		mine = false;
		auto = a; //true for server, false for client
		parent = p;
		auto_thread = new Auto(this);
	}

	public class Auto extends Thread{
		EnemyTank parentEnemyTank;

		public Auto(EnemyTank p){
			parentEnemyTank = p;
		}

		public void run(){
			try{

				Random rand = new Random();
				while (alive)
				{
					if (auto)
					{
						dir = rand.nextInt(4);
						synchronized(parentEnemyTank.parent.sender)
						{
							parent.sender.writeBytes("$enemyMotion\n");
							parent.sender.writeBytes(
								parent.enemies.indexOf(parentEnemyTank)+","+dir+"\n");
						}
					}
					moving = true;


					Thread.sleep(500);
					if (auto)
						fire();
					Thread.sleep(500);
				}
			} catch(Exception e){System.err.println(e);}
		}
	}
}