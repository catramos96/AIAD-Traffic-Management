package resources;

import agents.Car;
import agents.CarMonitored;
import agents.Radio;
import agents.RoadMonitor;
import algorithms.Quality;
import sajas.core.Agent;

/**
 * Auxiliary Class that provides methods for the debug.
 *
 */
public class Debug {

	public static boolean debugCarMessages = true; 	//a
	public static boolean debugQLearning = true;	//b
	public static boolean debugLearningMode =true;	//c
	public static boolean debugRoadMonitor = false;	//d
	public static boolean debugRadio = false;		//e
	public static boolean debugJourneyUpdate = true;//f
	public static boolean debugDiscoveries = false;	//g
	public static boolean debugUnvisitedJourney = true;	//h
	
	/**
	 * Set the debug modes received from the parameters
	 */
	public static void setDebugMessages(boolean carMessages, boolean qlearning, boolean learningMode, boolean roadMonitor, boolean radio, boolean journeyUpdate, boolean discoveries, boolean unvisitedJourney) {
		debugCarMessages = carMessages;
		debugQLearning = qlearning;
		debugLearningMode = learningMode;
		debugRoadMonitor = roadMonitor;
		debugRadio = radio;
		debugJourneyUpdate = journeyUpdate;
		debugDiscoveries = discoveries;
		debugUnvisitedJourney = unvisitedJourney;
	}
	
	/**
	 * Debug method for agent a when receiving a message with a certain content.
	 * @param a
	 * @param content
	 */
	public static void debugMessageReceived(Agent a, String content){
		if((a.getClass().equals(CarMonitored.class) && debugCarMessages) ||
				(a.getClass().equals(Radio.class) && debugRadio))
			System.out.println(a.getClass().getSimpleName() + " : MessageReceived : " + content);
	}
	
	/**
	 * Debug method for agent a when sending a message with a certain content.
	 * @param a
	 * @param content
	 */
	public static void debugMessageSent(Agent a, String content){
		if((a.getClass().equals(CarMonitored.class) && debugCarMessages) ||
				(a.getClass().equals(RoadMonitor.class) && debugRoadMonitor) ||
				(a.getClass().equals(Radio.class) && debugRadio))
			System.out.println(a.getClass().getSimpleName() + " : MessageSent : " + content);
	}
	
	/**
	 * Debug method for a car to show its current journey.
	 * @param c
	 */
	public static void debugJourney(Car c){
		
		if(c.getClass().equals(CarMonitored.class) && debugJourneyUpdate){
			String s = c.getClass().getSimpleName() + " : Journey : ";
			
			for(String r : c.getJourney())
				s += r + " ";

			System.out.println(s);
		}
	}

	/**
	 * Debug method for a car to show a certain Quality value (QLearning algorithm)
	 * @param c
	 * @param q
	 */
	public static void debugQLearning(Car c, Quality q){
		if(c.getClass().equals(CarMonitored.class) && debugQLearning){
			System.out.println(c.getClass().getSimpleName() + " : QLearning : " + q.print());
		}
	}
	
	/**
	 * Debug method for a car to show its learning mode.
	 * @param c
	 */
	public static void debugLearningMode(Car c){
		if(c.getClass().equals(CarMonitored.class) && debugQLearning){
			System.out.println(c.getClass().getSimpleName() + " : LearningMode : " + c.getLearningMode());
		}
	}
	
	/**
	 * Debug method for a car to show its new discovery.
	 * @param c
	 * @param name
	 * @param isRoad
	 */
	public static void debugDiscovery(Car c, String name, boolean isRoad){
		if(c.getClass().equals(CarMonitored.class) && debugDiscoveries){
			String disc = "New Road";
			
			if(!isRoad)
				disc = "New Intersection";
			
			System.out.println(c.getClass().getSimpleName() + " : " + disc + " : " + name);
		}
	}
	
	public static void debugUnvisitedJourney(Car c, String goal, String by){
		if(c.getClass().equals(CarMonitored.class) && debugUnvisitedJourney){			
			System.out.println(c.getClass().getSimpleName() + " : Journey to Unvisited : " + goal + " by " + by);
		}
	}
}
