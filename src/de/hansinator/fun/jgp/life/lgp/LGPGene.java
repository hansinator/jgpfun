package de.hansinator.fun.jgp.life.lgp;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import de.hansinator.fun.jgp.genetics.AbstractMutation;
import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.gui.ExecutionUnitGeneView;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * Genetic representation of an LGP program. Supports mutations
 * for insertion, removal and replacement of OpCodes to the program.
 * 
 * @author hansinator
 *
 */
public class LGPGene implements ExecutionUnit.Gene<World2d>
{
	private static final Random rnd = Settings.newRandomSource();
	
	// define chances for what mutation could happen in some sort of
	// percentage
	private static final int mutateIns = 22, mutateRem = 18, mutateRep = 20;
	
	static final int registerCount = Settings.getInt("registerCount");

	private final List<OpCode> program;

	private final int maxLength;
	
	private final List<IOUnit.Gene<ExecutionUnit<World2d>>> ioGenes = new ArrayList<IOUnit.Gene<ExecutionUnit<World2d>>>();
	
	private int exonSize = 0;
	
	private int inputCount = 0;
	
	private int outputCount = 0;
	
	// insert a random instruction at a random location
	private final Mutation mutationInsert = new AbstractMutation(mutateIns) {
		
		@Override
		public void mutate()
		{
			program.add(rnd.nextInt(program.size()), OpCode.randomOpCode(rnd));
		}
	};
	
	// remove a random instruction
	private final Mutation mutationRemove = new AbstractMutation(mutateRem) {
		
		@Override
		public void mutate()
		{
			program.remove(rnd.nextInt(program.size()));
		}
	};
	
	// replace a random instruction
	private final Mutation mutationReplace = new AbstractMutation(mutateRep) {
		
		@Override
		public void mutate()
		{
			program.set(rnd.nextInt(program.size()), OpCode.randomOpCode(rnd));
		}
	};
	
	private final Mutation[] mutations = { mutationInsert, mutationRemove, mutationReplace };
	

	public static LGPGene randomGene(int maxLength)
	{
		int size = rnd.nextInt(maxLength - 200) + 201;
		List<OpCode> program = new ArrayList<OpCode>(size);

		for (int i = 0; i < size; i++)
			program.add(OpCode.randomOpCode(rnd));

		return new LGPGene(program, maxLength);
	}


	private LGPGene(List<OpCode> program, int maxLength)
	{
		this.program = program;
		this.maxLength = maxLength;
	}

	@Override
	public LGPGene replicate()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.replicate());

		LGPGene lg = new LGPGene(p, maxLength);
		
		lg.inputCount = inputCount;
		lg.outputCount = outputCount;
		for(IOUnit.Gene<ExecutionUnit<World2d>> bg : ioGenes)
			lg.ioGenes.add(bg.replicate());
		
		return lg;
	}

	@Override
	public ExecutionUnit<World2d> express(World2d context)
	{
		int i, o, x;
		
		// create IO port arrays
		SensorInput[] inputs = (inputCount==0)?SensorInput.emptySensorInputArray:new SensorInput[inputCount];
		ActorOutput[] outputs = (outputCount==0)?ActorOutput.emptyActorOutputArray:new ActorOutput[outputCount];
		
		// create ExecutionUnit
		LGPMachine<World2d> eu = new BranchVM<World2d>(registerCount, inputs.length, program.toArray(new OpCode[program.size()]));
		
		// update exon size
		exonSize = eu.getProgramSize();
		
		// create IO
		@SuppressWarnings("unchecked")
		IOUnit<ExecutionUnit<World2d>>[] bodies = new IOUnit[ioGenes.size()];
		for(i = 0; i < ioGenes.size(); i++)
			bodies[i] = ioGenes.get(i).express(eu);

		// attach bodies
		eu.setIOUnits(bodies);

		// collect IO ports
		for(x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			// collect inputs
			for (SensorInput in : bodies[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : bodies[x].getOutputs())
				outputs[o++] = out;
		}

		// attach IO ports to brain
		eu.setInputs(inputs);
		eu.setOutputs(outputs);

		// attach to evaluation state
		eu.setExecutionContext(context);

		// return assembled organism
		return eu;
	}


	@SuppressWarnings("rawtypes")
	public List<Gene> getChildren() {
		List<Gene> list = new ArrayList<Gene>();
		list.addAll(program);
		return list;
	}


	@Override
	public int getExonSize()
	{
		return exonSize;
	}


	@Override
	public int getSize()
	{
		return program.size();
	}

	@Override
	public Mutation[] getMutations()
	{
		// if we have the max possible opcodes, we can't add a new one,
		// so adjust insert mutation chance accordingly
		mutationInsert.setMutationChance((program.size() >= maxLength)?0:mutateIns);
		
		// if we have only 4 opcodes left, don't delete more
		if (program.size() < 5)
		{
			mutationRemove.setMutationChance(0);
			
			// higher ins chance test: when prog is too small, mutation tends to vary the same loc multiple times,
			// so insert some more
			mutationInsert.setMutationChance(100);
		}
		else mutationRemove.setMutationChance(mutateRem);
				
		return mutations;
	}
	
	public void addIOGene(IOUnit.Gene<ExecutionUnit<World2d>> gene)
	{
		ioGenes.add(gene);
		inputCount += gene.getInputCount();
		outputCount += gene.getOutputCount();
	}


	@Override
	public ExecutionUnitGeneView getView()
	{
		return new View();
	}
	
	
	private class View extends JDialog implements ExecutionUnitGeneView
	{
		public View()
		{	
			int i = 0;
			
			// make a tabke model for the program
			String[] columns = new String[]{"loc", "op", "src1", "src2", "trg", "immediate"};
			DefaultTableModel tableModel = new DefaultTableModel(0, columns.length);
			tableModel.setColumnIdentifiers(columns);
			for(OpCode o : EvoCodeUtils.stripStructuralIntronCode(EvoVM.normalizeProgram(program.toArray(new OpCode[program.size()]), registerCount), registerCount, inputCount))
				tableModel.addRow(new String[] { "" + i++ , LGPMachine.ops[o.op.getValue()].getClass().getSimpleName(), o.src1.getValue().toString(), o.src2.getValue().toString(), o.trg.getValue().toString(), o.immediate.getValue().toString() });
			
			// create table 
			JTable programTable = new JTable(tableModel);
			programTable.setFillsViewportHeight(true);
			programTable.setColumnSelectionAllowed(false);
			programTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			// add table to view
			JScrollPane scrollPane = new JScrollPane(programTable);
			scrollPane.setPreferredSize(new Dimension(800, 600));
			Container contentPane = getContentPane();
			contentPane.add(scrollPane);
			
			pack();
		}
	}
}
