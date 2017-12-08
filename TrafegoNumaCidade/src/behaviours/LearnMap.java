package behaviours;

import java.util.ArrayList;
import java.util.HashMap;

import agents.CarAgent;
import agents.CarAgent.LearningMode;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import sajas.core.behaviours.CyclicBehaviour;

public class LearnMap extends CyclicBehaviour{

	private CarAgent car = null;
	private static final long serialVersionUID = 1L;
	private Intersection latestIntersection = null;
	private Road latestRoad = null;
	
	public LearnMap(CarAgent car){
		this.car = car;
		this.latestIntersection = car.getIntersection();
		this.latestRoad = car.getRoad();
		
		for(Road r : latestRoad.getStartIntersection().getInRoads())
			car.getUnexploredRoads().put(r.getName(), r);
		
		for(Road r : latestRoad.getStartIntersection().getOutRoads())
			car.getUnexploredRoads().put(r.getName(), r);
	}

	@Override
	public void action() {
		CityMap knowledge = car.getCityKnowledge();
		
		if(car.getIntersection() != null){
			
			//Don't know the intersection
			if(!knowledge.getIntersections().containsKey(car.getIntersection().getName())){
				
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
						car.getUnexploredRoads().put(i.getName(),i);
					}
					else{
						//Update the missing information
						Road knownRoad = knowledge.getRoads().get(i.getName());
						knownRoad.setEndIntersection(intersection);
						knownRoad.setEndPoint(i.getEndPoint());
						knownRoad.updateLength();
						
						if(car.getJourney().size() == 0)
							car.calculateAndUpdateJourney();
					}

				}
				
				for(Road o : outRoads){
					//New road discovered
					if(!knowledge.getRoads().containsKey(o.getName())){
						knowledge.getRoads().put(o.getName(), o);
						car.getUnexploredRoads().put(o.getName(),o);
					}
					else{
						//Update the missing information
						Road knownRoad = knowledge.getRoads().get(o.getName());
						knownRoad.setStartIntersection(intersection);
						knownRoad.setStartPoint(o.getEndPoint());
						knownRoad.updateLength();
						
						if(car.getJourney().size() == 0)
							car.calculateAndUpdateJourney();
					}

				}
			}
		}
				
		//change in road
		if(!car.getRoad().equals(latestRoad) && latestIntersection != null){
			
			//Reinforcment Learning
			car.getQLearning().updateQualityValues(latestIntersection.getName(), latestRoad.getName());
			
			latestRoad = car.getRoad();
			
			if(car.getUnexploredRoads().containsKey(latestRoad.getName()))
				car.getUnexploredRoads().remove(latestRoad.getName());
		}
		
		latestIntersection = car.getIntersection();
		
		//Knows all the city
		if(car.getUnexploredRoads().size() == 0){
			System.out.println(car.getLocalName() + " is applying his knowledge from qLearning values");
			car.setLearningMode(LearningMode.APPLYING);
			car.removeBehaviour(this);
		}

	}

}
