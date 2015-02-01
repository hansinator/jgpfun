package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Random;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.BodyPart.DrawablePart;

public abstract class Body2d extends AnimatableObject implements DrawablePart<ExecutionUnit<World2d>>
{
	private static final int bodyCollisionRadius = Settings.getInt("bodyCollisionRadius");

	protected static final Random rnd = Settings.newRandomSource();

	@SuppressWarnings("unchecked")
	protected IOUnit<Body2d>[] parts = BodyPart.emptyBodyPartArray;

	@SuppressWarnings("unchecked")
	protected BodyPart.DrawablePart<Body2d>[] drawableParts = BodyPart.DrawablePart.emptyDrawablePartArray;

	protected SensorInput[] inputs;

	protected ActorOutput[] outputs;

	public final ExecutionUnit<World2d> parent;

	public double lastSpeed = 0.0;

	public volatile boolean tagged = false;

	public Body2d(ExecutionUnit<World2d> parent, double x, double y, double dir)
	{
		// TODO: fix null pointer
		super(null, x, y, dir);
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public void setParts(IOUnit<Body2d>[] parts)
	{
		int i, o, d, x;

		this.parts = parts;

		// count I/O and drawable parts
		for(x = 0, i = 0, o = 0, d = 0; x < parts.length; x++)
		{
			i += parts[x].getInputs().length;
			o += parts[x].getOutputs().length;
			if (parts[x] instanceof BodyPart.DrawablePart)
				d++;
		}

		// create arrays
		inputs = new SensorInput[i];
		outputs = new ActorOutput[o];
		drawableParts = new BodyPart.DrawablePart[d];

		// collect I/O ports and drawable parts
		for(x = 0, i = 0, o = 0, d = 0; x < parts.length; x++)
		{
			// collect inputs
			for (SensorInput in : parts[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : parts[x].getOutputs())
				outputs[o++] = out;

			// collect drawable parts
			if (parts[x] instanceof BodyPart.DrawablePart)
				drawableParts[d++]= (BodyPart.DrawablePart<Body2d>) parts[x];
		}
	}

	@Override
	public void attachEvaluationState(ExecutionUnit<World2d> context)
	{
		world = context.getExecutionContext();
		
		for(IOUnit<Body2d> part : parts)
			part.attachEvaluationState(this);
		
		x = rnd.nextInt(world.getWidth());
		y = rnd.nextInt(world.getHeight());
		dir = rnd.nextDouble() * 2 * Math.PI;
		world.registerObject(this);
	}

	@Override
	public SensorInput[] getInputs()
	{
		return inputs;
	}

	@Override
	public ActorOutput[] getOutputs()
	{
		return outputs;
	}

	@Override
	public void sampleInputs()
	{
		for (IOUnit<Body2d> p : parts)
			p.sampleInputs();
	}

	@Override
	public void applyOutputs()
	{
		for (IOUnit<Body2d> p : parts)
			p.applyOutputs();
	}

	@Override
	public void draw(Graphics g)
	{
		final double sindir = Math.sin(dir);
		final double cosdir = Math.cos(dir);
		final double x_len_displace = 6.0 * sindir;
		final double y_len_displace = 6.0 * cosdir;
		final double x_width_displace = 4.0 * sindir;
		final double y_width_displace = 4.0 * cosdir;
		final double x_bottom = x - x_len_displace;
		final double y_bottom = y + y_len_displace;

		for (BodyPart.DrawablePart<Body2d> part : drawableParts)
			part.draw(g);

		Polygon p = new Polygon();
		p.addPoint(Math.round((float) (x + x_len_displace)), Math.round((float) (y - y_len_displace))); // top
		// of
		// triangle
		p.addPoint(Math.round((float) (x_bottom + y_width_displace)), Math.round((float) (y_bottom + x_width_displace))); // right
		// wing
		p.addPoint(Math.round((float) (x_bottom - y_width_displace)), Math.round((float) (y_bottom - x_width_displace))); // left
		// wing

		g.setColor(tagged ? Color.magenta : Color.red);
		g.drawPolygon(p);
		g.fillPolygon(p);

		//XXX TODO find a solution for the fitness dependency here
		//g.setColor(Color.green);
		//g.drawString("" + organism.getFitness(), Math.round((float) x) + 8, Math.round((float) y) + 8);
	}

	@Override
	public int getCollisionRadius()
	{
		return bodyCollisionRadius;
	}
	
	public World2d getWorld()
	{
		return world;
	}
}
