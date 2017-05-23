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
		auto = a;
		parent = p;
		auto_thread = new Auto();
	}

	public class Auto extends Thread{
		public void run(){
			try{

				Random rand = new Random();
				while (alive)
				{
					if (auto)
						dir = rand.nextInt(4);
					moving = true;

					Thread.sleep(500);
					fire();
					Thread.sleep(500);
				}
			} catch(Exception e){}
		}
	}
}