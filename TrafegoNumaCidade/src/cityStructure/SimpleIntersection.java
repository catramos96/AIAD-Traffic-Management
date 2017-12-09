package cityStructure;

import java.util.ArrayList;
import java.util.HashMap;
import resources.Point;

/**
 * Intersection with one point of area.
 * 
 * Example:
 * 
 *  Area with 1 point	
 * 4 Possible Roads			
 * SimpleIntersection		
 * 
 * 		_| |_						
 * 		_ X _ 					 
 * 	 	 | |
 *
 */
public class SimpleIntersection extends Intersection{
		
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param area
	 * @param name
	 */
	public SimpleIntersection(Point area, String name) {
		super(area, name);
	}	

	@Override
	public ArrayList<Point> getRouteToRoad(String roadInName, String roadOutName) {
		ArrayList<Point> route = new ArrayList<Point>();
		Road out = isOutRoad(roadOutName);
		Road in = isInRoad(roadInName);
		
		if(out != null && in !=null){
			route.add(getArea());
			route.add(out.getStartPoint());
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
