package agents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import algorithms.Quality;
import cityStructure.CityMap;
import resources.Point;

/**
 * Serializable class to save the car information so that in a next time
 * the car has some knowledge about the world.
 * This information is the car knowledge about the world such as: the city 
 * structure, the unvisited roads that it came across its journey, the 
 * qualities values(QLearning) and the destination point for those values.
 */
public class CarSerializable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//City Structure Knowledge
	private CityMap cityMap = null;
	private HashMap<String,String> unexploredRoads = new HashMap<String,String>();	
	
	//QLearning values
    private HashMap<String, ArrayList<Quality>> qualityValues = new HashMap<String, ArrayList<Quality>>();
    private Point qLearningDest = null;
	
    //Save options
	private String filename = "car.ser";
	
	//If the knowledge is new and wasn't loaded with an existing one
	private boolean newVersion = true;
	
	/**
	 * Constructor.
	 * @param p - Point with the width and height of the cityMap
	 */
	public CarSerializable(Point spaceDimensions){ 
		cityMap = new CityMap(spaceDimensions);
	}
	
	/*
	 * GETS & SETS
	 */
	
	/**
	 * Sets the destination learning point used
	 * to calculate the quality values for the 
	 * QLearning algorithm.
	 * @param destination
	 */
	public void setDestinationPoint(Point destination) {
		qLearningDest = destination;
	}

	/**
	 * Gets the city structure.
	 * @return
	 */
	public CityMap getCityKnowledge() {
		return cityMap;
	}

	/**
	 * Sets the city structure.
	 * @param cityKnowledge
	 */
	public void setCityKnowledge(CityMap cityKnowledge) {
		this.cityMap = cityKnowledge;
	}

	/**
	 * Sets the name of the file that will be created
	 * when the object is serialized.
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Gets the filename.
	 * @return
	 */
	public String getFilename() {
		return this.filename;
	}

	/**
	 * Gets the quality values (QLearning algorithm).
	 * @return
	 */
	public HashMap<String, ArrayList<Quality>> getQualityValues() {
		return qualityValues;
	}

	/**
	 * Sets the quality values (QLearning algorithm).
	 * @param qualityValues
	 */
	public void setQualityValues(HashMap<String, ArrayList<Quality>> qualityValues) {
		this.qualityValues = qualityValues;
	}

	/**
	 * Gets the names of unexplored roads. These are the names
	 * of the roads that the car passed by but didn't enter.
	 * @return
	 */
	public HashMap<String,String> getUnexploredRoads() {
		return unexploredRoads;
	}

	/**
	 * Sets the unexploredRoads of the car.
	 * @param unexploredRoads
	 */
	public void setUnexploredRoads(HashMap<String,String> unexploredRoads) {
		this.unexploredRoads = unexploredRoads;
	}

	/**
	 * Gets the destination point.
	 * @return
	 */
	public Point getDestinationPoint() {
		return qLearningDest;
	}

	/**
	 * Returns true if it wasn't loaded with an existing knowledge
	 * and false otherwise.
	 * @return
	 */
	public boolean isNew(){
		return newVersion;
	}
	
	/**
	 * Sets the version with true if it was loaded with an existing
	 * knowledge or with false otherwise.
	 * @param isNew
	 */
	public void setNewVersion(boolean isNew){
		newVersion = isNew;
	}
}
