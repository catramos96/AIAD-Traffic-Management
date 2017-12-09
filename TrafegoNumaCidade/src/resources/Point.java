package resources;

import java.io.Serializable;

/**
 * Class that represents coordinates.
 *
 */
public class Point implements Serializable {

	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	
	/**
	 * Constructor.
	 * @param x
	 * @param y
	 */
	public Point(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		Point p = (Point)obj;
		return (this.x == p.x && this.y == p.y);
	}
	
	/**
	 * Returns the coordinates of point in form of an array.
	 * @return
	 */
	public int[] toArray(){
		return new int[]{x,y};
	}
	
	/*
	 * GETS & SETS
	 */
	
	/**
	 * Gets the distance between two points.
	 * Since the possible movements in the space by a car are
	 * horizontal or vertical, the distance is given by the
	 * sum of the x's coordinates difference and the y's 
	 * coordinates difference.
	 * @param a
	 * @param b
	 * @return
	 */
	public static int getDistance(Point a, Point b){
		
		int x_dist = Math.abs(a.x - b.x);
		int y_dist = Math.abs(a.y - b.y);
		
		return x_dist + y_dist;
			
	}
	
	/**
	 * Method that returns a String with the main information about the object.
	 * @return
	 */
	public String print(){
		return new String("[" + x + "," + y + "]");
	}
	
	/**
	 * Gets the point in a String in the format [X,Y]
	 * @param print
	 * @return
	 */
	public static Point getPoint(String print){
		print = print.replace("[", "");
		print = print.replace("]", "");
		
		String coord[] = print.split(",");
		
		Point p = new Point(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]));
		return p;
	}
}

