package agents;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import sajas.wrapper.ContainerController;

/**
 * Extended class of the semaphore just with the purpose
 * of representing the different possible semaphore lights
 * in the space.
 * The light color is green.
 *
 */
public class SemaphoreGreen extends Semaphore{

	/**
	 * Constructor. The semaphore light is green.
	 * @param space
	 * @param c
	 * @param pos
	 */
	public SemaphoreGreen(Grid<Object> space, ContainerController c,Point pos) {
		super(space,c,pos, Resources.Light.Green);
	}

}
