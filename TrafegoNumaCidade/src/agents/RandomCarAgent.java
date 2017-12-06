package agents;

import cityStructure.CityMap;
import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;

public class RandomCarAgent extends CarAgent {

	//cor vermelho
	
	public RandomCarAgent(Grid<Object> space, CityMap map, Point origin, Point destination, Road startRoad,boolean enableCityLearning) {
		super(space, map, origin, destination, startRoad, false);	
	}
	
    @Override
	public void takeDown() {
    	super.takeDown();
    }

	
}
