package agents;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import sajas.wrapper.ContainerController;

public class SemaphoreGreen extends Semaphore{

	public SemaphoreGreen(Grid<Object> space, ContainerController c,Point pos) {
		super(space,c,pos, Resources.Light.Green);
	}

}
