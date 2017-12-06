package behaviours;

import java.util.ArrayList;

import com.jgoodies.common.internal.Messages;

import agents.CarAgent;
import algorithms.AStar;
import cityStructure.Road;
import cityTraffic.onto.ServiceOntology;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
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
				String destinationName = parts[2];
				Road r = null;
				
				//If the car knows the road
				if(car.getCityKnowledge().getRoads().containsKey(roadName)){
					r = car.getCityKnowledge().getRoads().get(roadName);
					ArrayList<String> route = car.getJourneyCalculations(r, destinationName);
					
					//If it knows the path to the destination
					if(route.size() > 0){
						AID receiver = message.getSender();
						message = new ACLMessage(ACLMessage.INFORM);
						message.setContent(MessagesResources.buildMessagePath(destinationName,route));
						message.addReceiver(receiver);
						car.send(message);
					}
				}
			}
			else if(type.equals(MessagesResources.MessageType.PATH)){
				
				//Don't process the same information
				if(car.getJorney().size() == 0){
					
					String parts[] = message.getContent().split(MessagesResources.SEPARATOR);
					
					String destination = parts[1];
					
					//Answer to GET_PATH message
					if(car.getDestinationName().equals(destination)){
					
						ArrayList<String> route = new ArrayList<String>();
						boolean foundRoad = true;
						
						//Looks for the current road in the route and builds the jorney path from there
						for(int i = 2; i < parts.length; i++){
							if(foundRoad)
								route.add(parts[i]);
							if(car.getRoad().getName().equals(parts[i]))
								foundRoad = true;
						}
						
						if(foundRoad){
							car.setJorney(route);
						}
					}
				}
				
			}
			else if(type.equals(MessagesResources.MessageType.WHICH_ROAD)){
				String parts[] = message.getContent().split(MessagesResources.SEPARATOR);
				Point destination = Point.getPoint(parts[1]);
				
				Road road = car.getCityKnowledge().isPartOfRoad(destination);
				
				if(road != null){
					AID receiver = message.getSender();
					message = new ACLMessage(ACLMessage.INFORM);
					message.setContent(MessagesResources.buildMessagePartOfRoad(destination, road.getName()));
					message.addReceiver(receiver);
					car.send(message);
				}
			}
			else if(type.equals(MessagesResources.MessageType.PART_OF_ROAD)){
				String parts[] = message.getContent().split(MessagesResources.SEPARATOR);
				Point destination = Point.getPoint(parts[1]);
				String roadName = parts[2];
				
				//Destination points matched!
				if(destination.equals(car.getDestination())){
					car.setDestinationName(roadName);
					
					//Try to find the path
					car.calculateAndUpdateJourney();
					
					if(car.getJorney().size() > 0)
						System.out.println("Found path by it self");
				}
			}
			else if(type.equals(MessagesResources.MessageType.BLOCKED)){
				String roadName = message.getContent().split(MessagesResources.SEPARATOR)[1];
				
				if(car.getCityKnowledge().getRoads().containsKey(roadName))
					car.getCityKnowledge().getRoads().get(roadName).blocked();
				
				if(car.getJorney().contains(roadName)){
					car.calculateAndUpdateJourney();
				}
			}
			else if(type.equals(MessagesResources.MessageType.UNBLOCKED)){
				String roadName = message.getContent().split(MessagesResources.SEPARATOR)[1];
				
				if(car.getCityKnowledge().getRoads().containsKey(roadName))
					car.getCityKnowledge().getRoads().get(roadName).unblocked();
				
				if(!car.getJorney().contains(roadName)){
					car.calculateAndUpdateJourney();
				}
			}
		}
	}
}
