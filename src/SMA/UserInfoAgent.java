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
                switch(message.getPerformative()){
                    case ACLMessage.REQUEST:
                        content = message.getContent();
                        System.out.println(myAgent.getLocalName() + "--> getRequest ");

                        try {
                            JsonNode rootNode = mapper.readTree(content); // read Json
                            String email = rootNode.path("email").asText();

                            //get user by id
                            user = DAOFactory.getUserDAO().selectByEmail(email);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //create reply
                        reply = message.createReply();
                        reply.setContent(user.toJSON());
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
