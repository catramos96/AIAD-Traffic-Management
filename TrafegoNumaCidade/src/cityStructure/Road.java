package cityStructure;
import java.io.Serializable;

import cityStructure.Intersection.EntryType;
import resources.Point;
import resources.Resources;
import resources.Resources.Direction;
import resources.SpaceResources;

/**
 * Class that represents a road in a city structure.
 * @author Catarina
 *
 */
public class Road implements Serializable {

	private static final long serialVersionUID = 1L;
	private Point startPoint;					//Entry point in its Start intersection
	private Point endPoint;						//Entry point in its End Intersection
	
	private Intersection startIntersection = null;
	private Intersection endIntersection = null;
	
	private Resources.Direction direction;
	
	private String name = new String();
	private boolean blocked = false;			//Blocked by transit?
	
	private int length = SpaceResources.INFINITE;	
	
	/**
	 * Constructor.
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
		
		//Find out which is the start and the end intersection
		
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
			//Updates direction and length
			
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
	 * Constructor for Perception Road.
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
	
	/**
	 * Gets the perception that is taken of the road when the road is discovered
	 * at its start intersection.
	 * @return
	 */
	public Road getRoadPerceptionAtStart(){
		return new Road(startPoint,null,null, null,direction,SpaceResources.INFINITE,name);
	}

	/**
	 * Gets the perception that is taken of the road when the road is discovered
	 * at its end intersection.
	 * @return
	 */
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
	
	/**
	 * Method that return a String with the main information about the object.
	 * @return
	 */
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
	
	/**
	 * Sets the start point of the road.
	 * @param s
	 */
	public void setStartPoint(Point s){
		startPoint = s;
	}
	
	/**
	 * Sets the end point of the road.
	 * @param e
	 */
	public void setEndPoint(Point e){
		endPoint = e;
	}
	
	/**
	 * Updates the length of the road if the start and the end points
	 * are known.
	 */
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
	
	/**
	 * Gets the end intersection of the road.
	 * @return
	 */
	public Intersection getEndIntersection(){
		return endIntersection;
	}
	
	/**
	 * Gets the start intersection of the road.
	 * @return
	 */
	public Intersection getStartIntersection(){
		return startIntersection;
	}
	
	/**
	 * To be used just by the behavior LearnMap
	 * @param endInt
	 */
	public void setEndIntersection(Intersection endInt){
		endIntersection = endInt;
	}
	
	/**
	 * To be used just by the behavior LearnMap
	 * @param startInt
	 */
	public void setStartIntersection(Intersection startInt){
		startIntersection = startInt;
	}
	
	/**
	 * Gets the road direction.
	 * @return
	 */
	public Resources.Direction getDirection(){
		return direction;
	}
	
	/**
	 * Gets the start point of the road.
	 * @return
	 */
	public Point getStartPoint(){
		return startPoint;
	}
	
	/**
	 * Gets the end point of the road.
	 * @return
	 */
	public Point getEndPoint(){
		return endPoint;
	}
	
	/**
	 * Gets the road name.
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the length of the road.
	 * @return
	 */
	public int getLength(){
		return length;
	}
	
	/**
	 * Sets the blocked attribute to true.
	 */
	public void blocked(){
		blocked = true;
	}
	
	/**
	 * Sets the blocked attribute to false;
	 */
	public void unblocked(){
		blocked = false;
	}
	
	/**
	 * Returns true if the road is blocked or false otherwise.
	 * @return
	 */
	public boolean isBlocked(){
		return blocked;
	}
}
