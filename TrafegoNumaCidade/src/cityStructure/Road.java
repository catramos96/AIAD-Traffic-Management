package cityStructure;
import java.io.Serializable;

import cityStructure.Intersection.EntryType;
import resources.Point;
import resources.Resources;
import resources.Resources.Direction;
import resources.SpaceResources;

public class Road implements Serializable {

	private static final long serialVersionUID = 1L;
	private Point startPoint;	//Out road of an intersection
	private Point endPoint;		//In road of an intersection
	
	private Intersection startIntersection = null;
	private Intersection endIntersection = null;
	private Resources.Direction direction;
	private String name = new String();
	private boolean blocked = false;
	
	private int length = SpaceResources.INFINITE;		//= weight in Dijkstra
	
	/**
	 * Constructor
	 * @param start
	 * @param end
	 * @param i1
	 * @param i2
	 * @param name
	 */
	public Road(Point start, Point end, Intersection i1, Intersection i2, String name){
		this.name = name;
		startPoint = start;
		endPoint = end;
		
		EntryType entry1 = i1.insertRoad(this);
		EntryType entry2 = i2.insertRoad(this);
		
		boolean insertOk = false;
		
		if(entry1.equals(EntryType.In)){
			endIntersection = i1;
			if(entry2.equals(EntryType.Out)){
				startIntersection = i2;
				insertOk = true;
			}
		}
		else if(entry2.equals(EntryType.In)){
			endIntersection = i2;
			if(entry1.equals(EntryType.Out)){
				startIntersection = i1;
				insertOk = true;
			}
		}
		
		if(insertOk){
			direction = Resources.getDirection(startPoint, endPoint);
			
			switch (direction) {
				case North: case South:
					length =  Math.abs(startPoint.y - endPoint.y) +1;
					break;
				case East: case West:
					length = Math.abs(startPoint.x - endPoint.x) + 1;
				default:
					break;
			}
		}
		else{
			System.out.println("Intersection insertion incorrect");
		}
	}
	
	/**
	 * Constructor for Perception Road
	 * @param start
	 * @param end
	 * @param d
	 * @param length
	 * @param name
	 */
	private Road(Point start,Point end, Intersection startInt, Intersection endInt, Direction d, int length, String name){
		this.name = name;
		this.startPoint = start;
		this.endPoint = end;
		this.direction = d;
		this.startIntersection = startInt;
		this.endIntersection = endInt;
		this.length = length;
	}
	
	public Road getRoadPerceptionAtStart(){
		return new Road(startPoint,null,null, null,direction,SpaceResources.INFINITE,name);
	}

	public Road getRoadPerceptionAtEnd(){
		return new Road(null,endPoint,null, null, direction,SpaceResources.INFINITE,name);
	}
	
	/**
	 * Function that returns an array with the length to reach the end of the road.
	 * If the @param point isn't part of the road, then the array content will
	 * be [-1,-1].
	 * @param point
	 * @return array with the length to the end of the road
	 */
	public boolean partOfRoad(Point point){

		if(startPoint != null && endPoint != null){
			if(direction.equals(Resources.Direction.East)){
				if(point.x >= startPoint.x && point.x <= endPoint.x && point.y == startPoint.y)
					return true;
			}
			else if(direction.equals(Resources.Direction.West)){
				if(point.x <= startPoint.x && point.x >= endPoint.x && point.y == startPoint.y)
					return true;
			}
			else if(direction.equals(Resources.Direction.North)){
				if(point.y >= startPoint.y && point.y <= endPoint.y && point.x == startPoint.x)
					return true;
			}
			else if(direction.equals(Resources.Direction.South)){
				if(point.y <= startPoint.y && point.y >= endPoint.y && point.x == startPoint.x)
					return true;
			}
		}
		
			
		return false;
	}
	
	public String print(){
		String s = "RoadName: " + name;
		
		s+= " Direction: " + direction.toString();
		
		s+=" StartPoint: ";
		
		if(startPoint == null)	s+= "?";
		else					s+= startPoint.print();
		
		s+= " EndPoint: ";
		
		if(endPoint == null)	s+= "?";
		else					s+= endPoint.print();
		
		s+= " StartInterName: ";
		
		if(startIntersection == null)	s+= "?";
		else							s+= startIntersection.getName();
		
		s+= " EndInterName: ";
		
		if(endIntersection == null)		s+= "?";
		else							s+= endIntersection.getName();
		
		s+= " Length: " + length;
		
		return s;
	}
	
	/*
	 * GETS & SETS
	 */
	
	public void setStartPoint(Point s){
		startPoint = s;
	}
	
	public void setEndPoint(Point e){
		endPoint = e;
	}
	
	public void updateLength(){
		if(startPoint != null && endPoint != null){
			switch (direction) {
			case North: case South:
				this.length =  Math.abs(startPoint.y - endPoint.y) +1;
				break;
			case East: case West:
				this.length = Math.abs(startPoint.x - endPoint.x) + 1;
			default:
				break;
			}
		}
	}
	
	public Intersection getEndIntersection(){
		return endIntersection;
	}
	
	public Intersection getStartIntersection(){
		return startIntersection;
	}
	
	/**
	 * To be used just by the behaviour LearnMap
	 * @param endInt
	 */
	public void setEndIntersection(Intersection endInt){
		endIntersection = endInt;
	}
	
	/**
	 * To be used just by the behaviour LearnMap
	 * @param endInt
	 */
	public void setStartIntersection(Intersection startInt){
		startIntersection = startInt;
	}
	
	public Resources.Direction getDirection(){
		return direction;
	}
	
	public Point getStartPoint(){
		return startPoint;
	}
	
	public Point getEndPoint(){
		return endPoint;
	}
	
	public String getName(){
		return name;
	}
	
	public int getLength(){
		return length;
	}
	
	public void blocked(){
		blocked = true;
	}
	
	public void unblocked(){
		blocked = false;
	}
	
	public boolean isBlocked(){
		return blocked;
	}
}
