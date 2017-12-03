package resources;

public class MessagesResources {

	public static final String SEPARATOR = "_#_";
	
	public static enum MessageType{
		TRANSIT,				//When a road has reached their max capacity of cars allowed
		NO_TRANSIT,				//When a road has no longer transit
		BLOCKED,				//When the radio announces that a road is no longer viable
		UNBLOCKED				//When the radio announces that a road is viable
		};
		
	public static String buildMessage(MessageType t, String roadName){		
		return t.toString() + SEPARATOR + roadName;
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
