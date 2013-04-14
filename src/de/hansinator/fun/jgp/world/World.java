package de.hansinator.fun.jgp.world;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import de.hansinator.fun.jgp.life.BaseOrganism;
import de.hansinator.fun.jgp.world.world2d.Food;

public interface World
{
	public void draw(Graphics g);

	public void clickEvent(int x, int y);

	public void animate();

	public void setOrganisms(List<BaseOrganism> organisms);

	public void resetState();
	
	public Food findNearestFood(Point.Double p);
	
	public int getWidth();
	
	public int getHeight();
}
