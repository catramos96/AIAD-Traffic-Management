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
		
		ArrayList<Road> j = AStar.shortestPath(car.getCityKnowledge(), car.getRoad(), car.getDestination());
		car.setJorney(j);
		car.jorneyConsume();
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
						
						if(car.getJorney().size() != 0){
							nextRoad = car.getJorney().get(0);
							car.jorneyConsume();
							intersectionRoute = car.getIntersection().getRouteToRoad(car.getRoad(), nextRoad);
							
							//no route found
							if(intersectionRoute.size() == 0){
								valid = false;
								car.setJorney(new ArrayList<Road>());
							}
						}
						else
							valid = false;
						
						if(!valid){
							//randomly chooses a road out (real city structure)

							String intName = car.getIntersection().getName();
							Intersection realDataInt = car.getMap().getIntersections().get(intName);
							
							int road_index = (int) (Math.random() * realDataInt.getOutRoads().size());
							nextRoad = realDataInt.getOutRoads().get(road_index);
							intersectionRoute = realDataInt.getRouteToRoad(car.getRoad(), nextRoad);
						}
						
						car.setRoad(nextRoad);
						
						//Calculate a new path starting on the nextRoad in case
						//the current path is not valid
						//using the their city knowledge
						if(!valid){
							ArrayList<Road> j = AStar.shortestPath(car.getCityKnowledge(), car.getRoad(), car.getDestination());
							car.setJorney(j);	
							car.jorneyConsume();
						}
						
						passageType = SpaceResources.PassageType.Intersection;
					}
				}
				else{
					Point next_position = Resources.incrementDirection(car.getRoad().getDirection(), pos);

					if(SpaceResources.hasCar(car.getSpace(), next_position) == null)
						car.setPosition(next_position);
				}
			}
			
			if(passageType.equals(PassageType.Intersection)){	
				
				Point next_position = intersectionRoute.get(0);

				if(SpaceResources.hasCar(car.getSpace(), next_position) == null){
					
					car.setPosition(next_position);
					intersectionRoute.remove(0);
					
					if(intersectionRoute.size()== 0)
						passageType = PassageType.Road;
				}
			}			
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}

}
