package de.hansinator.fun.jgp.genetics.lgp.operations;

import de.hansinator.fun.jgp.simulation.Simulator;

/**
 * 
 * @author dahmen
 */
public class OpSin implements Operation, UnaryOperation
{

	@Override
	public int execute(int src1, int src2)
	{
		return (int) (Math.sin(src1 / Simulator.intScaleFactor) * Simulator.intScaleFactor);
	}

}
