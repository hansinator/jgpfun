package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.genetics.ValueGene.FloatGene;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;

public class Body2d implements BodyPart<ExecutionUnit<World2d>>
{
	private static final Random rnd = Settings.newRandomSource();
	
	public final ExecutionUnit<World2d> parent;
	
	public volatile boolean selected = false;
	
	public final Color color;

	@SuppressWarnings("unchecked")
	private IOUnit<Body2d>[] parts = BodyPart.emptyBodyPartArray;

	@SuppressWarnings("unchecked")
	private BodyPart.DrawablePart<Body2d>[] drawableParts = BodyPart.DrawablePart.emptyDrawablePartArray;
	
	private final List<CollisionListener> collisionListeners = new ArrayList<CollisionListener>();

	private SensorInput[] inputs;

	private ActorOutput[] outputs;
	
	private org.jbox2d.dynamics.Body body;
	
	private Shape shape;
	
	private World2d world;
	

	public org.jbox2d.dynamics.Body getBody()
	{
		return body;
	}

	public Body2d(ExecutionUnit<World2d> parent, Shape shape, float color)
	{
		this.parent = parent;
		this.shape = shape;
		this.color = new Color(java.awt.Color.HSBtoRGB(color, 1.0f, 1.0f));
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
		world.registerObject(this);

		// box2d body
		{
			int x = rnd.nextInt(world.getWidth());
			int y = rnd.nextInt(world.getHeight());
			float dir = Math.round(rnd.nextDouble() * 2.0 * Math.PI);

			FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			fd.density = 1.0f;
			fd.friction = 0.9f;

			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.angularDamping = 12.0f;
			bd.linearDamping = 4.0f;
			bd.allowSleep = false;
			bd.position.set((float) x, (float) y);
			bd.angle = dir;
			body = world.getWorld().createBody(bd);
			body.setUserData(this);
			body.createFixture(fd);
		}

		// attach parts after body initialization is done
		for (IOUnit<Body2d> part : parts)
			part.attachEvaluationState(this);
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
	
	final synchronized public boolean addCollisionListener(CollisionListener listener)
	{
		return collisionListeners.add(listener);
	}

	final synchronized public boolean removeCollisionListener(CollisionListener listener)
	{
		return collisionListeners.remove(listener);
	}

	final void collision(Body object)
	{
		for(CollisionListener listener : collisionListeners)
			listener.onCollision(this, object);
	}
	
	public World2d getWorld()
	{
		return world;
	}
	

	public void draw(Graphics g)
	{
		for (BodyPart.DrawablePart<Body2d> part : drawableParts)
			part.draw(g);
	}
	
	public interface CollisionListener
	{
		public void onCollision(Body2d a, Body object);
	}
	
	public static class Gene extends IOUnit.Gene<ExecutionUnit<World2d>>
	{
		public static int locatorInputCount = new ObjectLocator.Gene().getInputCount();

		private List<IOUnit.Gene<Body2d>> children = new ArrayList<IOUnit.Gene<Body2d>>();
		
		private FloatGene bodyWidth = new FloatGene(1.0f, 500);
		
		private FloatGene bodyHeight = new FloatGene(1.0f, 500);
		
		private FloatGene color = new FloatGene(1.0f, 500);
		
		private static float maxWidth = 1.0f;
		
		private static float maxHeight = 1.0f;
		
		private static float minWidth = 0.625f;
		
		private static float minHeight = 0.625f;
		
		Mutation[] mutations = { bodyWidth, bodyHeight, color };

		int inputCount = 0;

		int outputCount = 0;

		@SuppressWarnings("rawtypes")
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			List<de.hansinator.fun.jgp.genetics.Gene> list = new ArrayList<de.hansinator.fun.jgp.genetics.Gene>();
			list.addAll(children);
			return list;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<ExecutionUnit<World2d>> replicate()
		{
			Body2d.Gene gene = new Body2d.Gene();

			gene.inputCount = inputCount;
			gene.outputCount = outputCount;
			for (IOUnit.Gene<Body2d> child : children)
				gene.children.add(child.replicate());
			
			gene.bodyWidth.setValue(bodyWidth.getValue());
			gene.bodyHeight.setValue(bodyHeight.getValue());
			gene.color.setValue(color.getValue());

			return gene;
		}

		@Override
		public IOUnit<ExecutionUnit<World2d>> express(ExecutionUnit<World2d> context)
		{
			// create ant body shape
			float w = (minWidth + (bodyWidth.getValue() * (maxWidth - minWidth))) / 2;
			float h = (minHeight + (bodyHeight.getValue() * (maxHeight - minHeight))) / 2;
			PolygonShape shape = new PolygonShape();
			Vec2 vertices[] = new Vec2[3];
			vertices[0] = new Vec2(0.0f, -h); // top of triangle
			vertices[1] = new Vec2(-w, h); // left wing
			vertices[2] = new Vec2(w, h); // right wing
			shape.set(vertices, 3);

			// create body
			Body2d body = new Body2d(context, shape, color.getValue());
			
			// create parts
			int i = 0;
			@SuppressWarnings("unchecked")
			IOUnit<Body2d>[] parts = new IOUnit[children.size()];
			for (IOUnit.Gene<Body2d> gene : children)
				parts[i++] = gene.express(body);

			// attach parts and return finished body
			body.setParts(parts);
			return body;
		}

		@Override
		public int getInputCount()
		{
			return inputCount;
		}

		@Override
		public int getOutputCount()
		{
			return outputCount;
		}

		@Override
		public Mutation[] getMutations()
		{
			return mutations;
		}

		public void addBodyPartGene(IOUnit.Gene<Body2d> gene)
		{
			children.add(gene);
			inputCount += gene.getInputCount();
			outputCount += gene.getOutputCount();
		}
	}
}
