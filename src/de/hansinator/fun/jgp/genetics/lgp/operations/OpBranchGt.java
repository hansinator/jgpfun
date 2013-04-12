package de.hansinator.fun.jgp.genetics.lgp.operations;

/**
 * 
 * @author dahmen
 */
public class OpBranchGt implements Operation, BranchOperation
{

	@Override
	public int execute(int src1, int src2)
	{
		if (src1 > src2)
			return 1;

		return 0;
	}
}
