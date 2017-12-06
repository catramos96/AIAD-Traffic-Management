package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import agents.CarAgent;
import cityStructure.Road;
import resources.Point;
import resources.Resources;

/**
 * Executed only after knowing the destination road name
 *
 */
public class QLearning {
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
		public int value = 0;
		
		public Quality(State state,String action_road,int value){
			this.state = state;
			this.action_road = action_road;
			this.value = value;
		}
		
		public void setValue(int value){
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
			
			System.out.println("Insert QL: " + q1.print());
			System.out.println("Insert QL: " + q2.print());

			
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

				System.out.println("QL: " +  q.print());

				break;
			}
		}
	}
	
	private Quality getMaxQuality(String fromRoad){
		int max = 0;
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
	
	private int getReward(String fromRoad, String toRoad, boolean withTransit){
		
		int reward = 0;
		
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
			
			if(withTransit)
				transitPenalty = r1.getLength() * Resources.transitPenaltyRatio * 10;
				
			if(toRoad.equals(car.getDestinationName()))
				reward = 1000 + transitPenalty;
			
			
			else if(r1.getEndIntersection() != null){
				int distance = Point.getDistance(r1.getEndIntersection().getOneEntry(), destination);
				reward = 1000 - (distance * 10);
			}
			
		}
		
		return reward;
	}

}


