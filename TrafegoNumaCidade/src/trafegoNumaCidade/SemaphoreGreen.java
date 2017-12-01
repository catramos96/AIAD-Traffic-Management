package trafegoNumaCidade;

import repast.simphony.engine.schedule.Schedule;
import repast.simphony.space.grid.Grid;
import sajas.wrapper.ContainerController;

public class SemaphoreGreen extends Semaphore{

	public SemaphoreGreen(Grid<Object> space, ContainerController c,Point pos) {
		super(space,c,pos, Light.Green);
	}

}
