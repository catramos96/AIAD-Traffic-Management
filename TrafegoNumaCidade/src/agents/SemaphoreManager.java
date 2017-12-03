package agents;

import java.util.ArrayList;
import java.util.LinkedList;

import behaviours.SwitchLights;
import resources.Resources.Light;
import jade.wrapper.StaleProxyException;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

public class SemaphoreManager extends Agent{
		
	private LinkedList<SemaphoreRed> redSemaphores = new LinkedList<SemaphoreRed>();
	private SemaphoreGreen greenSem = null;	//green light
	private SemaphoreYellow yellowSem = null;	//yellow semaphore
	
	private boolean isGreenActive = false;
	
	Grid<Object> space;
	
	private ContainerController container;
	
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
		
		for(int i = 0; i < controlPoints.size(); i++){
			
			//1 random green/yellow semaphore
			if(i == 0){
				Point p = controlPoints.get(i);
				
				//have the same position
				greenSem = new SemaphoreGreen(space,container,p);
				yellowSem = new SemaphoreYellow(space,container,p);
				
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
		addBehaviour(new SwitchLights(this,Resources.lightCheck,redSemaphores, yellowSem, greenSem));
	}
	
	public int getLightTime(){
		
		if(isGreenActive)
			return Resources.GreenLightTimeUnits;
		else
			return Resources.YellowLightTimeUnits;
	}
	
	/*
	 * Gets & Sets
	 */
	
	public boolean isGreenActive(){
		return isGreenActive;
	}
	
	public void setGreenActive(boolean b){
		isGreenActive = b;
	}
}
