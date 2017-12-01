package agents;

import repast.simphony.space.grid.Grid;
import resources.Point;
import sajas.wrapper.ContainerController;

public class SemaphoreYellow extends Semaphore{

	public SemaphoreYellow(Grid<Object> space,ContainerController c, Point pos) {
		super(space, c,pos, Light.Yellow);
	}

}
