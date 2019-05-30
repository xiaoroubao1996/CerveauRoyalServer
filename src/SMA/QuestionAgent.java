package SMA;

import DAO.DAOFactory;
import Model.Constant;

import Model.Question;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuestionAgent extends Agent {


    protected void setup() {
        System.out.println(getLocalName()+ "--> Installed");
        DF.registerAgent(this, Constant.QUESTION_NAME, getLocalName());
        addBehaviour(new waitMsgBehaviour());
    }

    private class waitMsgBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = receive(mt);
            if (message != null) {
                //Initialisation
                ACLMessage reply = null;
                String content = null;
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = new HashMap<String, Object>();
                content = message.getContent();
                ArrayList<Question> questions = null;
                String jsonStr = null;
                try {
                    JsonNode rootNode = mapper.readTree(content); // read Json
                    String subject = rootNode.path("subject").asText();

                    questions = DAOFactory.getQuestionDAO().selectBySubject(subject);

                    //TODO random 10 question
//                    questions = randomQuestion(questions);

                    jsonStr = mapper.writeValueAsString(questions);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //create reply
                reply = message.createReply();
                reply.setContent(jsonStr);
                send(reply);
            } else
                block();
        }
    }

}