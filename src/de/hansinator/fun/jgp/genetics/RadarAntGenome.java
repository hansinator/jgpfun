package de.hansinator.fun.jgp.genetics;

import java.util.ArrayList;
import java.util.List;

import de.hansinator.fun.jgp.genetics.lgp.BaseMachine;
import de.hansinator.fun.jgp.genetics.lgp.EvoVM;
import de.hansinator.fun.jgp.genetics.lgp.OpCode;
import de.hansinator.fun.jgp.world.world2d.AntBody;
import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Body2d.Part;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.actors.TankMotor;
import de.hansinator.fun.jgp.world.world2d.senses.RadarSense;
import de.hansinator.fun.jgp.world.world2d.senses.WallSense;

public class RadarAntGenome extends Genome
{

	public RadarAntGenome(List<OpCode> program, int maxLength)
	{
		super(program, maxLength);
	}

	public RadarAntGenome(int maxLength)
	{
		super(maxLength);
	}

	@Override
	public Genome clone()
	{
		List<OpCode> p = new ArrayList<OpCode>(program.size());

		for (OpCode oc : program)
			p.add(oc.clone());

		return new RadarAntGenome(p, maxLength);
	}

	@Override
	public Organism2d synthesize()
	{
		// create organism
		Organism2d organism = new Organism2d(this);

		// create body and attach parts
		Body2d body = new AntBody(organism);
		final Part[] parts = new Part[] { new RadarSense(body), body.new OrientationSense(), body.new SpeedSense(), new WallSense(body), new TankMotor(body)};
		body.setParts(parts);

		// attach body
		organism.setBodies(new Body2d[] { body });

		// create and attach brain
		BaseMachine brain = new EvoVM(registerCount, organism.getInputCount(), program.toArray(new OpCode[program.size()]));
		organism.setVM(brain);

		// return assembled organism
		return organism;
	}
}
