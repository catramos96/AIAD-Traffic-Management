package agents;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
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

import behaviours.CarMessagesReceiver;
import behaviours.CarMovement;
import behaviours.LearnMap;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import cityTraffic.onto.ServiceOntology;

public class CarAgent extends Agent {

	private Road road = null;
	private Intersection intersection = null;
	
	//Origin and Destination
	private CityMap cityKnowledge;				//What the agent knows about the city
	private CityMap map;						//City structure (Full)
	private Point position;
	private Point destination;
	private ArrayList<Road> jorney = new ArrayList<Road>();

	private Grid<Object> space;
    
    private Codec codec;
    private Ontology serviceOntology;
    
    private boolean knowsTheCity = false;
    
    /**
     * Constructor
     * @param space
     * @param map
     * @param origin
     * @param destination
     */
    public CarAgent(Grid<Object> space, CityMap map, Point origin, Point destination, Road startRoad, boolean knowsTheCity) 
	{
		this.space = space;
		this.destination = destination;
		this.position = origin;
		this.map = map;			
		this.road = startRoad;
		
		this.knowsTheCity = knowsTheCity;
		
		if(knowsTheCity)
			cityKnowledge = map;
		else
			cityKnowledge = new CityMap();
	}
    
    //JADE RELATED
    @Override
    public void setup() {

        // register language and ontology
        codec = new SLCodec();
        serviceOntology = ServiceOntology.getInstance();
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(serviceOntology);
        
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
        
        if(!knowsTheCity)
        	addBehaviour(new LearnMap(this));
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
    
    public CityMap getMap(){
    	return map;
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
    
    public void setJorney(ArrayList<Road> jorney){
    	this.jorney = jorney;
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
	
	public ArrayList<Road> getJorney(){
		return jorney;
	}
    
	public void jorneyConsume(){
		if(jorney.size() > 0)
			jorney.remove(0);
	}
	
	public Intersection getIntersection(){
		return intersection;
	}
	
	public void setIntersection(Intersection i){
		intersection = i;
	}
	
	public CityMap getCityKnowledge(){
		return cityKnowledge;
	}
}
