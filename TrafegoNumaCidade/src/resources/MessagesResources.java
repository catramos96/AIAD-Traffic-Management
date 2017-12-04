package resources;

import java.util.ArrayList;

public class MessagesResources {

	public static final int CommunicationRadius = 1;
	public static final String SEPARATOR = "_#_";
	
	public static enum MessageType{
		TRANSIT,				//When a road has reached their max capacity of cars allowed
		NO_TRANSIT,				//When a road has no longer transit
		BLOCKED,				//When the radio announces that a road is no longer viable
		UNBLOCKED,				//When the radio announces that a road is viable
		GET_PATH,				//When a car asks directions to others
		PATH					//When a car answers directions from others
		};
		
	public static String buildMessage(MessageType t, String roadName){		
		return t.toString() + SEPARATOR + roadName;
	}
	
	public static String buildMessageGetPath(String currentRoad, Point destination){
		return MessageType.GET_PATH + SEPARATOR + 
				currentRoad + SEPARATOR + 
				destination.print();
	}
	
	public static String buildMessagePath(ArrayList<String> path){
		String s = MessageType.PATH.toString();
		
		for(int i = 0; i < path.size(); i++){
			s+= SEPARATOR + path.get(i);
		}
		
		return s;
	}
	
	public static MessageType getMessageType(String content){
		MessageType[] types = MessageType.values();
		
		for(int i = 0; i < types.length; i++){
			if(content.startsWith(types[i].toString()))
				return types[i];
		}
		
		return null;
	}
}
