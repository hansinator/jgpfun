package de.hansinator.fun.jgp.life.lgp.operations;

/**
 * 
 * @author dahmen
 */
public class OpSub implements Operation
{

	@Override
	public int execute(int src1, int src2)
	{
		return src1 - src2;
	}
}
