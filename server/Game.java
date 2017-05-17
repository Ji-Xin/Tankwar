package game.server;

import java.util.*;
import java.lang.*;
import java.net.*;

// mainly focus on solving hits and bullets out of panel

public class Game{
	public static final int width=800, height=600;
	Hit hi;
	Motion mo;

	ArrayList<Tank> cTanks;
	ArrayList<EnemyTank> enemies;
	ArrayList<Bullet> myBullets;
	ArrayList<Bullet> enemyBullets;
	ArrayList<Wall> walls;

	ServerSocket server;
	ArrayList<Socket> clients;
	ArrayList<BufferedReader> receivers;
	ArrayList<DataOutputStream> senders;


	public Game(){
		cTanks = new ArrayList<Tank>();
		enemies = new ArrayList<EnemyTank>();
		myBullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		walls = new ArrayList<Wall>();

		for (int i=0; i<10; i++)
			walls.add(new Wall(150+30*i, 500, this));

		for (int i=0; i<8; i++)
			enemies.add(new EnemyTank(500, i*60+50, 0, this));

		init();

		hi = new Hit();
		hi.start();

		mo = new Motion();
		mo.start();
	}

	public void init(){
		server = new ServerSocket(2288);
		while (true)
		{
			try{
				Socket temp = socket.accept();
				BufferedReader br = new BufferedReader(
					new InputStreamReader(temp.getInputStream()));
				DataOutputStream dos = new DataOutputStream(temp.getOutputStream());
				clients.add(temp);
				receivers.add(br);
				senders.add(dos);
			} catch (Exception ex){}
		}
	}

	/*public static void main(String [] args) throws Exception{
		Game game = new Game();
	}*/

	private class Motion extends Thread{
		public void run(){
			while (true)
			{
				/*every one move!*/
				try{Thread.sleep(50);}catch(Exception e){}
			}
		}
	}

	private class Listener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			if (myTank.alive)
				myTank.pressed(e);
		}

		public void keyReleased(KeyEvent e){
			if (myTank.alive)
				myTank.released(e);
		}
	}

	private class Hit extends Thread{
		public void run(){
			while (true)
			{
				try{
					//myBullets and enemyTanks
					for (int i=0; i<myBullets.size(); i++)
						for (int j=0; j<enemies.size(); j++)
							if (collide(myBullets.get(i), enemies.get(j)))
							{
								enemies.get(j).alive = false;
								enemies.remove(j);
								myBullets.remove(i);
							}

					//enemyBullets and myTank
					if (myTank.alive)
					for (int i=0; i<enemyBullets.size(); i++)
						if (collide(enemyBullets.get(i), myTank))
						{
							myTank.alive = false;
							enemyBullets.remove(i);
						}

					//myBullets and walls
					for (int j=0; j<walls.size(); j++)
						for (int i=0; i<myBullets.size(); i++)
							if (collide(myBullets.get(i), walls.get(j)))
								myBullets.remove(i);

					//enemyBullets and walls
					for (int j=0; j<walls.size(); j++)
						for (int i=0; i<enemyBullets.size(); i++)
							if (collide(enemyBullets.get(i), walls.get(j)))
								enemyBullets.remove(i);

					//myTank and walls
					myTank.colliding = false;
					for (int i=0; i<walls.size(); i++)
						if (collide(myTank, walls.get(i)))
							myTank.colliding = true;

					//enemyTank and walls
					for (int i=0; i<enemies.size(); i++)
					{
						EnemyTank eT = enemies.get(i);
						eT.colliding = false;
						for (int j=0; j<walls.size(); j++)
							if (collide(eT, walls.get(j)))
								eT.colliding = true;
					}

					//myTank and enemyTank
					if (myTank.alive)
						for (int i=0; i<enemies.size(); i++)
							if (collide(myTank, enemies.get(i)))
							{
								myTank.alive = false;
								enemies.get(i).alive = false;
								enemies.remove(i);
							}

					Thread.sleep(50);
				}catch(Exception e){}
			}
		}		
	}

	public boolean collide(Bullet b, Tank t){
		if (b.x+Tank.gun_size>=t.x && b.x-Tank.size<=t.x
			&&
			b.y+Tank.gun_size>=t.y && b.y-Tank.size<=t.y)
			return true;
		return false;
	}

	public boolean collide(Bullet b, Wall t){
		if (b.x+Tank.gun_size>=t.x && b.x-Tank.size<=t.x
			&&
			b.y+Tank.gun_size>=t.y && b.y-Tank.size<=t.y)
			return true;
		return false;
	}

	public boolean collide(Tank t, Wall w){
		int rest=5; // pixels between collision
		if (
		(t.dir==0 && Math.abs(t.y-w.y)<Tank.size && t.x<=w.x+Tank.size+rest && t.x>=w.x) ||
		(t.dir==1 && Math.abs(t.x-w.x)<Tank.size && t.y<=w.y+Tank.size+rest && t.y>=w.y) ||
		(t.dir==2 && Math.abs(t.y-w.y)<Tank.size && t.x<=w.x && t.x>=w.x-Tank.size-rest) ||
		(t.dir==3 && Math.abs(t.x-w.x)<Tank.size && t.y<=w.y && t.y>=w.y-Tank.size-rest) )
			return true;
		return false;
	}

	public boolean collide(Tank t, Tank e){
		if (Math.abs(t.x-e.x)<Tank.size && Math.abs(t.y-e.y)<Tank.size)
			return true;
		return false;
	}
}