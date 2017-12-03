package agents;

import behaviours.RadioService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;
import sajas.domain.DFService;

public class Radio extends Agent{
	
	public Radio(){
	}
	
	@Override
	public void setup(){        
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
	        
		this.addBehaviour(new RadioService(this));

	}
}
