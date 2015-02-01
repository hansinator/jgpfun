package de.hansinator.fun.jgp.life.lgp.operations;

/**
 * 
 * @author dahmen
 */
public class OpMin implements Operation
{

	@Override
	public int execute(int src1, int src2)
	{
		return Math.min(src1, src2);
	}

}
