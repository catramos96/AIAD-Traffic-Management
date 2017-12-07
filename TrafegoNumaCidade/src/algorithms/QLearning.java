package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import agents.CarAgent;
import cityStructure.Road;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;

/**
 * Executed only after knowing the destination road name
 *
 */
public class QLearning {
	
	private final double IMPORTANCE1 = 0.6;			//Importance of reaching the destination
	private final double IMPORTANCE2 = 0.4;			//Importance of choosing paths with less transit and less length
	/*
	 * Auxiliary Classes
	 */
	
	private class State{
		public String road = null;
		public boolean hasTransit = false;
		
		public State(String road, boolean hasTransit){
			this.road = road;
			this.hasTransit = hasTransit;
		}
	}
	
	private class Quality{
		public State state = null;
		public String action_road = null;
		public double value = 0;
		
		public Quality(State state,String action_road,double value){
			this.state = state;
			this.action_road = action_road;
			this.value = value;
		}
		
		public void setValue(double value){
			this.value = value;
		}	
		
		public String print(){
			return new String("Q( State(" + state.road + "," + state.hasTransit + ") , " + action_road +" ) = " +value);
		}
	}
	
	private HashMap<String,HashMap<String,ArrayList<Quality>>> qualityValues = new HashMap<String, HashMap<String,ArrayList<Quality>>>(); 
	
	//algorithms parameters
	private double learningRate = 1;
	private double discount = 0.8;			//greedy measure: 0->very greedy
	
	private String lastRoad = null;
	
	private CarAgent car = null;
	
	public QLearning(CarAgent c,double learningRate, double discount){
		this.car = c;
		this.learningRate = learningRate;
		this.discount = discount;
	}
	
	public void insertNewRoad(Road road, ArrayList<Road> possibleRoads){
		
		for(Road r : possibleRoads){
			Quality q1 = new Quality(new State(road.getName(),true), r.getName(), 0);
			Quality q2 = new Quality(new State(road.getName(),false), r.getName(), 0);
			
			HashMap<String,ArrayList<Quality>> actions = new HashMap<String,ArrayList<Quality>>();
			ArrayList<Quality> qs = new ArrayList<Quality>();
			qs.add(q1);
			qs.add(q2);
			
			//System.out.println("Insert QL: " + q1.print());
			//System.out.println("Insert QL: " + q2.print());

			
			actions.put(r.getName(), qs);
			
			qualityValues.put(road.getName(),actions);
		}
	}
	
	public void updateQualityValues(String fromRoad,String toRoad, boolean transit){
		
		if(!(qualityValues.containsKey(fromRoad)))
			return; 
		else if(!qualityValues.get(fromRoad).containsKey(toRoad))
			return;
					
		for(Quality q : qualityValues.get(fromRoad).get(toRoad)){

			if(q.state.hasTransit == transit){
								
				Quality maxQnextState = getMaxQuality(toRoad);
				int quality_value = 0;
				
				if(maxQnextState != null)
					quality_value = (int) (q.value + learningRate * (getReward(fromRoad, toRoad, transit) + discount * maxQnextState.value - q.value));
				else
					quality_value = (int) (q.value + learningRate * (getReward(fromRoad, toRoad, transit) + discount * 0 - q.value));

				q.setValue(quality_value);

				System.out.println(car.getLocalName() + " QL: " +  q.print());

				break;
			}
		}
	}
	
	public Road getMaxQualityRoad(Road currentRoad){
		Road nextRoad = null;
		double maxValue = 0;
		
		for(Road r2 : currentRoad.getEndIntersection().getOutRoads()){
			
			if(qualityValues.containsKey(r2.getName())){
				
				double value = getMaxQualityActionValue(new State(r2.getName(),r2.isBlocked()));
				
				if(maxValue < value){
					maxValue = value;
					nextRoad = r2;
				}
				
			}
		}
		
		return nextRoad;
	}
	
	private Quality getMaxQuality(String fromRoad){
		double max = 0;
		Quality q =null;
		
		if(!qualityValues.containsKey(fromRoad))
			return null;
		
		for(String toRoad : qualityValues.get(fromRoad).keySet()){
			for(Quality tmpQ : qualityValues.get(fromRoad).get(toRoad)){
				if(max < tmpQ.value){
					max = tmpQ.value;
					q = tmpQ;
				}
			}
		}
		
		return q;
	}
	
	private double getMaxQualityActionValue(State state){
		double max = -99999;
		
		if(!qualityValues.containsKey(state.road))
			return max;
		
		for(String toRoad : qualityValues.get(state.road).keySet()){
			
			for(Quality tmpQ : qualityValues.get(state.road).get(toRoad)){
				
				if(max < tmpQ.value && tmpQ.state.hasTransit == state.hasTransit)
					max = tmpQ.value;
			}
			
		}
		
		return max;
	}
	
	private double getReward(String fromRoad, String toRoad, boolean withTransit){
		
		double reward = 0;
		
		if(car.getCityKnowledge().getRoads().containsKey(fromRoad) &&
				car.getCityKnowledge().getRoads().containsKey(toRoad)){
			
			Road r1 = car.getCityKnowledge().getRoads().get(fromRoad);
			Point destination = null;
			
			
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
			
			int transitPenalty = 0;
			int length = r1.getLength();
			
			if(withTransit){
				//Number of cells that the car could pass if he wasn't stopped at the transit
				transitPenalty = (r1.getEndIntersection().getInRoads().size()-1) * 
						(Resources.lightCheck*Resources.GreenLightTimeUnits + Resources.lightCheck + Resources.YellowLightTimeUnits) /
						Resources.carVelocity;
				
				if(transitPenalty == 0)
					transitPenalty = r1.getLength();
			}
			
			//If he doesn't know the length of the road
			if(length == SpaceResources.INFINITE)
				length = Math.max(car.getSpaceDimensions().x, car.getSpaceDimensions().y);		//assume worst case
			
			reward = 1.5 * (car.getSpaceDimensions().x + car.getSpaceDimensions().y) - 
					Point.getDistance(r1.getEndIntersection().getOneEntry(), destination) * 2 * IMPORTANCE1 - 
					(length + transitPenalty) * 2 * IMPORTANCE2;
			
		}
		
		return reward;
	}

}


