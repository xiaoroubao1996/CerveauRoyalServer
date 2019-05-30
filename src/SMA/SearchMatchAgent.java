package SMA;

import DAO.DAOFactory;
import Model.Constant;
import Model.JadeModel;
import Model.User;
import SMA.MatchAgent;
import Listener.ServletContextJade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchMatchAgent extends Agent {

    private User user1;
    private String subject;
    private ACLMessage ACLMessageFromEnv;

    protected void setup() {
        System.out.println(getLocalName()+ "--> Installed");
        DF.registerAgent(this, Constant.SEARCH_MATCH_NAME, getLocalName());
        user1 = null;
        subject = "";
        addBehaviour(new waitMsgBehaviour());
    }

    private class waitMsgBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = myAgent.receive(mt);

            if (message != null && message.getSender().getName().equals(Constant.ENVIRONEMENT_NAME)) {
                ACLMessageFromEnv = message;
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = null; // read Json
                String email = null;
                String subject = null;
                Boolean withFriend = null;
                String friendEmail = null;
                User user = null;
                try {
                    rootNode = mapper.readTree(message.getContent());
                    email = rootNode.path("email").asText();
                    subject = rootNode.path("subject").asText();
                    withFriend = rootNode.path("withFriend").asBoolean();
                    if(withFriend){
                        friendEmail = rootNode.path("subject").asText();
                    }
                    user = DAOFactory.getUserDAO().selectByEmail(email);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                addBehaviour(new doSearchingBehaviour());


                //GET match
                ArrayList<AID> matches = new ArrayList<AID>(DF.findAgents(myAgent, subject, String.valueOf(user1.getRank())));
               //if we find a match
                if (matches.size() > 0) {
                    addBehaviour(new getMatchBehaviour(matches.get(0)));
                //if we dont
                }else{
                    addBehaviour(new newMatchBehaviour());
                }
            }


        }
    }

    //send message other user to match to begin the game
    private class getMatchBehaviour extends OneShotBehaviour {
        private AID matchAID;

        public getMatchBehaviour(AID matchAID) {
            super();
            this.matchAID = matchAID;
        }

        @Override
        public void action() {
            ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
            message.addReceiver(matchAID);
            message.setContent(user1.toJSON());
            //add replyTo so the message to Match reply directly to Env (escape SearchMatchAgent)
            message.addReplyTo(new AID(Constant.ENVIRONEMENT_NAME,AID.ISLOCALNAME));
            message.setConversationId(ACLMessageFromEnv.getConversationId());
            send(message);
            //remove the agent
            DF.removeAgents(myAgent, matchAID);
            
        }
    }

//    private class doSearchingBehaviour extends OneShotBehaviour {
//        ACLMessage response;
//
//        @Override
//        public void action() {
//            ArrayList<AID> matches = new ArrayList<AID>(DF.findAgents(myAgent, subject, String.valueOf(user1.getRank())));
//
//             //if we find a match
//             if (matches.size() > 0) {
//                 response = new ACLMessage(ACLMessage.CONFIRM);
//                 response.addReceiver(new AID(Constant.ENVIRONEMENT_NAME,AID.ISLOCALNAME));
//                 response.setConversationId(ACLMessageFromEnv.getConversationId());
//                 Map<String, Object> map = new HashMap<String, Object>();
//                 map.put("matchId",matches.get(0).getName());
//
//
//                 response.setContent(matches.get(0).getName());
//                 DF.removeAgents(myAgent, matches.get(0));
//
//
//             //if we dont
//             } else {
//                 response = new ACLMessage(ACLMessage.REFUSE);
//                 response.addReceiver(new AID(Constant.ENVIRONEMENT_NAME,AID.ISLOCALNAME));
//                 response.setContent("No match found");
//             }
//             send(response);
//        }
//    }


//    private class getMatchBehaviour extends Behaviour {
//        private int step;
//        private AID matchAID;
//        private boolean stop = false;
//        public getMatchBehaviour(AID matchAID){
//            super();
//            this.matchAID = matchAID;
//        }
//        @Override
//        public void action() {
//            switch (step){
//                case 0:
//                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
//                    message.addReceiver(matchAID);
//                    message.setContent(user1.toJSON());
//                    message.setConversationId(ACLMessageFromEnv.getConversationId());
//                    send(message);
//                    step = 1;
//                    break;
//                case 1:
//                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
//                    ACLMessage messageFromMatch = myAgent.receive(mt);
//                    if (messageFromMatch != null) {
//                        ObjectMapper mapper = new ObjectMapper();
//                        String userJSON = null;
//                        try {
//                            JsonNode rootNode = mapper.readTree(messageFromMatch.getContent());
//                            userJSON = rootNode.path("user").asText();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        //create message to env
//                        ACLMessage response = new ACLMessage(ACLMessage.CONFIRM);
//                        response.addReceiver(new AID(Constant.ENVIRONEMENT_NAME,AID.ISLOCALNAME));
//                        response.setConversationId(ACLMessageFromEnv.getConversationId());
//                        Map<String, Object> map = new HashMap<String, Object>();
//                        map.put("matchId",matchAID.getName());
//                        map.put("user",userJSON);
//                        try {
//                            String jsonStr = mapper.writeValueAsString(map);
//                            response.setContent(jsonStr);
//                            send(response);
//                        } catch (JsonProcessingException e) {
//                            e.printStackTrace();
//                        }
//                        stop = true;
//                    }
//                    break;
//            }
//        }
//
//        @Override
//        public boolean done(){
//            return stop;
//        }
//    }

    //create a new match
    private class newMatchBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
           MatchAgent newMatch = new MatchAgent();
           try {
               Object[] list = new Object[1];
               Map<String, Object> params = new HashMap<>();
               params.put("subject", subject);
               params.put("user1", user1);
               params.put("MessageToReplyUser1",ACLMessageFromEnv);
               list[0] = params;
               JadeModel.getContainer().createNewAgent(Constant.MATCH_NAME + String.valueOf(System.currentTimeMillis()), "SMA.MatchAgent",list).start();
           } catch (StaleProxyException e) {
               e.printStackTrace();
           }
        }
    }

}
