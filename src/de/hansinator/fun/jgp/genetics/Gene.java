package de.hansinator.fun.jgp.genetics;

import de.hansinator.fun.jgp.life.BaseOrganism;

public interface Gene<T, O extends BaseOrganism>
{
	public Gene<T, O> replicate();

	public T express(O organism);
}
