/*
 */
package jgpfun.genetics.lgp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jgpfun.genetics.lgp.operations.NoSourceOperation;
import jgpfun.genetics.lgp.operations.UnaryOperation;

/**
 *
 * @author Hansinator
 */
class EvoCodeUtils {

    private static final Object dummy = new Object();


    public static OpCode[] stripStructuralIntronCode(OpCode[] program, int registerCount, int inputRegisterCount) {
        Map<Integer, Object> effectiveRegisters = new HashMap<Integer, Object>();
        Boolean[] markers = new Boolean[program.length];
        List<OpCode> strippedProgram;
        OpCode memVal;

        //add the output registers to the effective registers
        //in the current case these are magically number 3 and 4,
        //but this may change, beware!
        effectiveRegisters.put(3, dummy);
        effectiveRegisters.put(4, dummy);

        //also add the temp registers... oops!
        //they are necessary to compute temporary values that survive
        //from round to round
        //if we don't include them, they still persist and may have
        //random effects on functional code
        //-> this means if we forget them, we may strip functional code :(
        for (int i = 6; i < registerCount; i++) {
            effectiveRegisters.put(i, dummy);
        }

        //process the source bottom-up and mark all instructions whose
        //output register is not among the effective registers
        //if an instruction uses an effective register, remove the register
        //from the set and add the source operands as effective registers
        for (int i = program.length - 1; i >= 0; i--) {
            //fetch instruction
            memVal = program[i];

            //TODO: implement branch stuff
            //skip branches if the preceeding instruction was non-effective
            /*if ((instructionSet[opVal] == Instructions.OpBranchEq) ||
            (instructionSet[opVal] == Instructions.OpBranchGt) ||
            (instructionSet[opVal] == Instructions.OpBranchLt))
            {
            if (!markers[Math.Min(i + 1, markers.Length - 1)])
            {
            //mark the instruction as non-effective
            markers[i] = false;
            }
            else
            {
            sourceRegister1 = (UInt32)memVal.src1 % registerCount;
            if (!effectiveRegisters.Contains(sourceRegister1) && !immediate)
            {
            effectiveRegisters.Add(sourceRegister1, new Object());
            }

            sourceRegister2 = memVal.src2 % registerCount;
            if (!effectiveRegisters.Contains(sourceRegister2))
            {
            effectiveRegisters.Add(sourceRegister2, new Object());
            }

            markers[i] = true;
            }

            continue;
            }*/

            //see if target register is in effective registers
            if (effectiveRegisters.containsKey(memVal.trg)) {
                //now we should remove the target register from the set
                //and add the source operands
                effectiveRegisters.remove(memVal.trg);

                //special treatment for no source operations
                if (memVal.operation instanceof NoSourceOperation) {
                    markers[i] = true;
                    continue;
                }

                //add source operand 1
                if (!effectiveRegisters.containsValue(memVal.src1)) {
                    effectiveRegisters.put(memVal.src1, dummy);
                }

                //add source operand 2, if it is no immediate or unary operation
                if (!(memVal.immediate || memVal.operation instanceof UnaryOperation)) {
                    //add source operand 2
                    if (!effectiveRegisters.containsValue(memVal.src2)) {
                        effectiveRegisters.put(memVal.src2, dummy);
                    }
                }

                //mark the instruction as effective
                markers[i] = true;
            } else {
                //mark the instruction as non-effective
                markers[i] = false;
            }
        }

        //create stripped program from marked instructions
        strippedProgram = new LinkedList<OpCode>();
        for (int i = 0; i < program.length; i++) {
            if (markers[i]) {
                //add the instruction
                strippedProgram.add(program[i]);
            }
        }

        return strippedProgram.toArray(new OpCode[strippedProgram.size()]);
    }

}
