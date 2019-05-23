package SMA;

import Model.Constant;
import Model.JadeModel;
import Model.User;
import SMA.MatchAgent;
import Listener.ServletContextJade;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.List;

public class SearchMatchAgent extends Agent {

    private User user1;
    private String subject;

    protected void setup() {
        System.out.println(getLocalName()+ "--> Installed");
        DF.registerAgent(this, Constant.SEARCH_MATCH_NAME, getLocalName());
        user1 = null;
        subject = "";
    }

    private class waitMsgBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = myAgent.receive(mt);

            if (message != null && message.getSender().equals("Environment")) {
                ObjectMapper mapper = new ObjectMapper();
                String [] params = message.getContent().split("&");
                User user = User.read(params[0]);
                String subject = params[1];
            }


        }
    }

    private class doSearchingBehaviour extends OneShotBehaviour {
        ACLMessage response;

        @Override
        public void action() {
             List matches = new ArrayList<AID>(DF.findAgents(myAgent, subject, user1.getRank()));
             if (matches.size() > 0) {
                 response = new ACLMessage(ACLMessage.CONFIRM);
                 response.addReceiver(new AID("Environment",AID.ISLOCALNAME));
                 response.setContent(matches.get(0).toString());
                 DF.removeAgents(myAgent, (AID)matches.get(0));
             } else {
                 response = new ACLMessage(ACLMessage.REFUSE);
                 response.addReceiver(new AID("Environment",AID.ISLOCALNAME));
                 response.setContent("No match found");
             }
             send(response);
        }
    }

    private class newMatchBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
           MatchAgent newMatch = new MatchAgent();
           try {
               List<Object> params = new ArrayList<Object>();
               params.add(subject);
               params.add(user1);
               JadeModel.getContainer().createNewAgent("matchxxx", "SMA.MatchAgent",params.toArray()).start();
           } catch (StaleProxyException e) {
               e.printStackTrace();
           }
        }
    }

}
