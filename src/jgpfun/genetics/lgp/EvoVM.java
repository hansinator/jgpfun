package jgpfun.genetics.lgp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jgpfun.genetics.lgp.operations.OpAbs;
import jgpfun.genetics.lgp.operations.OpAdd;
import jgpfun.genetics.lgp.operations.OpDiv;
import jgpfun.genetics.lgp.operations.OpMax;
import jgpfun.genetics.lgp.operations.OpMin;
import jgpfun.genetics.lgp.operations.OpMod;
import jgpfun.genetics.lgp.operations.OpMov;
import jgpfun.genetics.lgp.operations.OpMul;
import jgpfun.genetics.lgp.operations.OpNeg;
import jgpfun.genetics.lgp.operations.OpSqrt;
import jgpfun.genetics.lgp.operations.OpSub;
import jgpfun.genetics.lgp.operations.Operation;
import jgpfun.genetics.lgp.operations.UnaryOperation;

/**
 *
 * @author hansinator
 */
public class EvoVM {

    static Operation[] ops;

    int pc;


    static {
        //compatible instruction set
        //ops = new Operation[]{new OpAdd(), new OpSub(), new OpMul(), new OpDiv(), new OpMod()};

        //extended instruction set
        ops = new Operation[]{
                    new OpAdd(),
                    new OpSub(),
                    new OpMul(),
                    new OpDiv(),
                    new OpMod(),
                    new OpSqrt(),
                    new OpNeg(),
                    new OpMin(),
                    new OpMax(),
                    new OpAbs(),
                    //new OpSin(),
                    new OpMov(), //new OpInc(),
                //new OpDec(),
                // OpBranchLt(),
                //new OpBranchGt()
                //new JumpOp(),
                //new JumpTarg()
                };
    }

    private final OpCode[] program;

    public int[] regs;

    public EvoVM(int numRegs, OpCode[] program) {
        regs = new int[numRegs];
        
        //normalize program and strip strctural intron code portions
        this.program = stripStructuralIntronCode(normalizeProgram(program, numRegs));
    }


    protected final OpCode[] normalizeProgram(OpCode[] program, int numRegs) {
        for (int i = 0; i < program.length; i++) {
            OpCode curop = program[i];

            curop.src1 = Math.abs(curop.src1) % numRegs;
            if (!curop.immediate) {
                curop.src2 = Math.abs(curop.src2) % numRegs;
            } else {
                curop.src2 /= 65535;
            }
            curop.trg = Math.abs(curop.trg) % numRegs;
            curop.op = Math.abs(curop.op) % ops.length;
            curop.operation = ops[curop.op];
        }

        return program;
    }


    public void run() {
        pc = 0;
        while (pc < program.length) {
            execute(pc++);
        }
    }


    public void execute(int pc) {
        OpCode curop = program[pc];
        
        /*if (op instanceof BranchOperation) {
        if(op.execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2])) != 1) {
        pc++;
        }*/
        /*} else if(op instanceof JumpOp) {
        //fast forward until the next jumptarg is found or program end is reached
        do {
        pc++;
        } while ((pc < program.length) && !(ops[program[pc].op] instanceof JumpTarg));
        } else if(op instanceof JumpTarg) {
        //do nothing*/
        //} else {
        //execute the operation
        regs[curop.trg] = curop.operation.execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2]));
        //}
    }


    //strip unused code portions
    protected final OpCode[] stripStructuralIntronCode(OpCode[] program) {
        Map<Integer, Object> effectiveRegisters = new HashMap<Integer, Object>();
        Boolean[] markers = new Boolean[program.length];
        List<OpCode> strippedProgram;
        OpCode memVal;

        //add the output registers to the effective registers
        //in the current case these are magically number 3 and 4,
        //but this may change, beware!
        effectiveRegisters.put(3, new Object());
        effectiveRegisters.put(4, new Object());

        //also add the temp registers... oops!
        //they are necessary to compute temporary values that survive
        //from round to round
        //if we don't include them, they still persist and may have
        //random effects on functional code
        //-> this means if we forget them, we may strip functional code :(
        //TODO: see how temp registers are treated in the current implementation
        for (int i = 6; i < regs.length; i++) {
            effectiveRegisters.put(i, new Object());
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

                //special treatment for no source operations - we don't have one yet
                /*
                //mark rnd as effective and continue
                if (instructionSet[opVal] == Instructions.OpRnd)
                {
                markers[i] = true;
                continue;
                }*/

                //add source operand 1
                if (!effectiveRegisters.containsValue(memVal.src1)) {
                    effectiveRegisters.put(memVal.src1, new Object());
                }

                //add source operand 2, if it is no immediate or unary operation
                if (!memVal.immediate && ops[memVal.op] instanceof UnaryOperation) {
                    //add source operand 2
                    if (!effectiveRegisters.containsValue(memVal.src2)) {
                        effectiveRegisters.put(memVal.src2, new Object());
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


    public int getProgramSize() {
        return program.length;
    }
}
