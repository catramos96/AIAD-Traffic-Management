package agents;

import java.util.ArrayList;

import cityStructure.Intersection;
import cityStructure.Map;
import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources.Direction;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

/**
 * Repast Agent to display the map.
 *
 */
public class City extends Agent{

	private Map map;
	private Grid<Object> space;
	private ContainerController container;
	
	/**
	 * Constructor
	 */
	public City(Grid<Object> space, ContainerController container){
		map = new Map();
		this.space = space;
		this.container = container;
		
		/*Create semaphores in the intersections*/
	    for(Intersection i : map.getIntersections()){
	    	if(i.getInRoads().size() > 1){
				ArrayList<Point> controlPoints = new ArrayList<Point>();
				
				for(Road r : i.getInRoads()){
					controlPoints.add(r.getEndPoint());
				}
				new SemaphoreManager(space,container,controlPoints);
			}
	    }
	};
	
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
	
	public Map getMap(){
		return map;
	}
	
	
}
