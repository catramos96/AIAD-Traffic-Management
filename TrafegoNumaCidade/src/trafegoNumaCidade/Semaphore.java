package trafegoNumaCidade;

import java.util.ArrayList;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;

public class Semaphore extends Agent{
	enum Light{Green,Yellow,Red};
	
	private ArrayList<Point> controlPoints;
	
	private Point activeControlPoint;	
	private Light activeLight;			//Yellow or Green
	
	//In seconds - 1 sec = 1000000
	private final int SecondsMult = 1000000;
	private final int YellowInterval = 250000;
	private int secondsToRed = 10;
	private int secondsToYellow = 3;
	private int secondsToGreen = 10;
	
	private int time = 0;
	
	private Point position;
	
	Grid<Object> space;
	
	public Semaphore(ArrayList<Point> controlPoints){		
		
		this.controlPoints = new ArrayList<Point>();
		
		for(Point p : controlPoints){
			this.controlPoints.add(p);
		}
		
		//Random Active Control Point
		int rndPoint = (int)(Math.random() * controlPoints.size()-1);
		activeControlPoint = controlPoints.get(rndPoint);
		
		//Random Active Light
		int rndLight = (int) Math.random();
		activeLight = Light.values()[rndLight];
		
		position = activeControlPoint;
	}
	
	@ScheduledMethod(start=1 , interval=1000000)
	public void updateLight(){
		
		time += SecondsMult;
		
		//Blink
		if(activeLight.equals(Light.Yellow) && time % YellowInterval == 0){
			if(position.equals(new Point(0,0)))	//ALTERAR
				position = activeControlPoint;
			else
				position = new Point(0,0);	//ALTERAR
			
			space.moveTo(this, position.toArray());
		}
		
		if(time > getLightTime() * SecondsMult){
			time = 0;
			
			//Next Light
			switch (activeLight) {
			case Green:
				activeLight = Light.Yellow;
				break;
			case Yellow:
				activeLight = Light.Red;
				break;
			case Red:
				activeLight = Light.Green;
				break;
			default:
				activeLight = Light.Yellow;
			}
			
			//Next Control Point
			if(activeLight.equals(Light.Red)){
				int index = controlPoints.indexOf(activeControlPoint);
				index++;
				
				if(index == controlPoints.size())
					index = 0;
				
				activeControlPoint = controlPoints.get(index);
				space.moveTo(this, activeControlPoint.toArray());

				activeLight = Light.Green;
			}
			
		}
	}

	public boolean isGreen(Point p){
		if(activeControlPoint.equals(p))
			return true;
		else
			return false;
	}
	/**
	 * GETS & SETS
	 */
	
	public Point getActiveControlPoint(){
		return activeControlPoint;
	}
	
	public Light getActiveLight(){
		return activeLight;
	}
	
	public Point getPosition(){
		return position;
	}
	
	private int getLightTime(){
		switch(activeLight){
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
	
	public void setSpace(Grid<Object> space){
		this.space = space;
	}
}
