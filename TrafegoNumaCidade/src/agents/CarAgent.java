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
import algorithms.AStar;
import algorithms.QLearning;
import behaviours.AskDirections;
import behaviours.CarMessagesReceiver;
import behaviours.CarMovement;
import behaviours.LearnMap;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;

public class CarAgent extends Agent {

	public static enum LearningMode {LEARNING, APPLYING, NONE};
	
	//current position
	protected Road road = null;							//Current road he is in (real world)
	protected Intersection intersection = null;			//Latest intersection (real world)
	protected Point position = null;

	protected CarSerializable knowledge = new CarSerializable(null);
	protected ArrayList<String> journey = new ArrayList<String>();	//journey to reach the destination (composed by the names of the roads to follow)
	protected QLearning qlearning = new QLearning(this, 1f, 0.8f);
	
	protected Grid<Object> space = null;
    
    public CarAgent(Grid<Object> space, Point origin, Road startRoad,CarSerializable knowledge) 
	{
		this.space = space;
		
		this.position = origin;
		this.road = startRoad;
		
		this.knowledge = knowledge;	//QualityValues, CityMap, Mode and Destination
		
		this.qlearning.setQualityValues(knowledge.getQualityValues());
	}
    /*
    public CarAgent(Grid<Object> space, CityMap map, Point origin, String endRoad, Road startRoad,CarSerializable knowledge) 
	{
		this.space = space;

		this.position = origin;
		this.road = startRoad;
		
		this.knowledge = knowledge;
		this.knowledge.setDestinationName(endRoad);
		
		this.qlearning.setQualityValues(knowledge.getQualityValues());
	}
	*/

    
    //JADE RELATED
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
        
        if(knowledge.getLearningMode().equals(LearningMode.LEARNING)){
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
     * GETS & SETS
     */
    public QLearning getQLearning(){
    	return qlearning;
    }
    
    /**
     * Calculates and updates the journey of itself
     * only if it was successful at calculating a new route
     */
    public void calculateAndUpdateJourney(){
    		
    		ArrayList<String> j =new ArrayList<String>();
    		
    		switch(knowledge.getLearningMode()){
	    		case NONE:{
	    			if(knowledge.getDestinationName() != null)
	    				j = AStar.shortestPath(knowledge.getCityKnowledge(), road, knowledge.getDestinationName(),true);
	    			break;
	    		}
	    		case LEARNING:{
	    			
	    			if(journey.size() == 0){
	    				//Chooses a path to an unvisited road
		    			for(String unexploredRoad : knowledge.getUnexploredRoads().keySet()){
		    				
		    				String intersection = knowledge.getUnexploredRoads().get(unexploredRoad);
		    				
		    				if(intersection != ""){
		    					if(this.getClass().equals(MonitoredCarAgent.class))
		    						System.out.println("TRY finding path to " + unexploredRoad + " by " + intersection);
		    					j = AStar.shortestPath(knowledge.getCityKnowledge(), road, intersection,false);
			    				if(j.size() > 0){
			    					System.out.println("FOUND PATH");
			    					break;
			    				}
		    				}
		    			}
		    			break;
	    			}
	    			
	    		}
	    		case APPLYING: {
	    			String road = qlearning.getNextRoad(intersection);
	    			if(road != null)
	    				j.add(road);
	    			break;
	    		}
    		}

    		
    		if(j.size() > 0){
	    		setJourney(j);
	    		jorneyConsume();
    		}
    	
    }
    
    /**
     * Calculates the journey of others
     * @param road
     * @param destinationName
     * @return
     */
    public ArrayList<String> getJourneyCalculations(Road road, String destinationName){
    	return AStar.shortestPath(knowledge.getCityKnowledge(), road, destinationName,true);
    }
    
    public Road getRoad(){
    	return road;
    }
    
    public Point getPosition(){
    	GridPoint p = space.getLocation(this);
    	return new Point(p.getX(),p.getY());
    }
    
    public Point getDestination(){
    	return knowledge.getDestination();
    }
    
    public void setJourney(ArrayList<String> journey){
    	
    	if(journey.size() > 0){
	    	this.journey = journey;
	    	Debug.debugJourney(this);
    	}
    }
    
    public void setRoad(Road r){
    	road = r;
    }
    
    public void setPosition(Point p){
    	this.position = p;
    	space.moveTo(this, position.toArray());
    }
    
    public String print(){
    	return new String("Car:\n" +
    			"Position: " + position.print() + "\n" +
    			"Destination: " + knowledge.getDestination().print());
    }

	public Grid<Object> getSpace() {
		return space;
	}


	public void setSpace(Grid<Object> space) {
		this.space = space;
	}
	
	public ArrayList<String> getJourney(){
		return journey;
	}
    
	public void jorneyConsume(){
		if(journey.size() > 0)
			journey.remove(0);
	}
	
	public Intersection getIntersection(){
		return intersection;
	}
	
	public void setIntersection(Intersection i){
		intersection = i;
	}
	
	public CityMap getCityKnowledge(){
		return knowledge.getCityKnowledge();
	}
	
	public String getDestinationName(){
		return knowledge.getDestinationName();
	}
	
	public void setDestinationName(String n){
		knowledge.setDestinationName(n);
	}
	
	public void setLearningMode(LearningMode mode){
		knowledge.setLearningMode(mode);
		Debug.debugLearningMode(this);
	}
	
	public HashMap<String,String> getUnexploredRoads(){
		return knowledge.getUnexploredRoads();
	}
	
	public LearningMode getLearningMode(){
		return knowledge.getLearningMode();
	}
}
