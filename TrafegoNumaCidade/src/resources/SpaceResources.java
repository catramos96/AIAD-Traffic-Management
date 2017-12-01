package resources;

import agents.CarAgent;
import agents.Semaphore;
import agents.SemaphoreRed;
import agents.SemaphoreYellow;
import repast.simphony.space.grid.Grid;

public class SpaceResources {

	
	public static boolean hasRedOrYellowSemaphore(Grid<Object> space, Point location){

		if(searchForObject(space, location, SemaphoreRed.class) == null &&
		searchForObject(space, location, SemaphoreYellow.class) == null)
			return false;
		else
			return true;
	}
	
	public static boolean hasCar(Grid<Object> space, Point location){
		
		if(searchForObject(space, location, CarAgent.class) != null)
			return true;
		else
			return false;
	}
	
	public static <T> T searchForObject(Grid<Object> space, Point location, Class<T> Class){
	
		for(Object o : space.getObjectsAt(location.toArray())){
			if(o.getClass().equals(Class)){
				return (T) o;
			}
		}
		return null;
	}
}
