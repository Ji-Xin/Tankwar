package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Base extends Wall{
	int hp;
	Game parent;

	public Base(Game p){
		super(0, 0, p);
		hp = 3;
	} 
}