package jgpfun.jgp;

import jgpfun.jgp.operations.BranchOperation;
import jgpfun.jgp.operations.JumpOp;
import jgpfun.jgp.operations.JumpTarg;
import jgpfun.jgp.operations.OpAbs;
import jgpfun.jgp.operations.OpAdd;
import jgpfun.jgp.operations.OpBranchGt;
import jgpfun.jgp.operations.OpBranchLt;
import jgpfun.jgp.operations.OpDec;
import jgpfun.jgp.operations.OpDiv;
import jgpfun.jgp.operations.OpInc;
import jgpfun.jgp.operations.OpMax;
import jgpfun.jgp.operations.OpMin;
import jgpfun.jgp.operations.OpMod;
import jgpfun.jgp.operations.OpMov;
import jgpfun.jgp.operations.OpMul;
import jgpfun.jgp.operations.OpNeg;
import jgpfun.jgp.operations.OpSin;
import jgpfun.jgp.operations.OpSqrt;
import jgpfun.jgp.operations.OpSub;
import jgpfun.jgp.operations.Operation;

/**
 *
 * @author hansinator
 */
public class EvoVM2 {

    static Operation[] ops;

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
                    new OpMov(),
                    new OpInc(),
                    new OpDec(),
                    new OpBranchLt(),
                    new OpBranchGt()
                    //new JumpOp(),
                    //new JumpTarg()
                };
    }
    OpCode[] program;
    public int[] regs;

    public EvoVM2(int numregs, OpCode[] program) {
        this.program = program;
        regs = new int[numregs];

        for (int pc = 0; pc < program.length; pc++) {
            OpCode curop = program[pc];

            curop.src1 = Math.abs(curop.src1) % numregs;
            if (!curop.immediate) {
                curop.src2 = Math.abs(curop.src2) % numregs;
            } else {
                curop.src2 /= 65535;
            }
            curop.trg = Math.abs(curop.trg) % numregs;
            curop.op = Math.abs(curop.op) % ops.length;
        }
    }
    int pc;

    public void run() throws Exception {
        pc = 0;
        while (pc < program.length) {
            execute(pc++);
        }
    }

    public void execute(int pc) throws Exception {
        OpCode curop = program[pc];
        Operation op = ops[curop.op];

        if (op instanceof BranchOperation) {
            if(op.execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2])) != 1) {
                pc++;
            }
        /*} else if(op instanceof JumpOp) {
            //fast forward until the next jumptarg is found or program end is reached
            do {
                pc++;
            } while ((pc < program.length) && !(ops[program[pc].op] instanceof JumpTarg));
        } else if(op instanceof JumpTarg) {
            //do nothing*/
        } else {
            //execute the operation
            regs[curop.trg] = op.execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2]));
        }
    }
}
