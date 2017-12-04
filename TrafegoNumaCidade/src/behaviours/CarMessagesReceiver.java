package behaviours;

import java.util.ArrayList;

import agents.CarAgent;
import algorithms.AStar;
import cityStructure.Road;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import resources.MessagesResources;
import resources.Point;
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
			System.out.println("Car " + car.getLocalName() + " received: " + message.getContent());

			if(type.equals(MessagesResources.MessageType.GET_PATH)){
				
				String parts[] = message.getContent().split(MessagesResources.SEPARATOR);
				
				String roadName = parts[1];
				Point destination = Point.getPoint(parts[2]);
				Road r = null;
				
				//If the car knows the road
				if(car.getCityKnowledge().getRoads().containsKey(roadName)){
					r = car.getCityKnowledge().getRoads().get(roadName);
					ArrayList<String> route = AStar.shortestPath(car.getCityKnowledge(), r, destination);
					
					//If it knows the path to the destination
					if(route.size() > 0){
						AID receiver = message.getSender();
						message = new ACLMessage(ACLMessage.INFORM);
						message.setContent(MessagesResources.buildMessagePath(route));
						message.addReceiver(receiver);
						car.send(message);
					}
				}
			}
			else if(type.equals(MessagesResources.MessageType.PATH)){
				
				//Don't process the same information
				if(car.getJorney().size() == 0){
					
					String parts[] = message.getContent().split(MessagesResources.SEPARATOR);
					
					ArrayList<String> route = new ArrayList<String>();
					boolean foundRoad = true;
					
					//Looks for the current road in the route and builds the jorney path from there
					for(int i = 1; i < parts.length; i++){
						if(foundRoad)
							route.add(parts[i]);
						if(car.getRoad().getName().equals(parts[i]))
							foundRoad = true;
					}
					
					if(foundRoad){
						car.setJorney(route);
						System.out.println("FOUND ROUTE");
					}
				}
				
			}
		}
	}
}
