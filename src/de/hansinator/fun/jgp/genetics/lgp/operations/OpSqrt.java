package de.hansinator.fun.jgp.genetics.lgp.operations;

/**
 * 
 * @author dahmen
 */
public class OpSqrt implements Operation, UnaryOperation
{

	@Override
	public int execute(int src1, int src2)
	{
		return (int) Math.sqrt(src1);
	}

}
