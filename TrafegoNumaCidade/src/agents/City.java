package agents;

import java.util.ArrayList;

import cityStructure.Intersection;
import cityStructure.CityMap;
import cityStructure.Road;
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
	
	/**
	 * Constructor.
	 * @param space - Space where the agent will be placed.
	 * @param dimensions - Point with the weight and height of the map.
	 * @param container - container of agents
	 * @param map_txt - filename to load the city structure.
	 */
	public City(Grid<Object> space, Point dimensions,ContainerController container, String map_txt){
		map = new CityMap(dimensions);
		map.load(map_txt);
		
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
	
	
}
