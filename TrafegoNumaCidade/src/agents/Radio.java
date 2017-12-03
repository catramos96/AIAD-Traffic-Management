package agents;

import behaviours.RadioService;
import cityTraffic.onto.ServiceOntology;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;
import sajas.domain.DFService;
import sajas.wrapper.ContainerController;

public class Radio extends Agent{

	private SLCodec codec = null;
	private Ontology ontology = null;
	private Grid<Object> space = null;
	
	public Radio(Grid<Object> space){
		this.space = space;
	}
	
	@Override
	public void setup(){
		codec = new SLCodec();
        ontology = ServiceOntology.getInstance();
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);
        
		DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("service-provider");
        sd.setName("RadioService");
        template.addServices(sd);
        try {
        	DFService.register(this, template);
        } catch(FIPAException ex) {
        	ex.printStackTrace();
        }
	        
		this.addBehaviour(new RadioService(this,1000,space));

	}
}
