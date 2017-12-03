package cityStructure;
import cityStructure.Intersection.EntryType;
import resources.Point;
import resources.Resources;

public class Road{
	private Point startPoint;	//Out road of an intersection
	private Point endPoint;		//In road of an intersection
	
	private Intersection startIntersection = null;
	private Intersection endIntersection = null;
	private Resources.Direction direction;
	private String name = new String();
	
	private int length = 1;		//= weight in Dijkstra
	
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
	 * Function that returns an array with the length to reach the end of the road.
	 * If the @param point isn't part of the road, then the array content will
	 * be [-1,-1].
	 * @param point
	 * @return array with the length to the end of the road
	 */
	public boolean partOfRoad(Point point){

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
			
		return false;
	}
	
	/*
	 * GETS & SETS
	 */
	
	public Intersection getEndIntersection(){
		return endIntersection;
	}
	
	public Intersection getStartIntersection(){
		return startIntersection;
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
}
