package cityStructure;

import java.util.ArrayList;
import java.util.HashMap;

import resources.Point;

public class SimpleIntersection extends Intersection{
		
	public SimpleIntersection(Point area, String name) {
		super(area, name);
	}

	/**
	 * Constructor
	 * Creates a new SimpleIntersection equal to i
	 * but withour any roads associated
	 * @param i
	 */
	private SimpleIntersection(SimpleIntersection i){
		super(i.getCleanedEntries(),i.getLength(),i.getName());
	}
	
	@Override
	public SimpleIntersection getIntersectionPerception(){
		return new SimpleIntersection(this);
	}

	@Override
	public ArrayList<Point> getRouteToRoad(Road roadEntry, Road roadOut) {
		ArrayList<Point> route = new ArrayList<Point>();
		
		if(isOutRoad(roadOut)){
			route.add(getArea());
			route.add(roadOut.getStartPoint());
		}
		
		return route;
	}

	@Override
	public Point getArea() {
		Point ret = null;
		
		for(Point p : entries.keySet())
			ret = p;
		
		return ret;
	}

}
