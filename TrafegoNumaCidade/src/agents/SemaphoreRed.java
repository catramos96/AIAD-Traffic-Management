package agents;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import sajas.wrapper.ContainerController;

public class SemaphoreRed extends Semaphore{

	public SemaphoreRed(Grid<Object> space, ContainerController c,Point pos) {
		super(space,c,pos, Resources.Light.Red);
	}

}
