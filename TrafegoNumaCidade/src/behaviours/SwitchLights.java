package behaviours;

import java.util.LinkedList;

import agents.Semaphore;
import agents.SemaphoreGreen;
import agents.SemaphoreManager;
import agents.SemaphoreRed;
import agents.SemaphoreYellow;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import sajas.core.behaviours.TickerBehaviour;

public class SwitchLights extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	private SemaphoreManager manager = null;
	private int time = 0;
	
	//semaphores
	private LinkedList<SemaphoreRed> redSemaphores = null;
	private SemaphoreYellow yellowSem = null;
	private SemaphoreGreen greenSem = null;
	
	public SwitchLights(SemaphoreManager manager, long period,LinkedList<SemaphoreRed> redS, SemaphoreYellow yellowS, SemaphoreGreen greenS) {
		super(manager, period);
		this.manager = manager;
		redSemaphores = redS;
		yellowSem = yellowS;
		greenSem = greenS;
	}

	@Override
	protected void onTick() {
		time += Resources.lightCheck;
		
		if(time >= manager.getLightTime() * Resources.lightCheck){
			
			time = 0;
			
			//Next Light
			if(manager.isGreenActive()){
				manager.setGreenActive(false);

				Point activePoint = greenSem.getPosition();
				
				//place a yellow light
				yellowSem.setPosition(activePoint);
				
				//remove green light from space
				greenSem.setPosition(SpaceResources.REST_CELL);
			}
			else{
				manager.setGreenActive(true);
				
				//Get next green light semaphore
				Semaphore tmp = redSemaphores.getFirst();
				Point green_position = tmp.getPosition();
				Point red_position = yellowSem.getPosition();
				
				yellowSem.setPosition(SpaceResources.REST_CELL);
				
				//swap semaphores (red <> green)
				tmp.setPosition(red_position);
				greenSem.setPosition(green_position);
				
				//add semaphore in the past active 
				redSemaphores.addLast((SemaphoreRed) tmp);	
				redSemaphores.removeFirst();
				
			}
			
		}		
	}

}
