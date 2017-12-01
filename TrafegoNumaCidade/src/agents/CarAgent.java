package agents;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import sajas.core.Agent;
import jade.core.AID;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.WakerBehaviour;
import sajas.core.behaviours.WrapperBehaviour;
import sajas.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import sajas.proto.ContractNetInitiator;
import sajas.proto.SubscriptionInitiator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.jogamp.common.util.RunnableTask;

import algorithms.AStar;
import cityStructure.CityMap;
import cityStructure.Intersection;
import cityStructure.Road;
import cityTraffic.onto.ContractOutcome;
import cityTraffic.onto.Results;
import cityTraffic.onto.ServiceOntology;
import cityTraffic.onto.ServiceProposalRequest;

public class CarAgent extends Agent {
	
	private String requiredService = "bla";


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

	Grid<Object> space;

	int sign = 1;
    
    private int nContracts;
    private ArrayList<ContractOutcome> contractOutcomes = new ArrayList<ContractOutcome>();
    private AID resultsCollector;
    
    // providers and their values
    private int nBestProviders;
    private Map<AID,ProviderValue> providersTable = new HashMap<AID,ProviderValue>();
    private ArrayList<ProviderValue> providersList = new ArrayList<ProviderValue>();
    
    private Context<?> context;
    private Grid<Object> contextSpace;
    private RepastEdge<Object> edge = null;
    
    private Codec codec;
    private Ontology serviceOntology;
    
    protected ACLMessage myCfp;
    
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
    		   System.out.println("Calculating shortest path ...");
    	   }

    	   public void run() {
    		   car.setJorney(AStar.shortestPath(car.getMap(), car.getRoad(), car.getDestination()));
    		   jorney.remove(0);	//removes the first road because is the road where the car is
    	   }
    	}
    
    /*
     * Scheduled Methods
     */
    
    @ScheduledMethod(start=1 , interval=150000)
	public void updateCarsPosition()
	{
		try
		{
			Point pos = new Point(space.getLocation(this).getX(),space.getLocation(this).getY());
			
			if(destination.equals(pos)){
				System.out.println("Destination Accomplished");
				setPosition(new Point(0,0));
				passageType = PassageType.Out;
			}
			
			if(passageType.equals(PassageType.Road) && road != null){
				
				//End of the road
				if(pos.equals(road.getEndPoint())){		
					
					//Check semaphores
					if(SpaceResources.hasRedOrYellowSemaphore(space, pos))
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
							System.out.println("jorney");
							
							//no route found
							if(intersectionRoute.size() == 0){
								valid = false;
								jorney = new ArrayList<Road>();
							}
						}
						else{
							valid = false;
							System.out.println("jorney invalid");
						}
						
						if(!valid){
							int road_index = (int) (Math.random() * intersection.getOutRoads().size());
							nextRoad = intersection.getOutRoads().get(road_index);
							intersectionRoute = intersection.getRouteToRoad(road, nextRoad);
							
							System.out.println("random");
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

					if(!SpaceResources.hasCar(space, next_position)){
						space.moveTo(this, next_position.toArray());
					}
				}
			}
			
			if(passageType.equals(PassageType.Intersection)){	
				
				Point next_position = intersectionRoute.get(0);

				if(!SpaceResources.hasCar(space, next_position)){
					
					space.moveTo(this, next_position.toArray());

					intersectionRoute.remove(0);
					
					if(intersectionRoute.size()== 0)
						passageType = PassageType.Road;
				}
			}			
			
		}
		catch(Exception e)
		{
			sign = -sign;
			System.out.println("Car out of the grid!");
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
    	
    	String s = new String();
		s = "Jorney\n" + print() + "\n";

    	for(Road r : jorney){
    		s+= r.getName() + " ";
    	}
    	
    	System.out.println(s);
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
    /******************** OTHER CLASSES  ********************/
    
    private class ProviderValue implements Comparable<ProviderValue> {
		private final double INITIAL_VALUE = 0.5;

		private AID provider;
		private int nSuccess = 0;
		private int nFailure = 0;
		private double value;
		
		ProviderValue(AID provider) {
			this.provider = provider;
			this.value = INITIAL_VALUE;
		}
		
		public AID getProvider() {
			return provider;
		}
		
		public void addOutcome(ContractOutcome.Value outcome) {
			switch(outcome) {
			case SUCCESS:
				nSuccess++;
				break;
			case FAILURE:
				nFailure++;
				break;
			}
			value = (double) nSuccess/(nSuccess+nFailure);
		}
		
		public double getValue() {
			return value;
		}
		
		@Override
		public int compareTo(ProviderValue o) {
			// descending order
			if(o.value < value) {
				return -1;
			} else if(o.value > value) {
				return 1;
			} else {
				return provider.compareTo(o.getProvider());
			}
		}
		
	}
    
    private class DFSubscInit extends SubscriptionInitiator {
		
		private static final long serialVersionUID = 1L;

		DFSubscInit(Agent agent, DFAgentDescription dfad) {
			super(agent, DFService.createSubscriptionMessage(agent, getDefaultDF(), dfad, null));
		}
		
		protected void handleInform(ACLMessage inform) {
			try {
				DFAgentDescription[] dfads = DFService.decodeNotification(inform.getContent());
				for(int i = 0; i < dfads.length; i++) {
					AID agent = dfads[i].getName();
					((ConsumerAgent) myAgent).addProvider(agent);
				}
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}

		}
		
	}
    
    private class StartCNets extends WakerBehaviour {

		private static final long serialVersionUID = 1L;

		public StartCNets(Agent a, long timeout) {
			super(a, timeout);
		}
		
		@Override
		public void onWake() {
			// context and network (RepastS)
			context = ContextUtils.getContext(myAgent);
			contextSpace = (Grid<Object>) context.getProjection("Street map");
			
			// initiate CNet protocol
			CNetInit cNetInit = new CNetInit(myAgent, (ACLMessage) myCfp.clone());
			addBehaviour(new CNetInitWrapper(cNetInit));
		}

	}
    
    private class CNetInitWrapper extends WrapperBehaviour {

		private static final long serialVersionUID = 1L;

		public CNetInitWrapper(Behaviour wrapped) {
			super(wrapped);
		}
		
		public int onEnd() {
			if(--nContracts > 0) {
				// initiate new CNet protocol
				CNetInit cNetInit = new CNetInit(myAgent, (ACLMessage) myCfp.clone());
				addBehaviour(new CNetInitWrapper(cNetInit));
				return 1;
			} else {
				if(resultsCollector != null) {
					ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
					inform.addReceiver(resultsCollector);
					inform.setLanguage(codec.getName());
					inform.setOntology(serviceOntology.getName());
					//
					Results results = new Results(nBestProviders, contractOutcomes);
					try {
						getContentManager().fillContent(inform, results);
					} catch (CodecException | OntologyException e) {
						e.printStackTrace();
					}
					myAgent.send(inform);
				}
				return 0;
			}
		}
		
	}
	
	private class CNetInit extends ContractNetInitiator {

		private static final long serialVersionUID = 1L;

		public CNetInit(Agent owner, ACLMessage cfp) {
			super(owner, cfp);
		}

		@Override
		public Vector prepareCfps(ACLMessage cfp) {
			// select best providers
			ArrayList<AID> bestProviders = ((CarAgent) myAgent).getBestProviders();
			for(AID provider : bestProviders) {
				cfp.addReceiver(provider);
			}
			
			return super.prepareCfps(cfp);
		}
	}

	protected ArrayList<AID> getBestProviders() {
		
		ArrayList<AID> bestProviders = new ArrayList<AID>();
		
		Collections.sort(providersList);
		for(int i = 0; i < nBestProviders && i < providersList.size(); i++) {
			bestProviders.add(providersList.get(i).getProvider());
		}
		
		return bestProviders;
	}
	
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
        template.addServices(sd);
        addBehaviour(new DFSubscInit(this, template));

        // prepare cfp message
        myCfp = new ACLMessage(ACLMessage.CFP);
        myCfp.setLanguage(codec.getName());
        myCfp.setOntology(serviceOntology.getName());
        myCfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        //
        ServiceProposalRequest serviceProposalRequest = new ServiceProposalRequest(requiredService);
        try {
            getContentManager().fillContent(myCfp, serviceProposalRequest);
        } catch (CodecException | OntologyException e) {
            e.printStackTrace();
        }
        
        // waker behaviour for starting CNets
        addBehaviour(new StartCNets(this, 2000));
    }
	
}
