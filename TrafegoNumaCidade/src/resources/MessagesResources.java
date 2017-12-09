package resources;

import java.util.ArrayList;

/**
 * Auxiliary class that provides methods for construction and handling of messages.
 *
 */
public class MessagesResources {

	public static final int CommunicationRadius = 1;		//radius of cells that a car can communicate with others
	public static final String SEPARATOR = "_#_";		
	
	public static enum MessageType{
		TRANSIT,				//When a road has reached their max capacity of cars allowed
		NO_TRANSIT,				//When a road has no longer transit
		BLOCKED,				//When the radio announces that a road is no longer viable
		UNBLOCKED,				//When the radio announces that a road is viable
		GET_PATH,				//When a car asks directions to others
		PATH,					//When a car answers directions from others
		WHICH_ROAD,				//When a car doesn't know the name of the destination road
		PART_OF_ROAD			//When a car answers a WHICH_ROAD message
		};
		
	/**
	 * Method that returns a String with the built content of a message of
	 * type TRANSIT/NO_TRANSIT.
	 * @param t
	 * @param roadName
	 * @return
	 */
	public static String buildMessage(MessageType t, String roadName){		
		return t.toString() + SEPARATOR + roadName;
	}
	
	/**
	 * Method that returns a String with the built content of a message of
	 * type WHICH_ROAD.
	 * @param destination
	 * @return
	 */
	public static String buildMessageWhichRoad(Point destination){
		return MessageType.WHICH_ROAD + SEPARATOR + 
				destination.print();
	}
	
	/**
	 * Method that returns a String with the built content of a message of
	 * type PART_OF_ROAD.
	 * @param destination
	 * @param roadName
	 * @return
	 */
	public static String buildMessagePartOfRoad(Point destination,String roadName){
		return MessageType.PART_OF_ROAD + SEPARATOR +
				destination.print() + SEPARATOR +
				roadName;
				
	}
	
	/**
	 * Method that returns a String with the built content of a message of
	 * type GET_PATH.
	 * @param currentRoad
	 * @param destinationRoad
	 * @return
	 */
	public static String buildMessageGetPath(String currentRoad, String destinationRoad){
		return MessageType.GET_PATH + SEPARATOR + 
				currentRoad + SEPARATOR + 
				destinationRoad;
	}
	
	/**
	 * Method that returns a String with the built content of a message of
	 * type PATH.
	 * @param destinationRoad
	 * @param path
	 * @return
	 */
	public static String buildMessagePath(String destinationRoad, ArrayList<String> path){
		String s = MessageType.PATH.toString() + SEPARATOR + 
				destinationRoad;
		
		for(int i = 0; i < path.size(); i++){
			s+= SEPARATOR + path.get(i);
		}
		
		return s;
	}
	
	/**
	 * Method that gets the message type out of the content of a message.
	 * @param content
	 * @return
	 */
	public static MessageType getMessageType(String content){
		MessageType[] types = MessageType.values();
		
		for(int i = 0; i < types.length; i++){
			if(content.startsWith(types[i].toString()))
				return types[i];
		}
		
		return null;
	}
}
