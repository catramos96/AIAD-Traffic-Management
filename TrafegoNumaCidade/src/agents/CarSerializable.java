package agents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import algorithms.Quality;
import cityStructure.CityMap;
import resources.Point;

/*
 * Aqui sao guardadas todas as informacoes de um agente de modo a permitir que este seja serialized e que 
 * da proxima vez que este for loaded tenha o conhecimento necessário sobre o mundo 
 */
public class CarSerializable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//What the agent knows about the city -> calculate the journey to the destination
	private CityMap cityMap = null;
	private HashMap<String,String> unexploredRoads = new HashMap<String,String>();		//<RoadUnexplored,Intersection> 
	
	//qlearning algorithm needs
    private HashMap<String, ArrayList<Quality>> qualityValues = new HashMap<String, ArrayList<Quality>>();
    private Point qLearningDest = null;
	
	private String filename = "car.ser";
	
	private boolean newVersion = true;
	
	public CarSerializable(Point p){ 
		cityMap = new CityMap(p);
	}
	
	public void setLearn(Point destination) {
		qLearningDest = destination;
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

	public HashMap<String, ArrayList<Quality>> getQualityValues() {
		return qualityValues;
	}

	public void setQualityValues(HashMap<String, ArrayList<Quality>> qualityValues) {
		this.qualityValues = qualityValues;
	}

	public HashMap<String,String> getUnexploredRoads() {
		return unexploredRoads;
	}

	public void setUnexploredRoads(HashMap<String,String> unexploredRoads) {
		this.unexploredRoads = unexploredRoads;
	}

	public Point getqLearningDest() {
		return qLearningDest;
	}

	public void setqLearningDest(Point qLearningDest) {
		this.qLearningDest = qLearningDest;
	}

	public boolean isNew(){
		return newVersion;
	}
	
	public void setNewVersion(boolean isNew){
		newVersion = isNew;
	}
}
