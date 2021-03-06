package code;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import javax.imageio.*;
import javax.sound.sampled.*;

public abstract class Game extends JPanel{
	JFrame frame;
	public static final int width=800, height=600, extra_width=200;
	public static final int delay=50;
	Color bground;
	Tank myTank, fTank; //friendTank
	Motion mo;
	Hit hi;
	ArrayList<Bullet> myBullets; //including friend bullets
	ArrayList<Bullet> enemyBullets;
	public ArrayList<EnemyTank> enemies;
	ArrayList<Wall> walls;
	boolean isServer;
	int myPoint, fPoint;
	boolean paused;
	JMenuBar bar;
	JMenu menu;
	JMenuItem item1, item2;
	String history;
	String myName, fName;
	static Image wall_image;
	static File explode_sound;
	Clip clip;
	String directory;


	BufferedReader receiver;
	DataOutputStream sender;
	Chat ch;

	public static void dealWithException(Exception ex){
		ex.printStackTrace();
	}

	public Game(String title) throws Exception{

		directory = new File(new File(System.getProperty("java.class.path")
			).getAbsolutePath()).getParent();


		wall_image = ImageIO.read(new File(directory+"/code/source/Wall.jpg"));

		explode_sound = new File(directory+"/code/source/Explode.wav");



		bground = Color.black;
		frame = new JFrame(title);
		frame.setSize(width+extra_width, height+23);
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);


		frame.addKeyListener(new Listener());

		bar = new JMenuBar();
		menu = new JMenu("Game");
		item1 = new JMenuItem("Start/Pause");
		item1.addActionListener(new SPListener());
		bar.add(menu);
		menu.add(item1);
		frame.setJMenuBar(bar);

		myBullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		enemies = new ArrayList<EnemyTank>();
		walls = new ArrayList<Wall>();
		paused = true;
		myPoint = 0;
		fPoint = 0;
		history = "";

		draw_map();

		hi = new Hit();
		mo = new Motion();
		ch = new Chat(this);


		frame.setVisible(true);

		//URL bgm = getClass().getResource("/code/source/Background.wav");
		File bgm = new File(directory+"/code/source/Background.wav");
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(bgm);
		DataLine.Info info = new DataLine.Info(Clip.class, audioIn.getFormat());
		clip = (Clip)AudioSystem.getLine(info);
		clip.open(audioIn);


		JFrame df = new JFrame();
		JOptionPane op = new JOptionPane();

		String instruction = 
"1. Press direction keys to move, space key to fire.\n" +
"2. Your tank and normal enemy tank will be destroyed at one shot, but special enemy\n" +
"Tank will be destroyed after two shots. Kill a normal enemy to earn one point, special\n" +
"special one to earn three.\n" +
"3. If your tank and your friend's tank are all dead, or the base is destroyed, your team loses;\n"+
"4. If all enemy tanks are dead, your team wins;";

		myName = op.showInputDialog(df,
			"Please input your username\n\nInstructions:\n"+instruction,
			"Game start",
			JOptionPane.QUESTION_MESSAGE);
		if (myName==null)
			System.exit(0);
		else
		{
			paused = false;
		}
	}

	private void add_edge(int x, int interval){
		for (int i=0; i<4; i++)
		{
			walls.add(new Wall(x, height/2-interval/2-Tank.size-Tank.size*i, this, false));
			walls.add(new Wall(x, height/2+interval/2+Tank.size*i, this, false));				
		}
	}

	private void draw_map(){		
		walls.add(new Wall(width/2-Tank.size/2, 500, this, true));//base

		int interval = (width-6*Tank.size)/7;
		for (int i=0; i<6; i++)
			add_edge(i*(interval+Tank.size)+interval, (int)(50+Math.abs(i-2.5)*70));
	}

	public class SPListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			start_pause(true);
		}
	}

	public void start_pause(boolean send){
		try{
			paused = !paused;
			if (send)
				synchronized(sender)
				{
					sender.writeBytes("$start_pause\n");
				}
		} catch(Exception ex){dealWithException(ex);}
	}


	public class Chat extends Thread{
		Game parent;

		public Chat(Game p){
			parent = p;
		}

		public void run(){
			try{
				while (true)
				{

					String s = receiver.readLine();

					if (s.equals("$myTankMotion"))
					{
						String temp = receiver.readLine();
						fTank.dir = Integer.parseInt(temp);
						fTank.move();
					}

					if (s.equals("$fire"))
					{
						String temp = receiver.readLine();
						String [] arr = temp.split(",");
						Bullet bul = new Bullet(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]),
							Integer.parseInt(arr[2]), Boolean.parseBoolean(arr[3]), false);
						if (arr[3].equals("true"))
							myBullets.add(bul);
						else
							enemyBullets.add(bul);
					}

					if (s.equals("$enemyMotion"))
					{
						String temp = receiver.readLine();
						String [] arr = temp.split(",");
						enemies.get(Integer.parseInt(arr[0])).dir = Integer.parseInt(arr[1]);
					}

					if (s.equals("$tankDead"))
					{
						fTank.alive = false;
					}

					if (s.equals("$sync")) //only received by client
					{
						//fTank
						String temp = receiver.readLine();
						String [] arr = temp.split(",");
						fTank.x = Integer.parseInt(arr[0]);
						fTank.y = Integer.parseInt(arr[1]);
						fTank.dir = Integer.parseInt(arr[2]);

						//enemies
						int e_count = Integer.parseInt(receiver.readLine());
						if (e_count==enemies.size())
						{
							for (int i=0; i<e_count; i++)
							{
								EnemyTank etemp = enemies.get(i);
								String st = receiver.readLine();
								String [] arrt = st.split(",");
								etemp.x = Integer.parseInt(arrt[0]);
								etemp.y = Integer.parseInt(arrt[1]);
								etemp.dir = Integer.parseInt(arrt[2]);
								etemp.health = Integer.parseInt(arrt[3]);
							}
						}
						else
						{
							enemies.clear();
							for (int i=0; i<e_count; i++)
							{
								String st = receiver.readLine();
								String [] arrt = st.split(",");
								EnemyTank etemp = new EnemyTank(Integer.parseInt(arrt[0]),
									Integer.parseInt(arrt[1]), Integer.parseInt(arrt[2]),
									parent, false, Boolean.parseBoolean(arrt[4]));
								etemp.health = Integer.parseInt(arrt[3]);
								enemies.add(etemp);
							}
						for (int i=0; i<e_count; i++)
							enemies.get(i).auto_thread.start();

						}
					}

					if (s.equals("$start_pause"))
					{
						start_pause(false);
					}

					if (s.equals("$history"))
					{
						synchronized(history)
						{
							for (int i=0; i<5; i++)
								history += receiver.readLine()+"\n";
						}
					}

					if (s.equals("$name"))
					{
						fName = receiver.readLine();
					}


				}
			} catch(Exception ex){dealWithException(ex);}
		}
	}

	public void info(Graphics g){
		g.setColor(Color.white);
		g.fillRect(width, 0, extra_width, height);
		g.setColor(Color.black);
		g.setFont(new Font("Sans", Font.BOLD, 16));
		g.drawString("My points: "+myPoint, width+extra_width/4, 50);
		g.drawString("Friend points: "+fPoint, width+extra_width/6, 70);
		g.drawString("Base HP: "+walls.get(0).life, width+extra_width/4, 90);

	}


	public void paintComponent(Graphics g){
		try{
			g.setColor(bground);
			g.fillRect(0, 0, width, height);

			for (int i=0; i<myBullets.size(); i++)
			{
				Bullet b = myBullets.get(i);
				b.draw(g);
				if (b.out())
					myBullets.remove(b);
			}

			for (int i=0; i<enemyBullets.size(); i++)
			{
				Bullet b = enemyBullets.get(i);
				b.draw(g);
				if (b.out())
					enemyBullets.remove(b);
			}


			for (int i=0; i<walls.size(); i++)
			{
				Wall w = walls.get(i);
				if ( (i==0 && w.life>0) || (i>0) )
					w.draw(g);
			}

			for (int i=0; i<enemies.size(); i++)
			{
				EnemyTank e = enemies.get(i);
				e.draw(g);
				e.move();
			}

			if (fTank.alive)
			{
				fTank.draw(g);
			}

			if (myTank.alive)
			{
				myTank.draw(g);
			}


			info(g);

		} catch(Exception ex) {}
	}

	public class Motion extends Thread{
		public void run(){
			while (true)
			{
				if (!paused)
					repaint();
				try{Thread.sleep(delay);}
						catch(Exception ex){dealWithException(ex);}
			}
		}
	}

	public class Listener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			if (myTank.alive)
				myTank.pressed(e);
		}

		public void keyReleased(KeyEvent e){
			if (myTank.alive)
				myTank.released(e);
		}
	}

	public class Hit extends Thread{
		public void run(){
			while (true)
				try{
				if (!paused)
				{
					// win or not
					int flag = 0;
					if (walls.get(0).life==0 || (!myTank.alive && !fTank.alive))
						flag = -1;
					else if (enemies.size()==0)
						flag = 1;
					if (flag!=0)
					{
						clip.stop();
						while (!isServer && history.length()==0)
							Thread.sleep(delay);
						start_pause(true);
						JFrame df = new JFrame();
						JOptionPane op = new JOptionPane();
						String message;
						if (flag==1)
							message = "Your team wins!";
						else
							message = "Your team loses!";
						Object [] options = {"Exit"};

						//count score
						if (isServer)
							record();


						int sign = op.showOptionDialog(df, "History High:"+"\n"+history+
							"Press the key to exit.",
							message, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, options, options[0]);
						if (sign==0)
							System.exit(0);
					}

					//{myTank, fTank, enemies, myBullets, enemyBullets, walls}
					//myBullets and enemyTanks
					for (int j=0; j<enemies.size(); j++)
						for (int i=0; i<myBullets.size(); i++)
							if (collide(myBullets.get(i), enemies.get(j)))
							{
								EnemyTank temp = enemies.get(j);
								if (temp.health==1)
								{
									int point=1;
									if (temp.strong)
										point = 3;
									if (myBullets.get(i).byMe)
										myPoint += point;
									else
										fPoint += point;
									temp.alive = false;
									enemies.remove(j);
									myBullets.remove(i);

									AudioInputStream audioIn = 
										AudioSystem.getAudioInputStream(explode_sound);
									DataLine.Info info = 
										new DataLine.Info(Clip.class, audioIn.getFormat());
									Clip clip = (Clip)AudioSystem.getLine(info);
									clip.open(audioIn);
									clip.start();
								}
								else
								{
									temp.health--;
									myBullets.remove(i);
								}
								break;
							}

					//enemyBullets and myTank
					if (myTank.alive)
					for (int i=0; i<enemyBullets.size(); i++)
						if (collide(enemyBullets.get(i), myTank))
						{
							myTank.alive = false;
							enemyBullets.remove(i);
							synchronized(sender)
							{
								sender.writeBytes("$tankDead\n");
							}

							AudioInputStream audioIn = 
								AudioSystem.getAudioInputStream(explode_sound);
							DataLine.Info info = 
								new DataLine.Info(Clip.class, audioIn.getFormat());
							Clip clip = (Clip)AudioSystem.getLine(info);
							clip.open(audioIn);
							clip.start();

							break;
						}

					//myBullets and walls
					for (int j=0; j<walls.size(); j++)
						for (int i=0; i<myBullets.size(); i++)
							if (collide(myBullets.get(i), walls.get(j)))
								myBullets.remove(i);

					//enemyBullets and walls
					for (int j=0; j<walls.size(); j++)
						for (int i=0; i<enemyBullets.size(); i++)
						{
							Wall temp = walls.get(j);
							if (collide(enemyBullets.get(i), temp))
							{
								enemyBullets.remove(i);
								if (temp.base)
									temp.life--;
							}
						}

					//{myTank, fTank} and walls
					//the following block: check if myTank is blocked from a certain direction
					//if not, set the colliding back to -1
					boolean all = true;
					for (int i=0; i<walls.size(); i++)
					{
						int temp = collide(myTank, walls.get(i));
						if (temp>-1)
						{
							all = false;
							myTank.colliding = temp;
						}
					}
					if (all)
						myTank.colliding = -1;

					all = true;
					for (int i=0; i<walls.size(); i++)
					{
						int temp = collide(fTank, walls.get(i));
						if (temp>-1)
						{
							all = false;
							fTank.colliding = temp;
						}
					}
					if (all)
						fTank.colliding = -1;


					//enemyTank and walls
					for (int i=0; i<enemies.size(); i++)
					{
						all = true;
						EnemyTank et =enemies.get(i);
						for (int j=0; j<walls.size(); j++)
						{
							int temp = collide(et, walls.get(j));
							if (temp>-1)
							{
								all = false;
								et.colliding = temp;
							}
						}
						if (all)
							et.colliding = -1;
					}

					//{myTank, fTank} and enemyTank
					if (myTank.alive)
						for (int i=0; i<enemies.size(); i++)
							if (collide(myTank, enemies.get(i))>-1)
							{
								myTank.alive = false;
								enemies.get(i).alive = false;
								int temp = 1;
								if (enemies.get(i).strong)
									temp = 3;
								myPoint += temp;
								enemies.remove(i);
							}
					if (fTank.alive)
						for (int i=0; i<enemies.size(); i++)
							if (collide(fTank, enemies.get(i))>-1)
							{
								fTank.alive = false;
								enemies.get(i).alive = false;
								int temp = 1;
								if (enemies.get(i).strong)
									temp = 3;
								fPoint += temp;
								enemies.remove(i);
							}

					//myTank and fTank
					if (myTank.alive && fTank.alive)
					{
						int temp = collide(myTank, fTank);
						if (temp>-1)
							myTank.colliding = temp;
						temp = collide(fTank, myTank);
						if (temp>-1)
							fTank.colliding = temp;
					}

					//between enemies
					for (int i=0; i<enemies.size()-1; i++)
						for (int j=i+1; j<enemies.size(); j++)
						{
							EnemyTank eti=enemies.get(i), etj=enemies.get(j);
							int temp = collide(eti, etj);
							if (temp>-1)
								eti.colliding = temp;
							temp = collide(etj, eti);
							if (temp>-1)
								etj.colliding = temp;
						}

				}
				Thread.sleep(delay);
				} catch(Exception ex){}
			
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

	public int collide(Tank t, Wall w){
		int rest=3; // pixels between collision
		if (
		(t.dir==0 && Math.abs(t.y-w.y)<Tank.size && t.x<=w.x+Tank.size+rest && t.x>=w.x) ||
		(t.dir==1 && Math.abs(t.x-w.x)<Tank.size && t.y<=w.y+Tank.size+rest && t.y>=w.y) ||
		(t.dir==2 && Math.abs(t.y-w.y)<Tank.size && t.x<=w.x && t.x>=w.x-Tank.size-rest) ||
		(t.dir==3 && Math.abs(t.x-w.x)<Tank.size && t.y<=w.y && t.y>=w.y-Tank.size-rest) )
			return t.dir;
		return -1;
	}

	public int collide(Tank t, Tank w){
		int rest=3; // pixels between collision
		if (
		(t.dir==0 && Math.abs(t.y-w.y)<Tank.size && t.x<=w.x+Tank.size+rest && t.x>=w.x) ||
		(t.dir==1 && Math.abs(t.x-w.x)<Tank.size && t.y<=w.y+Tank.size+rest && t.y>=w.y) ||
		(t.dir==2 && Math.abs(t.y-w.y)<Tank.size && t.x<=w.x && t.x>=w.x-Tank.size-rest) ||
		(t.dir==3 && Math.abs(t.x-w.x)<Tank.size && t.y<=w.y && t.y>=w.y-Tank.size-rest) )
			return t.dir;
		return -1;
	}

	public abstract void record();
}