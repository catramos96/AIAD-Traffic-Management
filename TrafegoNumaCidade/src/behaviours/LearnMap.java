package behaviours;

import java.util.ArrayList;
import agents.CarAgent;
import agents.CarAgent.LearningMode;
import cityStructure.CityMap;
import cityStructure.ComplexIntersection;
import cityStructure.Intersection;
import cityStructure.Road;
import cityStructure.SimpleIntersection;
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
		
		boolean newRoad = true;
		
		//It the car knowledge knows the road in it's totality
		//then it's not necessary to insert it in the unexplored roads
		if(!car.getKnowledge().isNew()){
			if(car.getCityKnowledge().getRoads().containsKey(latestRoad.getName())){
				Road r = car.getCityKnowledge().getRoads().get(latestRoad.getName());
				if(r.getStartIntersection() != null && r.getEndIntersection() != null)
					newRoad = false;
			}	
		}
		
		if(car.getKnowledge().isNew() || newRoad)
		car.getUnexploredRoads().put(latestRoad.getName(), latestRoad.getStartIntersection().getName());
	}

	@Override
	public void action() {
		CityMap knowledge = car.getCityKnowledge();

		
		if(car.getIntersection() != null){
			
			
			//Don't know the intersection
			if(!knowledge.getIntersections().containsKey(car.getIntersection().getName())){

				Intersection intersection = null;
				Intersection realI = car.getIntersection();
				
				if(car.getIntersection().getClass().equals(SimpleIntersection.class))
					intersection = new SimpleIntersection(realI.getArea(),realI.getName());
				else
					intersection = new ComplexIntersection(realI.getArea(),realI.getName(),((ComplexIntersection)realI).getCircuit());
					
				
				if(car.getLearningMode().equals(LearningMode.LEARNING))
					car.getQLearning().insertNewIntersection(realI);

				
				//Saves the discovered intersection
				knowledge.getIntersections().put(intersection.getName(),intersection);
				
				
				ArrayList<Road> inRoads = realI.getInRoads();
				ArrayList<Road> outRoads = realI.getOutRoads();
				
				for(int i = 0; i < inRoads.size(); i++){
					
					Road in = inRoads.get(i);
					
					//New road discovered
					if(!knowledge.getRoads().containsKey(in.getName())){
						Road r = in.getRoadPerceptionAtEnd();
						r.setEndIntersection(intersection);
						intersection.insertRoad(r, false);
						
						knowledge.getRoads().put(r.getName(), r);
						
						//Save unexplored road, intersection of start is unknown 
						car.getUnexploredRoads().put(in.getName(),"");
					}
					else{
						//Update the missing information
						Road knownRoad = knowledge.getRoads().get(in.getName());
						knownRoad.setEndPoint(in.getEndPoint());
						knownRoad.updateLength();
						knownRoad.setEndIntersection(intersection);

						intersection.insertRoad(knownRoad, false);
						
						if(car.getDestinationName() == null){
							if(knownRoad.partOfRoad(car.getDestination()))
								car.setDestinationName(knownRoad.getName());
						}
						
						if(car.getJourney().size() != 0)
							car.calculateAndUpdateJourney();
					}

				}
				
				for(int i = 0; i < outRoads.size(); i++){
					Road out = outRoads.get(i);
					
					//New road discovered
					if(!knowledge.getRoads().containsKey(out.getName())){						
						Road r = out.getRoadPerceptionAtStart();
						r.setStartIntersection(intersection);
						intersection.insertRoad(r, false);
						knowledge.getRoads().put(r.getName(), r);

						
						//Save unexplored road, intersection of start is known
						car.getUnexploredRoads().put(out.getName(),intersection.getName());
					}
					else{
						//Update the missing information
						Road knownRoad = knowledge.getRoads().get(out.getName());
						knownRoad.setStartPoint(out.getStartPoint());
						knownRoad.updateLength();
						knownRoad.setStartIntersection(intersection);
						
						intersection.insertRoad(knownRoad, false);
						
						if(car.getDestinationName() == null){
							if(knownRoad.partOfRoad(car.getDestination()))
								car.setDestinationName(knownRoad.getName());
						}
						
						//If road is still unexplored
						if(car.getUnexploredRoads().containsKey(out)) {
							car.getUnexploredRoads().put(out.getName(), intersection.getName());
						}
						
						if(car.getJourney().size() != 0) {
							car.calculateAndUpdateJourney();
						}
					}

				}
				
			}
			
		}
		
				
		//change in road
		if(car.getRoad() != null){
			if(!car.getRoad().equals(latestRoad) && 
					latestIntersection != null){
				
				//Reinforcment Learning
				if(car.getLearningMode().equals(LearningMode.LEARNING))
					car.getQLearning().updateQualityValues(latestIntersection.getName(), latestRoad.getName());
				
				latestRoad = car.getRoad();
				
				if(car.getUnexploredRoads().containsKey(latestRoad.getName()))
					car.getUnexploredRoads().remove(latestRoad.getName());
			}
		}
		
		
		latestIntersection = car.getIntersection();
		
		//Knows all the city
		if(car.getUnexploredRoads().size() == 0 && car.getLearningMode().equals(LearningMode.LEARNING)){
			car.setLearningMode(LearningMode.APPLYING);
			car.getJourney().clear();
			car.removeBehaviour(this);
		}

	}

}
