/*
 */
package jgpfun.genetics.lgp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgpfun.genetics.lgp.operations.Operation;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 *
 * @author Hansinator
 */
public class EvoCompiler {

    static final String CLASS_NAME = CompiledVM.class.getName() + "-";

    static final String INTERNAL_NAME = CLASS_NAME.replace('.', '/');

    static final String SUPER_INTERNAL_NAME = CompiledVM.class.getName().replace('.', '/');

    static final Type OP_TYPE = Type.getType(Operation.class);

    static final Type OP_ARRAY_TYPE = Type.getType("[Ljgpfun/genetics/lgp/operations/Operation;");

    static final Type INT_ARRAY_TYPE = Type.getType("[I");

    static final Method EXECUTE_METHOD = Method.getMethod("int execute(int,int)");

    private static int ii = 0;


    public static BaseMachine compile(final int numRegs, OpCode[] program) throws IOException {
        final OpCode[] prg = EvoCodeUtils.stripStructuralIntronCode(BaseMachine.normalizeProgram(program, numRegs), numRegs, 0);

        //begin class
        final Type owner = Type.getType("L" + INTERNAL_NAME + ii + ";");
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, INTERNAL_NAME + ii, null, SUPER_INTERNAL_NAME, null);

        //write constructor
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        Method m = Method.getMethod("void <init> ()");
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, mv);
        mg.loadThis();
        mg.invokeConstructor(Type.getType(Object.class), m);
        mg.loadThis();
        mg.push(numRegs);
        mg.newArray(Type.INT_TYPE);
        mg.putField(owner, "regs", INT_ARRAY_TYPE);
        mg.loadThis();
        mg.push(prg.length);
        mg.putField(owner, "programSize", Type.INT_TYPE);
        mg.returnValue();
        mg.endMethod();
        mv.visitEnd();

        //write run method
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "run", "()V", null, null);
        mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, Method.getMethod("void run ()"), mv);

        //prepare local variables
        final int regs = mg.newLocal(INT_ARRAY_TYPE);
        final int ops = mg.newLocal(OP_ARRAY_TYPE);
        final int[] lregs = new int[numRegs];
        final int[] lops = new int[BaseMachine.ops.length];
        mg.loadThis();
        mg.getField(owner, "regs", INT_ARRAY_TYPE);
        mg.getStatic(owner, "ops", OP_ARRAY_TYPE);
        mg.storeLocal(ops);
        mg.storeLocal(regs);
        for (int i = 0; i < numRegs; i++) {
            lregs[i] = mg.newLocal(Type.INT_TYPE);
            mg.loadLocal(regs);
            mg.push(i);
            mg.arrayLoad(Type.INT_TYPE);
            mg.storeLocal(lregs[i]);
        }
        for (int i = 0; i < BaseMachine.ops.length; i++) {
            lops[i] = mg.newLocal(OP_TYPE);
            mg.loadLocal(ops);
            mg.push(i);
            mg.arrayLoad(OP_TYPE);
            mg.storeLocal(lops[i]);
        }

        //compile source to bytecode here
        for (OpCode op : prg) {
            //get operator
            mg.loadLocal(lops[op.op]);

            // get src 2 value
            if (op.immediate) {
                mg.push(op.src2);
            } else {
                mg.loadLocal(lregs[op.src2]);
            }

            // get src 1 value
            mg.loadLocal(lregs[op.src1]);

            //invoke operations
            mg.invokeInterface(OP_TYPE, EXECUTE_METHOD);

            //store trg
            mg.storeLocal(lregs[op.trg]);
        }

        //store results to register file
        for (int i = 0; i < numRegs; i++) {
            mg.loadLocal(regs);
            mg.push(i);
            mg.loadLocal(lregs[i]);
            mg.arrayStore(Type.INT_TYPE);

        }

        //end run method
        mg.returnValue();
        mg.endMethod();

        //end class
        cw.visitEnd();

        try {
            return (BaseMachine) loadClass(cw.toByteArray(), CLASS_NAME + (ii++)).newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(BaseMachine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BaseMachine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    private static Class loadClass(byte[] b, String name) {
        //override classDefine (as it is protected) and define the class.
        Class clazz = null;
        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            Class cls = Class.forName("java.lang.ClassLoader");
            java.lang.reflect.Method method =
                    cls.getDeclaredMethod("defineClass", new Class[]{String.class, byte[].class, int.class, int.class});

            // protected method invocaton
            method.setAccessible(true);
            try {
                Object[] args = new Object[]{name, b, new Integer(0), new Integer(b.length)};
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


    private static class CompiledVM extends BaseMachine {

        int programSize;

        @Override
        public void run() {
        }


        @Override
        public int getProgramSize() {
            return programSize;
        }
        
    }
}
