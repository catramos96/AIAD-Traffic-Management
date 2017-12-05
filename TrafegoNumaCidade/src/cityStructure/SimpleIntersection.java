package cityStructure;

import java.util.ArrayList;
import java.util.HashMap;

import cityStructure.Intersection.CellEntry;
import resources.Point;

public class SimpleIntersection extends Intersection{
		
	public SimpleIntersection(Point area, String name) {
		super(area, name);
	}
	
	private SimpleIntersection(HashMap<Point,HashMap<CellEntry,Road>> entries, ArrayList<Road> inRoads, ArrayList<Road> outRoads, String name, int length){
		super(entries,inRoads,outRoads,name,length);
	}
	
	public SimpleIntersection getPerception(){
		HashMap<Point,HashMap<CellEntry,Road>> e = null;
		ArrayList<Road> iR = new ArrayList<Road>();
		ArrayList<Road> oR = new ArrayList<Road>();
		
		e = this.getPerceptionsEntries(iR, oR);
		
		SimpleIntersection inter = new SimpleIntersection(e,iR,oR,name,length);
		
		for(Road i : inter.getInRoads())
			i.setEndIntersection(inter);
		
		for(Road o : inter.getOutRoads())
			o.setStartIntersection(inter);
		
		return inter;
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
