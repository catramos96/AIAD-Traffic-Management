package behaviours;


import agents.CarAgent;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import repast.simphony.space.grid.Grid;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;

public class TestBehaviour extends TickerBehaviour {

	private Agent agent;
	protected ACLMessage inform;
	private Grid<Object> space;
	private Codec codec;
	private Ontology onto;
	
	public TestBehaviour(Agent a, long period, Grid<Object> space, Codec codec, Ontology serviceOntology) {
		super(a, period);
		// TODO Auto-generated constructor stub
		this.agent = a;
		this.space = space;
		this.codec = codec;
		this.onto = serviceOntology;
	}
	
	@Override
	protected void onTick() {
		// prepare inform message
        inform = new ACLMessage(ACLMessage.INFORM);
        inform.setLanguage(codec.getName());
        inform.setOntology(onto.getName());
        inform.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        inform.setContent("Ola amigo");
        
        AID r = null;
        for(Object o : space.getObjects()){
    		if(o.getClass().equals(CarAgent.class)){
    			r = ((CarAgent)o).getAID();
    			break;
    		}
    	}
        
        inform.addReceiver(r);
        agent.send(inform);
        
        System.out.println("Message sent!");
	}

}
