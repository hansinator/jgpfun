package de.hansinator.fun.jgp.world.world2d;

import java.util.ArrayList;
import java.util.List;

import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.world.world2d.senses.ObjectLocator;

/**
 * 
 * @author Hansinator
 */
public class AntBody extends Body2d
{
	public final ObjectLocator locator;


	public AntBody(ExecutionUnit<World2d> context)
	{
		super(context, 0.0, 0.0, 0.0);

		// init locator sense
		locator = new ObjectLocator(this);
	}
	
	public static class Gene extends IOUnit.Gene<ExecutionUnit<World2d>>
	{
		public static int locatorInputCount = new ObjectLocator.Gene().getInputCount();
				
		private List<IOUnit.Gene<Body2d>> children = new ArrayList<IOUnit.Gene<Body2d>>();
		
		boolean useInternalLocator;
		
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
			for(IOUnit.Gene<Body2d> child : children)
				gene.children.add(child.replicate());
			
			return gene;
		}

		@Override
		public IOUnit<ExecutionUnit<World2d>> express(ExecutionUnit<World2d> context)
		{
			AntBody body = new AntBody(context);
			@SuppressWarnings("unchecked")
			IOUnit<Body2d>[] parts = new IOUnit[children.size() + (useInternalLocator?1:0)];
			int i = 0;
			
			if(useInternalLocator)
				parts[i++] = body.locator;
			
			for(IOUnit.Gene<Body2d> gene : children)
				parts[i++] = gene.express(body);
			
			body.setParts(parts);
			
			return body;
		}

		@Override
		public int getInputCount()
		{
			return inputCount + (useInternalLocator?locatorInputCount:0);
		}

		@Override
		public int getOutputCount()
		{
			return outputCount;
		}

		@Override
		public Mutation[] getMutations()
		{
			return Mutation.emptyMutationArray;
		}

		public void addBodyPartGene(IOUnit.Gene<Body2d> gene)
		{
			children.add(gene);
			inputCount += gene.getInputCount();
			outputCount += gene.getOutputCount();
		}
	}
}
