package trafegoNumaCidade;

public class Point {
	public int x;
	public int y;
	
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
	
	public int[] toArray(){
		return new int[]{x,y};
	}
}

