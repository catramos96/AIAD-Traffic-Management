package trafegoNumaCidade;

import java.util.ArrayList;

public class ComplexIntersection extends Intersection{

	public ComplexIntersection(ArrayList<Point> area, String name) {
		super(area, name);
	}

	@Override
	public ArrayList<Point> getRouteToRoad(Road roadEntry, Road roadOut) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Point> getArea() {
		ArrayList<Point> ret = new ArrayList<Point>();
		
		for(Point p : entries.keySet())
			ret.add(p);
		
		return ret;
	}

}
