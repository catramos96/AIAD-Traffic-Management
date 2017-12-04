package behaviours;

import agents.CarAgent;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import sajas.core.behaviours.CyclicBehaviour;

public class LearnMap extends CyclicBehaviour{

	private CarAgent car = null;
	private static final long serialVersionUID = 1L;
	
	public LearnMap(CarAgent car){
		this.car = car;
	}

	@Override
	public void action() {
		CityMap knowledge = car.getCityKnowledge();
		
		Road currentRoad = car.getRoad();
		Intersection i1 = currentRoad.getStartIntersection();
		Intersection i2 = currentRoad.getEndIntersection();
		
		//If current road is unknown
		if(!knowledge.getRoads().containsKey(currentRoad.getName())){
			
			//saves in knowledge
			knowledge.getRoads().put(currentRoad.getName(),currentRoad);
			
			if(i1 != null){
				//if intersection is unknown then it's saved
				if(!knowledge.getIntersections().containsKey(i1.getName())){
					knowledge.getIntersections().put(i1.getName(), i1);
				}
				//or updated with the current road
				else
					knowledge.getIntersections().get(i1.getName()).insertRoad(currentRoad);
			}
			
			if(i2 != null){
				//if intersection is unknown then it's saved
				if(!knowledge.getIntersections().containsKey(i2.getName())){
					knowledge.getIntersections().put(i2.getName(), i2);
				}
				//or updated with the current road
				else
					knowledge.getIntersections().get(i2.getName()).insertRoad(currentRoad);
			}
		}
	}

}
