package game.server;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Wall{
	Game parent;
	int x,y;
	Color br1 = new Color(204, 102, 0);
	Color br2 = new Color(170, 51, 17);

	public Wall(int xx, int yy, Game p){
		x = xx;
		y = yy;
		parent = p;
	}

	public void draw(Graphics g){
		g.setColor(br1);
		g.fillRect(x, y, Tank.size/2, Tank.size/3);
		g.fillRect(x+Tank.size/4, y+Tank.size/3, Tank.size/2, Tank.size/3);
		g.fillRect(x, y+2*Tank.size/3, Tank.size/2, Tank.size/3);

		g.setColor(br2);
		g.fillRect(x+Tank.size/2, y, Tank.size/2, Tank.size/3);
		g.fillRect(x+Tank.size/2, y+2*Tank.size/3, Tank.size/2, Tank.size/3);
		g.fillRect(x, y+Tank.size/3, Tank.size/4, Tank.size/3);
		g.fillRect(x+3*Tank.size/4, y+Tank.size/3, Tank.size/4+1, Tank.size/3);
	}
}