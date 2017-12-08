package agents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import agents.CarAgent.LearningMode;
import algorithms.Quality;
import cityStructure.CityMap;
import resources.Point;

public class CarSerializable implements Serializable {

	private static final long serialVersionUID = 1L;
	//What the agent knows about the city -> calculate the journey to the destination
	private CityMap cityMap = null;
    private LearningMode learningMode = LearningMode.NONE;
    private HashMap<String, ArrayList<Quality>> qualityValues = new HashMap<String, ArrayList<Quality>>();

	private String filename = "car.ser";
	
	public CarSerializable(Point p) { 
		cityMap = new CityMap(p);
	}

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

	public LearningMode getLearningMode() {
		return learningMode;
	}

	public void setLearningMode(LearningMode learningMode) {
		this.learningMode = learningMode;
	}

	public HashMap<String, ArrayList<Quality>> getQualityValues() {
		return qualityValues;
	}

	public void setQualityValues(HashMap<String, ArrayList<Quality>> qualityValues) {
		this.qualityValues = qualityValues;
	}
}
