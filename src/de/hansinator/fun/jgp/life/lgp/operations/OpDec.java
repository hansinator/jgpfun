package de.hansinator.fun.jgp.life.lgp.operations;

/**
 * 
 * @author Administrator
 */
public class OpDec implements Operation, UnaryOperation
{

	@Override
	public int execute(int src1, int src2)
	{
		return src1 - 1;
	}

}
