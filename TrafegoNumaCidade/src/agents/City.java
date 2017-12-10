package agents;

import java.sql.Timestamp;
import java.util.ArrayList;

import cityStructure.Intersection;
import cityStructure.CityMap;
import cityStructure.Road;
import jade.wrapper.StaleProxyException;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources.Direction;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

/**
 * Agent that represents the city structure and displays in the space the image correspondent 
 * to the map.
 * It's responsible of creating the semaphore managers regarding the intersections in the 
 * city structure.
 */
public class City extends Agent{

	private CityMap map;					//city structure
	private Grid<Object> space;				//space where the agent is placed
	private Point dimensions;
	private ContainerController container;
	
	/**
	 * Constructor.
	 * @param space - Space where the agent will be placed.
	 * @param dimensions - Point with the weight and height of the map.
	 * @param container - container of agents
	 * @param map_txt - filename to load the city structure.
	 */
	public City(Grid<Object> space, Point dimensions, ContainerController container, String map_txt){
		map = new CityMap(dimensions);
		map.load(map_txt);
		
		this.dimensions = dimensions;
		this.container = container;
		this.space = space;
		
		//Creates a semaphore manager for each intersection if the
		//there is more than one in road.
	    for(Intersection i : map.getIntersections().values()){
	    	if(i.getInRoads().size() > 1){
				ArrayList<Point> controlPoints = new ArrayList<Point>();
				
				for(Road r : i.getInRoads()){
					controlPoints.add(r.getEndPoint());
				}
				new SemaphoreManager(space,container,controlPoints);
			}
	    }
	};
	
	/**
	 * Method that gives a random points belonging to the
	 * given road.
	 * @param r - Road
	 * @return
	 */
	public Point getRandomRoadPosition(Road r){
		Direction d = r.getDirection();
		
		switch (d) {
		case South: case North:
		{
			int rnd_pos = (int)(Math.random() * r.getLength());
			
			if(r.getStartPoint().y < r.getEndPoint().y)
				return new Point(r.getEndPoint().x,r.getStartPoint().y + rnd_pos);
			else
				return new Point(r.getEndPoint().x,r.getEndPoint().y + rnd_pos);
		}
		case East: case West:
		{
			int rnd_pos = (int)(Math.random() * r.getLength());

			if(r.getStartPoint().x < r.getEndPoint().x)
				return new Point(r.getStartPoint().x + rnd_pos, r.getStartPoint().y);
			else
				return new Point(r.getEndPoint().x + rnd_pos, r.getStartPoint().y);

		}
		default:
			return new Point(0,0);
		}
	}
	
	/*
	 * GETS & SETS
	 */
	
	/**
	 * Gets the city structure.
	 * @return
	 */
	public CityMap getMap(){
		return map;
	}

	/**
	 * Gets the space where the agents are placed.
	 * @return
	 */
	public Grid<Object> getSpace() {
		return space;
	}

	/**
	 * Sets the space where the agents are placed.
	 * @param space
	 */
	public void setSpace(Grid<Object> space) {
		this.space = space;
	}
	
	/**
	 * 
	 * @param n
	 * @throws StaleProxyException 
	 */
	@ScheduledMethod(start=1 , interval=100000)
	public void createRandomCar() {
		System.out.println("----------------");
		
		int rnd_road;
		Road startRoad = null, endRoad = null;
		Point origin = null, destination = null;
		
		boolean position_ok = false;
		
		//Search Origin Random
		while(!position_ok){
			
			rnd_road = (int)(Math.random() * map.getRoads().size());
			startRoad = (Road) map.getRoads().values().toArray()[rnd_road];
			origin = getRandomRoadPosition(startRoad);
			
			position_ok = true;

			//check if there are no cars at the location
			/*for(int j = 0; j < n; j++){
				if(SpaceResources.hasCar(space, origin) != null)
					position_ok = false;
			}*/
		}
		
		//Search Destination Random
		rnd_road = (int)(Math.random() * map.getRoads().size());
		endRoad = (Road) map.getRoads().values().toArray()[rnd_road];
		destination = getRandomRoadPosition(endRoad);

		// Search Destination Random
		rnd_road = (int) (Math.random() * map.getRoads().size());
		endRoad = (Road) map.getRoads().values().toArray()[rnd_road];
		destination = getRandomRoadPosition(endRoad);
		
		// the car has previous knowledge of the city
		CarSerializable know = new CarSerializable(dimensions);
		know.setCityKnowledge(map);
		Car car = new CarNoneLearning(space, origin, startRoad, destination, know);
	
		try {
			long timestamp =  (new Timestamp(System.currentTimeMillis())).getTime();
			container.acceptNewAgent("CarRandom"+timestamp, car).start();
			space.getAdder().add(space, car);
			car.setPosition(origin);
			System.out.println(car.print());
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}
