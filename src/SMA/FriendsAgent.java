package SMA;

import DAO.DAOFactory;
import Model.Constant;
import Model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.google.android.gcm.server.*;
import com.google.android.gcm.server.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FriendsAgent extends Agent{

    private User friend;
    private AID MatchID;
    private String subject;
    private String deviceToken;
    public static final String API_GCM = "AIzaSyCAtOoMnNAj2uzqwebB_zrcxY6KciMwdbo";

    protected void setup() {
        System.out.println(getLocalName()+ "--> Installed");
        DF.registerAgent(this, Constant.FRIEND_NAME, getLocalName());

        friend = null;
        deviceToken = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue((String) getArguments()[0], Map.class);
            Integer idFriend = (Integer) map.get("user_id");
            friend = DAOFactory.getUserDAO().selectByID(idFriend);
            MatchID = new AID((String) map.get("match_id"), AID.ISLOCALNAME);
            subject = (String) map.get("subject");

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (friend != null) addBehaviour(new sendInvitationBehaviour());
        addBehaviour(new getReplyBehaviour());
    }

    private class sendInvitationBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            Map<String, Object> map = new HashMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();
            map.put("agentName", getLocalName());
            map.put("user",friend.toJSON());
            try {
                String jsonStr = mapper.writeValueAsString(map);
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(new AID(Constant.JADEGATEWAY_NAME, AID.ISLOCALNAME));
                request.setContent(jsonStr);
                sendMessageTest();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class getReplyBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = myAgent.receive(mt);

            if (message != null) {
                if (message.getSender().getName().equals(Constant.JADEGATEWAY_NAME)) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        Map<String, Object> map = mapper.readValue(message.getContent(), Map.class);
                        boolean s = (Boolean) map.get("success");
                        ACLMessage result = new ACLMessage(ACLMessage.INFORM);
                        result.addReceiver(MatchID);
                        Map<String, Object> map2 = new HashMap<String, Object>();
                        if (s) {
                            map2.put("success", true);
                        } else map2.put("success", false);
                        String jsonStr = mapper.writeValueAsString(map);
                        result.setContent(jsonStr);
                        send(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void sendMessageTest() throws IOException {
        Sender sender = new Sender(API_GCM);
        Message message = new Message.Builder()
                .addData("Message", "Testing")
                .build();
        Result result = sender.send(message, deviceToken, 5);
    }

}