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
	
	public static int INFINITE = 999999;		//Max weight
	
	/**
	 * Method to get the shortest path between a road and its destination.
	 * The destination can be the name of a road (destinationIsRoad = true)
	 * or the name of an intersection (destinationIsRoad = false).
	 * 
	 * @param map - city structure
	 * @param startRoad - Road of start
	 * @param destinationName - String name of an intersection or a road
	 * @param destinationIsRoad - True if the destinationName is the name of a road
	 * or False if it's the name of an intersection.
	 * @return - ArrayList with the names of roads to follow to reach the destination or 
	 * empty if it failed to calculate a path.
	 */
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
		else if(map.getIntersections().containsKey(destinationName) && !destinationIsRoad)		//If it's a intersection
			endIntersection = map.getIntersections().get(destinationName);
		else 
			return path;
		
		//The start road and destinationName (road) are the same
		if(startRoad.getName().equals(destinationName) && destinationIsRoad)					
			return path;
		
		//Add start road to the set to be evaluated later
		toEvaluateSet.add(startRoad);
		//Cost of start is 0
		costs.put(startRoad, 0);
		
		Point destination = null;
		
		//Find destination point
		if(destinationIsRoad && endRoad.getStartIntersection() !=null)		//if destination is a road
			destination = endRoad.getStartIntersection().getOneEntry();		//destination is the start intersection of the endRoad
		else if(!destinationIsRoad)											//if destination is an intersection
			destination = endIntersection.getOneEntry();					//destination the endIntersection
		else
			return path;
		
		//Estimate the cost from the start road until the destination
		if(startRoad.getEndIntersection() != null)
			final_costs.put(startRoad,Point.getDistance(startRoad.getEndIntersection().getOneEntry(), destination));
		else
			return path;

		//Until there is no road to evaluate
		while(!toEvaluateSet.isEmpty()){
			
			//get the cheapest road
			Road current = getMinimumCost(toEvaluateSet,final_costs);

			//if the current road being analyzed is the destination
			if(destinationIsRoad){											//if destination is a road
				if(current.getName().equals(destinationName))
					return buildPath(cameFrom, current);
			}
			//if the current road being analyzed leads to the destination
			else if(current.getEndIntersection() != null){					//if destination is an intersection
				if(current.getEndIntersection().getName().equals(destinationName))
					return buildPath(cameFrom,current);
			}
			
			//Remove the current road being analyzed from the set to be evaluated
			toEvaluateSet.remove(current);
			//Add it to the already evaluated set
			evaluatedSet.add(current);
			
			//If the end Intersection of the current road is known
			if(current.getEndIntersection() != null) {
				
				//Look for possible nextRoads
				for(Road next : current.getEndIntersection().getOutRoads()){
														
					//If end intersection of nextRoad is known
					if(!evaluatedSet.contains(next)  && next.getEndIntersection() != null){
						
						//Evaluate later
						if(!toEvaluateSet.contains(next))
							toEvaluateSet.add(next);
						
						//Failure
						if(!costs.containsKey(next))
							return path;
							
						int transitPenalty = 0;
						
						//If the road has transit, then it has a penalty to the costs
						if(next.isBlocked())
							transitPenalty = CityMap.getTransitPenalty(map, next.getName());
				
						//Calculate costs from the start to the current road being analyzed plus the nextRoad
						int cost_next = costs.get(current) + current.getEndIntersection().getLength() + next.getLength() + transitPenalty;
						
						//If the costs are cheaper then the previous one registed for this nextRoad
						if(cost_next < costs.get(next)){
							cameFrom.put(next, current);
							costs.put(next, cost_next);
							
							//Costs from the start to end of the road plus an estimate cost from the nextRoad to the destination
							final_costs.put(next, cost_next + Point.getDistance(next.getEndIntersection().getOneEntry(), destination));
						}
					}
				}
			}
			
			
		}
		return path;
	}
	
	/**
	 * Method that builds the path from to the destination.
	 * @param cameFrom
	 * @param last
	 * @return
	 */
	private static ArrayList<String> buildPath(HashMap<Road,Road> cameFrom, Road last){
		ArrayList<String> path = new ArrayList<String>();
		
		path.add(last.getName());
		
		while(cameFrom.containsKey(last)){
			last = cameFrom.get(last);
			path.add(0, last.getName());
		}
		
		return path;
	};
	
	/**
	 * Method that gets the minimum cost road in the set to be evaluated.
	 * @param toEvaluateSet
	 * @param cost
	 * @return
	 */
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
