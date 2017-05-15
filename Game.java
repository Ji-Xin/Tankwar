package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Game extends JPanel{
	JFrame frame;
	public static final int width=800, height=600;
	Color bground;
	Tank myTank;
	Motion mo;
	ArrayList<Bullet> bullets;
	ArrayList<EnemyTank> enemies;


	public Game(){
		bground = Color.white;
		myTank = new Tank(100, 100, 1, this);

		frame = new JFrame("Tank War!");
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(500, 50);
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setVisible(true);

		frame.addKeyListener(new Listener());

		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<EnemyTank>();

		enemies.add(new EnemyTank(500, 200, 0, this));
		enemies.add(new EnemyTank(500, 400, 0, this));

		mo = new Motion();
		mo.run();

	}

	public void paint(Graphics g){
		g.setColor(bground);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.black);

		myTank.draw(g);
		myTank.move();

		for (int i=0; i<enemies.size(); i++)
		{
			EnemyTank e = enemies.get(i);
			e.draw(g);
		}

		for (int i=0; i<bullets.size(); i++)
		{
			Bullet b = bullets.get(i);
			b.draw(g);
		}
	}

	public static void main(String [] args) throws Exception{
		Game game = new Game();
		while (true)
		{
			game.repaint();
			Thread.sleep(50);
		}
	}

	private class Motion extends Thread{
		public void run(){
			while (true)
			{
				repaint();
				try{Thread.sleep(50);}catch(Exception e){}
			}
		}
	}

	private class Listener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			myTank.pressed(e);
		}

		public void keyReleased(KeyEvent e){
			myTank.released(e);
		}
	}
}