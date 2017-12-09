package agents;
import jade.wrapper.StaleProxyException;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

/**
 * Agent that represents a semaphore.
 *
 */
public abstract class Semaphore extends Agent{
	
	private Point position = new Point(0,0);		//Position in the space
	private Resources.Light light = null;			//Light
	private Grid<Object> space = null;				//Space where the agent is
	
	/**
	 * Constructor.
	 * @param space
	 * @param container
	 * @param pos
	 * @param light
	 */
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
	
	/*
	 * GETS & SETS
	 */
	
	/**
	 * Gets the position.
	 * @return
	 */
	public Point getPosition(){
		return position;
	}
	
	/**
	 * Sets the position.
	 * @param p
	 */
	public void setPosition(Point p){
		position = p;
		space.moveTo(this, position.toArray());
	}
	
	/**
	 * Sets the space.
	 * @param space
	 */
	public void setSpace(Grid<Object> space){
		this.space = space;
	}
	
	/**
	 * Sets the light
	 * @param l - Light
	 */
	public void setLight(Resources.Light l){
		light = l;
	}
	
	/**
	 * Gets the light.
	 * @return
	 */
	public Resources.Light getLight(){
		return light;
	}
	
	/**
	 * Return true if the light is green and false otherwise.
	 * @return
	 */
	public boolean isGreen(){
		return light.equals(Resources.Light.Green);
	}
}
