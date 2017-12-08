package agents;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import resources.Point;
import resources.Resources;
import sajas.core.Agent;
import sajas.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.ArrayList;
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
	
	private Road road = null;							//Current road he is in (real world)
	private Intersection intersection = null;			//Latest intersection (real world)
	
	//Origin and Destination
	protected Point position;
	protected Point destination;
	protected String destinationName = null;
	protected ArrayList<String> journey = new ArrayList<String>();	//journey to reach the destination (composed by the names of the roads to follow)

	protected Grid<Object> space = null;
	
	private QLearning qlearning = new QLearning(this,1f,0.8f);
	//private HashMap<Integer,QLearning> transitKnowledge = new HashMap<Integer,QLearning>();  
	
	protected Knowledge knowledge = new Knowledge(null);

    public CarAgent(Grid<Object> space, Point origin, Point destination, Road startRoad,Knowledge knowledge) 
	{
		this.space = space;
		this.destination = destination;
		this.position = origin;
		this.road = startRoad;
		
		this.knowledge = knowledge;	//QualityValues, CityMap and mode
		this.qlearning.setQualityValues(knowledge.getQualityValues());
	}
    
    public CarAgent(Grid<Object> space, CityMap map, Point origin, Point destination, String endRoad, Road startRoad,Knowledge knowledge) 
	{
		this.space = space;
		this.destination = destination;
		this.position = origin;
		this.road = startRoad;
		this.destinationName = endRoad;
		
		this.knowledge = knowledge;
		this.qlearning.setQualityValues(knowledge.getQualityValues());
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
    	
    	if(destinationName != null){
    		
    		ArrayList<String> j =new ArrayList<String>();
    		
    		if(!knowledge.getLearningMode().equals(LearningMode.APPLYING))
    			j = AStar.shortestPath(knowledge.getCityKnowledge(), road, destinationName);
    		else{
    			String road = qlearning.getNextRoad(intersection);
    			if(road != null)
    				j.add(road);
    		}

    		
    		if(j.size() > 0){
	    		setJorney(j);
	    		jorneyConsume();
    		}
    	}
    	
    }
    
    /**
     * Calculates the journey of others
     * @param road
     * @param destinationName
     * @return
     */
    public ArrayList<String> getJourneyCalculations(Road road, String destinationName){
    	return AStar.shortestPath(knowledge.getCityKnowledge(), road, destinationName);
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
    
    public void setJorney(ArrayList<String> jorney){
    	this.journey = jorney;
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
    			"Destination: " + destination.print());
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
}
