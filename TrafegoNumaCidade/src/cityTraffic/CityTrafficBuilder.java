package cityTraffic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import resources.Debug;
import agents.Car;
import agents.Car.LearningMode;
import agents.City;
import agents.CarSerializable;
import agents.CarShortLearning;
import agents.CarMonitored;
import agents.Radio;
import agents.CarNoneLearning;
import agents.RoadMonitor;
import cityStructure.Road;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;
import resources.Point;
import resources.SpaceResources;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class CityTrafficBuilder extends RepastSLauncher {

	//params
	private static Point myOrigin;
	private static Point myDest;
	private static CarSerializable myKnowledge;
	private static LearningMode myMode;
	private static String path;
	private static Point spacePos;

	private static int nCars;
	private static int n = -1; 		//number of the car being produced
	private static int prob = 0; 	//probability to learn the city
	
	Grid<Object> space;

	public static final boolean USE_RESULTS_COLLECTOR = true;

	public static final boolean SEPARATE_CONTAINERS = false;
	private ContainerController mainContainer;
	private ContainerController agentContainer;

	private Schedule schedule = null;
	
	private static Point spaceDimensions = new Point(21,21);

	/**
	 * GEts an agent in the given context with the given aid.
	 * @param context
	 * @param aid
	 * @return
	 */
	public static Agent getAgent(Context<?> context, AID aid) {
		for (Object obj : context.getObjects(Agent.class)) {
			if (((Agent) obj).getAID().equals(aid)) {
				return (Agent) obj;
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return "Street map -- SAJaS RepastS Test";
	}

	@Override
	protected void launchJADE() {

		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);

		if (SEPARATE_CONTAINERS) {
			Profile p2 = new ProfileImpl();
			agentContainer = rt.createAgentContainer(p2);
		} else {
			agentContainer = mainContainer;
		}
		launchAgents();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Context build(Context<Object> context) {
		Parameters params = RunEnvironment.getInstance().getParameters();
		schedule = new Schedule();
		
		//1. origin monitored car
		int x = (Integer) params.getValue("originX");
		int y = (Integer) params.getValue("originY");
		myOrigin = new Point(x, y);
		//2. dest monitored car
		x = (Integer) params.getValue("destX");
		y = (Integer) params.getValue("destY");
		myDest = new Point(x, y);
		
		//3. agent learning mode
		String mode = (String) params.getValue("type");

		//4. agent filename
		String filename = (String) params.getValue("objPath");
		
		if(filename.equals("new")) 
		{
			createNewAgent(mode);
		}
		//load knowledge
		else {		
			try {
				String path = new File("").getAbsolutePath();
				path += "\\objs\\"+filename;
				File ser = new File(path);
				FileInputStream fileIn = new FileInputStream(ser);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				myKnowledge = (CarSerializable) in.readObject();
				in.close();
				fileIn.close();
				System.out.println("Data loaded from "+path+"\n");
				
				//Escolhi fazer A*
				if(mode.equals("A*")) {
					myMode = LearningMode.SHORT_LEARNING;
				}
				//escolhi QLearning
				else if(mode.equals("QL"))
				{
					myMode = LearningMode.LEARNING;
					
					//verifico se ja existe neste agente
					if(myKnowledge.getDestinationPoint() != null) 
					{
						//nao tenho qLearning para esta posicao -> executo A*
						if(!myKnowledge.getDestinationPoint().equals(myDest)) {
							myMode = LearningMode.SHORT_LEARNING;
						}
					}
					//nao existe -> vou aprender
					else 
					{
						myKnowledge.setDestinationPoint(myDest);
						filename = "qlearnig_"+x+"_"+y;
					}
				}
				//Applying
				else{
					myMode = LearningMode.APPLYING;
					
					//verifico se ja existe neste agente
					if(myKnowledge.getDestinationPoint() != null) 
					{
						//nao tenho qLearning para esta posicao -> executo A*
						if(!myKnowledge.getDestinationPoint().equals(myDest)) {
							myMode = LearningMode.SHORT_LEARNING;
						}
					}
					//nao existe -> vou aprender
					else 
					{
						myKnowledge.setDestinationPoint(myDest);
						filename = "qlearnig_"+x+"_"+y;
					}
				}
				
				myKnowledge.setFilename(filename);
				
				//indicate is an old version
				myKnowledge.setNewVersion(false);
				
			} catch (FileNotFoundException f) {
				System.out.println("Car file not found");
				createNewAgent(mode);
			} catch (IOException i) {
				i.printStackTrace();
			} catch (ClassNotFoundException c) {
				System.out.println("MonitoredCarAgent class not found");
			}
		}
		
		//5. hour
		calculateNumCars((int) params.getValue("hour"));
		
		//6. map path
		String type = (String) params.getValue("mapPath");
		path = new File("").getAbsolutePath();
		if(type.equals("Small")) {
			path += "\\maps\\small.txt";
			spaceDimensions = new Point(21,21);
			spacePos = new Point(10,10);
			int ntemp = (int) (nCars*0.5);
			nCars = ntemp;
		}
		else {
			path += "\\maps\\big.txt";
			spaceDimensions = new Point(62,62);
			spacePos = new Point(30,20);
			int ntemp = (int) (nCars*2);
			nCars = ntemp;
		}
		//System.out.println("Carros gerados final : "+nCars);
		
		//debug mode
		boolean a = (boolean) params.getValue("carMessages");
		boolean b = (boolean) params.getValue("qLearning");
		boolean c = (boolean) params.getValue("learningMode");
		boolean d = (boolean) params.getValue("roadMonitor");
		boolean e = (boolean) params.getValue("radio");
		boolean f = (boolean) params.getValue("journeyUpdate");
		boolean g = (boolean) params.getValue("discoveries");
		boolean h = (boolean) params.getValue("unvisitedJourney");
		Debug.setDebugMessages(a,b,c,d,e,f,g,h);
		
		//create path
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		space = gridFactory.createGrid(new String("street_map"), context, 
				new GridBuilderParameters<Object>(new StrictBorders(), new SimpleGridAdder<Object>(), true,spaceDimensions.x, spaceDimensions.y));
		
		return super.build(context);
	}

	
	/*
	 * OTHERS
	 */
	
	/**
	 * Method that launch all the needed agents.
	 */
	private void launchAgents() {

		// random probability of cars learning the city
		prob = (int) (Math.random() * 100);
		//System.out.println("Probability to learn the city = " + prob + " %");
		
		try {
			
			//CITY AGENT
			
			City city = new City(space,spaceDimensions,agentContainer,path);
			
			agentContainer.acceptNewAgent("city", city).start();
			space.getAdder().add(space, city);
			space.moveTo(city, spacePos.x,spacePos.y);
			
			//update map with new cars
			schedule.schedule(city);
			
			//RADIO AGENT
			
			Radio radio = new Radio();
			agentContainer.acceptNewAgent("radio", radio).start();

			//ROAD MONITOR AGENTS
			
			for (Road r : city.getMap().getRoads().values()) {
				
				if (r.getLength() > 3) 
				{
					RoadMonitor monitor = new RoadMonitor(r, space, radio);
					agentContainer.acceptNewAgent("road monitor-" + r.getName(), monitor).start();
					space.getAdder().add(space, monitor);
					space.moveTo(monitor, r.getEndPoint().toArray());
				}
			}

			//CAR AGENTS
			
			
			// create monitored car
			Road myStartRoad = city.getMap().isPartOfRoad(myOrigin);
			if (myStartRoad != null) 
			{
				//testes
				//myMode = LearningMode.NONE;
				//myKnowledge.setCityKnowledge(city.getMap());
				
				CarMonitored car = new CarMonitored(space, myOrigin, myStartRoad,myDest,myKnowledge,myMode);
				agentContainer.acceptNewAgent("MonitoredCarAgent", car).start();
				space.getAdder().add(space, car);
				car.setPosition(myOrigin);
				
				System.out.println(car.print());
			}
						
			// create cars in random positions
			for (int i = 0; i < nCars; i++) {
				createRandomCar(city);
			}

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create random cars.
	 * @param city
	 * @throws StaleProxyException
	 */
	public void createRandomCar(City city) throws StaleProxyException 
	{
		n++;	//inc car number
		
		int rnd_road;
		Road startRoad = null, endRoad = null;
		Point origin = null, destination = null;
		
		boolean position_ok = false;
		
		//Search Origin Random
		while(!position_ok){
			
			rnd_road = (int)(Math.random() * city.getMap().getRoads().size());
			startRoad = (Road) city.getMap().getRoads().values().toArray()[rnd_road];
			origin = city.getRandomRoadPosition(startRoad);
			
			position_ok = true;

			//check if there are no cars at the location
			for(int j = 0; j < n; j++){
				if(SpaceResources.hasCar(space, origin) != null)
					position_ok = false;
			}
		}
		
		//Search Destination Random
		rnd_road = (int)(Math.random() * city.getMap().getRoads().size());
		endRoad = (Road) city.getMap().getRoads().values().toArray()[rnd_road];
		destination = city.getRandomRoadPosition(endRoad);

		// Search Destination Random
		rnd_road = (int) (Math.random() * city.getMap().getRoads().size());
		endRoad = (Road) city.getMap().getRoads().values().toArray()[rnd_road];
		destination = city.getRandomRoadPosition(endRoad);

		// create car
		Car car = null;
		double randProb = Math.random() * 100;
		if (randProb <= prob) {
			// the car must learn
			CarSerializable know = new CarSerializable(spaceDimensions);
			car = new CarShortLearning(space, origin, startRoad, destination, know);
		} else {
			// the car has previous knowledge of the city
			CarSerializable know = new CarSerializable(spaceDimensions);
			know.setCityKnowledge(city.getMap());
			car = new CarNoneLearning(space, origin, startRoad, destination, know);
		}

		agentContainer.acceptNewAgent("CarRandom"+n, car).start();
		space.getAdder().add(space, car);
		car.setPosition(origin);
		//System.out.println(car.print() + "Prob : " + randProb+"\nNumber : "+n);
	}
	
	
	/**
	 * Creates new knowledge.
	 * @param mode
	 */
	private void createNewAgent(String mode) {
		
		myKnowledge = new CarSerializable(spaceDimensions);
		String temp = "";
		if(mode.equals("A*")) {
			myMode = LearningMode.SHORT_LEARNING;
			long timestamp =  (new Timestamp(System.currentTimeMillis())).getTime();
			temp = "astar"+timestamp+".ser";
		}
		else if(mode.equals("QL")){
			myMode = LearningMode.LEARNING;
			myKnowledge.setDestinationPoint(myDest);
			temp = "qlearnig_"+myDest.x+"_"+myDest.y+".ser";
		}
		else{
			myMode = LearningMode.APPLYING;
			myKnowledge.setDestinationPoint(myDest);
			temp = "qlearnig_"+myDest.x+"_"+myDest.y+".ser";
		}
		myKnowledge.setFilename(temp);
	}

	/**
	 * Calculates the number of cars given the hour of the day.
	 * @param hour
	 */
	private void calculateNumCars(int hour) 
	{
		if(hour < 0) hour = 0;
		if(hour > 24) hour = 0;
		
		//muito transito
		if(7 <= hour && hour <= 9 || 12 <= hour && hour <= 14 || 17 <= hour && hour <= 19) {
			nCars = (int) (Math.random() * 30 + 70); // 70 - 100 
		}
		//moderado
		else if(9 < hour && hour < 12 || 14 < hour && hour < 17 || 19 < hour && hour < 22) {
			nCars = (int) (Math.random() * 45 + 25); //25 - 70
		}
		//fraco
		else {
			nCars = (int) (Math.random() * 20 + 5); //5 - 25
		}
	}
	
}
