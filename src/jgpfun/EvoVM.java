package jgpfun;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 * @author hansinator
 */
public class EvoVM {

    int src1, src2, trg;
    static Method[] ops = new Method[1];

    static {
        ArrayList<Method> l = new ArrayList<Method>();
        for (Method m : EvoVM.class.getDeclaredMethods()) {
            if (m.getName().startsWith("op")) {
                l.add(m);
            }
        }

        ops = l.toArray(ops);
    }

    void opAdd() {
        trg = src1 + src2;
    }

    void opSub() {
        trg = src1 - src2;
    }

    void opMul() {
        trg = src1 * src2;
    }

    void opDiv() {
        if (src2 != 0) {
            trg = src1 / src2;
        } else {
            trg = Integer.MAX_VALUE;
        }
    }

    void opMod() {
        if (src2 != 0) {
            trg = src1 % src2;
        } else {
            trg = Integer.MAX_VALUE;
        }
    }
    
    OpCode[] program;
    int[] regs;

    public EvoVM(int numregs, OpCode[] program) {
        this.program = program;
        regs = new int[numregs];

        for (int pc = 0; pc < program.length; pc++) {
            OpCode curop = program[pc];

            curop.src1 = Math.abs(curop.src1) % numregs;
            if (!curop.immediate) {
                curop.src2 = Math.abs(curop.src2) % numregs;
            }
            curop.trg = Math.abs(curop.trg) % numregs;
            curop.op = Math.abs(curop.op) % ops.length;
        }
    }

    public void run() throws Exception {
        for (int pc = 0; pc < program.length; pc++) {
            execute(pc);
        }
    }

    public void execute(int pc) throws Exception {
        OpCode curop = program[pc];

        src1 = regs[curop.src1];
        if (curop.immediate) {
            src2 = curop.src2;
        } else {
            src2 = regs[curop.src2];
        }

        ops[curop.op].invoke(this);

        regs[curop.trg] = trg;
    }
}
