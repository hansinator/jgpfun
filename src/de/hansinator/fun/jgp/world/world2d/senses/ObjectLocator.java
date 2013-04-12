package de.hansinator.fun.jgp.world.world2d.senses;

import de.hansinator.fun.jgp.world.world2d.Body2d;
import de.hansinator.fun.jgp.world.world2d.Food;
import de.hansinator.fun.jgp.world.world2d.Organism2d;
import de.hansinator.fun.jgp.world.world2d.World2d;
import de.hansinator.fun.jgp.world.world2d.World2dObject;

/**
 * Sensory input to locate objects in world.
 * Currently only locates food objects.
 * 
 * @author Hansinator
 *
 */
public class ObjectLocator {
	
    private final Body2d body;

    private final World2d world;

	public Food target;
	
	private double objDist;
	
	
    public ObjectLocator(Body2d body, World2d world) {
        this.body = body;
        this.world = world;
    }
	
    
	public void locate() {
		target = world.findNearestFood(body);
		objDist = World2dObject.distance(target, Math.round((float) body.x),
				Math.round((float) body.y));
	}
	
	
	public final SensorInput senseDirX = new SensorInput() {

		@Override
		public int get() {
			return (int) (((target.x - body.x) / objDist) * Organism2d.intScaleFactor);
		}

	};

	public final SensorInput senseDirY = new SensorInput() {

		@Override
		public int get() {
			return (int) (((target.y - body.y) / objDist) * Organism2d.intScaleFactor);
		}

	};

	public final SensorInput senseDist = new SensorInput() {

		@Override
		public int get() {
			return (int) (objDist * Organism2d.intScaleFactor);
		}

	};

	public final SensorInput senseDist2 = new SensorInput() {

		@Override
		public int get() {
			return Math.round((float) objDist);
		}

	};
}
