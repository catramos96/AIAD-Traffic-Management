package behaviours;

import agents.RoadMonitor;
import cityStructure.Road;
import cityTraffic.onto.ServiceOntology;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import repast.simphony.space.grid.Grid;
import resources.Debug;
import resources.MessagesResources;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import sajas.core.AID;
import sajas.core.behaviours.TickerBehaviour;

public class TransitMonitorization extends TickerBehaviour {


	private static final long serialVersionUID = 1L;
	private RoadMonitor monitor = null;
	private Grid<Object> space = null;
	private boolean warnedRadio = false;
	private ACLMessage message = null;
	
	public TransitMonitorization(RoadMonitor monitor, long period, Grid<Object> space) {
		super(monitor, period);
		
		this.monitor = monitor;
		this.space = space;	
		
		SLCodec codec = new SLCodec();
        Ontology ontology = ServiceOntology.getInstance();
        monitor.getContentManager().registerLanguage(codec);
        monitor.getContentManager().registerOntology(ontology);
		
		message = new ACLMessage(ACLMessage.INFORM);
		message.setLanguage(codec.getName()); 
        message.setOntology(ontology.getName()); 
        message.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST); 
        
		AID receiver = (AID) monitor.getRadio().getAID();
        message.addReceiver(receiver); 
	}
	
	@Override
	protected void onTick() {
		Road r = monitor.getRoad();
		Point p = r.getStartPoint();
		int n = 0;
		
		for(int i = 0; i < r.getLength(); i++){
			if(SpaceResources.hasCar(space, p) != null)
				n++;
			p = Resources.incrementDirection(r.getDirection(), p);
		}
		
		if(n > SpaceResources.getMaxCarStopped(r.getLength())){
			
			if(!warnedRadio){
		        message.setContent(MessagesResources.buildMessage(MessagesResources.MessageType.TRANSIT,r.getName())); 
		        
		        monitor.send(message); 
		        Debug.debugMessageSent(monitor, message.getContent());
		        
		        warnedRadio = true;
			}
		}
		else{
			
			if(warnedRadio == true){
		        message.setContent(MessagesResources.buildMessage(MessagesResources.MessageType.NO_TRANSIT,r.getName())); 
		        
		        monitor.send(message); 
		        Debug.debugMessageSent(monitor, message.getContent());
			}
			warnedRadio = false;
		}
	}

}
