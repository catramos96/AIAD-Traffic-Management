package behaviors;

import java.util.ArrayList;
import java.util.Random;

import agents.Car;
import agents.Car.LearningMode;
import agents.CarMonitored;
import cityStructure.Road;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import resources.SpaceResources.PassageType;
import sajas.core.behaviours.TickerBehaviour;

/**
 * Behavior that sets the position of the car across the (real)
 * city structure and respecting the semaphores, the transit
 * and roads direction.
 *
 */
public class CarMovement extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	private Car car = null;
	
	//Current passage
	private PassageType passageType = PassageType.Road;
	//Route to be performed to get out of an intersection and go to one of its out roads
	private ArrayList<Point> intersectionRoute = new ArrayList<Point>();
	
	//Semaphore at the end of the road
	private boolean greenSemaphore = true;
	
	/**
	 * Constructor.
	 * @param car
	 * @param time
	 */
	public CarMovement(Car car, long time){
		super(car,time);
		this.car = car;
		
		//Tries to calculate a path to its destination
		if(car.getJourney().size() == 0 && !car.getLearningMode().equals(LearningMode.APPLYING))
			car.calculateAndUpdateJourney();
	}

	@Override
	protected void onTick() {
		
		//try
		//{
			Point pos = car.getPosition();
			
			//It reached it's destination
			if(car.getDestination().equals(pos) && !car.getLearningMode().equals(LearningMode.LEARNING)){
				passageType = PassageType.Out;
				car.takeDown();
			}
			
			if(passageType.equals(PassageType.Road) && car.getRoad() != null){
				
				//End of the road
				if(pos.equals(car.getRoad().getEndPoint()))	
					handleEndOfRoad(pos);
				//Is inside the road boundaries
				else
					handleRoadTrip(pos);
			}
			
			if(passageType.equals(PassageType.Intersection))
				handleIntersection();
			
		//}
		/*catch(Exception e){
			
			System.out.println(car.getLocalName() + ":Movement Exception " + e.getMessage());
			if(car.getRoad() == null)
				System.out.println("NULL ROAD");
			if(car.getIntersection() == null)
				System.out.println("NULL INTERSECTION");
			
			car.removeBehaviour(this);
			car.takeDown();
		}*/
		
	}
	
	/**
	 * Method that handles the end of the road and
	 * fills the intersection route to go to a certain road.
	 * @param pos
	 */
	private void handleEndOfRoad(Point pos){
		//Check semaphores
		if(SpaceResources.hasRedOrYellowSemaphore(car.getSpace(), pos) != null)
			greenSemaphore = false;
		else
			greenSemaphore = true;
		
		//If it's OK to advance
		if(greenSemaphore){
			boolean newIntersection = !car.getCityKnowledge().getIntersections().containsKey(car.getRoad().getEndIntersection().getName());
			car.setIntersection(car.getRoad().getEndIntersection());
			
			Road nextRoad = null;
			boolean valid = true;    					
			
			//Tries to calculate a journey if there is none
			if(car.getJourney().size() == 0)
				car.calculateAndUpdateJourney();
			
			//Follows the journey if any
			if(car.getJourney().size() != 0){
				
				//Checks if the next road to follow is a road out of the intersection
				nextRoad = car.getIntersection().isOutRoad(car.getJourney().get(0));
				
				if(nextRoad == null){
					valid = false;
				}
				else{
					//removes first road in the journey
					car.jorneyConsume();
					
					//get route to go to the road
					intersectionRoute = car.getIntersection().getRouteToRoad(car.getRoad().getName(), nextRoad.getName());
					
					//no valid route
					if(intersectionRoute.size() == 0){
						valid = false;
						car.getJourney().clear();
					}
				}
			}
			else
				valid = false;
			
			//failed to calculate journey
			if(!valid){
				
				//chooses a road not visited before
				ArrayList<Road> possibleRoads = car.getIntersection().getOutRoads();
				Road tmp = null;
				Point destination = null;
				int minDist = SpaceResources.INFINITE;
				
				
				if(car.getDestinationName() != null){
					if(car.getCityKnowledge().getRoads().containsKey(car.getDestinationName())){
						Road d = car.getCityKnowledge().getRoads().get(car.getDestinationName());
						if(d.getStartIntersection() != null)
							destination = d.getStartIntersection().getOneEntry();
					}
				}
				if(destination == null)
					destination = car.getDestination();
				
				boolean hasUnvisited = false;
				
				for(Road r : possibleRoads){
					
					boolean isUnvisited = false;
					
					//Road is destination
					if(car.getDestinationName() != null && 
							car.getLearningMode().equals(LearningMode.SHORT_LEARNING)){
						if(r.getName().equals(car.getDestinationName())){
							tmp = r;
							break;
						}
					}
					//Take into account the unvisited roads in case
					//The current intersections was already known before
					if(!newIntersection && car.getUnexploredRoads().containsKey(r.getName())){
						
						//clears min value
						if(!hasUnvisited){
							minDist = SpaceResources.INFINITE;
							hasUnvisited = true;
						}
						
						isUnvisited = true;
					}
										
					//Chooses the road closer to the destination
					//It gives priority to unvisited roads
					if(!hasUnvisited || (hasUnvisited && isUnvisited)){
						int dist = Point.getDistance(destination, r.getStartPoint());

						if(dist < minDist){
							minDist = dist;
							tmp = r;
							
						}
					}
				}
				
				nextRoad = tmp;
				
				//if it visited all roads, then the next road would be random
				if(nextRoad == null){
					Random r = new Random();
					int road_index = r.nextInt(possibleRoads.size()-1);
					nextRoad = possibleRoads.get(road_index);
				}
				
				intersectionRoute = car.getIntersection().getRouteToRoad(car.getRoad().getName(), nextRoad.getName());
			}
			
			//update the current road
			car.setRoad(nextRoad);
			
			if(!valid && !car.getLearningMode().equals(LearningMode.APPLYING)){
				car.calculateAndUpdateJourney();	
			}
			
			//Perform the intersection route
			passageType = SpaceResources.PassageType.Intersection;
		}
	}

	/**
	 * Method that handles the road trip.
	 * @param pos
	 */
	private void handleRoadTrip(Point pos){
		Point next_position = Resources.incrementDirection(car.getRoad().getDirection(), pos);

		//Only advance if there is no car in the next position
		if(SpaceResources.hasCar(car.getSpace(), next_position) == null)
			car.setPosition(next_position);
	}

	/**
	 * Method that handles an intersection passage.
	 */
	private void handleIntersection(){
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
