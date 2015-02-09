package de.hansinator.fun.jgp.life.lgp;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.life.lgp.operations.OpAbs;
import de.hansinator.fun.jgp.life.lgp.operations.OpAdd;
import de.hansinator.fun.jgp.life.lgp.operations.OpBranchGt;
import de.hansinator.fun.jgp.life.lgp.operations.OpBranchLt;
import de.hansinator.fun.jgp.life.lgp.operations.OpCmp;
import de.hansinator.fun.jgp.life.lgp.operations.OpDiv;
import de.hansinator.fun.jgp.life.lgp.operations.OpMax;
import de.hansinator.fun.jgp.life.lgp.operations.OpMin;
import de.hansinator.fun.jgp.life.lgp.operations.OpMod;
import de.hansinator.fun.jgp.life.lgp.operations.OpMov;
import de.hansinator.fun.jgp.life.lgp.operations.OpMul;
import de.hansinator.fun.jgp.life.lgp.operations.OpNeg;
import de.hansinator.fun.jgp.life.lgp.operations.OpSin;
import de.hansinator.fun.jgp.life.lgp.operations.OpSqrt;
import de.hansinator.fun.jgp.life.lgp.operations.OpSub;
import de.hansinator.fun.jgp.life.lgp.operations.Operation;
import de.hansinator.fun.jgp.world.World;

/**
 * 
 * @author hansinator
 */
public abstract class LGPMachine<E extends World> implements ExecutionUnit<E>
{
	/*
	 * Notes 31.12.2014
	 * 
	 * I am not happy with this mix of organism and machine functions. Though
	 * one machine is on top of the IO hierarchy, it and similar classes should
	 * not be burdened to implement IO management logic.
	 * 
	 * Also I don't like the fitness evaluator being attached to this class.
	 * Fitness evaluation should be decoupled from the function of the unit
	 * to be evaluated. Furthermore the getFitness path should not be through
	 * any element of the evaluation unit, as the fitness value is something
	 * that is inherent to the genome or more specifically to a pair of genome
	 * and a concrete evaluation enviroment.
	 * 
	 */

	// compatible instruction set
	// static Operation[] ops = new Operation[]{new OpAdd(), new OpSub(), new
	// OpMul(), new
	// OpDiv(), new OpMod()};

	// extended instruction set
	static Operation[] ops = new Operation[] { new OpAdd(), new OpSub(), new OpMul(), new OpDiv(), new OpMod(),
			new OpSqrt(), new OpNeg(), new OpMin(), new OpMax(), new OpAbs(), new OpCmp(),
			new OpSin(),
			new OpMov(), //
	// new OpInc(),
	// new OpDec(),
	// new OpBranchLt(), new OpBranchGt()
	// new JumpOp(),
	// new JumpTarg()
	};

	protected final Instruction[] program;

	public int[] regs;
	
	@SuppressWarnings("unchecked")
	private IOUnit<ExecutionUnit<E>>[] ioUnits = IOUnit.emptyIOUnitArray;

	protected SensorInput[] inputs = SensorInput.emptySensorInputArray;

	protected ActorOutput[] outputs = ActorOutput.emptyActorOutputArray;
	
	private E executionContext;
	
	protected static class Instruction
	{
		int op, src1, src2, trg;
		boolean immediate;
		Operation operation;
		
		public Instruction(int op, int src1, int src2, int trg, boolean immediate)
		{
			this.op = op;
			this.src1 = src1;
			this.src2 = src2;
			this.trg = trg;
			this.immediate = immediate;
			operation = ops[op];
		}
	}
	

	public LGPMachine(int numRegs, OpCode[] program)
	{
		this.regs = new int[numRegs];
		this.program = new Instruction[program.length];
		
		int i = 0;
		for(OpCode op : program)
			this.program[i++] = new Instruction(op.op.getValue(), op.src1.getValue(), op.src2.getValue(), op.trg.getValue(), op.immediate.getValue());
	}
	
	protected abstract void step();
	
	/**
	 * Evaluate this program
	 */
	//XXX i need some computation graph to replace this call sequence
	public void execute()
	{
		// prepare sensor readings
		for (IOUnit<ExecutionUnit<E>> u : ioUnits)
			u.sampleInputs();

		step();

		// apply outputs (move motor etc)
		for (IOUnit<ExecutionUnit<E>> u : ioUnits)
			u.applyOutputs();
	}

	public void setIOUnits(IOUnit<ExecutionUnit<E>>[] ioUnits)
	{
		this.ioUnits = ioUnits;
	}

	@Override
	public IOUnit<ExecutionUnit<E>>[] getIOUnits()
	{
		return ioUnits;
	}

	@Override
	public void setInputs(SensorInput[] inputs)
	{
		this.inputs = inputs;
	}

	@Override
	public void setOutputs(ActorOutput[] outputs)
	{
		this.outputs = outputs;
	}

	@Override
	public int getInputCount()
	{
		return inputs.length;
	}

	@Override
	public int getProgramSize()
	{
		return program.length;
	}

	@Override
	public void setExecutionContext(E executionContext)
	{
		this.executionContext = executionContext;
		
		// attach bodies to world state
		for (int x = 0; x < ioUnits.length; x++)
			ioUnits[x].attachEvaluationState(this);
	}
	
	@Override
	public E getExecutionContext()
	{
		return executionContext;
	}
	
	public static OpCode[] normalizeProgram(OpCode[] program, int numRegs)
	{
		for (int i = 0; i < program.length; i++)
		{
			OpCode curop = program[i];

			curop.src1.setValue(Math.abs(curop.src1.getValue()) % numRegs);
			curop.src2.setValue(curop.immediate.getValue() ? curop.src2.getValue()
					: (Math.abs(curop.src2.getValue()) % numRegs));
			curop.trg.setValue(Math.abs(curop.trg.getValue()) % numRegs);
			curop.op.setValue(Math.abs(curop.op.getValue()) % ops.length);
			curop.operation = ops[curop.op.getValue()];
		}

		return program;
	}

}
