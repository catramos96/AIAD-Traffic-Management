package trafegoNumaCidade;

import trafegoNumaCidade.Intersection.RoadEntry;

public class Road extends CityElement{
	private Point startPoint;	//Out road of an intersection
	private Point endPoint;		//In road of an intersection
	private Intersection startIntersection;
	private Intersection endIntersection;
	private Point direction = new Point(0,0);
	
	public Road(Point start, Point end, Intersection i1, Intersection i2){
		startPoint = start;
		endPoint = end;
		
		RoadEntry entry1 = i1.insertRoad(this);
		RoadEntry entry2 = i2.insertRoad(this);
		
		boolean insertOk = false;
		
		if(entry1.equals(RoadEntry.In)){
			endIntersection = i1;
			if(entry2.equals(RoadEntry.Out)){
				startIntersection = i2;
				insertOk = true;
			}
		}
		else if(entry2.equals(RoadEntry.In)){
			endIntersection = i2;
			if(entry1.equals(RoadEntry.Out)){
				startIntersection = i1;
				insertOk = true;
			}
		}
		
		if(insertOk)
			updateDirection();
		else
			System.out.println("Direction not updated");
		
	}
	
	/**
	 * Function that returns an array with the length to reach the end of the road.
	 * If the @param point isn't part of the road, then the array content will
	 * be [-1,-1].
	 * @param point
	 * @return array with the length to the end of the road
	 */
	public Point partOfRoad(Point point){
		//Direction East
		if(direction.x > 0 && point.y == startPoint.y){
			if(point.x >= startPoint.x && point.x <= endPoint.x)
				return new Point(endPoint.x - point.x,0);
		}
		//Direction West
		else if(direction.x < 0 && point.y == startPoint.y){
			if(point.x <= startPoint.x && point.x >= endPoint.x)
				return new Point(startPoint.x - point.x,0);
		}
		//Direction North
		else if(direction.y > 0 && point.x == startPoint.x){
			if(point.y >= startPoint.y && point.y <= endPoint.y)
				return new Point(0,endPoint.y - point.y);
		}
		//Direction South
		else if(direction.y < 0 && point.x == startPoint.x){
			if(point.y <= startPoint.y && point.y >= endPoint.y)
				return new Point(0, startPoint.y - point.y);
		}
			
		return new Point(-1,-1);
	}
	
	/*
	 * GETS & SETS
	 */
	
	public Intersection getEndIntersection(){
		return endIntersection;
	}
	
	public Point getDirection(){
		return direction;
	}
	
	public void setStartIntersection(Intersection i){
		startIntersection = i;
	}
	
	public void setEndIntersection(Intersection i){
		endIntersection = i;
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
	
	public void updateDirection(){
		Point diff = new Point(endIntersection.getArea().x-startIntersection.getArea().x,endIntersection.getArea().y-startIntersection.getArea().y);
		
		if(diff.x == 0)
			if(diff.y > 0)
				direction = new Point(0,1);
			else
				direction = new Point(0,-1);
		else if(diff.y == 0)
			if(diff.x > 0)
				direction = new Point(1,0);
			else
				direction = new Point(-1,0);
		
	}
}
