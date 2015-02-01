package de.hansinator.fun.jgp.life.lgp.operations;

import java.io.Serializable;

/**
 * 
 * @author dahmen
 */
public interface Operation extends Serializable
{
	public int execute(int src1, int src2);
}
