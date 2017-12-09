package behaviours;

import java.util.ArrayList;
import java.util.HashMap;

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
		
		car.getUnexploredRoads().put(latestRoad.getName(), latestRoad.getStartIntersection().getName());
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
				//and the inRoads the endPoint, Direction and name
				//Intersection intersection = car.getIntersection().getPerception();
				
				Intersection intersection = null;
				Intersection realI = car.getIntersection();
				
				if(car.getIntersection().getClass().equals(SimpleIntersection.class))
					intersection = new SimpleIntersection(realI.getArea(),realI.getName());
				else
					intersection = new ComplexIntersection(realI.getArea(),realI.getName(),((ComplexIntersection)realI).getCircuit());
					
				
				if(car.getLearningMode().equals(LearningMode.LEARNING))
					car.getQLearning().insertNewIntersection(intersection);

				
				//Saves the discovered intersection
				knowledge.getIntersections().put(intersection.getName(),intersection);
				
				
				ArrayList<Road> inRoads = realI.getInRoads();
				ArrayList<Road> outRoads = realI.getOutRoads();
				
				for(int i = 0; i < inRoads.size(); i++){
					
					Road in = inRoads.get(i);
					
					//New road discovered
					if(!knowledge.getRoads().containsKey(in.getName())){
						//knowledge.getRoads().put(in.getName(), in);
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


						
						//if(car.getJourney().size() != 0)
							//car.calculateAndUpdateJourney();
					}

				}
				
				for(int i = 0; i < outRoads.size(); i++){
					Road out = outRoads.get(i);
					
					//New road discovered
					if(!knowledge.getRoads().containsKey(out.getName())){
						//knowledge.getRoads().put(out.getName(), out);
						
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
						
						intersection.insertRoad(knownRoad, true);

						
						//If road is still unexplored
						if(car.getUnexploredRoads().containsKey(out))
							car.getUnexploredRoads().put(out.getName(), intersection.getName());
						
						//if(car.getJourney().size() != 0)
							//car.calculateAndUpdateJourney();
					}

				}
				
			}
			
			/*if(!car.getIntersection().equals(latestIntersection)){
				System.out.println(car.getCityKnowledge().getIntersections().get(car.getIntersection().getName()).print() + "\n");
			}*/
			
			
		}
		
				
		//change in road
		if(!car.getRoad().equals(latestRoad) && latestIntersection != null){
			
			//Reinforcment Learning
			if(car.getLearningMode().equals(LearningMode.LEARNING))
				car.getQLearning().updateQualityValues(latestIntersection.getName(), latestRoad.getName());
			
			latestRoad = car.getRoad();
			
			if(car.getUnexploredRoads().containsKey(latestRoad.getName()))
				car.getUnexploredRoads().remove(latestRoad.getName());
		}
		
		latestIntersection = car.getIntersection();
		
		//Knows all the city
		if(car.getUnexploredRoads().size() == 0 && car.getLearningMode().equals(LearningMode.LEARNING)){
			car.setLearningMode(LearningMode.APPLYING);
			car.removeBehaviour(this);
		}

	}

}
