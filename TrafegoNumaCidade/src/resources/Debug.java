package resources;

import agents.Car;
import agents.MonitoredCarAgent;
import agents.Radio;
import agents.RoadMonitor;
import algorithms.Quality;
import sajas.core.Agent;

public class Debug {

	public static boolean debugCarMessages = true;
	public static boolean debugQLearning = true;
	public static boolean debugLearningMode =true;
	public static boolean debugRoadMonitor = false;
	public static boolean debugRadio = false;
	public static boolean debugJourneyUpdate = true;
	
	public static void debugMessageReceived(Agent a, String content){
		if((a.getClass().equals(MonitoredCarAgent.class) && debugCarMessages) ||
				(a.getClass().equals(Radio.class) && debugRadio))
			System.out.println(a.getClass().getSimpleName() + " : MessageReceived : " + content);
	}
	
	public static void debugMessageSent(Agent a, String content){
		if((a.getClass().equals(MonitoredCarAgent.class) && debugCarMessages) ||
				(a.getClass().equals(RoadMonitor.class) && debugRoadMonitor) ||
				(a.getClass().equals(Radio.class) && debugRadio))
			System.out.println(a.getClass().getSimpleName() + " : MessageSent : " + content);
	}
	
	public static void debugJourney(Car c){
		
		if(c.getClass().equals(MonitoredCarAgent.class) && debugJourneyUpdate){
			String s = c.getClass().getSimpleName() + " : Journey : ";
			
			for(String r : c.getJourney())
				s += r + " ";

			System.out.println(s);
		}
	}

	public static void debugQLearning(Car c, Quality q){
		if(c.getClass().equals(MonitoredCarAgent.class) && debugQLearning){
			System.out.println(c.getClass().getSimpleName() + " : QLearning : " + q.print());
		}
	}
	
	public static void debugLearningMode(Car c){
		if(c.getClass().equals(MonitoredCarAgent.class) && debugQLearning){
			System.out.println(c.getClass().getSimpleName() + " : LearningMode : " + c.getLearningMode());
		}
	}
}
