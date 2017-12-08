package algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import resources.Point;

/**
 * Class that implements the A* algorithm when searching for the best shortest
 * path between two given points in a city map.
 * The heuristic used to calculate the future cost is the distance between two points.
 *
 */
public class AStar {
	
	public static int INFINITE = 999999;
	
	public static ArrayList<String> shortestPath(CityMap map, Road startRoad, String destinationName, boolean destinationIsRoad){
		
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
		Intersection endIntersection = null;
		
		if(map.getRoads().containsKey(destinationName) && destinationIsRoad)					//If it's a road
			endRoad = map.getRoads().get(destinationName);
		else if(map.getIntersections().containsKey(destinationName) && !destinationIsRoad)			//If it's a intersection
			endIntersection = map.getIntersections().get(destinationName);
		else 
			return path;
		
		if(startRoad.getName().equals(destinationName) && destinationIsRoad)
			return path;
		
		toEvaluateSet.add(startRoad);
		costs.put(startRoad, 0);
		
		Point destination = null;
		
		if(destinationIsRoad && endRoad.getStartIntersection() !=null)		//if destination is a road
			destination = endRoad.getStartIntersection().getOneEntry();
		else if(!destinationIsRoad)											//if destination is an intersection
			destination = endIntersection.getOneEntry();
		else
			return path;
		
		if(startRoad.getEndIntersection() != null)
			final_costs.put(startRoad,Point.getDistance(startRoad.getEndIntersection().getOneEntry(), destination));
		else
			return path;
		
		System.out.println("");

		
		while(!toEvaluateSet.isEmpty()){

			System.out.println("");

			
			Road current = getMinimumCost(toEvaluateSet,final_costs);
			
			System.out.println("CURRENT " + current.getName());

			if(destinationIsRoad){											//if destination is a road
				if(current.getName().equals(destinationName))
					return buildPath(cameFrom, current);
			}
			else if(current.getEndIntersection() != null){					//if destination is an intersection
				if(current.getEndIntersection().getName().equals(destinationName))
					return buildPath(cameFrom,current);
			}
			
			toEvaluateSet.remove(current);
			evaluatedSet.add(current);
			
			//If the end Intersection of the current road is known
			if(current.getEndIntersection() != null) {
				
				//Look for possible nextRoads
				for(Road next : current.getEndIntersection().getOutRoads()){
					
					System.out.println("CHILDREN " + next.getName());

					
					if(next.getEndIntersection() == null)
						System.out.println("CHILDREN END NULL");

					
					//If end intersection of nextRoad is known
					if(!evaluatedSet.contains(next)  && next.getEndIntersection() != null){
						
						if(!toEvaluateSet.contains(next))
							toEvaluateSet.add(next);
						
						//failure
						if(!costs.containsKey(next))
							return path;
							
						//If the road has transit, then it has a penalty to the costs
						int transitPenalty = 0;
						
						if(next.isBlocked())
							transitPenalty = CityMap.getTransitPenalization(map, next.getName());
				
						int cost_next = costs.get(current) + current.getEndIntersection().getLength() + next.getLength() + transitPenalty;
						
						if(cost_next < costs.get(next)){
							cameFrom.put(next, current);
							costs.put(next, cost_next);
							
							//custos finais = custo até à rua actual + possível custo até ao final
							final_costs.put(next, cost_next + Point.getDistance(next.getEndIntersection().getOneEntry(), destination));
						}
					}
				}
			}
			
			
		}
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
