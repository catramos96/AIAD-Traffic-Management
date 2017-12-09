package behaviors;

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


/**
 * Behavior implemented by a semaphore manager to
 * witch the semaphores between the several entry points
 * (Illusion of light switching).
 *
 */
public class SwitchLights extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	private SemaphoreManager manager = null;
	private int time = 0;
	
	//semaphores
	private LinkedList<SemaphoreRed> redSemaphores = null;
	private SemaphoreYellow yellowSem = null;
	private SemaphoreGreen greenSem = null;
	
	/**
	 * Constructor
	 * @param manager
	 * @param period
	 * @param redS
	 * @param yellowS
	 * @param greenS
	 */
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
		
		//Time to change the light
		if(time >= manager.getLightTime() * Resources.lightCheck){
			
			time = 0;
			
			//Set to Yellow
			if(manager.isGreenActive()){
				manager.setGreenActive(false);

				Point activePoint = greenSem.getPosition();
				
				//place a yellow light
				yellowSem.setPosition(activePoint);
				
				//remove green light from space
				greenSem.setPosition(SpaceResources.REST_CELL);
			}
			//Set to green
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
