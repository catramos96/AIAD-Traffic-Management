package trafegoNumaCidade;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class TrafegoCidadeBuilder extends RepastSLauncher {

	private static int N_CARS = 100;
	
	private static int N = 10;
	private static int N_CONSUMERS = N;
	private static int N_CONSUMERS_FILTERING_PROVIDERS = N;
	
	Grid<Object> space;
	
	ArrayList<CarAgent> cars = new ArrayList<CarAgent>();
	
	
	protected static int FILTER_SIZE = 5; 
	
	
	private int N_CONTRACTS = 100;
	
	public static final boolean USE_RESULTS_COLLECTOR = true;
	
	public static final boolean SEPARATE_CONTAINERS = false;
	private ContainerController mainContainer;
	private ContainerController agentContainer;

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
		
		Schedule schedule = new Schedule();
		
		try {
			Map map = new Map();
			agentContainer.acceptNewAgent("map", map).start();
			space.getAdder().add(space, map);
			space.moveTo(map, 25, 25);
			// create cars
			for (int i = 0; i < N_CARS; i++) {
				CarAgent car = new CarAgent(space);
				cars.add(car);
				agentContainer.acceptNewAgent("CarAgent" + i, car).start();
				space.getAdder().add(space, car);
				
				int[] location = {(int) (Math.random()*50), (int) (Math.random()*50)};
				space.moveTo(car,location);
				schedule.schedule(car);
				
			}

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateCarsPosition()
	{
		for(CarAgent car : cars)
		{
			GridPoint pos = space.getLocation(car);
			
			System.out.println("Before: x:" + pos.getX() + " y:" + pos.getY());
			space.moveTo(car, pos.toIntArray(null));
			pos = space.getLocation(car);
			System.out.println("After: x:" + pos.getX() + " y:" + pos.getY());
		}
	}
	
	@Override
	public Context build(Context<Object> context) {		
		Parameters params = RunEnvironment.getInstance().getParameters();
		int carCount = (Integer) params.getValue("carCount");
		System.out.println(carCount);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		space = gridFactory.createGrid(new String("street_map"), context, 
				new GridBuilderParameters<Object>(new StrictBorders(), new SimpleGridAdder<Object>(), true,50, 50));

		return super.build(context);
	}

}
