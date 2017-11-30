package trafegoNumaCidade;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import trafegoNumaCidade.Semaphore.Light;

public class SemaphoreManager {

	//In seconds - 1 sec = 1000000
	private final int SecondsMult = 1000000;
	private final int YellowInterval = 250000;
	private int secondsToRed = 10;
	private int secondsToYellow = 3;
	private int secondsToGreen = 10;
	
	private int time = 0;
		
	private ArrayList<Semaphore> semaphores = new ArrayList<Semaphore>();
	private Semaphore active = null;
	private int active_index = 0;
	
	public SemaphoreManager(ArrayList<Point> controlPoints){
		
		for(Point p : controlPoints){
			semaphores.add(
					new Semaphore(
							p,Light.Red));
		}
			
		//Random Active Control Point
		int rndPoint = (int)(Math.random() * controlPoints.size()-1);
		active = semaphores.get(rndPoint);
		active_index = rndPoint;
		
		//Random Active Light
		int rndLight = (int) Math.random();
		
		active.setLight(Light.values()[rndLight]);
	}
	
	@ScheduledMethod(start=1 , interval=1000000)
	public void updateLight(){
		
		time += SecondsMult;
		
		//Blink
		/*if(light.equals(Light.Yellow) && time % YellowInterval == 0){
			if(position.equals(new Point(0,0)))	//ALTERAR
				position = position;
			else
				position = new Point(0,0);	//ALTERAR
			
			space.moveTo(this, position.toArray());
		}*/
		
		if(time > getLightTime() * SecondsMult){
			time = 0;
			
			//Next Light
			if(active.getLight().equals(Light.Green)){
				active.setLight(Light.Yellow);
			}
			else{
				int next_index = active_index+1;
				
				if(next_index == semaphores.size())
					next_index = 0;
				
				Semaphore tmp = semaphores.get(next_index);
				Point nextPosition = tmp.getPosition();
				
				//swap semaphores
				tmp.setPosition(active.getPosition());
				active.setPosition(nextPosition);
			}
			
		}
	}
	
	private int getLightTime(){
		switch(active.getLight()){
			case Red:
				return secondsToRed;
			case Green:
				return secondsToGreen;
			case Yellow:
				return secondsToYellow;
			default:
				return 0;
		}
	}
	
	public ArrayList<Semaphore> getSemaphores(){
		return semaphores;
	}
}
