package behaviours;

import agents.RoadMonitor;
import cityStructure.Road;
import repast.simphony.space.grid.Grid;
import resources.Point;
import resources.Resources;
import resources.SpaceResources;
import sajas.core.behaviours.TickerBehaviour;

public class CheckTransit extends TickerBehaviour {


	private static final long serialVersionUID = 1L;
	private RoadMonitor monitor = null;
	private Grid<Object> space = null;
	
	public CheckTransit(RoadMonitor monitor, long period, Grid<Object> space) {
		super(monitor, period);
		
		this.monitor = monitor;
		this.space = space;
		
		System.out.println("Check Transit ready");
		
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
			
			System.out.println("Transit in " + r.getName());
			/*AID receiver = null;
			
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.setLanguage(monitor.getCodec().getName()); 
	        message.setOntology(monitor.getOntology().getName()); 
	        message.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST); 
	        
	        message.setContent(MessagesResources.MessageType.CAR_IN_TRANSIT+MessagesResources.SEPARATOR+r.getName()); 
	         
	        message.addReceiver(receiver); 
	        monitor.send(message); 
	         
	        System.out.println("Message sent!"); */
		}
	}

}
