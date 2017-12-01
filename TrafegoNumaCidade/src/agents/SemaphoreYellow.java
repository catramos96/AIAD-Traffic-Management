package agents;

import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import sajas.wrapper.ContainerController;

public class SemaphoreYellow extends Semaphore{

	public SemaphoreYellow(Grid<Object> space,ContainerController c, Point pos) {
		super(space, c,pos, Resources.Light.Yellow);
	}

}
