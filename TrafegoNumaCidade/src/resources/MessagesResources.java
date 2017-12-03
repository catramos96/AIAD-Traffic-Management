package resources;

public class MessagesResources {

	public static final String SEPARATOR = "_#_";
	public static enum MessageType{
		CAR_IN_TRANSIT,				//When a car is stopped at a semaphore
		};
		
	public static MessageType getMessageType(String content){
		MessageType[] types = MessageType.values();
		
		for(int i = 0; i < types.length; i++){
			if(content.startsWith(types[i].toString()))
				return types[i];
		}
		
		return null;
	}
}
