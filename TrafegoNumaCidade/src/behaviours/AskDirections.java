package behaviours;

import java.util.ArrayList;

import agents.Car;
import agents.Car.LearningMode;
import agents.CarMonitored;
import cityTraffic.onto.ServiceOntology;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import resources.Debug;
import resources.MessagesResources;
import resources.Point;
import resources.SpaceResources;
import sajas.core.AID;
import sajas.core.behaviours.TickerBehaviour;

public class AskDirections extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	private Car car = null;
	private ArrayList<AID> carsAsked = new ArrayList<AID>();
	private String lastRoadName = "";
	
	//Messages specification
    private SLCodec codec;
    private Ontology serviceOntology;
	 
	public AskDirections(Car car, long time) {
		super(car,time);
		this.car = car;
		lastRoadName = car.getRoad().getName();
		
		 // register language and ontology
        codec = new SLCodec();
        serviceOntology = ServiceOntology.getInstance();
        car.getContentManager().registerLanguage(codec);
        car.getContentManager().registerOntology(serviceOntology);
	}

	@Override
	protected void onTick() {
		
		//Car already learned enough
		if(car.getLearningMode().equals(LearningMode.APPLYING))
			car.removeBehaviour(this);
		
		//If it didn't received any valuable directions before
		if(car.getJourney().size() == 0 || car.getDestinationName() == null){
			
			if(!car.getRoad().getName().equals(lastRoadName)){
				lastRoadName = car.getRoad().getName();
				carsAsked.clear();
			}
			
			//prepare message
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.setLanguage(codec.getName()); 
	        message.setOntology(serviceOntology.getName()); 
	        message.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST); 
	        
	       
	        if(car.getDestinationName() == null)
	        	message.setContent(MessagesResources.buildMessageWhichRoad(car.getDestination()));
	        else if(car.getJourney().size() == 0)
		       	message.setContent(MessagesResources.buildMessageGetPath(car.getRoad().getName(),car.getDestinationName()));
	        
	        boolean gotReceiver = false;

			//Get cells inside the car comunication radius
			for(int i = 0; i < MessagesResources.CommunicationRadius * 2 + 1; i++){
				for(int j = 0; j < MessagesResources.CommunicationRadius * 2 + 1;j++){
					
					Point p = new Point(car.getPosition().x- MessagesResources.CommunicationRadius + i, 
							car.getPosition().y - MessagesResources.CommunicationRadius + j);
					
					//if valid cell
					if(!p.equals(car.getPosition()) && p.x >= 0 && p.y >= 0 &&
							p.x < car.getSpace().getDimensions().getWidth() && 
							p.y < car.getSpace().getDimensions().getHeight()){

						Car c = SpaceResources.hasCar(car.getSpace(), p);	
						
						if(c != null){
							if(!carsAsked.contains(c.getAID())){
								message.addReceiver(c.getAID());
								carsAsked.add((AID) c.getAID());
								gotReceiver = true;
							}
						}
					}
				}
			}

			if(gotReceiver){
				car.send(message);
				Debug.debugMessageSent(car, message.getContent());
			}
			
		}
	}

}
