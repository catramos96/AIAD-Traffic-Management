package agents;

import cityStructure.CityMap;
import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;

public class MonitoredCarAgent extends CarAgent {

	//cor azul
	
	public MonitoredCarAgent(Grid<Object> space, CityMap map, Point origin, Point destination, Road startRoad,boolean enableCityLearning) {
		super(space, map, origin, destination, startRoad, enableCityLearning);	
	}

}
