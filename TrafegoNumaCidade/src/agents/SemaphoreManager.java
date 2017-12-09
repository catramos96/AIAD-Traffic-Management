package agents;

import java.util.ArrayList;
import java.util.LinkedList;

import behaviors.SwitchLights;
import resources.Resources.Light;
import jade.wrapper.StaleProxyException;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

/**
 * Agent that it's responsible to manage the semaphores in
 * an intersection and switch the lights after a certain time.
 *
 */
public class SemaphoreManager extends Agent{
		
	private LinkedList<SemaphoreRed> redSemaphores = new LinkedList<SemaphoreRed>();//red semaphores = number of entries in the intersection - 1
	private SemaphoreGreen greenSem = null;											//green semaphore
	private SemaphoreYellow yellowSem = null;										//yellow semaphore
	
	private boolean isGreenActive = false;
	
	Grid<Object> space;
	
	private ContainerController container;
	
	/**
	 * Constructor. Creates the necessary semaphores and randomly positions a yellow semaphore
	 * or a green one.
	 * @param space
	 * @param mainContainer
	 * @param controlPoints
	 */
	public SemaphoreManager(Grid<Object> space, ContainerController mainContainer, ArrayList<Point> controlPoints){
		
		this.space = space;
		this.container = mainContainer;
		
		try {
			this.container.acceptNewAgent("SemaphoreManager_" + controlPoints.get(0).x + "_" + controlPoints.get(0).y, this).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		this.space.getAdder().add(space, this);
		
		//Random Active Light
		int rndLight = (int) (Math.random() * 2);
		
		//Creates a red semaphore for each control point except the first one
		//in which there will be created a yellow/green semaphore.
		for(int i = 0; i < controlPoints.size(); i++){
			
			//1 random green/yellow semaphore
			if(i == 0){
				Point p = controlPoints.get(i);
				
				//have the same position
				greenSem = new SemaphoreGreen(space,container,p);
				yellowSem = new SemaphoreYellow(space,container,p);
				
				//Just one will be active
				if(Light.values()[rndLight].equals(Light.Green)){
					isGreenActive = true;
					yellowSem.setPosition(SpaceResources.REST_CELL);
				}
				else if(Light.values()[rndLight].equals(Light.Yellow)){
					isGreenActive = false;
					greenSem.setPosition(SpaceResources.REST_CELL);
				}
			}
			//red semaphores
			else{
				redSemaphores.addLast(new SemaphoreRed(space,container,controlPoints.get(i)));
			}
		}
	}

	@Override
	public void setup(){
		//Switch semaphore types between the control points
		addBehaviour(new SwitchLights(this,Resources.lightCheck,redSemaphores, yellowSem, greenSem));
	}
	
	/**
	 * Gets the time of the current active semaphore.
	 * @return
	 */
	public int getLightTime(){
		if(isGreenActive)
			return Resources.GreenLightTimeUnits;
		else
			return Resources.YellowLightTimeUnits;
	}
	
	/*
	 * Gets & Sets
	 */
	
	/**
	 * Return true if the green semaphore is active in one of
	 * the control points and false otherwise.
	 * @return
	 */
	public boolean isGreenActive(){
		return isGreenActive;
	}
	
	/**
	 * Sets with true if the green semaphore is active in one
	 * of the control points or false otherwise.
	 * @param b
	 */
	public void setGreenActive(boolean b){
		isGreenActive = b;
	}
}
