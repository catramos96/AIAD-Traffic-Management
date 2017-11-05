package trafegoNumaCidade;

import jade.core.AID;

public class ConsumerFilterAgent extends ConsumerAgent {

	public ConsumerFilterAgent(int nContracts, AID resultsCollector) {
		super(TrafegoCidadeBuilder.FILTER_SIZE, nContracts, resultsCollector);
	}

}
