package agents;

import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;

public class RandomCarAgent extends Car {

	//RED
	
	public RandomCarAgent(Grid<Object> space, Point origin, Road startRoad,Point destination,CarSerializable knowledge) {
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
