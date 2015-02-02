package de.hansinator.fun.jgp.life.lgp.operations;

public class OpCmp implements Operation
{
	@Override
	public int execute(int src1, int src2)
	{
		if(src1 > src2) return 1;
		else if (src2 < src1) return -1;
		else return 0;
	}
}
