package resources;

import agents.Semaphore;
import agents.SemaphoreRed;
import agents.SemaphoreYellow;
import repast.simphony.space.grid.Grid;

public class SpaceResources {

	
	public static Semaphore hasRedOrYellowSemaphore(Grid<Object> space, Point location){

		for(Object o : space.getObjectsAt(location.toArray())){
			
			if(		o.getClass().equals(SemaphoreYellow.class) ||
					o.getClass().equals(SemaphoreRed.class))
				return (Semaphore)o;
		}
		
		return null;
	}
}
