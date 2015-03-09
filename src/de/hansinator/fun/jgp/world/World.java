package de.hansinator.fun.jgp.world;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Map;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.life.ExecutionUnit;

public interface World
{
	public void draw(Graphics g, Map<ExecutionUnit<? extends World>, Genome> generation);

	public void clickEvent(MouseEvent e, Map<ExecutionUnit<? extends World>, Genome> generation);

	public void animate();

	public void resetState();
	
	public void setDraw(de.hansinator.fun.jgp.gui.DebugDrawJ2D draw);
}
