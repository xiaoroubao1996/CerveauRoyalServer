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
	public static void registerAgent(Agent agent, String nameSubject, String nameRank, String nameUserId) {
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(agent.getAID());
		
		//type as key && name as value
		ServiceDescription sdSubject = new ServiceDescription();
		sdSubject.setType("subject");
		sdSubject.setName(nameSubject);
		dafd.addServices(sdSubject);

		ServiceDescription sdRank = new ServiceDescription();
		sdRank.setType("rank");
		sdRank.setName(nameRank);
		dafd.addServices(sdRank);
		
		ServiceDescription sdUserId = new ServiceDescription();
		sdUserId.setType("userId");
		sdUserId.setName(nameUserId);
		dafd.addServices(sdUserId);
		
		try {
			DFService.register(agent, dafd);
		} catch (FIPAException e) {
			System.err.println("Unable to register agent " + agent.getAID());
			e.printStackTrace();
		}
	}
	
	public static List<AID> findAgents(Agent agent, String nameSubject, String nameRank, String nameUserId) {
		List<AID> rec = new ArrayList<>();
		DFAgentDescription template = new DFAgentDescription();
		
		ServiceDescription sdSubject = new ServiceDescription();
		sdSubject.setType("subject");
		sdSubject.setName(nameSubject);
		template.addServices(sdSubject);
		
		ServiceDescription sdRank = new ServiceDescription();
		sdRank.setType("rank");
		sdRank.setName(nameRank);
		template.addServices(sdRank);
		

		//we don't need a service name for searches, just the type
		ServiceDescription sdUserId = new ServiceDescription();
		sdUserId.setType("userId");
		if(nameUserId != null) {
			sdUserId.setName(nameUserId);
		}
		template.addServices(sdUserId);
		
		DFAgentDescription[] result;
		try {
			result = DFService.search(agent, template);
			for(DFAgentDescription desc : result) {
				rec.add(desc.getName());
			}
		} catch (FIPAException e) {
			System.err.println("Unable to find agent matching template: " + nameSubject + ";" + nameRank + ";" + nameUserId);
			e.printStackTrace();
		}
		return rec;
	}

//	public static void removeAgents(Agent agent, AID name) {
//		try {
//			DFService.deregister(agent, name);
//			System.out.println("remove success");
//		} catch (FIPAException e) {
//			System.err.println("Unable to remove agent " + name);
//			e.printStackTrace();
//		}
//	}

	public static void removeAgents(Agent agent) {
		try {
			DFService.deregister(agent);
			System.out.println("remove success");
		} catch (FIPAException e) {
			System.err.println("Unable to remove agent ");
			e.printStackTrace();
		}
	}
}
