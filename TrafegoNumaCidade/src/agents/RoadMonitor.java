package agents;

import behaviours.TransitMonitorization;
import cityStructure.Road;
import cityTraffic.onto.ServiceOntology;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;
import sajas.domain.DFService;

/**
 * Agent that is responsible to track the transit in
 * the road that it's associated with and communicate any
 * change in the transit flow to the radio.
 *
 */
public class RoadMonitor extends Agent{

	private Road road = null;			//Road responsible to track the transit
	private Radio radio = null;			//Radio to communicate with
	private Grid<Object> space = null;	//Space where the car agents are
	
	/**
	 * Constructor.
	 * @param road
	 * @param space
	 * @param radio
	 */
	public RoadMonitor(Road road, Grid<Object> space, Radio radio){
		this.road = road;
		this.space = space;
		this.radio = radio;
	}
	
	
	//JADE RELATED
	@Override
	public void setup(){		
        
		DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("service-provider");
        sd.setName("MonitorService");
        template.addServices(sd);
        template.setName(this.getAID());
        try {
        	DFService.register(this, template);
        } catch(FIPAException ex) {
        	ex.printStackTrace();
        }
	        
        //Transit flow handler
		this.addBehaviour(new TransitMonitorization(this,1500,space));
	}
	
	/*
	 * Gets & Sets
	 */
	
	/**
	 * Gets the road that it is responsible with.
	 * @return
	 */
	public Road getRoad(){
		return road;
	}
	
	/**
	 * Gets the radio.
	 * @return
	 */
	public Radio getRadio(){
		return radio;
	}
}
