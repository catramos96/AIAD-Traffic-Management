package cityTraffic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import agents.City;
import agents.Knowledge;
import agents.MonitoredCarAgent;
import agents.Radio;
import agents.RandomCarAgent;
import agents.RoadMonitor;
import cityStructure.CityMap;
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

	private static int nCars;
	private static Point myOrigin;
	private static Point myDest;
	private static Knowledge myKnowledge;

	Grid<Object> space;

	public static final boolean USE_RESULTS_COLLECTOR = true;

	public static final boolean SEPARATE_CONTAINERS = false;
	private ContainerController mainContainer;
	private ContainerController agentContainer;

	private Schedule schedule;

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

	private void launchAgents() {

		schedule = new Schedule();

		try {
			String map_txt = new File("").getAbsolutePath();
			map_txt += "\\maps\\Map.txt";

			City city = new City(space, agentContainer, map_txt);
			agentContainer.acceptNewAgent("city", city).start();
			space.getAdder().add(space, city);
			space.moveTo(city, 10, 10);

			Radio radio = new Radio();
			agentContainer.acceptNewAgent("radio", radio).start();

			// create road monitors (transit)
			for (Road r : city.getMap().getRoads().values()) {
				if (r.getLength() > 3) {
					RoadMonitor monitor = new RoadMonitor(r, space, radio);
					agentContainer.acceptNewAgent("road monitor-" + r.getName(), monitor).start();
					space.getAdder().add(space, monitor);
					space.moveTo(monitor, r.getEndPoint().toArray());

					schedule.schedule(monitor);
				}
			}
			// random probability of cars learning the city
			double prob = Math.random() * 100;
			System.out.println("Probability to learn the city = " + prob + " %");

			// create cars in random positions
			for (int i = 0; i < nCars; i++) {
				int rnd_road;
				Road startRoad = null, endRoad = null;
				Point origin = null, destination = null;

				boolean position_ok = false;

				// Search Origin Random
				while (!position_ok) {

					rnd_road = (int) (Math.random() * city.getMap().getRoads().size());
					startRoad = (Road) city.getMap().getRoads().values().toArray()[rnd_road];
					origin = city.getRandomRoadPosition(startRoad);

					position_ok = true;

					// check if there are no cars at the location
					for (int j = 0; j < i; j++) {
						if (SpaceResources.hasCar(space, origin) != null)
							position_ok = false;
					}
				}

				// Search Destination Random
				rnd_road = (int) (Math.random() * city.getMap().getRoads().size());
				endRoad = (Road) city.getMap().getRoads().values().toArray()[rnd_road];
				destination = city.getRandomRoadPosition(endRoad);

				// create car
				RandomCarAgent car = null;
				double randProb = Math.random() * 100;
				if (randProb <= prob) {
					// the car must learn
					car = new RandomCarAgent(space, new CityMap(), origin, destination, startRoad, true);
				} else {
					// the car has previous knowledge of the city
					car = new RandomCarAgent(space, city.getMap(), origin, destination, startRoad, false);
				}

				agentContainer.acceptNewAgent("RandomCarAgent" + i, car).start();
				space.getAdder().add(space, car);
				car.setPosition(origin);

				System.out.println(i + "  " + car.print() + "\nProb : " + randProb);
			}

			// create monitored car
			Road myStartRoad = city.getMap().isPartOfRoad(myOrigin);
			if (myStartRoad != null) {
				MonitoredCarAgent car = new MonitoredCarAgent(space, myKnowledge.getCityKnowledge(), myOrigin, myDest, myStartRoad);

				agentContainer.acceptNewAgent("MonitoredCarAgent", car).start();
				space.getAdder().add(space, car);
				car.setPosition(myOrigin);

				System.out.println("Monitored Car : " + car.print());
			}

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Context build(Context<Object> context) {
		Parameters params = RunEnvironment.getInstance().getParameters();
		nCars = (Integer) params.getValue("carCount");
		int x = (Integer) params.getValue("originX");
		int y = (Integer) params.getValue("originY");
		myOrigin = new Point(x, y);
		x = (Integer) params.getValue("destX");
		y = (Integer) params.getValue("destY");
		myDest = new Point(x, y);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		space = gridFactory.createGrid(new String("street_map"), context,
				new GridBuilderParameters<Object>(new StrictBorders(), new SimpleGridAdder<Object>(), true, 21, 21));

		// load knowledge
		myKnowledge = new Knowledge();
		try {
			String file = new File("").getAbsolutePath();
			file += "\\objs\\car.ser";
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			myKnowledge = (Knowledge) in.readObject();
			in.close();
			fileIn.close();
			System.out.println("Data loaded from car.ser\n");
		} catch (FileNotFoundException f) {
			System.out.println("Car file not found");
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("MonitoredCarAgent class not found");
		}
		return super.build(context);
	}

}
