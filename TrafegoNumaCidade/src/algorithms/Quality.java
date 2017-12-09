package algorithms;

import java.io.Serializable;

/**
 * Auxiliary class of the QLearning (algorithm)
 * to represent the quality of a state, its action
 * and its value.
 *
 */
public class Quality implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//State
	public String intersection = null;			
	
	//Action
	public String action_road = null;			
	public boolean action_transit = false;
	
	//Value
	public float value = 0;				
	
	/**
	 * Constructor.
	 * @param intersection - State
	 * @param action_road - Action
	 * @param action_transit - Action
	 * @param value - Value of quality
	 */
	public Quality(String intersection, String action_road, boolean action_transit, float value){
		this.intersection = intersection;
		this.action_road = action_road;
		this.action_transit = action_transit;
		this.value = value;
	}
	
	/*
	 * GETS & SETS
	 */
	
	/**
	 * Sets the value of the quality.
	 * @param value
	 */
	public void setValue(float value){
		this.value = value;
	}	
	
	/**
	 * Method that returns a String with the main information that
	 * represents the quality.
	 * @return
	 */
	public String print(){
		return new String("Q( State(" + intersection + ") , Action( " + action_road + " , " + action_transit + " ) = " +value);
	}
}