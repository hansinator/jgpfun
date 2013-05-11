package de.hansinator.fun.jgp.world;

import java.awt.Graphics;
import java.util.List;

import de.hansinator.fun.jgp.life.BaseOrganism;

public interface World
{
	public void draw(Graphics g);

	public void clickEvent(int x, int y);

	public void setOrganisms(List<BaseOrganism> organisms);

	public void animate();

	public void resetState();
}
