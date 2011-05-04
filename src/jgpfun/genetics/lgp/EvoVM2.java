package jgpfun.genetics.lgp;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 *
 * @author hansinator
 */
public abstract class EvoVM2 {

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

    private OpCode[] program;

    public int[] regs;


    public static EvoVM2 compile(final int numRegs, OpCode[] program) throws IOException {
        ClassReader cr = new ClassReader(EvoVM2.class.getCanonicalName());
        ClassWriter cw = new ClassWriter(cr, 0);
        final OpCode[] prg = stripStructuralIntronCode(normalizeProgram(program, numRegs), numRegs);

        cr.accept(new ClassAdapter(cw) {


            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                if(name.equals("regs")) {
                    super.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL, "regs", "[Ljava/lang/Integer]", null, "new int[" + numRegs + "]");
                }

                return super.visitField(access, name, desc, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int i, String name, String desc, String signature, String[] exceptions) {
                if (name.equals("run")) {
                    GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, Method.getMethod("void run()"), super.visitMethod(i, name, desc, signature, exceptions));

                    //compile source to bytecode here
                    for (OpCode op : prg) {
                        //get operand
                        mg.getStatic(Type.getType(EvoVM2.class), "ops", Type.getType("[Ljgpfun/genetics/lgp/operations/Operation];"));
                        mg.push(op.op);
                        mg.arrayLoad(Type.getType(Operation.class));

                        // get src 1 value
                        mg.getField(Type.getType(EvoVM2.class), "regs", Type.getType("[Ljava/lang/Integer];"));
                        mg.push(op.src1);
                        mg.arrayLoad(Type.INT_TYPE);

                        // get src 2 value
                        if(op.immediate)
                            mg.push(op.src2);
                        else {
                            mg.getField(Type.getType(EvoVM2.class), "regs", Type.getType("[Ljava/lang/Integer];"));
                            mg.push(op.src2);
                            mg.arrayLoad(Type.INT_TYPE);
                        }

                        mg.invokeVirtual(Type.getType(Operation.class), Method.getMethod("int execute(int,int)"));

                        //store trg
                        mg.getField(Type.getType(EvoVM2.class), "regs", Type.getType("[Ljava/lang/Integer];"));
                        mg.push(op.trg);
                        mg.arrayStore(Type.INT_TYPE);
                    }
                    mg.returnValue();
                    mg.endMethod();

                    return mg;
                }

                return super.visitMethod(i, name, desc, signature, exceptions);
            }

        }, 0);
        try {
            return (EvoVM2) loadClass(cw.toByteArray()).newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(EvoVM2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(EvoVM2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static int ii = 0;

     private static Class loadClass (byte[] b) {
    //override classDefine (as it is protected) and define the class.
    Class clazz = null;
    try {
      ClassLoader loader = ClassLoader.getSystemClassLoader();
      Class cls = Class.forName("java.lang.ClassLoader");
      java.lang.reflect.Method method =
        cls.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class });

      // protected method invocaton
      method.setAccessible(true);
      try {
        Object[] args = new Object[] { "jgpfun.genetics.lgp.EvoVM2", b, new Integer(0), new Integer(b.length)};
        clazz = (Class) method.invoke(loader, args);
      } finally {
        method.setAccessible(false);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return clazz;
  }


    protected static OpCode[] normalizeProgram(OpCode[] program, int numRegs) {
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


    abstract public void run();

    //strip unused code portions

    protected static OpCode[] stripStructuralIntronCode(OpCode[] program, int registerCount) {
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
        for (int i = 6; i < registerCount; i++) {
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
