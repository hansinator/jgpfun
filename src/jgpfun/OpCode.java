package jgpfun;

import java.util.Random;

public class OpCode {

    int op;
    int src1;
    int src2;
    int trg;
    boolean immediate;

    static OpCode randomOne(Random rnd) {
        OpCode oc = new OpCode();
        
        oc.op = rnd.nextInt();
        oc.src1 = rnd.nextInt();
        oc.src2 = rnd.nextInt();
        oc.trg = rnd.nextInt();
        oc.immediate = rnd.nextBoolean();

        return oc;
    }

    @Override
    public OpCode clone() {
        OpCode oc = new OpCode();

        oc.op = op;
        oc.src1 = src1;
        oc.src2 = src2;
        oc.trg = trg;
        oc.immediate = immediate;

        return oc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OpCode other = (OpCode) obj;
        if (this.op != other.op) {
            return false;
        }
        if (this.src1 != other.src1) {
            return false;
        }
        if (this.src2 != other.src2) {
            return false;
        }
        if (this.trg != other.trg) {
            return false;
        }
        if (this.immediate != other.immediate) {
            return false;
        }
        return true;
    }
}
