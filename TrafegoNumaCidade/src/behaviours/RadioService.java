package behaviours;
import agents.CarAgent;
import agents.Radio;
import jade.lang.acl.ACLMessage;
import repast.simphony.context.Context;
import repast.simphony.util.ContextUtils;
import resources.MessagesResources;
import resources.MessagesResources.MessageType;
import sajas.core.behaviours.CyclicBehaviour;

public class RadioService extends CyclicBehaviour{

	private static final long serialVersionUID = 1L;
	private Radio radio = null;
	
	public RadioService(Radio radio) {
		this.radio = radio;
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
				
				if(type.equals(MessagesResources.MessageType.TRANSIT))
					message.setContent(MessagesResources.buildMessage(MessageType.BLOCKED,roadName));
				else
					message.setContent(MessagesResources.buildMessage(MessageType.UNBLOCKED,roadName));
				
				//Warn the cars via radio
				Context<CarAgent> context = ContextUtils.getContext(radio);
			    for(CarAgent c : context.getAgentLayer(CarAgent.class)){
			    	message.addReceiver(c.getAID());
			    }
			    
				message.removeReceiver(radio.getAID());
				radio.send(message);
			}
		}
		
		
	}
}
