package agents;

import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;

/**
 * It's an extended class of car just with the purpose of handling the debug
 * messages in a different way and to enable the choice of a different icon
 * in the repast display, such that it can be easily distinguished in the space 
 * from the other cars.
 * The color of this car is red.
 */
public class CarNoneLearning extends Car {
	
	/**
	 * Constructor.
	 * By default the learning mode is NONE.
	 * @param space
	 * @param origin
	 * @param startRoad
	 * @param destination
	 * @param knowledge
	 */
	public CarNoneLearning(Grid<Object> space, Point origin, Road startRoad,Point destination,CarSerializable knowledge) {
		super(space, origin, startRoad,destination, knowledge,LearningMode.NONE);	
	}
	
    @Override
	public void takeDown() {
    	super.takeDown();
    }

	@Override
	public String print() {
		String ret = "-- Random Car Agent Informations --\n";
		ret += "Origin : "+position.print()+"\n";
		ret += "Destination : "+destination.print()+"\n";
		return ret;
	}
    
	
}
