package trafegoNumaCidade;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import sajas.core.Agent;
import jade.core.AID;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.WakerBehaviour;
import sajas.core.behaviours.WrapperBehaviour;
import sajas.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import sajas.proto.ContractNetInitiator;
import sajas.proto.SubscriptionInitiator;
import trafegoNumaCidade.onto.ContractOutcome;
import trafegoNumaCidade.onto.Results;
import trafegoNumaCidade.onto.ServiceOntology;
import trafegoNumaCidade.onto.ServiceProposal;
import trafegoNumaCidade.onto.ServiceProposalRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class CarAgent extends Agent {
	
private String requiredService = "bla";

	Grid<Object> spaceCont;

	int sign = 1;
    
    private int nContracts;
    private ArrayList<ContractOutcome> contractOutcomes = new ArrayList<ContractOutcome>();
    private AID resultsCollector;
    
    // providers and their values
    private int nBestProviders;
    private Map<AID,ProviderValue> providersTable = new HashMap<AID,ProviderValue>();
    private ArrayList<ProviderValue> providersList = new ArrayList<ProviderValue>();
    
    private Context<?> context;
    private Grid<Object> space;
    private RepastEdge<Object> edge = null;
    
    private Codec codec;
    private Ontology serviceOntology;
    
    protected ACLMessage myCfp;
    
    private class ProviderValue implements Comparable<ProviderValue> {
		private final double INITIAL_VALUE = 0.5;

		private AID provider;
		private int nSuccess = 0;
		private int nFailure = 0;
		private double value;
		
		ProviderValue(AID provider) {
			this.provider = provider;
			this.value = INITIAL_VALUE;
		}
		
		public AID getProvider() {
			return provider;
		}
		
		public void addOutcome(ContractOutcome.Value outcome) {
			switch(outcome) {
			case SUCCESS:
				nSuccess++;
				break;
			case FAILURE:
				nFailure++;
				break;
			}
			value = (double) nSuccess/(nSuccess+nFailure);
		}
		
		public double getValue() {
			return value;
		}
		
		@Override
		public int compareTo(ProviderValue o) {
			// descending order
			if(o.value < value) {
				return -1;
			} else if(o.value > value) {
				return 1;
			} else {
				return provider.compareTo(o.getProvider());
			}
		}
		
	}
    
    private class DFSubscInit extends SubscriptionInitiator {
		
		private static final long serialVersionUID = 1L;

		DFSubscInit(Agent agent, DFAgentDescription dfad) {
			super(agent, DFService.createSubscriptionMessage(agent, getDefaultDF(), dfad, null));
		}
		
		protected void handleInform(ACLMessage inform) {
			try {
				DFAgentDescription[] dfads = DFService.decodeNotification(inform.getContent());
				for(int i = 0; i < dfads.length; i++) {
					AID agent = dfads[i].getName();
					((ConsumerAgent) myAgent).addProvider(agent);
				}
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}

		}
		
	}
    
    private class StartCNets extends WakerBehaviour {

		private static final long serialVersionUID = 1L;

		public StartCNets(Agent a, long timeout) {
			super(a, timeout);
		}
		
		@Override
		public void onWake() {
			// context and network (RepastS)
			context = ContextUtils.getContext(myAgent);
			space = (Grid<Object>) context.getProjection("Street map");
			
			// initiate CNet protocol
			CNetInit cNetInit = new CNetInit(myAgent, (ACLMessage) myCfp.clone());
			addBehaviour(new CNetInitWrapper(cNetInit));
		}

	}
    
    private class CNetInitWrapper extends WrapperBehaviour {

		private static final long serialVersionUID = 1L;

		public CNetInitWrapper(Behaviour wrapped) {
			super(wrapped);
		}
		
		public int onEnd() {
			if(--nContracts > 0) {
				// initiate new CNet protocol
				CNetInit cNetInit = new CNetInit(myAgent, (ACLMessage) myCfp.clone());
				addBehaviour(new CNetInitWrapper(cNetInit));
				return 1;
			} else {
				if(resultsCollector != null) {
					ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
					inform.addReceiver(resultsCollector);
					inform.setLanguage(codec.getName());
					inform.setOntology(serviceOntology.getName());
					//
					Results results = new Results(nBestProviders, contractOutcomes);
					try {
						getContentManager().fillContent(inform, results);
					} catch (CodecException | OntologyException e) {
						e.printStackTrace();
					}
					myAgent.send(inform);
				}
				return 0;
			}
		}
		
	}
	
	
	private class CNetInit extends ContractNetInitiator {

		private static final long serialVersionUID = 1L;

		public CNetInit(Agent owner, ACLMessage cfp) {
			super(owner, cfp);
		}

		@Override
		public Vector prepareCfps(ACLMessage cfp) {
			// select best providers
			ArrayList<AID> bestProviders = ((CarAgent) myAgent).getBestProviders();
			for(AID provider : bestProviders) {
				cfp.addReceiver(provider);
			}
			
			return super.prepareCfps(cfp);
		}
	}
	
	public CarAgent(Grid<Object> space) 
	{
		spaceCont = space;
	}
	
	@ScheduledMethod(start=1 , interval=20000)
	public void updateCarsPosition()
	{
		try
		{
			GridPoint pos = spaceCont.getLocation(this);
			spaceCont.moveTo(this, pos.toIntArray(null));
		}
		catch(Exception e)
		{
			sign = -sign;
		}
	}
	
protected ArrayList<AID> getBestProviders() {
		
		ArrayList<AID> bestProviders = new ArrayList<AID>();
		
		Collections.sort(providersList);
		for(int i = 0; i < nBestProviders && i < providersList.size(); i++) {
			bestProviders.add(providersList.get(i).getProvider());
		}
		
		return bestProviders;
	}
	
	@Override
    public void setup() {

        // register language and ontology
        codec = new SLCodec();
        serviceOntology = ServiceOntology.getInstance();
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(serviceOntology);
        
        // subscribe DF
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("service-provider");
        template.addServices(sd);
        addBehaviour(new DFSubscInit(this, template));

        // prepare cfp message
        myCfp = new ACLMessage(ACLMessage.CFP);
        myCfp.setLanguage(codec.getName());
        myCfp.setOntology(serviceOntology.getName());
        myCfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        //
        ServiceProposalRequest serviceProposalRequest = new ServiceProposalRequest(requiredService);
        try {
            getContentManager().fillContent(myCfp, serviceProposalRequest);
        } catch (CodecException | OntologyException e) {
            e.printStackTrace();
        }
        
        // waker behaviour for starting CNets
        addBehaviour(new StartCNets(this, 2000));
    }
	
}
