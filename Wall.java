package code;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Wall{
	Game parent;
	int x,y;
	Color br1 = new Color(204, 102, 0);
	Color br2 = new Color(170, 51, 17);
	boolean base;
	int life;

	public Wall(int xx, int yy, Game p, boolean b){
		x = xx;
		y = yy;
		parent = p;
		base = b;
		if (b)
			life = 2;
	}

	public void draw(Graphics g){
		if (base)
		{
			g.setColor(Color.yellow);
			g.fillRect(x, y, Tank.size, Tank.size);
			g.setColor(Color.cyan);
			g.fillOval(x, y, Tank.size, Tank.size);
			if (life==2)
			{
				g.setColor(new Color(225, 0, 160));
				g.fillRect(x+Tank.size/4, y+Tank.size/4, Tank.size/2+2, Tank.size/2+2);
			}
		}
		else
		{
			/*g.setColor(br1);
			g.fillRect(x, y, Tank.size/2, Tank.size/3);
			g.fillRect(x+Tank.size/4, y+Tank.size/3, Tank.size/2, Tank.size/3);
			g.fillRect(x, y+2*Tank.size/3, Tank.size/2, Tank.size/3);

			g.setColor(br2);
			g.fillRect(x+Tank.size/2, y, Tank.size/2, Tank.size/3);
			g.fillRect(x+Tank.size/2, y+2*Tank.size/3, Tank.size/2, Tank.size/3);
			g.fillRect(x, y+Tank.size/3, Tank.size/4, Tank.size/3);
			g.fillRect(x+3*Tank.size/4, y+Tank.size/3, Tank.size/4+1, Tank.size/3);*/
			g.drawImage(Game.wall_image, x, y, Tank.size, Tank.size, null, null);
		}
	}
}