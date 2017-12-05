package behaviours;

import java.util.ArrayList;

import agents.CarAgent;
import algorithms.AStar;
import cityStructure.Intersection;
import cityStructure.Road;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import resources.SpaceResources.PassageType;
import sajas.core.behaviours.TickerBehaviour;

public class CarMovement extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	private CarAgent car = null;
	
	private PassageType passageType = PassageType.Road;
	private ArrayList<Point> intersectionRoute = new ArrayList<Point>();
	
	//Semaphore
	private boolean greenSemaphore = true;
	
	public CarMovement(CarAgent car, long time){
		super(car,time);
		this.car = car;
		
		if(car.getDestinationName() != null){
			//calculate the path to the destination based on the knowledge he haves
			ArrayList<String> j = AStar.shortestPath(car.getCityKnowledge(), car.getRoad(), car.getDestinationName());
			car.setJorney(j);
			car.jorneyConsume();	
		}
	}

	@Override
	protected void onTick() {
		
		try
		{
			Point pos = car.getPosition();
			
			if(car.getDestination().equals(pos)){
				passageType = PassageType.Out;
				car.takeDown();
			}
			
			if(passageType.equals(PassageType.Road) && car.getRoad() != null){
				
				//End of the road
				if(pos.equals(car.getRoad().getEndPoint())){		
					
					//Check semaphores
					if(SpaceResources.hasRedOrYellowSemaphore(car.getSpace(), pos) != null)
						greenSemaphore = false;
					else
						greenSemaphore = true;
					
					//If it's ok to advance
					if(greenSemaphore){
						car.setIntersection(car.getRoad().getEndIntersection());
						
						Road nextRoad = null;
						boolean valid = true;
						
						//follow the jorney
						if(car.getJorney().size() != 0){
							
							//checks if the next road to follow is a road out of the intersection (real world)
							nextRoad = car.getIntersection().isOutRoad(car.getJorney().get(0));
							
							if(nextRoad == null)
								valid = false;
							else{
								car.jorneyConsume();
								
								//get route to go to the road
								intersectionRoute = car.getIntersection().getRouteToRoad(car.getRoad().getName(), nextRoad.getName());
								
								//no valid route
								if(intersectionRoute.size() == 0){
									valid = false;
									car.setJorney(new ArrayList<String>());
								}
							}
						}
						else
							valid = false;
						
						//if the previous jorney wasn't valid
						if(!valid){
							//chooses a road not visited before
							//if it visited all roads, then the next road would be random
							ArrayList<Road> unvisitedRoads = car.getIntersection().getOutRoads();
							
							for(Road r : unvisitedRoads){
								if(car.getCityKnowledge().getRoads().containsKey(r))
									unvisitedRoads.remove(r);
							}
							
							if(unvisitedRoads.size() == 0)
								unvisitedRoads = car.getIntersection().getOutRoads();
							
							int road_index = (int) (Math.random() * unvisitedRoads.size());
							nextRoad = unvisitedRoads.get(road_index);
							intersectionRoute = car.getIntersection().getRouteToRoad(car.getRoad().getName(), nextRoad.getName());
						}
						
						//update the current road
						car.setRoad(nextRoad);
						
						//if previous jorney wasn't valid
						if(!valid && car.getDestinationName() != null){
							
							//calculate the path to the destination based on the knowledge that he haves
							ArrayList<String> j = AStar.shortestPath(car.getCityKnowledge(), car.getRoad(), car.getDestinationName());
							car.setJorney(j);
							car.jorneyConsume();		
						}
						
						//Perform the intersection route
						passageType = SpaceResources.PassageType.Intersection;
					}
				}
				//Continue in the current road
				else{
					Point next_position = Resources.incrementDirection(car.getRoad().getDirection(), pos);

					//Only advance if there is no car in the next position
					if(SpaceResources.hasCar(car.getSpace(), next_position) == null)
						car.setPosition(next_position);
				}
			}
			
			if(passageType.equals(PassageType.Intersection)){	
				
				//Follow the route to get out of the intersection
				Point next_position = intersectionRoute.get(0);
				

				//Only advance if there is no car in the next position
				if(SpaceResources.hasCar(car.getSpace(), next_position) == null){
					car.setPosition(next_position);
					intersectionRoute.remove(0);
					
					//if out of the intersection -> continue in the road
					if(intersectionRoute.size()== 0)
						passageType = PassageType.Road;
				}
			}			
			
		}
		catch(Exception e){
			System.out.println("Exception " + e.getMessage());
		}
		
	}

}
