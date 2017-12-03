package behaviours;

import java.util.ArrayList;

import agents.Radio;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
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
				
				String content[] = message.getContent().split(MessagesResources.SEPARATOR);
				String roadName = content[1];
				
				System.out.println("Radio received: " + content[0] + " on " + roadName);
				
				ArrayList<AID> aids = SpaceResources.getCarsAID(space);
				
				if(type.equals(MessagesResources.MessageType.TRANSIT)){
					message.setContent(MessagesResources.MessageType.BLOCKED + 
							MessagesResources.SEPARATOR +
							roadName);
				}
				else{
					message.setContent(MessagesResources.MessageType.UNBLOCKED + 
							MessagesResources.SEPARATOR +
							roadName);
				}
				
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
