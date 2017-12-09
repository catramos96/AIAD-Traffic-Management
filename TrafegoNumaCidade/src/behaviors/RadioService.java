package behaviors;
import agents.Car;
import agents.Radio;
import jade.lang.acl.ACLMessage;
import repast.simphony.context.Context;
import repast.simphony.util.ContextUtils;
import resources.Debug;
import resources.MessagesResources;
import resources.MessagesResources.MessageType;
import sajas.core.behaviours.CyclicBehaviour;

/**
 * Behavior to handle the messages received by a radio.
 *
 */
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
			
			Debug.debugMessageReceived(radio, message.getContent());
			
			MessageType type = MessagesResources.getMessageType(message.getContent());

			//Message is about transit flow ?
			if(type.equals(MessagesResources.MessageType.TRANSIT) || 
					type.equals(MessagesResources.MessageType.NO_TRANSIT)){
				
				String content[] = message.getContent().split(MessagesResources.SEPARATOR);
				String roadName = content[1];
				
				if(type.equals(MessagesResources.MessageType.TRANSIT))
					message.setContent(MessagesResources.buildMessage(MessageType.BLOCKED,roadName));
				else
					message.setContent(MessagesResources.buildMessage(MessageType.UNBLOCKED,roadName));
				
				//Warn the cars via radio
				Context<Car> context = ContextUtils.getContext(radio);
			    for(Car c : context.getAgentLayer(Car.class)){
			    	message.addReceiver(c.getAID());
			    }
			    
				message.removeReceiver(radio.getAID());
				
				radio.send(message);
				Debug.debugMessageSent(radio, message.getContent());
			}
		}
		
		
	}
}
