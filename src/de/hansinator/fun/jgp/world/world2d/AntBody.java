package de.hansinator.fun.jgp.world.world2d;

import java.util.ArrayList;
import java.util.List;

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


	public AntBody(ExecutionUnit<World2d> organism)
	{
		super(organism, 0.0, 0.0, 0.0);

		// init locator sense
		locator = new ObjectLocator(this);
	}
	
	public static class Gene implements IOUnit.Gene<ExecutionUnit<World2d>>
	{
		
		public List<IOUnit.Gene<Body2d>> children = new ArrayList<IOUnit.Gene<Body2d>>();

		@Override
		public void mutate()
		{
		}

		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			return null;
		}

		@Override
		public void setMutationChance(int mutationChance)
		{
		}

		@Override
		public int getMutationChance()
		{
			return 0;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<ExecutionUnit<World2d>> replicate()
		{
			AntBody.Gene gene =  new AntBody.Gene();
			
			for(IOUnit.Gene<Body2d> child : children)
				gene.children.add(child.replicate());
			
			return gene;
		}

		@Override
		public IOUnit<ExecutionUnit<World2d>> express(ExecutionUnit<World2d> context)
		{
			AntBody body = new AntBody(context);
			@SuppressWarnings("unchecked")
			IOUnit<Body2d>[] parts = new IOUnit[children.size()];
			int i = 0;
			
			for(IOUnit.Gene<Body2d> gene : children)
				parts[i++] = gene.express(body);
			body.setParts(parts);
			
			return body;
		}

		@Override
		public int getInputCount()
		{
			int i = 0;
			
			for(IOUnit.Gene<Body2d> child : children)
				i += child.getInputCount();
			
			return i;
		}

		@Override
		public int getOutputCount()
		{
			int o = 0;
			
			for(IOUnit.Gene<Body2d> child : children)
				o += child.getOutputCount();
			
			return o;
		}

	}
}
