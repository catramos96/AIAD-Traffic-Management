package behaviours;

import agents.RoadMonitor;
import cityStructure.Road;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import repast.simphony.space.grid.Grid;
import resources.MessagesResources;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import sajas.core.AID;
import sajas.core.behaviours.TickerBehaviour;

public class CheckTransit extends TickerBehaviour {


	private static final long serialVersionUID = 1L;
	private RoadMonitor monitor = null;
	private Grid<Object> space = null;
	private boolean warnedRadio = false;
	private ACLMessage message = null;
	
	public CheckTransit(RoadMonitor monitor, long period, Grid<Object> space) {
		super(monitor, period);
		
		this.monitor = monitor;
		this.space = space;	
		
		message = new ACLMessage(ACLMessage.INFORM);
		message.setLanguage(monitor.getCodec().getName()); 
        message.setOntology(monitor.getOntology().getName()); 
        message.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST); 
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
				AID receiver = (AID) monitor.getRadio().getAID();
				
		        message.setContent(
		        		MessagesResources.MessageType.TRANSIT+
		        		MessagesResources.SEPARATOR+
		        		r.getName()); 
		         
		        message.addReceiver(receiver); 
		        monitor.send(message); 
		        
		        warnedRadio = true;
			}
		}
		else{
			
			if(warnedRadio == true){
				AID receiver = (AID) monitor.getRadio().getAID();
				
		        message.setContent(
		        		MessagesResources.MessageType.NO_TRANSIT+
		        		MessagesResources.SEPARATOR+
		        		r.getName()); 
		         
		        message.addReceiver(receiver); 
		        monitor.send(message); 	
		        System.out.println("Sent Message: " + message.getContent());
			}
			
			warnedRadio = false;
		}
	}

}
