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
			while (true)
			{
				fire();
				try{Thread.sleep(2000);}catch(Exception e){}
			}
		}
	}
}