package trafegoNumaCidade;

import java.util.ArrayList;
import java.util.HashMap;
import trafegoNumaCidade.Point;

import trafegoNumaCidade.Semaphore;

public class Intersection extends CityElement{
	private enum IntersectionWay {Right, Left, Ahead};
	//Intersection Roads Entry
	private Point bottomRoadEntry;
	private Point topRoadEntry;
	private Point leftRoadEntry;
	private Point rightRoadEntry;
	
	private ArrayList<Road> inRoads = new ArrayList<Road>();
	private ArrayList<Road> outRoads = new ArrayList<Road>();
	private Semaphore semaphore = null;
	
	/*
	 * Area with 4 points		Area with 1 point
	 * 8 Possible Roads			4 Possible Roads
	 * 
	 * 	_|||_						_| |_
	 * 	_X|X_						_ X _		
	 *  _X|X_	 					 | |
	 * 	 |||
	 */
	
	//Substituir por uma matriz e deduzir as entries
	private Point area;
	private HashMap<Point,Road> entries = new HashMap<Point,Road>();
							
	
	public Intersection(Point area, String name){
		this.area = area;
		this.name = name;
		
		bottomRoadEntry = new Point(area.x,area.y-1);
		topRoadEntry = new Point(area.x,area.y+1);
		leftRoadEntry = new Point(area.x-1,area.y);
		rightRoadEntry = new Point(area.x+1,area.y);
		
		entries.put(bottomRoadEntry,null);
		entries.put(topRoadEntry, null);
		entries.put(leftRoadEntry,null);
		entries.put(rightRoadEntry, null);
	}
	
	public ArrayList<Point> getRouteToRoad(Road roadEntry,Road roadOut){
		
		ArrayList<Point> route = new ArrayList<Point>();
		
		if(isOutRoad(roadOut)){
			route.add(area);
			route.add(roadOut.getStartPoint());
		}
		return route;
	}
	
	/*
	 * GETS & SETS
	 */
	public boolean isOutRoad(Road r){
		for(int i = 0; i < outRoads.size(); i++){
			if(outRoads.get(i).equals(r))
				return true;
		}
		return false;
	}
	public boolean insertRoad(Road r){
		Point startPoint = r.getStartPoint();
		Point endPoint = r.getEndPoint();

		//System.out.println("\nstart: " + startPoint.x + " " + startPoint.y + "\nend: " + endPoint.x + " " + endPoint.y);
		for(Point p : entries.keySet()){
			
			//System.out.println("Entry: " + p.x + " " + p.y);
			
			if(p.x == startPoint.x && p.y == startPoint.y && entries.get(p) == null){
				entries.put(p, r);
				outRoads.add(r);
				r.setStartIntersection(this);
				return true;
			}
			else if(p.x == endPoint.x && p.y == endPoint.y && entries.get(p) == null){
				entries.put(p, r);
				inRoads.add(r);
				r.setEndIntersection(this);
				return true;
			}
		}
		System.out.println("COULDN'T INSERT ROAD");
		return false;
	}
	
	public void setSemaphore(Semaphore s){
		semaphore = s;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public ArrayList<Road> getOutRoads(){
		return outRoads;
	}
	
	public ArrayList<Road> getInRoads(){
		return inRoads;
	}
	
	public Semaphore getSemaphore(){
		return semaphore;
	}
	
	public Point getArea(){
		return area;
	}
}
