package trafegoNumaCidade;

import java.util.ArrayList;

import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;
import sajas.wrapper.ContainerController;
import trafegoNumaCidade.Semaphore.Light;

public class SemaphoreManager extends Agent{

	//In seconds - 1 sec = 1000000
	private final int SecondsMult = 1000000;
	
	private int secondsToYellow = 3;
	private int secondsToGreen = 10;
	
	private int time = 0;
		
	private ArrayList<Semaphore> semaphores = new ArrayList<Semaphore>();
	private Semaphore active = null;
	private int active_index = 0;
	
	Grid<Object> space;
	
	private ContainerController container;
	
	public SemaphoreManager(Grid<Object> space, ContainerController mainContainer, ArrayList<Point> controlPoints){
		
		this.space = space;
		
		this.container = mainContainer;
		
		//Random Active Control Point
		int rndPoint = (int)(Math.random() * controlPoints.size());
		
		//Random Active Light
		int rndLight = (int) (Math.random() * 2);

		
		for(int i = 0; i < controlPoints.size(); i++){
			
			//1 random green semaphore
			if(i == rndPoint){
				
				//Green Light
				if(Light.values()[rndLight].equals(Light.Green))
					active = new SemaphoreGreen(space,container,controlPoints.get(i));
				//Yellow Light
				else
					active = new SemaphoreYellow(space,container,controlPoints.get(i));
				
				semaphores.add(active);
				active_index = i;
			}
			//red semaphores
			else{
				semaphores.add(new SemaphoreRed(space,container,controlPoints.get(i)));;
			}
		}
	}
	
	@ScheduledMethod(start=1 , interval=1000000)
	public void updateLight(){
		
		time += SecondsMult;
		System.out.println(time);
		
		if(time >= getLightTime() * SecondsMult){
			time = 0;
			
			
			//Next Light
			if(active.getLight().equals(Light.Green)){
				
				//same position
				Point tmp = active.getPosition();
				
				//remove green light from space
				removeSemaphore(active_index);
				
				//place a yellow light
				active = new SemaphoreYellow(space,container,tmp);
				semaphores.add(active_index, active);
			}
			else{
				//Get next green light semaphore
				int next_index = active_index+1;
				
				if(next_index == semaphores.size())
					next_index = 0;
				
				Semaphore tmp = semaphores.get(active_index);
				Point nextPosition = tmp.getPosition();
				
				//swap semaphores
				tmp.setPosition(active.getPosition());								//red semaphore is in the past active semaphore position
				removeSemaphore(active_index);										//remove yellow semaphore
				active = new SemaphoreGreen(space,container,nextPosition);
				semaphores.add(active_index, active);

				active_index = next_index;
			}
			
		}
	}
	
	private int getLightTime(){
		
		System.out.println(active.getLight().toString());
		switch(active.getLight()){
			case Green:
				return secondsToGreen;
			case Yellow:
				return secondsToYellow;
			default:
				return secondsToYellow;
		}
	}
	
	public ArrayList<Semaphore> getSemaphores(){
		return semaphores;
	}
	
	public void removeSemaphore(int index){
		container.removeLocalAgent(semaphores.get(index));
		semaphores.remove(index);
	}
}
