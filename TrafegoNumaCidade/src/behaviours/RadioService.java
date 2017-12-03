package behaviours;

import java.util.ArrayList;

import agents.Radio;
import jade.lang.acl.ACLMessage;
import repast.simphony.space.grid.Grid;
import resources.MessagesResources;
import resources.SpaceResources;
import resources.MessagesResources.MessageType;
import sajas.core.AID;
import sajas.core.behaviours.CyclicBehaviour;

public class RadioService extends CyclicBehaviour{

	private static final long serialVersionUID = 1L;
	private Radio radio = null;
	private Grid<Object> space = null;
	
	public RadioService(Radio radio, long period, Grid<Object> space) {
	
		this.radio = radio;
		this.space = space;
	}

	@Override
	public void action() {
		ACLMessage message = radio.receive();
		
		if(message != null){
			MessageType type = MessagesResources.getMessageType(message.getContent());

			if(type.equals(MessagesResources.MessageType.TRANSIT) || 
					type.equals(MessagesResources.MessageType.NO_TRANSIT)){
				
				System.out.println("Radio received: " + message.getContent());
				
				String content[] = message.getContent().split(MessagesResources.SEPARATOR);
				String roadName = content[1];
				
				ArrayList<AID> aids = SpaceResources.getCarsAID(space);
				
				if(type.equals(MessagesResources.MessageType.TRANSIT))
					message.setContent(MessagesResources.buildMessage(MessageType.BLOCKED,roadName));
				else
					message.setContent(MessagesResources.buildMessage(MessageType.UNBLOCKED,roadName));
				
				//Warn the cars via radio
				for(AID a : aids){
					message.addReceiver(a);
				}
				message.removeReceiver(radio.getAID());
				radio.send(message);
			}
		}
		
		
	}
}
