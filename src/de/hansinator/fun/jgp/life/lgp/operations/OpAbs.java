package de.hansinator.fun.jgp.life.lgp.operations;

/**
 * 
 * @author dahmen
 */
public class OpAbs implements Operation, UnaryOperation
{

	@Override
	public int execute(int src1, int src2)
	{
		return Math.abs(src1);
	}

}
