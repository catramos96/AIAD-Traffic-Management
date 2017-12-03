package agents;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import sajas.core.Agent;
import sajas.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

import algorithms.AStar;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import cityTraffic.onto.ServiceOntology;

public class CarAgent extends Agent {
	
	//private String requiredService = "bla";

	//Map Positioning
	private enum PassageType {Road, Intersection, Out};
	private PassageType passageType = PassageType.Road;
	//road
	private Road road = null;
	//intersection
	private Intersection intersection = null;
	private ArrayList<Point> intersectionRoute = new ArrayList<Point>();
	
	//Semaphore
	private boolean greenSemaphore = true;
	
	//Origin and Destination
	private CityMap map;
	private Point position;
	private Point destination;
	private ArrayList<Road> jorney = new ArrayList<Road>();

	private Grid<Object> space;

	int sign = 1;
	
    private Context<?> context;
    //private Grid<Object> contextSpace;
    private RepastEdge<Object> edge = null;
    
    private Codec codec;
    private Ontology serviceOntology;
    
    
    /**
     * Contructor
     * @param space
     */
    
    public CarAgent(Grid<Object> space, CityMap map, Point origin, Point destination) 
	{
		this.space = space;
		this.destination = destination;
		this.position = origin;
		this.map = map;			
		
		new Thread(new CalculateShortestPath(this)).start();
	}
    
    /*
     * Threads
     */
    public class CalculateShortestPath implements Runnable {

    	CarAgent car = null;
    	
    	   public CalculateShortestPath(CarAgent c) {
    		   this.car = c;
    	   }

    	   public void run() {
    		   car.setJorney(AStar.shortestPath(car.getMap(), car.getRoad(), car.getDestination()));
    		   jorney.remove(0);	//removes the first road because is the road where the car is
    	   }
    	}
    
    /*
     * Scheduled Methods
     */
    
    @ScheduledMethod(start=1, interval = 100000)
    public void receiveMessages(){
    	ACLMessage message = this.receive();
    	
    	if(message != null){
    		System.out.println(message.getContent());
    	}
    }
    
    
    @ScheduledMethod(start=1 , interval=150000)
	public void updateCarsPosition()
	{
		try
		{
			Point pos = new Point(space.getLocation(this).getX(),space.getLocation(this).getY());
			
			if(destination.equals(pos)){
				setPosition(new Point(0,0));
				passageType = PassageType.Out;
			}
			
			if(passageType.equals(PassageType.Road) && road != null){
				
				//End of the road
				if(pos.equals(road.getEndPoint())){		
					
					//Check semaphores
					if(SpaceResources.hasRedOrYellowSemaphore(space, pos) != null)
						greenSemaphore = false;
					else
						greenSemaphore = true;
					
					//If it's ok to advance
					if(greenSemaphore){
						intersection = road.getEndIntersection();
						
						Road nextRoad = null;
						boolean valid = true;
						
						if(jorney.size() != 0){
							nextRoad = jorney.get(0);
							jorney.remove(0);
							intersectionRoute = intersection.getRouteToRoad(road, nextRoad);
							
							//no route found
							if(intersectionRoute.size() == 0){
								valid = false;
								jorney = new ArrayList<Road>();
							}
						}
						else
							valid = false;
						
						if(!valid){
							int road_index = (int) (Math.random() * intersection.getOutRoads().size());
							nextRoad = intersection.getOutRoads().get(road_index);
							intersectionRoute = intersection.getRouteToRoad(road, nextRoad);
						}
						
						road = nextRoad;
						
						//Calculate a new path starting on the nextRoad in case
						//the current path is not valid
						if(!valid)
							new Thread(new CalculateShortestPath(this)).start();
						
						passageType = PassageType.Intersection;
					}
				}
				else{
					Point next_position = Resources.incrementDirection(road.getDirection(), pos);

					if(SpaceResources.hasCar(space, next_position) == null){
						space.moveTo(this, next_position.toArray());
					}
				}
			}
			
			if(passageType.equals(PassageType.Intersection)){	
				
				Point next_position = intersectionRoute.get(0);

				if(SpaceResources.hasCar(space, next_position) == null){
					
					space.moveTo(this, next_position.toArray());

					intersectionRoute.remove(0);
					
					if(intersectionRoute.size()== 0)
						passageType = PassageType.Road;
				}
			}			
			
		}
		catch(Exception e){
			sign = -sign;
		}
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
        //addBehaviour(new DFSubscInit(this, template));
        try {
        	DFService.register(this, template);
        } catch(FIPAException ex) {
        	ex.printStackTrace();
        }

        //addBehaviour(new TestBehaviour(this,1000,space,codec,serviceOntology));
      
        /*
        ServiceProposalRequest serviceProposalRequest = new ServiceProposalRequest(requiredService);
        try {
            getContentManager().fillContent(inform, serviceProposalRequest);
        } catch (CodecException | OntologyException e) {
            e.printStackTrace();
        }
                
        // waker behaviour for starting CNets
        addBehaviour(new StartCNets(this, 2000));
        */
    }

    @Override
    protected void takeDown() {
    	try {
        	DFService.deregister(this);
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
    	return position;
    }
    
    public Point getDestination(){
    	return destination;
    }
    
    public void setJorney(ArrayList<Road> jorney){
    	this.jorney = jorney;
    }
    
    public void setRoad(Road r){
    	road = r;
    	passageType = PassageType.Road;
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
    
	
}
