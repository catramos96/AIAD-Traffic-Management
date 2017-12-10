package resources;

import java.util.ArrayList;

import agents.Car;
import agents.CarMonitored;
import agents.CarNoneLearning;
import agents.CarShortLearning;
import agents.Semaphore;
import agents.SemaphoreRed;
import agents.SemaphoreYellow;
import repast.simphony.space.grid.Grid;
import sajas.core.AID;

/**
 * Auxiliary Class that provides methods and constants about the space.
 *
 */
public class SpaceResources {

	public static enum PassageType {Road, Intersection, Out};
	public static final Point REST_CELL = new Point(0,0);
	public static final int INFINITE = 99999;
	
	/**
	 * Gets the max number of cars that can be in a road with length roadLenght
	 * without being consider transit.
	 * If this number is surpass, then it's consider transit in that road.
	 * @param roadlength
	 * @return
	 */
	public static int getMaxCarStopped(int roadlength){
		
		double value = 0;
		
		if(roadlength <= 3)
			value = 3;
		else if(roadlength <= 5)
			value = roadlength - 1;
		else if(roadlength <= 8)
			value = ((double)roadlength - ((double)roadlength)/4);
		else
			value = ((double)roadlength - ((double)roadlength)/3);
		
		return (int) Math.round(value);
	}
	
	/**
	 * Gets all the cars AID in a given space.
	 * @param space
	 * @return
	 */
	public static ArrayList<AID> getCarsAID(Grid<Object> space){
		ArrayList<AID> cars = new ArrayList<AID>();
		
		for(Object o : space.getObjects()){
			if(o.getClass().equals(Car.class))
				cars.add((AID) ((Car)o).getAID());
		}
		
		return cars;		
	}
	
	/**
	 * Returns a Yellow or Green semaphore if there is any in a given space at a
	 * given location. If there are none, it will return null.
	 * @param space
	 * @param location
	 * @return
	 */
	public static Semaphore hasRedOrYellowSemaphore(Grid<Object> space, Point location){
		Semaphore s1 = searchForObject(space, location, SemaphoreRed.class);
		Semaphore s2 = searchForObject(space, location, SemaphoreYellow.class);
		
		
		if(s1 == null && s2 == null)
			return null;
		else if(s1 == null)
			return s2;
		else
			return s1;
	}
	
	/**
	 * Returns a Car if there is any in a given space at a given location. If there
	 * are nine, it will return null.
	 * @param space
	 * @param location
	 * @return
	 */
	public static Car hasCar(Grid<Object> space, Point location){
		for(Object o : space.getObjectsAt(location.toArray())){
			if(o.getClass().equals(CarMonitored.class) ||
					o.getClass().equals(CarNoneLearning.class) ||
					o.getClass().equals(CarShortLearning.class)){
				return (Car) o;
			}
		}
		return null;
	}
	
	/**
	 * Returns a Class type object if there are any at the given space and at the
	 * given location. If there are none, then it will return null.
	 * @param space
	 * @param location
	 * @param Class
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T searchForObject(Grid<Object> space, Point location, Class<T> Class){
		
		for(Object o : space.getObjectsAt(location.toArray())){
			if(o.getClass().equals(Class)){
				return (T) o;
			}
		}
		return null;
	}
}
