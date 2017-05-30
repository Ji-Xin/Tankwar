package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class EnemyTank extends Tank{
	boolean auto;
	boolean strong;
	int health;
	public Auto auto_thread;
	Game parent;

	public EnemyTank(int xx, int yy, int d, Game p, boolean a, boolean s){
		super(xx, yy, d, p);
		mine = false;
		auto = a; //true for server, false for client
		parent = p;
		auto_thread = new Auto(this);
		strong = s;
		if (s)
			health = 2;
		else
			health = 1;
	}

	public void draw(Graphics g){
		g.setColor(Color.red);
		g.fillRect(x, y, size, size);

		if (health==2)
		{
			g.setColor(Color.cyan);
			g.fillRect(x, y, size, size/5);
			g.fillRect(x, y+4*size/5, size, size/5);
			g.fillRect(x, y, size/5, size);
			g.fillRect(x+4*size/5, y, size/5, size);
		}

		g.setColor(Color.orange);
		switch(dir)
		{
			case 0:				
				g.fillRect(x-size/2, y-gun_size/2+size/2, size, gun_size);
				break;
			case 1:
				g.fillRect(x+size/2-gun_size/2, y-size/2, gun_size, size);
				break;
			case 2:
				g.fillRect(x+size/2, y-gun_size/2+size/2, size, gun_size);
				break;
			case 3:
				g.fillRect(x+size/2-gun_size/2, y+size/2, gun_size, size);
				break;
		}
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
					if (auto && !parent.paused)
					{
						dir = rand.nextInt(4);
						synchronized(parentEnemyTank.parent.sender)
						{
							parent.sender.writeBytes("$enemyMotion\n");
							parent.sender.writeBytes(
								parent.enemies.indexOf(parentEnemyTank)+","+dir+"\n");
						}
					}


					Thread.sleep(500);
					if (auto && !parent.paused)
						fire();
					Thread.sleep(500);
				}
			} catch(Exception e){System.err.println(e);}
		}
	}
}