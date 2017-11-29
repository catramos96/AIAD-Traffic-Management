package trafegoNumaCidade;

import java.util.ArrayList;

public class SimpleIntersection extends Intersection{
		


	public SimpleIntersection(Point area, String name) {
		super(area, name);
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
