package trafegoNumaCidade;
import jade.wrapper.StaleProxyException;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

public abstract class Semaphore extends Agent{
	enum Light{Green,Yellow,Red};
	
	private Point position = new Point(0,0);
	private Light light = null;
	private Grid<Object> space = null;
	
	
	public Semaphore(Grid<Object> space, ContainerController container, Point pos, Light light){	
		
		this.space = space;
		position = pos;
		this.light = light;
		
		try {
			container.acceptNewAgent("SemaphoreAgent_" + pos.x + "_" + pos.y + "_" + light.toString(), this).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		space.getAdder().add(space, this);
		space.moveTo(this, position.toArray());
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
