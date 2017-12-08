package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import agents.CarAgent;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import resources.Debug;
import resources.Point;
import resources.SpaceResources;

/**
 * Executed only after knowing the destination road name
 *
 */
public class QLearning {
	
	private final float IMPORTANCE1 = 0.6f;			//Importance of reaching the destination
	private final float IMPORTANCE2 = 0.4f;			//Importance of choosing paths with less transit and less length
	
	private HashMap<String, ArrayList<Quality>> qualityValues = new HashMap<String, ArrayList<Quality>>(); 
	
	//algorithms parameters
	private float learningRate = 1;
	private float discount = (float) 0.8;			//greedy measure: 0->very greedy

	private CarAgent car = null;
	
	public QLearning(CarAgent c,float learningRate, float discount){
		this.car = c;
		this.learningRate = learningRate;
		this.discount = discount;
	}
	
	public void insertNewIntersection(Intersection intersection){
		
		ArrayList<Quality> actions = new ArrayList<Quality>();

		for(Road r : intersection.getOutRoads()){
			Quality q1 = new Quality(intersection.getName(), r.getName(), true, 0);		//with transit
			Quality q2 = new Quality(intersection.getName(), r.getName(),false, 0);		//without transit
			
			actions.add(q1);
			actions.add(q2);
			
			updateQualityValues(intersection.getName(),r.getName());
		}
		
		qualityValues.put(intersection.getName(),actions);

	}
	
	public void updateQualityValues(String intersection,String toRoad){
		
		if(!(qualityValues.containsKey(intersection)))
			return;
		
		boolean transitHandled = false;
		boolean noTransitHandled = false;
					
		for(Quality q : qualityValues.get(intersection)){

			if(q.action_road.equals(toRoad)){
								
				Quality maxQnextState = getMaxQuality(toRoad);
				int quality_value = 0;
				
				if(maxQnextState != null)
					quality_value =  (int) (100 * (q.value + learningRate * (getReward(intersection, toRoad, q.action_transit) + discount * maxQnextState.value - q.value)));
				else
					quality_value =  (int) (100 * (q.value + learningRate * (getReward(intersection, toRoad, q.action_transit) + discount * 0 - q.value)));

				q.setValue(((float)quality_value)/100);

				Debug.debugQLearning(car, q);
				
				if(q.action_transit)
					transitHandled = true;
				else
					noTransitHandled = true;

				if(transitHandled && noTransitHandled)
					return;
			}
		}
	}
	
	private Quality getMaxQuality(String intersection){
		float max = 0;
		Quality maxQ =null;
		
		if(!qualityValues.containsKey(intersection))
			return null;
		
		for(Quality q : qualityValues.get(intersection)){
			
				if(max < q.value){
					max = q.value;
					maxQ = q;
				}
				
		}
		
		return maxQ;
	}
	
	private float getReward(String intersection, String toRoad, boolean withTransit){
		
		float reward = 0;
		
		if(car.getCityKnowledge().getIntersections().containsKey(intersection) &&
				car.getCityKnowledge().getRoads().containsKey(toRoad)){
			
			Road nextRoad =car.getCityKnowledge().getRoads().get(toRoad);
			Point destination = null;
			Intersection nextRoadInter = nextRoad.getEndIntersection();
			
			
			if(car.getDestinationName() != null){
				
				String dName = car.getDestinationName();
				
				if(car.getCityKnowledge().getRoads().containsKey(dName)){
					Road d = car.getCityKnowledge().getRoads().get(dName);
					
					if(d.getStartIntersection() != null)
						destination = d.getStartIntersection().getOneEntry();
				}
			}
			
			if(destination == null)
				destination = car.getDestination();
			
			float transitPenalty = 0;
			float length = nextRoad.getLength();
			
			//If he doesn't know the length of the road
			if(length == SpaceResources.INFINITE)
				length = Math.max(car.getCityKnowledge().getDimensions().x, car.getCityKnowledge().getDimensions().y);		//assume worst case
			
			
			if(withTransit){
				//Number of cells that the car could pass if he wasn't stopped at the transit
				transitPenalty = (float)(CityMap.getTransitPenalization(car.getCityKnowledge(), toRoad));
			}			
			
			reward = (float) (1.5 * (car.getCityKnowledge().getDimensions().x + car.getCityKnowledge().getDimensions().y) - 
					Point.getDistance(nextRoadInter.getOneEntry(), destination) * 2 * IMPORTANCE1 - 
					(length + transitPenalty) * 2 * IMPORTANCE2);
			
		}
		
		return reward;
	}

	public String getNextRoad(Intersection i){
		String road = null;
		float value = - SpaceResources.INFINITE;
		
		if(qualityValues.containsKey(i.getName())){
			
			//Search quality values for state = i
			for(Quality q : qualityValues.get(i.getName())){
				
				//Check state of the transit in the cityMapKnowledge and analyse the 
				//quality values for those actions
				for(Road r : i.getOutRoads()){
					if(q.action_transit == r.isBlocked() && q.action_road.equals(r.getName())
							&& value < q.value){
						value = q.value;
						road = r.getName();
					}
				}
			}

		}
		
		return road;
	}

	
	public HashMap<String, ArrayList<Quality>> getQualityValues() {
		return qualityValues;
	}

	
	public void setQualityValues(HashMap<String, ArrayList<Quality>> qualityValues) {
		this.qualityValues = qualityValues;
	}

}


