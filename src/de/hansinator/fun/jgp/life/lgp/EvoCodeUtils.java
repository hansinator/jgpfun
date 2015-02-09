/*
 */
package de.hansinator.fun.jgp.life.lgp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hansinator.fun.jgp.life.lgp.operations.NoSourceOperation;
import de.hansinator.fun.jgp.life.lgp.operations.UnaryOperation;

/**
 * 
 * @author Hansinator
 */
class EvoCodeUtils
{

	private static final Object dummy = new Object();

	
	public static OpCode[] stripStructuralIntronCode(OpCode[] program, int inputRegisterCount, int outputRegisterCount)
	{
		Map<Integer, Object> effectiveRegisters = new HashMap<Integer, Object>();
		boolean[] markers = new boolean[program.length];
		List<OpCode> strippedProgram;
		OpCode memVal;
		
		// add the output registers to the effective registers
		for (int i = inputRegisterCount; i < (inputRegisterCount + outputRegisterCount); i++)
			effectiveRegisters.put(i, dummy);

		// do two passes
		// after the first pass all instructions that are not effective
		// are removed and the effectiveRegisters set contains all registers
		// that are used as input to the computation, including working registers.
		// If we re-add the output registers and remove the input registers, we
		// obtain a set that contains output registers and working registers.
		// if we run a second pass using this set, we effectively make the working
		// registers retain their value between executions and keep code-flows
		// that work on these registers
		for(int pass = 0; pass < 2; pass++)
		{
			// process the source bottom-up and mark all instructions whose
			// output register is not among the effective registers
			// if an instruction uses an effective register, remove the register
			// from the set and add the source operands as effective registers
			for (int i = program.length - 1; i >= 0; i--)
			{
				// fetch instruction
				memVal = program[i];
	
				// TODO: implement branch stuff
				// skip branches if the preceeding instruction was non-effective
				/*
				 * if ((instructionSet[opVal] == Instructions.OpBranchEq) ||
				 * (instructionSet[opVal] == Instructions.OpBranchGt) ||
				 * (instructionSet[opVal] == Instructions.OpBranchLt)) { if
				 * (!markers[Math.Min(i + 1, markers.Length - 1)]) { //mark the
				 * instruction as non-effective markers[i] = false; } else {
				 * sourceRegister1 = (UInt32)memVal.src1 % registerCount; if
				 * (!effectiveRegisters.Contains(sourceRegister1) && !immediate) {
				 * effectiveRegisters.Add(sourceRegister1, new Object()); }
				 * 
				 * sourceRegister2 = memVal.src2 % registerCount; if
				 * (!effectiveRegisters.Contains(sourceRegister2)) {
				 * effectiveRegisters.Add(sourceRegister2, new Object()); }
				 * 
				 * markers[i] = true; }
				 * 
				 * continue; }
				 */
	
				// see if target register is in effective registers
				if (effectiveRegisters.containsKey(memVal.trg.getValue()))
				{
					// now we should remove the target register from the set
					// and add the source operands
					effectiveRegisters.remove(memVal.trg.getValue());
	
					// if operation has sourcesmarkers
					if (!(memVal.operation instanceof NoSourceOperation))
					{
						// add source operand 1
						effectiveRegisters.put(memVal.src1.getValue(), dummy);
	
						// add source operand 2, if it is no immediate or unary operation
						if (!(memVal.immediate.getValue() || memVal.operation instanceof UnaryOperation))
							effectiveRegisters.put(memVal.src2.getValue(), dummy);
	
						// mark the instruction as effective
						markers[i] = true;
					}
				} else // mark the instruction as non-effective
					markers[i] = false;
			}
			
			// remove input registers and add output registers before doing another pass
			for (int i = 0; i < inputRegisterCount; i++)
				if (effectiveRegisters.containsKey(i))
					effectiveRegisters.remove(i);
			for (int i = inputRegisterCount; i < (inputRegisterCount + outputRegisterCount); i++)
				effectiveRegisters.put(i, dummy);
		}

		// create stripped program from marked instructions
		strippedProgram = new ArrayList<OpCode>();
		for (int i = 0; i < program.length; i++)
			if (markers[i])
				strippedProgram.add(program[i]);

		return strippedProgram.toArray(new OpCode[strippedProgram.size()]);
	}

}
