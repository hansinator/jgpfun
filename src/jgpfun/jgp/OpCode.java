package jgpfun.jgp;

import java.util.Random;
import jgpfun.jgp.operations.Operation;

public class OpCode {

    public int op;
    public int src1;
    public int src2;
    public int trg;
    public boolean immediate;
    Operation operation;

    public static OpCode randomOpCode(Random rnd) {
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


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.op;
        hash = 29 * hash + this.src1;
        hash = 29 * hash + this.src2;
        hash = 29 * hash + this.trg;
        hash = 29 * hash + (this.immediate ? 1 : 0);
        return hash;
    }

    
}
