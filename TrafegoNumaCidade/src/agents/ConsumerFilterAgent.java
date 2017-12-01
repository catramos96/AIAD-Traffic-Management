package agents;

import cityTraffic.CityTrafficBuilder;
import jade.core.AID;

public class ConsumerFilterAgent extends ConsumerAgent {

	public ConsumerFilterAgent(int nContracts, AID resultsCollector) {
		super(CityTrafficBuilder.FILTER_SIZE, nContracts, resultsCollector);
	}

}
