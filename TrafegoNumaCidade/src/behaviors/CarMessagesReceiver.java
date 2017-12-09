package behaviors;

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

/**
 * Behavior that handles the messages received by a car.
 *
 */
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
			
			switch(type){
				case GET_PATH:{
					handleGetPath(message);
					break;
				}
				case PATH:{
					handlePath(message);
					break;
				}
				case WHICH_ROAD:{
					handleWhichRoad(message);
					break;
				}
				case PART_OF_ROAD:{
					handlePartOfRoad(message);
					break;
				}
				case BLOCKED:{
					handleBlocked(message);
					break;
				}
				case UNBLOCKED:{
					handleUnblocked(message);
					break;
				}
				default:{
					break;
				}
			}
		}
	}

	/**
	 * Method that handles the GET_PATH message.
	 * If the car knows the destination road than it will
	 * calculate the shortest path and send a PATH message
	 * in return.
	 * @param message
	 */
	private void handleGetPath(ACLMessage message){
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

	/**
	 * Method that handles the PATH message.
	 * It the car is in the SHORT_LEARN learning mode then
	 * it will update is journey. If it's in the LEARNING
	 * mode then it will update is journey just if the route
	 * has some unknown road.
	 * Also, the journey would be updated if it contains the
	 * current road that the car is in.
	 * @param message
	 */
	private void handlePath(ACLMessage message){
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
				
				//Just updates if the path contains the road that the car is in. If is
				//the LEARNING mode, it will update if the path has some unknown road.
				if((car.getLearningMode().equals(LearningMode.SHORT_LEARNING) ||
						!allRoadsVisited) && foundRoad){
					car.setJourney(route);
					car.jorneyConsume();
				}
			}
		}
	}

	/**
	 * Method that handles the WHICH_ROAD message.
	 * If the car knows the road that has the point then
	 * it will send a response message of type PART_OF_ROAD.
	 * @param message
	 */
	private void handleWhichRoad(ACLMessage message){
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

	/**
	 * Method that handles the PART_OF_ROAD message.
	 * The car updates the destination road name if the
	 * destination point in the message matches its 
	 * destination point.
	 * @param message
	 */
	private void handlePartOfRoad(ACLMessage message){
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

	/**
	 * Method that handles the BLOCKED message.
	 * Updates the state of the road has blocked and tries to
	 * calculate a new journey if the current journey has the
	 * blocked road.
	 * @param message
	 */
	private void handleBlocked(ACLMessage message){
		String roadName = message.getContent().split(MessagesResources.SEPARATOR)[1];
		
		if(car.getCityKnowledge().getRoads().containsKey(roadName))
			car.getCityKnowledge().getRoads().get(roadName).blocked();
		
		if(car.getJourney().contains(roadName)){
			car.calculateAndUpdateJourney();
		}
	}

	/**
	 * Method that handles the UNBLOCKED message.
	 * Updates the state of the road has unblocked and tries to
	 * calculate a new journey if the current journey hasn't the
	 * unblocked road.
	 * @param message
	 */
	private void handleUnblocked(ACLMessage message){
		String roadName = message.getContent().split(MessagesResources.SEPARATOR)[1];
		
		if(car.getCityKnowledge().getRoads().containsKey(roadName))
			car.getCityKnowledge().getRoads().get(roadName).unblocked();
		
		if(!car.getJourney().contains(roadName)){
			car.calculateAndUpdateJourney();
		}
	}
}
