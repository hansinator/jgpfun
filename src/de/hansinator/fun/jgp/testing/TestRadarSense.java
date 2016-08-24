/*
 */
package de.hansinator.fun.jgp.testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jbox2d.dynamics.BodyType;

import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.lgp.LGPGene;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.senses.RadarSense;

/**
 * 
 * @author Hansinator
 */
@SuppressWarnings("serial")
public class TestRadarSense extends JPanel
{

	private final ExecutionUnit<World2d> organism;
	
	private final Body2d body;
	
	private RadarSense sense;

	private final JSlider sliderBody;
	
	private final JSlider sliderBeam;

	private final World2d world;

	public LGPGene randomGenome()
	{
		Body2d.Gene bodyGene = new Body2d.Gene();
		bodyGene.addBodyPartGene(new RadarSense.Gene());

		LGPGene organismGene = LGPGene.randomGene(Settings.newRandomSource(), 256);
		organismGene.addIOGene(bodyGene);

		return organismGene;
	}

	public TestRadarSense() throws IOException
	{
		setPreferredSize(new Dimension(800, 600));
		world = new World2d(800, 600, 23);
		world.resetState();
		organism = randomGenome().express(world);
		organism.setExecutionContext(world);

		body = ((Body2d) organism.getIOUnits()[0]);
		body.getBody().setType(BodyType.STATIC);
		body.sampleInputs();
		
		for(IOUnit<Body2d> part : body.getParts())
			if(part instanceof RadarSense)
				sense = (RadarSense) part;
		
		sense.get();

		sliderBody = new JSlider(0, 360);
		sliderBody.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e)
			{
				body.getBody().setTransform(body.getBody().getPosition(), Math.round(sliderBody.getValue() * (Math.PI / 180.0)));
				sense.get();
				repaint();
			}

		});
		
		sliderBeam = new JSlider(0, 360);
		sliderBeam.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e)
			{
				sense.direction = sliderBeam.getValue() * (Math.PI / 180.0);
				sense.get();
				repaint();
			}

		});
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		body.draw(g);
		g.setColor(Color.yellow);
		g.drawString("BodyDir: " + String.format("%.2f", body.getBody().getAngle()), 10, 15);
		g.drawString("BeamDir: " + String.format("%.2f", sense.direction), 10, 25);
	}
	
	public static void main(String[] args) throws IOException
	{
		Point p = new Point(10,10);
		Point p1 = new Point(10,10);
		Point p2 = new Point(20,10);
		Point p3 = new Point(10,20);
		
		double alpha = ((p2.y - p3.y) * (p.x - p3.x) + (p3.x - p2.x) * (p.y - p3.y)) /
				(double)((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
		double beta = ((p3.y - p1.y) * (p.x - p3.x) + (p1.x - p3.x) * (p.y - p3.y)) /
				(double)((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
		double gamma = 1.0f - alpha - beta;
		
		System.out.println("alpha: " + alpha + " beta: " + beta + " gamma: " + gamma);
		
		Settings.load(new File("default.properties"));
		JFrame frame = new JFrame("test");
		TestRadarSense testOrganismDraw = new TestRadarSense();
		frame.setLayout(new BorderLayout());
		frame.add(testOrganismDraw, BorderLayout.CENTER);
		frame.add(testOrganismDraw.sliderBody, BorderLayout.PAGE_START);
		frame.add(testOrganismDraw.sliderBeam, BorderLayout.PAGE_END);
		frame.pack();
		frame.setVisible(true);
	}
}
