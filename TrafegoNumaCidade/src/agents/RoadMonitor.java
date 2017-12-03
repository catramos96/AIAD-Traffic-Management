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

public class RoadMonitor extends Agent{

	private Road road = null;
	private Radio radio = null;
	private Grid<Object> space = null;
	private SLCodec codec;
	private Ontology ontology;
	
	public RoadMonitor(Road road, Grid<Object> space, Radio radio){
		this.road = road;
		this.space = space;
		this.radio = radio;
	}
	
	
	//JADE RELATED
	@Override
	public void setup(){

		codec = new SLCodec();
        ontology = ServiceOntology.getInstance();
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);
        
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
	        
		this.addBehaviour(new TransitMonitorization(this,1500,space));
	}
	
	/*
	 * Gets & Sets
	 */
	
	public Road getRoad(){
		return road;
	}
	
	public SLCodec getCodec(){
		return codec;
	}
	
	public Ontology getOntology(){
		return ontology;
	}
	
	public Radio getRadio(){
		return radio;
	}
}
