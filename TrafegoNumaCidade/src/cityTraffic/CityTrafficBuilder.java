package cityTraffic;

import java.util.ArrayList;

import agents.CarAgent;
import agents.City;
import algorithms.AStar;
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

	private static int N_CARS = 20;
	
	private static int N = 10;
	private static int N_CONSUMERS = N;
	private static int N_CONSUMERS_FILTERING_PROVIDERS = N;
	
	Grid<Object> space;
	
	ArrayList<CarAgent> cars = new ArrayList<CarAgent>();
	
	public static int FILTER_SIZE = 5; 
	
	
	private int N_CONTRACTS = 100;
	
	public static final boolean USE_RESULTS_COLLECTOR = true;
	
	public static final boolean SEPARATE_CONTAINERS = false;
	private ContainerController mainContainer;
	private ContainerController agentContainer;
	
	private Schedule schedule;

	public static Agent getAgent(Context<?> context, AID aid) {
		for(Object obj : context.getObjects(Agent.class)) {
			if(((Agent) obj).getAID().equals(aid)) {
				return (Agent) obj;
			}
		}
		return null;
	}

	public int getN() {
		return N;
	}

	public void setN(int N) {
		this.N = N;
	}

	public int getFILTER_SIZE() {
		return FILTER_SIZE;
	}

	public void setFILTER_SIZE(int FILTER_SIZE) {
		this.FILTER_SIZE = FILTER_SIZE;
	}

	public int getN_CONTRACTS() {
		return N_CONTRACTS;
	}

	public void setN_CONTRACTS(int N_CONTRACTS) {
		this.N_CONTRACTS = N_CONTRACTS;
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
		
		if(SEPARATE_CONTAINERS) {
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
			City city = new City(space,agentContainer);
			agentContainer.acceptNewAgent("city", city).start();
			space.getAdder().add(space, city);
			space.moveTo(city, 10, 10);
			
			//To test the algorithm of the shortest path
			/*Road test_road = null;
			for(Road r : city.getMap().getRoads()){
				if(r.partOfRoad(new Point(4,20)))
					test_road = r;
			}
			
			ArrayList<Road> path = AStar.shortestPath(city.getMap(), test_road, new Point(4,20), new Point(19,14));

			for(Road r : path){
				System.out.println(r.getName());
			}*/
			
			// create cars
			for (int i = 0; i < N_CARS; i++) {
				CarAgent car = new CarAgent(space);
				cars.add(car);
				agentContainer.acceptNewAgent("CarAgent" + i, car).start();
				space.getAdder().add(space, car);
				
				boolean position_ok = false;
				Point location = null;
				Road r = null;
				
				while(!position_ok){
					//RANDOM Location -> ALTERAR ?
					
					int rnd_road = (int)(Math.random() * city.getMap().getRoads().size());
					r = city.getMap().getRoads().get(rnd_road);
					location = city.getRandomRoadPosition(r);
					
					position_ok = true;

					//check if there are no cars at the location
					for(int j = 0; j < i; j++){
						if(SpaceResources.hasCar(space, location))
							position_ok = false;
					}
				}

				car.setRoad(r);
				space.moveTo(car,location.toArray());
				schedule.schedule(car);
				
			}
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public Context build(Context<Object> context) {		
		Parameters params = RunEnvironment.getInstance().getParameters();
		int carCount = (Integer) params.getValue("carCount");
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		space = gridFactory.createGrid(new String("street_map"), context, 
				new GridBuilderParameters<Object>(new StrictBorders(), new SimpleGridAdder<Object>(), true,21, 21));

		return super.build(context);
	}

}
