package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import agents.Car;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import resources.Debug;
import resources.Point;
import resources.SpaceResources;

/**
 * Class that implements the QLearning algorithm for
 * a city structure.
 *
 */
public class QLearning {
	
	private final float IMPORTANCE1 = 0.6f;			//Importance of reaching the destination
	private final float IMPORTANCE2 = 0.4f;			//Importance of choosing paths with less transit and less length
	
	private HashMap<String, ArrayList<Quality>> qualityValues = new HashMap<String, ArrayList<Quality>>(); 
	
	//Some parameters factors
	private float learningRate = 1;
	private float discount = (float) 0.8;			//greedy measure: 0->very greedy

	private Car car = null;
	
	/**
	 * Constructor.
	 * @param c
	 * @param learningRate
	 * @param discount
	 * @param values
	 */
	public QLearning(Car c,float learningRate, float discount,HashMap<String, ArrayList<Quality>> values){
		this.car = c;
		this.learningRate = learningRate;
		this.discount = discount;
		this.qualityValues = values;
	}
	
	/**
	 * Register the new intersection out roads and insert new
	 * quality values regarding those new roads.
	 * Used when a car discovers a new intersection.
	 * 
	 * @param intersection
	 */
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
	
	/**
	 * Update the quality values of choosing the out road "toRoad" when
	 * the state was "intersection". The values update are for the road
	 * with and without transit.
	 * @param intersection
	 * @param toRoad
	 */
	public void updateQualityValues(String intersection,String toRoad){
		
		if(!(qualityValues.containsKey(intersection)))
			return;
		
		boolean transitHandled = false;
		boolean noTransitHandled = false;
					
		//Searchs for the Q(intersection,toRoad with transit) and Q(intersection,toRoad no transit)
		for(Quality q : qualityValues.get(intersection)){

			if(q.action_road.equals(toRoad)){
								
				//Calculates the next possible state with the higher value
				Quality maxQnextState = getMaxQuality(toRoad);
				int quality_value = 0;
				
				//Calculates the quality value for this Q(intersection,toRoad)
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
	
	/**
	 * Gets the max quality that has the highest value for the 
	 * the intersection state.
	 * @param intersection
	 * @return
	 */
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
	
	/**
	 * Gets the reward from choosing the road "toRoad" with a given
	 * transit in the intersection.
	 * 
	 * Reward = 1.5 * (space_width + space_height) - distance(toRoad_endIntersection, destination) * 2 * IMPORTANCE1 - 
	 * 					(toRoad_length + transitPenalty) * 2 * IMPORTANCE2);
	 * 
	 * TransitPenalty = 0 if no transit,
	 * 				  = (Number_of_semaphores_at_end_intersection - 1) * (time_yellow + time_green) / time_car_move_1_cell
	 * 
	 * The transit penalty represents the number of cells that the car could be moving if it wasn't stopped at the transit.
	 * 
	 * @param intersection
	 * @param toRoad
	 * @param withTransit
	 * @return
	 */
	private float getReward(String intersection, String toRoad, boolean withTransit){
		
		float reward = 0;
		
		if(car.getCityKnowledge().getIntersections().containsKey(intersection) &&
				car.getCityKnowledge().getRoads().containsKey(toRoad)){
			
			Road nextRoad =car.getCityKnowledge().getRoads().get(toRoad);
			Point destination = null;
			Intersection nextRoadInter = nextRoad.getEndIntersection();
			
			
			//if name of the destination road is known, then the destination
			//will the the start intersection of that road
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
			
			//If he doesn't know the length of the road assume the worst case
			if(length == SpaceResources.INFINITE)
				length = Math.max(car.getCityKnowledge().getDimensions().x, car.getCityKnowledge().getDimensions().y);
					
			if(withTransit){
				//Number of cells that the car could pass if he wasn't stopped at the transit
				transitPenalty = (float)(CityMap.getTransitPenalty(car.getCityKnowledge(), toRoad));
			}			
			
			reward = (float) (1.5 * (car.getCityKnowledge().getDimensions().x + car.getCityKnowledge().getDimensions().y) - 
					Point.getDistance(nextRoadInter.getOneEntry(), destination) * 2 * IMPORTANCE1 - 
					(length + transitPenalty) * 2 * IMPORTANCE2);
			
		}
		
		return reward;
	}

	/**
	 * Method that gives the action road that gives the most value when the car is in the intersection i.
	 * It takes in consideration the current transit in the out roads of intersection i.
	 * @param i Intersection
	 * @return Name of the next road
	 */
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
	
	
	/*
	 * GETS & SETS
	 */

	/**
	 * Gets the quality values.
	 * @return
	 */
	public HashMap<String, ArrayList<Quality>> getQualityValues() {
		return qualityValues;
	}

	/**
	 * Sets the quality values.
	 * @param qualityValues
	 */
	public void setQualityValues(HashMap<String, ArrayList<Quality>> qualityValues) {
		this.qualityValues = qualityValues;
	}

}


