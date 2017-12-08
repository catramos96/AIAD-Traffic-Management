package agents;

import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;

public class RandomCarAgent extends CarAgent {

	//cor vermelho
	
	public RandomCarAgent(Grid<Object> space, Point origin, Road startRoad,CarSerializable knowledge) {
		super(space, origin, startRoad, knowledge);	
	}
	
    @Override
	public void takeDown() {
    	super.takeDown();
    }

	
}
