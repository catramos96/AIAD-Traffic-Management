package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import agents.CarAgent;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import resources.Point;
import resources.SpaceResources;

/**
 * Executed only after knowing the destination road name
 *
 */
public class QLearning {
	
	private final float IMPORTANCE1 = 0.6f;			//Importance of reaching the destination
	private final float IMPORTANCE2 = 0.4f;			//Importance of choosing paths with less transit and less length
	
	/*
	 * Auxiliary Classes
	 */
	
	public class Quality{
		public String intersection = null;				//State
			
		public String action_road = null;		//action
		public boolean action_transit = false;
		
		public float value = 0;				//quality value
		
		public Quality(String intersection, String action_road, boolean action_transit, float value){
			this.intersection = intersection;
			this.action_road = action_road;
			this.action_transit = action_transit;
			this.value = value;
		}
		
		public void setValue(float value){
			this.value = value;
		}	
		
		public String print(){
			return new String("Q( State(" + intersection + ") , Action( " + action_road + " , " + action_transit + " ) = " +value);
		}
	}
	
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
			
			System.out.println("ADDED: " + q1.print());
		}
		
		qualityValues.put(intersection.getName(),actions);

	}
	
	public void updateQualityValues(String intersection,String toRoad, boolean transit){
		
		if(!(qualityValues.containsKey(intersection)))
			return;
					
		for(Quality q : qualityValues.get(intersection)){

			if(q.action_transit == transit && q.action_road.equals(toRoad)){
				
				System.out.println("Matched road");
								
				Quality maxQnextState = getMaxQuality(toRoad);
				int quality_value = 0;
				
				if(maxQnextState != null)
					quality_value =  (int) (100 * (q.value + learningRate * (getReward(intersection, toRoad, transit) + discount * maxQnextState.value - q.value)));
				else
					quality_value =  (int) (100 * (q.value + learningRate * (getReward(intersection, toRoad, transit) + discount * 0 - q.value)));

				q.setValue(((float)quality_value)/100);

				System.out.println(car.getLocalName() + " QL: " +  q.print());

				return;
			}
		}
		
		System.out.println("Matched road failed - " + intersection + " " + toRoad + " " + transit);

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
}


