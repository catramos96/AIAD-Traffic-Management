package agents;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import sajas.wrapper.ContainerController;

/**
 * Extended class of the semaphore just with the purpose
 * of representing the different possible semaphore lights
 * in the space.
 * The light color is red.
 *
 */
public class SemaphoreRed extends Semaphore{

	/**
	 * Constructor. The semaphore light is red.
	 * @param space
	 * @param c
	 * @param pos
	 */
	public SemaphoreRed(Grid<Object> space, ContainerController c,Point pos) {
		super(space,c,pos, Resources.Light.Red);
	}

}
