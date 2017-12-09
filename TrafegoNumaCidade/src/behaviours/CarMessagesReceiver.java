package behaviours;

import java.util.ArrayList;
import agents.Car;
import agents.Car.LearningMode;
import cityStructure.Road;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import resources.Debug;
import resources.MessagesResources;
import resources.Point;
import resources.MessagesResources.MessageType;
import sajas.core.behaviours.CyclicBehaviour;

public class CarMessagesReceiver extends CyclicBehaviour{

	private static final long serialVersionUID = 1L;
	private Car car = null;
	
	public CarMessagesReceiver(Car car) {
		this.car = car;
	}

	@Override
	public void action() {
		ACLMessage message = car.receive();
		
		if(message != null){
			
			Debug.debugMessageReceived(car, message.getContent());
			
			MessageType type = MessagesResources.getMessageType(message.getContent());

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
						Debug.debugMessageSent(car, message.getContent());
					}
				}
			}
			else if(type.equals(MessagesResources.MessageType.PATH)){
				
				//Don't process the same information
				if(car.getJourney().size() == 0){
					
					String parts[] = message.getContent().split(MessagesResources.SEPARATOR);
					
					String destination = parts[1];
					
					//Answer to GET_PATH message
					if(car.getDestinationName().equals(destination)){
					
						ArrayList<String> route = new ArrayList<String>();
						boolean foundRoad = true;
						boolean allRoadsVisited = true;
						
						//Looks for the current road in the route and builds the jorney path from there
						for(int i = 2; i < parts.length; i++){
							
							if(!car.getCityKnowledge().getRoads().containsKey(parts[i]) ||
									car.getUnexploredRoads().containsKey(parts[i])){
								allRoadsVisited = false;
							}
							
							if(foundRoad)
								route.add(parts[i]);
							
							if(car.getRoad().getName().equals(parts[i]))
								foundRoad = true;
						}
						
						if((car.getLearningMode().equals(LearningMode.SHORT_LEARNING) ||
								!allRoadsVisited) && foundRoad){
							car.setJourney(route);
							car.jorneyConsume();
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
					Debug.debugMessageSent(car, message.getContent());
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
				}
			}
			else if(type.equals(MessagesResources.MessageType.BLOCKED)){
				String roadName = message.getContent().split(MessagesResources.SEPARATOR)[1];
				
				if(car.getCityKnowledge().getRoads().containsKey(roadName))
					car.getCityKnowledge().getRoads().get(roadName).blocked();
				
				if(car.getJourney().contains(roadName)){
					car.calculateAndUpdateJourney();
				}
			}
			else if(type.equals(MessagesResources.MessageType.UNBLOCKED)){
				String roadName = message.getContent().split(MessagesResources.SEPARATOR)[1];
				
				if(car.getCityKnowledge().getRoads().containsKey(roadName))
					car.getCityKnowledge().getRoads().get(roadName).unblocked();
				
				if(!car.getJourney().contains(roadName)){
					car.calculateAndUpdateJourney();
				}
			}
		}
	}
}
