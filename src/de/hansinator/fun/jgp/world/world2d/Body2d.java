package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Random;

import javax.swing.JButton;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.WheelJoint;
import org.jbox2d.dynamics.joints.WheelJointDef;

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
	
	private org.jbox2d.dynamics.Body body;

	public org.jbox2d.dynamics.Body getBody()
	{
		return body;
	}

	public Body2d(ExecutionUnit<World2d> parent, double x, double y, double dir)
	{
		super(parent.getExecutionContext(), x, y, dir);
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
	
	public IOUnit<Body2d>[] getParts()
	{
		return parts;
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
		
	    // box2d body
	    {
	      PolygonShape chassis = new PolygonShape();
	      Vec2 vertices[] = new Vec2[3];
	      vertices[0] = new Vec2(0.0f, -6.0f); // top of triangle
	      vertices[1] = new Vec2(-4.0f, 6.0f); // left wing
	      vertices[2] = new Vec2(4.0f, 6.0f); // right wing
	      chassis.set(vertices, 3);

	      FixtureDef fd = new FixtureDef();
	      fd.shape = chassis;
	      fd.density = 1.0f;
	      fd.friction = 0.9f;

	      BodyDef bd = new BodyDef();
	      bd.type = BodyType.DYNAMIC;
	      bd.angularDamping = 5.0f;
	      bd.linearDamping = 0.1f;
	      bd.position.set((float)x, (float)y);
	      body = world.getWorld().createBody(bd);
	      body.createFixture(fd);
	    }
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
		
		dir = body.getAngle();
		
		Vec2 pos = body.getPosition();
		x = pos.x;
		y = pos.y;
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
		p.addPoint(Math.round((float) (x + x_len_displace)), Math.round((float) (y - y_len_displace))); // top of triangle
		p.addPoint(Math.round((float) (x_bottom + y_width_displace)), Math.round((float) (y_bottom + x_width_displace))); // right wing
		p.addPoint(Math.round((float) (x_bottom - y_width_displace)), Math.round((float) (y_bottom - x_width_displace))); // left wing

		g.setColor(selected ? Color.magenta : Color.red);
		g.drawPolygon(p);
		g.fillPolygon(p);
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
