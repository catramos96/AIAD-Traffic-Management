package resources;

import java.util.ArrayList;

import agents.Car;
import agents.MonitoredCarAgent;
import agents.RandomCarAgent;
import agents.Semaphore;
import agents.SemaphoreRed;
import agents.SemaphoreYellow;
import repast.simphony.space.grid.Grid;
import sajas.core.AID;

public class SpaceResources {

	public static enum PassageType {Road, Intersection, Out};
	public static final Point REST_CELL = new Point(0,0);
	public static final int INFINITE = 99999;
	
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
	
	public static ArrayList<AID> getCarsAID(Grid<Object> space){
		ArrayList<AID> cars = new ArrayList<AID>();
		
		for(Object o : space.getObjects()){
			if(o.getClass().equals(Car.class))
				cars.add((AID) ((Car)o).getAID());
		}
		
		return cars;		
	}
	
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
	
	public static Car hasCar(Grid<Object> space, Point location){
		Car temp = searchForObject(space, location, MonitoredCarAgent.class);
		if(temp == null)
			return searchForObject(space, location, RandomCarAgent.class);
		return temp;
	}
	
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
