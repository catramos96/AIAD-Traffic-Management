package resources;

/**
 * Auxiliary Class that provides methods and constants.
 *
 */
public class Resources {
	
	/*
	 * Others
	 */
	
	public static final int lightCheck = 1000;			//Time between light checks (and updates if needed)
	public static final int carVelocity = 300;			//Time in milliseconds for a car to change cell if possible
	
	public static final int transitPenaltyRatio = 3;
	
	/*
	 * Semaphore
	 */
	
	public enum Light{Green,Yellow,Red};
	
	public static final int GreenLightTimeUnits = 2;	// * Light Check milliseconds
	
	public static final int YellowLightTimeUnits = 1;	// * Light Check milliseconds


	/*
	 * Direction
	 */
	
	public static enum Direction{North,South,East,West,None};
	
	/**
	 * Gets the direction regarding a start and an end point.
	 * @param startPoint
	 * @param endPoint
	 * @return
	 */
	public static Direction getDirection(Point startPoint,Point endPoint){
		Point diff = new Point(endPoint.x-startPoint.x,endPoint.y-startPoint.y);
		
		if(diff.x == 0){
			if(diff.y > 0)
				return Direction.North;
			else if(diff.y < 0)
				return Direction.South;
		}
		else if(diff.y == 0){
			if(diff.x > 0)
				return Direction.East;
			else if(diff.x < 0)
				return Direction.West;
		}
		
		return Direction.None;
	}
	
	/**
	 * Gets a point that represents a direction.
	 * @param d
	 * @return
	 */
	public static Point getDirectionPoint(Direction d){
		switch (d) {
			case North:
				return new Point(0,1);
			case South:
				return new Point(0,-1);
			case East:
				return new Point(1,0);
			case West:
				return new Point (-1,0);
			default:
				return new Point(0,0);
		}
	}
	
	/**
	 * Method that returns a point that is the increment by one in the 
	 * point p in a certain direction.
	 * @param d
	 * @param p
	 * @return
	 */
	public static Point incrementDirection(Direction d, Point p){
		Point inc = getDirectionPoint(d);
		return new Point(p.x + inc.x,p.y + inc.y);
	}
	
	/**
	 * Same as the getDirection(...) but with just a cell apart
	 * @param origin
	 * @param destination
	 * @return
	 */
	public static Direction getAdjacentDirection(Point origin, Point destination){
		Point diff = new Point(destination.x-origin.x,destination.y-origin.y);
		
		if(diff.x == 0){
			if(diff.y == 1)
				return Direction.North;
			else if(diff.y == -1)
				return Direction.South;
		}
		else if(diff.y == 0){
			if(diff.x == 1)
				return Direction.East;
			else if(diff.x == -1)
				return Direction.West;
		}
		
		return Direction.None;
	}

}
