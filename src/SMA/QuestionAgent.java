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
                    int subject = rootNode.path("subject").asInt();

                    questions = DAOFactory.getQuestionDAO().selectBySubject(subject);

                    //get 10 random question
                    questions = getRandomQuestion(questions);

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

    //get n random num(no same num)
    private int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    private ArrayList<Question> getRandomQuestion(ArrayList<Question> allQestions){
        int[] num10Questions = randomCommon(0, allQestions.size(), 10);
        ArrayList<Question> newQuestions = new ArrayList<>();
        for(int i : num10Questions){
            newQuestions.add(allQestions.get(i));
        }
        return newQuestions;
    }

}