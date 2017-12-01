package agents;

import java.util.ArrayList;
import java.util.LinkedList;

import resources.Resources.Light;
import jade.wrapper.StaleProxyException;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;

public class SemaphoreManager extends Agent{
	
	private int time = 0;
		
	private LinkedList<Semaphore> redSemaphores = new LinkedList<Semaphore>();
	private Semaphore greenSem = null;	//green light
	private Semaphore yellowSem = null;	//yellow semaphore
	
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
					yellowSem.setPosition(Resources.Semaphore_Rest);
				}
				else if(Light.values()[rndLight].equals(Light.Yellow)){
					isGreenActive = false;
					greenSem.setPosition(Resources.Semaphore_Rest);
				}
			}
			//red semaphores
			else{
				redSemaphores.addLast(new SemaphoreRed(space,container,controlPoints.get(i)));
			}
		}
	}
	
	@ScheduledMethod(start=1 , interval=1000000)
	public void updateLight(){
		
		time += 1000000;
		
		if(time >= Resources.getLightTime(isGreenActive) * Resources.TimeUnitInTicks){
			
			time = 0;
			
			//Next Light
			if(isGreenActive){
				isGreenActive = false;

				Point activePoint = greenSem.getPosition();
				
				//place a yellow light
				yellowSem.setPosition(activePoint);
				
				//remove green light from space
				greenSem.setPosition(Resources.Semaphore_Rest);
			}
			else{
				isGreenActive = true;
				
				//Get next green light semaphore
				Semaphore tmp = redSemaphores.getFirst();
				Point green_position = tmp.getPosition();
				Point red_position = yellowSem.getPosition();
				
				yellowSem.setPosition(Resources.Semaphore_Rest);
				
				//swap semaphores (red <> green)
				tmp.setPosition(red_position);
				greenSem.setPosition(green_position);
				
				//add semaphore in the past active 
				redSemaphores.addLast(tmp);	
				redSemaphores.removeFirst();
				
			}
			
		}
	}
}
