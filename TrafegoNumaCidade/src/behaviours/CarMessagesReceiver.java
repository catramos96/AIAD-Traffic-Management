package behaviours;

import agents.CarAgent;
import jade.lang.acl.ACLMessage;
import resources.MessagesResources;
import resources.MessagesResources.MessageType;
import sajas.core.behaviours.CyclicBehaviour;

public class CarMessagesReceiver extends CyclicBehaviour{

	private static final long serialVersionUID = 1L;
	private CarAgent car = null;
	
	public CarMessagesReceiver(CarAgent car) {
		this.car = car;
	}

	@Override
	public void action() {
		ACLMessage message = car.receive();
		
		if(message != null){
			MessageType type = MessagesResources.getMessageType(message.getContent());
			
			System.out.println("Car received: " + message.getContent());
		}
		
	}

}
