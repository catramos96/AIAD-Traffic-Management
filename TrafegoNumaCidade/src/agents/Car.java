package agents;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import resources.Debug;
import resources.Point;
import resources.Resources;
import sajas.core.Agent;
import sajas.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import algorithms.AStar;
import algorithms.QLearning;
import behaviors.AskDirections;
import behaviors.CarMessagesReceiver;
import behaviors.CarMovement;
import behaviors.LearnMap;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;

/**
 * Agent that represents a car.
 */
public class Car extends Agent {

	public static enum LearningMode {
		SHORT_LEARNING,		//Learns the city and applies the A* algorithm to reach its destination
		LEARNING, 			//Learns the city and fills the quality values of the QLearning algorithm
							//also uses the A* to discover unvisited roads
		APPLYING, 			//Knows the all city and uses the quality values to reach its destination
		NONE};				//Knows the all city and uses the A* algorithm to reach its destination
	
	//current position
	protected Road road = null;							//Current road he is in (real world)
	protected Intersection intersection = null;			//Latest intersection (real world)
	protected Point position = null;
	
	//destination
	protected Point destination = null;
	protected String destinationName = null;

    //learning mode
    protected LearningMode learningMode = LearningMode.NONE;
	protected QLearning qlearning = null;
    
    //persistent knowledge of the agent
	protected CarSerializable knowledge = new CarSerializable(null);
	
	//Name of the roads to follow to reach some destination (destination point or unvisited roads)
	protected ArrayList<String> journey = new ArrayList<String>();
	
	//space where the agent moves
	protected Grid<Object> space = null;
	
	//statistics
	protected long init = 0;
	protected long secs = 0;
	protected int countGetPathSend = 0;
	protected int countWhichRoadSend = 0;
	protected int countNewJourney = 0;
    
	/**
	 * Constructor of the class Car.
	 * @param space - Grid<Object> where the car updates its position.
	 * @param origin - Point where the car starts.
	 * @param startRoad - Road where the car starts.
	 * @param destination - Point where the car has to reach.
	 * @param knowledge - CarSerializable Persistent knowledge of the car (new/old).
	 * @param mode - Learning Mode.
	 */
    public Car(Grid<Object> space, Point origin, Road startRoad, Point destination, CarSerializable knowledge, LearningMode mode) 
	{
		this.space = space;
		
		this.position = origin;
		this.road = startRoad;
		this.destination = destination;
		this.learningMode = mode;
		this.knowledge = knowledge;
		this.qlearning = new QLearning(this, 1f, 0.8f,knowledge.getQualityValues());
		
		//If the all city is known, then the destinationName is also known
		if(learningMode.equals(LearningMode.NONE) || learningMode.equals(LearningMode.SHORT_LEARNING)){
			for(Road r : knowledge.getCityKnowledge().getRoads().values()){
				if(r.partOfRoad(destination)){
					destinationName = r.getName();
					break;
				}
			}
		}
		
		init = System.currentTimeMillis();
	}
    
    @Override
    public void setup() {
        
        // subscribe DF
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("service-provider");
        sd.setName("CarService");
        template.addServices(sd);
        template.setName(this.getAID());
        try {
        	DFService.register(this, template);
        } catch(FIPAException ex) {
        	ex.printStackTrace();
        }
        
        addBehaviour(new CarMessagesReceiver(this));
        addBehaviour(new CarMovement(this, Resources.carVelocity));
        
        //If it needs to learn the city
        if(learningMode.equals(LearningMode.LEARNING) ||
        		learningMode.equals(LearningMode.SHORT_LEARNING)){
        	addBehaviour(new LearnMap(this));
        	addBehaviour(new AskDirections(this,5000));
        }
    }

    @Override
	public void takeDown() {
    	try {    		
        	DFService.deregister(this);
        	this.doDelete();
        } catch(FIPAException ex) {
        	ex.printStackTrace();
        }
    }
    
    /*
     * PATHS
     */
    
    /**
     * Calculates and updates the journey of itself
     * only if it was successful at calculating the new route.
     */
    public void calculateAndUpdateJourney(){
    		
		ArrayList<String> j =new ArrayList<String>();
		
		switch(learningMode){
    		case NONE: {
    			if(destinationName != null){
    				if(knowledge.getCityKnowledge().getRoads().containsKey(road.getName())){
    					
    					//Searches for the shortest path (using a*) to reach the destination road
    					Road r = knowledge.getCityKnowledge().getRoads().get(road.getName());
    					j = AStar.shortestPath(knowledge.getCityKnowledge(), r,destinationName,true);
    				}
    			}
    			break;
    		}
    		case SHORT_LEARNING:{
    			if(destinationName != null){
    				if(knowledge.getCityKnowledge().getRoads().containsKey(road.getName())){
    					
    					//Searches for the shortest path (using a*) to reach the destination road
    					Road r = knowledge.getCityKnowledge().getRoads().get(road.getName());
    					j = AStar.shortestPath(knowledge.getCityKnowledge(), r, destinationName,true);
    				}
    			}
    			
    			//If it was unsuccessful at calculating the previous route
    			//It will calculate the route to an unvisited road
    			if(j.size() == 0)
    				j = getJourneyToUnvisited();
    				
    			break;
    		}
    		case LEARNING:{
    			//Tries to discover new roads by explore the unvisited roads
    			//that the car as across along its course
    			j = getJourneyToUnvisited();
    			
    			if(getUnexploredRoads().size() == 0 && destinationName != null){
    				Random r = new Random();
    				if(r.nextInt(100) < 10){
    					j = AStar.shortestPath(knowledge.getCityKnowledge(), road,destinationName,true);
    				}
    			}
    			
    			break;
    			
    		}
    		case APPLYING: {
    			//Gets the next road after an intersection according to the 
    			//current transit in the possibles roads
    			String road = qlearning.getNextRoad(intersection);
    			if(road != null){
    				j.add(this.road.getName());	//it will be consumed
    				j.add(road);
    			}
    			break;
    		}
		}

		
		if(j.size() > 0){
    		setJourney(j);
    		//removes the first road in the path
    		//it corresponds to the current road
    		jorneyConsume();
		}
    	
    }
    
    /**
     * Calculates the journey of other cars using the A* algorithm.
     * @param road - Road current road of the other car.
     * @param destinationName - String name of the road of its destination.
     * @return - A route to reach the car's destination if success.
     */
    public ArrayList<String> getJourneyCalculations(Road road, String destinationName){
    	return AStar.shortestPath(knowledge.getCityKnowledge(), road, destinationName,true);
    }
    
    /**
     * Calculates and updates its journey to reach an unvisited road.
     * If the current road has unvisited roads in its end intersection,
     * then it won't be necessary to calculate the route.
     * that it came across its course
     * @return - A route to an unvisited road.
     */
    private ArrayList<String> getJourneyToUnvisited(){
    	
    	ArrayList<String> j = new ArrayList<String>();
    	
    	//Look for the paths to unvisited roads the end of the current road is unknown
		if(journey.size() == 0 && knowledge.getCityKnowledge().getIntersections().containsKey(road.getEndIntersection().getName())){
			
			boolean hasUnvisited = false;
			
			//check if next intersection has unvisited roads
			for(Road r : road.getEndIntersection().getOutRoads()){
				if(!knowledge.getCityKnowledge().getRoads().containsKey(r.getName()) ||
						knowledge.getUnexploredRoads().containsKey(r.getName())){
					hasUnvisited = true;
					break;
				}
			}
			
			if(!hasUnvisited){
			//Chooses a path to an unvisited road
    			for(String unexploredRoad : knowledge.getUnexploredRoads().keySet()){
    				
    				String intersection = knowledge.getUnexploredRoads().get(unexploredRoad);
    				
    				//if the start intersection of the unvisited road isn't unknown
    				if(intersection != ""){
    					
    					//Searches for the path to the start intersection of the unvisited road
    					Road r = knowledge.getCityKnowledge().getRoads().get(road.getName());
    					j = AStar.shortestPath(knowledge.getCityKnowledge(), r, intersection,false);
	    				
    					if(j.size() != 0){
    						j.add(unexploredRoad);
    						
    						Debug.debugUnvisitedJourney(this, unexploredRoad, intersection);
    						
    						break;
	    				}
    				}
    			}
			}
		}
		
		return j;
    }

    /*
     * GETS & SETS
     */
    
    /**
     * Gets the QLearning.
     * @return - QLearning
     */
    public QLearning getQLearning(){
    	return qlearning;
    }
    
    /**
     * Gets the current road that the car is in.
     * @return - Road
     */
    public Road getRoad(){
    	return road;
    }
    
    /**
     * Gets the current position in the space.
     * @return - Point
     */
    public Point getPosition(){
    	GridPoint p = space.getLocation(this);
    	return new Point(p.getX(),p.getY());
    }
    
    /**
     * Gets the destination points.
     * @return - Point
     */
    public Point getDestination(){
    	return destination;
    }
    
    /**
     * Sets the journey.
     * @param journey
     */
    public void setJourney(ArrayList<String> journey){
    	
    	if(journey.size() > 0){
    		countNewJourney++;
	    	this.journey = journey;
	    	Debug.debugJourney(this);
    	}
    }
    
    /**
     * Sets the current road.
     * @param r - Road
     */
    public void setRoad(Road r){
    	road = r;
    }
    
    /**
     * Sets the current position.
     * It also updates the position in the space.
     * @param p - Point
     */
    public void setPosition(Point p){
    	this.position = p;
    	space.moveTo(this, position.toArray());
    }

    /**
     * Gets the space.
     * @return - Grid<Object>
     */
	public Grid<Object> getSpace() {
		return space;
	}

	/**
	 * Sets the space.
	 * @param space
	 */
	public void setSpace(Grid<Object> space) {
		this.space = space;
	}
	
	/**
	 * Gets the current journey that the car is following.
	 * @return
	 */
	public ArrayList<String> getJourney(){
		return journey;
	}
    
	/**
	 * Removes the first road of the journey.
	 */
	public void jorneyConsume(){
		if(journey.size() > 0)
			journey.remove(0);
	}
	
	/**
	 * Gets the current or latest intersection.
	 * @return
	 */
	public Intersection getIntersection(){
		return intersection;
	}
	
	/**
	 * Sets the current intersection
	 * @param i - Intersection
	 */
	public void setIntersection(Intersection i){
		intersection = i;
	}
	
	/**
	 * Gets the knowledge that the car has over the 
	 * city structure (map).
	 * @return
	 */
	public CityMap getCityKnowledge(){
		return knowledge.getCityKnowledge();
	}
	
	/**
	 * Gets the destination road name.
	 * @return
	 */
	public String getDestinationName(){
		return destinationName;
	}
	
	/**
	 * Sets the destination road name.
	 * @param n - Name of the road of its destination
	 */
	public void setDestinationName(String n){
		destinationName = n;
	}
	
	/**
	 * Sets the learningMode of the car.
	 * @param mode - LearningMode
	 */
	public void setLearningMode(LearningMode mode){
		learningMode = mode;
		Debug.debugLearningMode(this);
	}
	
	/**
	 * Gets the unexplored roads that the car as come
	 * across on its course.
	 * @return
	 */
	public HashMap<String,String> getUnexploredRoads(){
		return knowledge.getUnexploredRoads();
	}
	
	/**
	 * Gets the learning mode.
	 * @return
	 */
	public LearningMode getLearningMode(){
		return learningMode;
	}
	
	/**
	 * Gets the knowledge of the car.
	 * @return
	 */
	public CarSerializable getKnowledge(){
		return knowledge;
	}

	/**
	 * Function that returns a string with the main information
	 * about the car and its status,
	 * @return
	 */
	public String print() {
		return null;
	}
	
	/**
	 * 
	 */
	public void incCountGetPathSend() {
		countGetPathSend++;
	}
	
	/**
	 * 
	 */
	public void incCountWhichRoadSend() {
		countWhichRoadSend++;
	}
	
	/**
	 * 
	 */
	public String printStatistics() {
		String ret = "\n -- Monitored Car Statistics --\n";
		ret += "  GetPaths = "+countGetPathSend;
		ret += "\n  WhichRoads = "+countWhichRoadSend;
		ret += "\n  Secs = "+secs;
		ret += "\n  New Jorney = "+countNewJourney;
		ret += "\n -- -- \n";
		return ret;
	}
	
	
}
