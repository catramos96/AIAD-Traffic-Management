package trafegoNumaCidade;

import repast.simphony.engine.schedule.Schedule;
import repast.simphony.space.grid.Grid;
import sajas.wrapper.ContainerController;

public class SemaphoreRed extends Semaphore{

	public SemaphoreRed(Grid<Object> space, ContainerController c,Point pos) {
		super(space,c,pos, Light.Red);
	}

}
