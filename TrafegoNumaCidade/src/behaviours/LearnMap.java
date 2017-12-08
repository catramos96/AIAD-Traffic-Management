package behaviours;

import java.util.ArrayList;

import agents.CarAgent;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import sajas.core.behaviours.CyclicBehaviour;

public class LearnMap extends CyclicBehaviour{

	private CarAgent car = null;
	private static final long serialVersionUID = 1L;
	private Intersection latestIntersection = null;
	private Road latestRoad = null;
	private boolean latestTransit = false;
	
	public LearnMap(CarAgent car){
		this.car = car;
		this.latestIntersection = car.getIntersection();
		this.latestRoad = car.getRoad();
	}

	@Override
	public void action() {
		CityMap knowledge = car.getCityKnowledge();
		boolean newIntersection = false;
		
		if(car.getIntersection() != null){
			
			//Don't know the intersection
			if(!knowledge.getIntersections().containsKey(car.getIntersection().getName())){
				newIntersection = true;
				
				//Get the perception of the intersection
				//Knows the intersection roads (in/out) but only with the "visible" information
				//at the moment: the outRoads will only have the startPoint, Direction and Name
				//and the inRoads the endPoint, Direction and aname
				Intersection intersection = car.getIntersection().getPerception();
				
				car.getQLearning().insertNewIntersection(intersection);

				
				//Saves the discovered intersection
				knowledge.getIntersections().put(intersection.getName(),intersection);
				
				ArrayList<Road> inRoads = intersection.getInRoads();
				ArrayList<Road> outRoads = intersection.getOutRoads();
				
				for(Road i : inRoads){
					//New road discovered
					if(!knowledge.getRoads().containsKey(i.getName())){
						knowledge.getRoads().put(i.getName(), i);
					}
					else{
						//Update the missing information
						Road knownRoad = knowledge.getRoads().get(i.getName());
						knownRoad.setEndIntersection(intersection);
						knownRoad.setEndPoint(i.getEndPoint());
						knownRoad.updateLength();
						
						car.calculateAndUpdateJourney();
					}

				}
				
				for(Road o : outRoads){
					//New road discovered
					if(!knowledge.getRoads().containsKey(o.getName())){
						knowledge.getRoads().put(o.getName(), o);
					}
					else{
						//Update the missing information
						Road knownRoad = knowledge.getRoads().get(o.getName());
						knownRoad.setStartIntersection(intersection);
						knownRoad.setStartPoint(o.getEndPoint());
						knownRoad.updateLength();
						
						car.calculateAndUpdateJourney();
					}

				}
			}
		}
				
		//change in road
		if(!car.getRoad().equals(latestRoad) && latestIntersection != null){
			
			//Reinforcment Learning
			car.getQLearning().updateQualityValues(latestIntersection.getName(), latestRoad.getName(), car.getRoad().isBlocked());
			
			latestRoad = car.getRoad();
			latestTransit = latestRoad.isBlocked();
		}
		else if(latestTransit == false){
			latestTransit = latestRoad.isBlocked();					
		}
		
		if(newIntersection)
			latestIntersection = car.getIntersection();

	}

}
