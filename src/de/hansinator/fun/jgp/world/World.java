package de.hansinator.fun.jgp.world;

import java.awt.Graphics;

public interface World
{
	public void draw(Graphics g);

	public void clickEvent(int x, int y);

	public void animate();

	public void resetState();
}
