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
import java.sql.SQLException;
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
     *  SUBSCRIBE: Login
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
                    case ACLMessage.SUBSCRIBE:
                        content = message.getContent();
                        try {
                            JsonNode rootNode = mapper.readTree(content); // read Json
                            String email = rootNode.path("email").asText();
                            String password = rootNode.path("password").asText();
                            String deviceToken = rootNode.path("deviceToken").asText();
                            //get user by email
                            user = DAOFactory.getUserDAO().selectByEmail(email);
                            reply = message.createReply();
                            if(user != null && user.getPassword().equals(password)){
                                map.put("success", true);
                                String jsonStr = mapper.writeValueAsString(map);
                                reply.setContent(jsonStr);

                                //update user's token
                                user.setDeviceToken(deviceToken);
                                DAOFactory.getUserDAO().update(user);
                            }else{
                                map.put("success", false);
                                String jsonStr = mapper.writeValueAsString(map);
                                reply.setContent(jsonStr);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            reply = message.createReply();
                            String jsonStr = "{\"success\" : false }";
                            reply.setContent(jsonStr);
                        }
                        send(reply);
                        break;

                    case ACLMessage.REQUEST:
                        content = message.getContent();
                        try {
                            JsonNode rootNode = mapper.readTree(content); // read Json
                            String email = rootNode.path("email").asText();

                            //get user by email
                            user = DAOFactory.getUserDAO().selectByEmail(email);
                            reply = message.createReply();
                            map.put("user",user.toJSON());
                            String jsonStr = mapper.writeValueAsString(map);
                            reply.setContent(jsonStr);

                        } catch (IOException e) {
                            e.printStackTrace();
                            reply = message.createReply();
                            String jsonStr = "{\"success\" : false }";
                            reply.setContent(jsonStr);
                        }
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
                        try {
                            JsonNode rootNode = mapper.readTree(content); // read Json
                            String email = rootNode.path("email").asText();
                            String nickname = rootNode.path("nickname").asText();
                            String password = rootNode.path("password").asText();
                            String deviceToken = rootNode.path("deviceToken").asText();
                            int avatar = rootNode.path("avatar").asInt();
                            user = new User(nickname, avatar, email, password, deviceToken);
                            DAOFactory.getUserDAO().add(user);
                            reply = message.createReply();
                            map.put("success",true);
                            String jsonStr = mapper.writeValueAsString(map);
                            reply.setContent(jsonStr);

                        } catch (IOException e) {
                            reply = message.createReply();
                            String jsonStr = "{\"success\" : false }";
                            reply.setContent(jsonStr);
                        } catch (SQLException e) {
                            reply = message.createReply();
                            String jsonStr = "{\"success\" : false }";
                            reply.setContent(jsonStr);
                        }
                        send(reply);
                        break;
                }
            } else
                block();
        }
    }
}
