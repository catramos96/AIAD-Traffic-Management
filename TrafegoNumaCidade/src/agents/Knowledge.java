package agents;

import java.io.Serializable;
import cityStructure.CityMap;

public class Knowledge implements Serializable {

	private static final long serialVersionUID = 1L;
	//What the agent knows about the city -> calculate the journey to the destination
	private CityMap cityMap = new CityMap();
	private String filename = "car.ser";
	
	public Knowledge() { }

	public CityMap getCityKnowledge() {
		return cityMap;
	}

	public void setCityKnowledge(CityMap cityKnowledge) {
		this.cityMap = cityKnowledge;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return this.filename;
	}
}
