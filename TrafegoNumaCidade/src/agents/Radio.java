package agents;

import behaviours.RadioService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sajas.core.Agent;
import sajas.domain.DFService;

/**
 * Agent that is responsible to communicate the transit
 * flow to the cars in the same container.
 *
 */
public class Radio extends Agent{
	
	/**
	 * Constructor
	 */
	public Radio(){}
	
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
	        
        //Message Handler
		this.addBehaviour(new RadioService(this));
	}
}
