package de.hansinator.jgpfun.genetics.lgp;

import de.hansinator.jgpfun.genetics.lgp.operations.OpAbs;
import de.hansinator.jgpfun.genetics.lgp.operations.OpAdd;
import de.hansinator.jgpfun.genetics.lgp.operations.OpDiv;
import de.hansinator.jgpfun.genetics.lgp.operations.OpMax;
import de.hansinator.jgpfun.genetics.lgp.operations.OpMin;
import de.hansinator.jgpfun.genetics.lgp.operations.OpMod;
import de.hansinator.jgpfun.genetics.lgp.operations.OpMov;
import de.hansinator.jgpfun.genetics.lgp.operations.OpMul;
import de.hansinator.jgpfun.genetics.lgp.operations.OpNeg;
import de.hansinator.jgpfun.genetics.lgp.operations.OpSqrt;
import de.hansinator.jgpfun.genetics.lgp.operations.OpSub;
import de.hansinator.jgpfun.genetics.lgp.operations.Operation;
import de.hansinator.jgpfun.world2d.Organism2d;

/**
 *
 * @author hansinator
 */
public abstract class BaseMachine {

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
                    new OpMov(), //
                //new OpInc(),
                //new OpDec(),
                // OpBranchLt(),
                //new OpBranchGt()
                //new JumpOp(),
                //new JumpTarg()
                };
    }

    static Operation[] ops;

    public int[] regs;


    protected static OpCode[] normalizeProgram(OpCode[] program, int numRegs) {
        for (int i = 0; i < program.length; i++) {
            OpCode curop = program[i];

            curop.src1 = Math.abs(curop.src1) % numRegs;
            curop.src2 = curop.immediate ? (curop.src2 / (int)Organism2d.intScaleFactor) : (Math.abs(curop.src2) % numRegs);
            curop.trg = Math.abs(curop.trg) % numRegs;
            curop.op = Math.abs(curop.op) % ops.length;
            curop.operation = ops[curop.op];
        }

        return program;
    }


    public abstract void run();


    public abstract int getProgramSize();

}
