package de.hansinator.fun.jgp.world.world2d;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.genetics.ValueGene.FloatGene;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;

/**
 * 
 * @author Hansinator
 */
public class AntBody extends Body2d
{
	protected final ObjectLocator locator;

	public AntBody(ExecutionUnit<World2d> context, Shape shape)
	{
		super(context, 0.0, 0.0, 0.0, shape);

		// init locator sense
		locator = new ObjectLocator(this);
	}

	public static class Gene extends IOUnit.Gene<ExecutionUnit<World2d>>
	{
		public static int locatorInputCount = new ObjectLocator.Gene().getInputCount();

		private List<IOUnit.Gene<Body2d>> children = new ArrayList<IOUnit.Gene<Body2d>>();

		boolean useInternalLocator;
		
		private FloatGene bodyWidth = new FloatGene(1.0f, 500);
		
		private FloatGene bodyHeight = new FloatGene(1.0f, 500);
		
		private static float maxWidth = 3.0f;
		
		private static float maxHeight = 4.0f;
		
		private static float minWidth = 1.0f;
		
		private static float minHeight = 2.0f;
		
		Mutation[] mutations = {bodyWidth, bodyHeight };

		int inputCount = 0;

		int outputCount = 0;

		public Gene(boolean useInternalLocator)
		{
			this.useInternalLocator = useInternalLocator;
		}

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
			AntBody.Gene gene = new AntBody.Gene(useInternalLocator);

			gene.inputCount = inputCount;
			gene.outputCount = outputCount;
			for (IOUnit.Gene<Body2d> child : children)
				gene.children.add(child.replicate());
			
			gene.bodyWidth.setValue(bodyWidth.getValue());
			gene.bodyHeight.setValue(bodyHeight.getValue());

			return gene;
		}

		@Override
		public IOUnit<ExecutionUnit<World2d>> express(ExecutionUnit<World2d> context)
		{
			float w = (minWidth + bodyWidth.getValue() * (maxWidth - minWidth)) / 2;
			float h = (minHeight + bodyHeight.getValue() * (maxHeight - minHeight)) / 2;
			PolygonShape shape = new PolygonShape();
			Vec2 vertices[] = new Vec2[3];
			vertices[0] = new Vec2(0.0f, -h); // top of triangle
			vertices[1] = new Vec2(-w, h); // left wing
			vertices[2] = new Vec2(w, h); // right wing
			shape.set(vertices, 3);

			AntBody body = new AntBody(context, shape);
			@SuppressWarnings("unchecked")
			IOUnit<Body2d>[] parts = new IOUnit[children.size() + (useInternalLocator ? 1 : 0)];
			int i = useInternalLocator ? 1 : 0;

			if (useInternalLocator)
				parts[0] = body.locator;

			for (IOUnit.Gene<Body2d> gene : children)
				parts[i++] = gene.express(body);

			body.setParts(parts);

			return body;
		}

		@Override
		public int getInputCount()
		{
			return inputCount + (useInternalLocator ? locatorInputCount : 0);
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
