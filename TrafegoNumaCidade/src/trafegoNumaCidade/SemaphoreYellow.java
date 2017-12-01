package trafegoNumaCidade;

import repast.simphony.engine.schedule.Schedule;
import repast.simphony.space.grid.Grid;
import sajas.wrapper.ContainerController;

public class SemaphoreYellow extends Semaphore{

	public SemaphoreYellow(Grid<Object> space,ContainerController c, Point pos) {
		super(space, c,pos, Light.Yellow);
	}

}
