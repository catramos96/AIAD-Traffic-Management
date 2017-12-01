package agents;
import jade.wrapper.StaleProxyException;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

public abstract class Semaphore extends Agent{
	
	private Point position = new Point(0,0);
	private Resources.Light light = null;
	private Grid<Object> space = null;
	
	
	public Semaphore(Grid<Object> space, ContainerController container, Point pos, Resources.Light light){	
		
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
		return light.equals(Resources.Light.Green);
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
	
	public void setLight(Resources.Light l){
		light = l;
	}
	
	public Resources.Light getLight(){
		return light;
	}
}
