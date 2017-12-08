package algorithms;

import java.io.Serializable;

public class Quality implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String intersection = null;				//State		
	public String action_road = null;		//action
	public boolean action_transit = false;
	public float value = 0;				//quality value
	
	public Quality(String intersection, String action_road, boolean action_transit, float value){
		this.intersection = intersection;
		this.action_road = action_road;
		this.action_transit = action_transit;
		this.value = value;
	}
	
	public void setValue(float value){
		this.value = value;
	}	
	
	public String print(){
		return new String("Q( State(" + intersection + ") , Action( " + action_road + " , " + action_transit + " ) = " +value);
	}
}