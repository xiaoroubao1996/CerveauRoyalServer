package SMA;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DF {
	public static void registerAgent(Agent agent, String type, String name) {
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(agent.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		dafd.addServices(sd);
		try {
			DFService.register(agent, dafd);
		} catch (FIPAException e) {
			System.err.println("Unable to register agent " + name);
			e.printStackTrace();
		}
	}
	
	public static List<AID> findAgents(Agent agent, String type, String name) {
		List<AID> rec = new ArrayList<>();
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		template.addServices(sd);
		DFAgentDescription[] result;
		try {
			result = DFService.search(agent, template);
			for(DFAgentDescription desc : result) {
				rec.add(desc.getName());
			}
		} catch (FIPAException e) {
			System.err.println("Unable to find agent matching template " + name);
			e.printStackTrace();
		}
		return rec;
	}
}
