package de.hansinator.fun.jgp.world.world2d.senses;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.genetics.ValueGene.DoubleGene;
import de.hansinator.fun.jgp.genetics.ValueGene.FloatGene;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * Sensory input to locate objects in world. Currently only locates food
 * objects.
 * 
 * @author Hansinator
 * 
 */
public class ObjectLocator implements BodyPart.DrawablePart<Body2d>
{

	private World2d world;

	private Body parent;

	private Vec2 origin, target;

	private double objDist;
	
	private static Color beamColor = new Color(32, 32, 32);
	
	private final double senseDirScaleFactor, distScaleFactor;


	public final SensorInput senseDirX = new SensorInput()
	{

		@Override
		public double get()
		{
			return ((target.x - origin.x) / objDist) * senseDirScaleFactor;
		}

	};

	public final SensorInput senseDirY = new SensorInput()
	{

		@Override
		public double get()
		{
			return ((target.y - origin.y) / objDist) * senseDirScaleFactor;
		}

	};

	public final SensorInput senseDist = new SensorInput()
	{

		@Override
		public double get()
		{
			return objDist * distScaleFactor;
		}

	};

	SensorInput[] inputs = { senseDirX, senseDirY, senseDist };
	
	
	public ObjectLocator(double senseDirScaleFactor, double distScaleFactor)
	{
		this.senseDirScaleFactor = senseDirScaleFactor;
		this.distScaleFactor = distScaleFactor;
	}

	@Override
	public SensorInput[] getInputs()
	{
		return inputs;
	}

	@Override
	public ActorOutput[] getOutputs()
	{
		return ActorOutput.emptyActorOutputArray;
	}

	@Override
	public void sampleInputs()
	{
		origin = parent.getPosition();
		target = world.findNearestFood(origin);
		objDist = Math.sqrt(((target.x - origin.x) * (target.x - origin.x)) + ((target.y - origin.y) * (target.y - origin.y)));
	}

	@Override
	public void applyOutputs()
	{
	}

	@Override
	public void draw(Graphics g)
	{
		if (target != null)
		{
			Vec2 t = new Vec2();
			Vec2 o = new Vec2(Math.round(origin.x), Math.round(origin.y));
			
			world.getDraw().getViewportTranform().getWorldToScreen(target, t);
			world.getDraw().getViewportTranform().getWorldToScreen(o, o);
			
			g.setColor(beamColor);
			g.drawLine(Math.round(o.x), Math.round(o.y), Math.round(t.x), Math.round(t.y));
		}
	}

	@Override
	public void attachEvaluationState(Body2d context)
	{
		this.world = context.getWorld();
		this.parent = context.getBody();
	}
	
	
	public static class Gene extends IOUnit.Gene<Body2d>
	{
		private DoubleGene senseDirScaleFactor = new DoubleGene(1.0, Settings.getInt("senseDirScaleFactorMutChance"));
		
		private DoubleGene distScaleFactor = new DoubleGene(Settings.getInt("distScaleFactorMutChance"));
		
		Mutation[] mutations = { senseDirScaleFactor, distScaleFactor };
		
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			ObjectLocator.Gene gene = new ObjectLocator.Gene();
			gene.senseDirScaleFactor.setValue(senseDirScaleFactor.getValue());
			gene.distScaleFactor.setValue(distScaleFactor.getValue());
			return gene;
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new ObjectLocator(senseDirScaleFactor.getValue() * EvolutionaryProcess.intScaleFactor, distScaleFactor.getValue() * EvolutionaryProcess.intScaleFactor);
		}

		@Override
		public int getInputCount()
		{
			return 3;
		}

		@Override
		public int getOutputCount()
		{
			return 0;
		}

		@Override
		public Mutation[] getMutations()
		{
			return mutations;
		}
	}
}
