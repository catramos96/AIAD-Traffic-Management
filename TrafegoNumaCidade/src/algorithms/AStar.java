package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import cityStructure.CityMap;
import cityStructure.Road;
import resources.Point;
import resources.Resources;

/**
 * Class that implements the A* algorithm when searching for the best shortest
 * path between two given points in a city map.
 * The heuristic used to calculate the future cost is the distance between two points.
 *
 */
public class AStar {
	
	public static int INFINITE = 999999;
	
	/**
	 * Return the shortest path between a startRoad and its destination
	 * @param map
	 * @param startRoad
	 * @param destination
	 * @return
	 */
	public static ArrayList<String> shortestPath(CityMap map, Road startRoad, Point destination){
		
		ArrayList<String> path = new ArrayList<String>();
		ArrayList<Road> evaluatedSet = new ArrayList<Road>();		
		ArrayList<Road> toEvaluateSet = new ArrayList<Road>();
		HashMap<Road, Road> cameFrom = new HashMap<Road,Road>();
		HashMap<Road, Integer> costs = new HashMap<Road, Integer>();
		HashMap<Road, Integer> final_costs = new HashMap<Road,Integer>();
		
		//inits
		for(Road r : map.getRoads().values()){
			costs.put(r, INFINITE);
			final_costs.put(r, INFINITE);
		}
		
		Road endRoad = null;
		for(Road r : map.getRoads().values()){
			if(r.partOfRoad(destination)){
				endRoad = r;
				break;
			}
		}
		
		if(endRoad == null)
			return path;
		
		toEvaluateSet.add(startRoad);
		costs.put(startRoad, 0);
		final_costs.put(startRoad,Point.getDistance(startRoad.getEndIntersection().getOneEntry(), 
				endRoad.getStartIntersection().getOneEntry()));
		
		while(!toEvaluateSet.isEmpty()){

			Road current = getMinimumCost(toEvaluateSet,final_costs);

			if(current.equals(endRoad))
				return buildPath(cameFrom, current);
			
			toEvaluateSet.remove(current);
			evaluatedSet.add(current);
			
			for(Road next : current.getEndIntersection().getOutRoads()){
				
				if(!evaluatedSet.contains(next)){
					
					if(!toEvaluateSet.contains(next))
						toEvaluateSet.add(next);
					
					//failure
					if(!costs.containsKey(next))
						return path;
						
					//If the road has transit, then it has a penalty to the costs
					int transitPenalty = 0;
					
					if(next.isBlocked())
						transitPenalty = Resources.transitPenaltyRatio * next.getLength();
						
					int cost_next = costs.get(current) + current.getEndIntersection().getLength() + next.getLength() + transitPenalty;
					
					if(cost_next < costs.get(next)){
						cameFrom.put(next, current);
						costs.put(next, cost_next);
						
						//custos finais = custo at� � rua actual + poss�vel custo at� ao final
						final_costs.put(next, cost_next + Point.getDistance(next.getEndIntersection().getOneEntry(), 
								endRoad.getStartIntersection().getOneEntry()));
					}
				}
			}
			
		}
		
		System.out.println("Failed to get the shortest path between" + startRoad.getName() + " " + destination.x + "_" + destination.y);
		return path;
	}
	
	private static ArrayList<String> buildPath(HashMap<Road,Road> cameFrom, Road last){
		ArrayList<String> path = new ArrayList<String>();
		
		path.add(last.getName());
		
		while(cameFrom.containsKey(last)){
			last = cameFrom.get(last);
			path.add(0, last.getName());
		}
		
		return path;
	};
	
	private static Road getMinimumCost(ArrayList<Road> toEvaluateSet,HashMap<Road,Integer> cost){
		int min = INFINITE;
		int v;
		Road road = null;
		
		for(Road r : toEvaluateSet){
			v = cost.get(r).intValue();
			
			if(v < min){
				min = v;
				road = r;
			}
		}
	
		return road;
	}

}
