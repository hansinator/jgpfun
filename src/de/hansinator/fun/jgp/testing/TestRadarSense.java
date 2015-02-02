/*
 */
package de.hansinator.fun.jgp.testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.FitnessEvaluator;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.lgp.LGPGene;
import de.hansinator.fun.jgp.simulation.FindingFoodScenario.FoodFitnessEvaluator;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.AntBody;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Food;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.OrientationSense;
import de.hansinator.fun.jgp.world.world2d.senses.RadarSense;
import de.hansinator.fun.jgp.world.world2d.senses.SpeedSense;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

/**
 * 
 * @author Hansinator
 */
@SuppressWarnings("serial")
public class TestRadarSense extends JPanel
{

	private final ExecutionUnit<World2d> organism;
	
	private final AntBody body;
	
	private RadarSense sense;

	private final JSlider sliderBody;
	
	private final JSlider sliderBeam;

	private final World2d world;

	public LGPGene randomGenome()
	{
		AntBody.Gene bodyGene = new AntBody.Gene(false);
		bodyGene.addBodyPartGene(new RadarSense.Gene());

		LGPGene organismGene = LGPGene.randomGene(256);
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

		body = ((AntBody) organism.getIOUnits()[0]);
		body.x = 400.0;
		body.y = 300.0;
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
				body.dir = sliderBody.getValue() * (Math.PI / 180.0);
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
		
		for (Food f : world.food)
			f.draw(g);
		
		body.draw(g);
		g.setColor(Color.yellow);
		g.drawString("BodyDir: " + String.format("%.2f", body.dir), 10, 15);
		g.drawString("BeamDir: " + String.format("%.2f", sense.direction), 10, 25);
	}

	public static void main(String[] args) throws IOException
	{
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
