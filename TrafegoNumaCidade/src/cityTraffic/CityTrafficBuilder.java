package cityTraffic;
import agents.CarAgent;
import agents.City;
import agents.Radio;
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

	private static int N_CARS = 20;
	Grid<Object> space;
	
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
			//TMP
			String map_txt = "C:\\Users\\Catarina\\Documents\\Académico\\4º Ano\\AIAD\\AIAD-Traffic-Management\\Map.txt";
			
			City city = new City(space,agentContainer,map_txt);
			agentContainer.acceptNewAgent("city", city).start();
			space.getAdder().add(space, city);
			space.moveTo(city, 10, 10);

			
			Radio radio = new Radio();
			agentContainer.acceptNewAgent("radio", radio).start();
			
			//create road monitors (transit)
			for(Road r : city.getMap().getRoads().values()){
				RoadMonitor monitor = new RoadMonitor(r,space, radio);
				agentContainer.acceptNewAgent("road monitor-" + r.getName(), monitor).start();
				space.getAdder().add(space, monitor);
				space.moveTo(monitor, r.getEndPoint().toArray());
				
				schedule.schedule(monitor);
			}
			// create cars
			for (int i = 0; i < N_CARS; i++) {
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
					for(int j = 0; j < i; j++){
						if(SpaceResources.hasCar(space, origin) != null)
							position_ok = false;
					}
				}
				
				//Search Destination Random
				rnd_road = (int)(Math.random() * city.getMap().getRoads().size());
				endRoad = (Road) city.getMap().getRoads().values().toArray()[rnd_road];
				destination = city.getRandomRoadPosition(endRoad);

				//create car
				CarAgent car = null;
				
				//Don't know the city
				/*if(i < 16)
					car = new CarAgent(space,new CityMap(),origin,destination,startRoad,true);
				//Knows the city
				else*/
					car = new CarAgent(space,new CityMap(), origin,destination,startRoad,true);		

					
				agentContainer.acceptNewAgent("CarAgent" + i, car).start();
				space.getAdder().add(space, car);
				car.setPosition(origin);
				schedule.schedule(car);
				
				System.out.println(car.print());
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
