/*
 */
package jgpfun.genetics.lgp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgpfun.genetics.lgp.operations.Operation;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
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

    final static String cname = "jgpfun.genetics.lgp.EvoVM2-";

    final static String iname = cname.replace('.', '/');

    final static String oldname = "jgpfun.genetics.lgp.EvoVM2".replace('.', '/');

    private static int ii = 0;


    public static EvoVM2 compile(final int numRegs, OpCode[] program) throws IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        final OpCode[] prg = EvoVM2.stripStructuralIntronCode(EvoVM2.normalizeProgram(program, numRegs), numRegs);

        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, iname + ii, null, oldname, null);

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        Method m = Method.getMethod("void <init> ()");
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, mv);
        mg.loadThis();
        mg.invokeConstructor(Type.getType(Object.class), m);
        mg.loadThis();
        mg.push(numRegs);
        mg.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
        mg.visitFieldInsn(Opcodes.PUTFIELD, "L" + iname + ii + ";", "regs", "[I");
        mg.returnValue();
        mg.endMethod();
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "run", "()V", null, null);
        mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, Method.getMethod("void run ()"), mv);
        final Type owner = Type.getType("L" + iname + ii + ";");

        //prepare local variables
        final int regs = mg.newLocal(Type.getType("[I"));
        final int ops = mg.newLocal(Type.getType("[Ljgpfun/genetics/lgp/operations/Operation;"));
        mg.loadThis();
        mg.getField(owner, "regs", Type.getType("[I"));
        mg.getStatic(owner, "ops", Type.getType("[Ljgpfun/genetics/lgp/operations/Operation;"));
        mg.storeLocal(ops);
        mg.storeLocal(regs);
        final int[] lregs = new int[numRegs];
        for(int i = 0; i < numRegs; i++) {
            lregs[i] = mg.newLocal(Type.getType("I"));
            mg.loadLocal(regs);
            mg.push(i);
            mg.arrayLoad(Type.INT_TYPE);
            mg.storeLocal(lregs[i]);
        }

        //compile source to bytecode here
        for (OpCode op : prg) {
            //get operand
            mg.loadLocal(ops);
            mg.push(op.op);
            mg.arrayLoad(Type.getType(Operation.class));

            // get src 2 value
            if (op.immediate) {
                mg.push(op.src2);
            } else {
                mg.loadLocal(lregs[op.src2]);
            }

            // get src 1 value
            mg.loadLocal(lregs[op.src1]);

            //invoke operations
            mg.invokeInterface(Type.getType(Operation.class), Method.getMethod("int execute(int,int)"));

            //store trg
            mg.storeLocal(lregs[op.trg]);
        }

        for(int i = 0; i < numRegs; i++) {
            mg.loadLocal(regs);
            mg.push(i);
            mg.loadLocal(lregs[i]);
            mg.arrayStore(Type.INT_TYPE);

        }

        mg.returnValue();
        mg.endMethod();

        cw.visitEnd();

        //new ClassReader(cw.toByteArray()).accept(new ASMifierClassVisitor(new PrintWriter(System.out)), numRegs);
        try {
            return (EvoVM2) loadClass(cw.toByteArray(), cname + (ii++)).newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(EvoVM2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(EvoVM2.class.getName()).log(Level.SEVERE, null, ex);
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

}
