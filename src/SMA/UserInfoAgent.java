package SMA;

import DAO.DAOFactory;
import DAO.UserDAO;
import Model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserInfoAgent extends Agent {
    protected void setup() {
        System.out.println(getLocalName()+ "--> Installed");
        addBehaviour(new waitMsgBehaviour());
    }

    /**
     * do something when get the message
     *  REQUEST: get
     *  INFORM: put(update)
     *  PROPOSE: post(create)
     */
    private class waitMsgBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage message = receive();
            if (message != null) {
                //Initialisation
                ACLMessage reply = null;
                String content = null;
                User user = null;
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = new HashMap<String, Object>();
                switch(message.getPerformative()){
                    case ACLMessage.REQUEST:
                        System.out.println("user : get message from env");
                        content = message.getContent();
                        System.out.println(myAgent.getLocalName() + "--> getRequest ");

                        try {
                            JsonNode rootNode = mapper.readTree(content); // read Json
                            String email = rootNode.path("email").asText();
                            String password = rootNode.path("password").asText();
                            //get user by id
                            user = DAOFactory.getUserDAO().selectByEmail(email);
                            if(user.getPassword().equals(password)){
                                reply = message.createReply();
                                map.put("user",user.toJSON());
                                map.put("isLogin", true);
                                String jsonStr = mapper.writeValueAsString(map);
                                reply.setContent(jsonStr);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //create reply
//                        reply = message.createReply();
//                        reply.setContent(user.toJSON());
//                        reply.setContent(message.getContent());
                        send(reply);
                        break;

                    case ACLMessage.INFORM:
                        content = message.getContent();
                        System.out.println(myAgent.getLocalName() + "--> getInform ");
                        user = User.read(content);
                        DAOFactory.getUserDAO().update(user);

//                        //create reply
//                        reply = message.createReply();
//                        reply.setContent("wow");
//                        send(reply);
                        break;
                    case ACLMessage.PROPOSE:
                        content = message.getContent();
                        System.out.println(myAgent.getLocalName() + "--> getPropose ");

                        user = User.read(content);
                        DAOFactory.getUserDAO().add(user);
                        //create reply
                        reply = message.createReply();
                        reply.setContent("wow");
                        send(reply);
                        break;
                }
            } else
                block();
        }
    }
}
