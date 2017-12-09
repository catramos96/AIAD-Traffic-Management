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

	public static enum LearningMode {SHORT_LEARNING,LEARNING, APPLYING, NONE};
	
	//current position
	protected Road road = null;							//Current road he is in (real world)
	protected Intersection intersection = null;			//Latest intersection (real world)
	protected Point position = null;
	
	//destination
	protected Point destination = null;
	protected String destinationName = null;

    //actual destination and learning mode to be apply
    protected LearningMode learningMode = LearningMode.NONE;
    
    //persistent knowledge of the agent
	protected CarSerializable knowledge = new CarSerializable(null);
	
	protected ArrayList<String> journey = new ArrayList<String>();	//journey to reach the destination (composed by the names of the roads to follow)
	protected QLearning qlearning = null;
	
	protected Grid<Object> space = null;
    
    public CarAgent(Grid<Object> space, Point origin, Road startRoad, Point destination, CarSerializable knowledge, LearningMode mode) 
	{
		this.space = space;
		
		this.position = origin;
		this.road = startRoad;
		this.destination = destination;
		this.learningMode = mode;
		this.knowledge = knowledge;	//QualityValues, CityMap, Mode
		this.qlearning = new QLearning(this, 1f, 0.8f,knowledge.getQualityValues());
		
		if(learningMode.equals(LearningMode.NONE)){
			for(Road r : knowledge.getCityKnowledge().getRoads().values()){
				if(r.partOfRoad(destination)){
					destinationName = r.getName();
					break;
				}
			}
		}
	}

    
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
		
		switch(learningMode){
    		case NONE: {
    			if(destinationName != null){
    				if(knowledge.getCityKnowledge().getRoads().containsKey(road.getName())){
    					Road r = knowledge.getCityKnowledge().getRoads().get(road.getName());
    					j = AStar.shortestPath(knowledge.getCityKnowledge(), r,destinationName,true);
    				}
    			}
    			break;
    		}
    		case SHORT_LEARNING:{
    			if(destinationName != null){
    				if(knowledge.getCityKnowledge().getRoads().containsKey(road.getName())){
        				System.out.println("Trying calculating ...");
    					Road r = knowledge.getCityKnowledge().getRoads().get(road.getName());
    					j = AStar.shortestPath(knowledge.getCityKnowledge(), r, destinationName,true);
    				}
    			}
    			//Look for the paths to unvisited roads the end of the current road is unknown
    			if(journey.size() == 0 && j.size() == 0 && 
    					knowledge.getCityKnowledge().getIntersections().containsKey(road.getEndIntersection().getName())){
    				
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
		    				
		    				if(intersection != ""){
		    					
		    					if(this.getClass().equals(MonitoredCarAgent.class))
		    						System.out.println("TRY finding path to " + unexploredRoad + " by " + intersection);
		    					
		    					Road r = knowledge.getCityKnowledge().getRoads().get(road.getName());
		    					j = AStar.shortestPath(knowledge.getCityKnowledge(), r, intersection,false);
			    				
		    					if(j.size() != 0){
		    						j.add(unexploredRoad);
		    						break;
			    				}
		    				}
		    			}
    				}
    			}
    			break;
    		}
    		case LEARNING:{
    			
    			if(journey.size() == 0){
    				//Look for the paths to unvisited roads the end of the current road is unknown
        			if(journey.size() == 0 && j.size() == 0 && 
        					knowledge.getCityKnowledge().getIntersections().containsKey(road.getEndIntersection().getName())){
        				
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
    		    				
    		    				if(intersection != ""){
    		    					
    		    					if(this.getClass().equals(MonitoredCarAgent.class))
    		    						System.out.println("TRY finding path to " + unexploredRoad + " by " + intersection);
    		    					
    		    					Road r = knowledge.getCityKnowledge().getRoads().get(road.getName());
    		    					j = AStar.shortestPath(knowledge.getCityKnowledge(), r, intersection,false);
    			    				
    		    					if(j.size() != 0){
    		    						j.add(unexploredRoad);
    		    						break;
    			    				}
    		    				}
    		    			}
        				}
        			}
    			}
    			break;
    			
    		}
    		case APPLYING: {
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
    	return destination;
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
		return destinationName;
	}
	
	public void setDestinationName(String n){
		destinationName = n;
	}
	
	public void setLearningMode(LearningMode mode){
		learningMode = mode;
		Debug.debugLearningMode(this);
	}
	
	public HashMap<String,String> getUnexploredRoads(){
		return knowledge.getUnexploredRoads();
	}
	
	public LearningMode getLearningMode(){
		return learningMode;
	}
	
	public CarSerializable getKnowledge(){
		return knowledge;
	}


	public String print() {
		// TODO Auto-generated method stub
		return null;
	}
}
