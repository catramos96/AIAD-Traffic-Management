package trafegoNumaCidade;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;

public class Semaphore extends Agent{
	enum Light{Green,Yellow,Red};
	
	private Point position = new Point(0,0);
	private Light light = null;
	private Grid<Object> space = null;
	
	public Semaphore(Point pos, Light light){	
		
		System.out.println(pos.x + " " + pos.y);
		position = pos;
		this.light = light;
	}
	
	public boolean isGreen(){
		return light.equals(Light.Green);
	}
	
	/**
	 * GETS & SETS
	 */
	
	public Point getPosition(){
		return position;
	}
	
	public void setPosition(Point p){
		position = p;
		space.moveTo(this, position.toArray());
	}
	
	public void setSpace(Grid<Object> space){
		this.space = space;
	}
	
	public void setLight(Light l){
		light = l;
	}
	
	public Light getLight(){
		return light;
	}
}
