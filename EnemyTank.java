package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class EnemyTank extends Tank{

	public EnemyTank(int xx, int yy, int d, Game p){
		super(xx, yy, d, p);
		mine = false;
		(new Auto()).start();
	}

	private class Auto extends Thread{
		public void run(){
			Random rand = new Random();
			while (alive)
			{
				dir = rand.nextInt(4);
				if (x<0)
					dir = 2;
				if (x>Game.width)
					dir = 0;
				if (y<0)
					dir = 3;
				if (y>Game.height)
					dir = 1;
				moving = true;

				fire();
				try{Thread.sleep(1000);}catch(Exception e){}
				fire();
				try{Thread.sleep(1000);}catch(Exception e){}

			}
		}
	}
}