package de.hansinator.fun.jgp.life.lgp.operations;


/**
 * 
 * @author dahmen
 */
public class OpSin implements Operation, UnaryOperation
{
	private static double scaleFactor = (double)Integer.MAX_VALUE / (2.0 * Math.PI);
	
	@Override
	public int execute(int src1, int src2)
	{
		return (int) (Math.sin(src1 / scaleFactor) * scaleFactor);
	}

}
