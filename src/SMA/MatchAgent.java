package SMA;

import Model.Constant;
import Model.User;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Map;

public class MatchAgent extends Agent {
    private User user1;
    private User user2;
    private int userScore1;
    private int userScore2;

    protected void setup() {
        Object[] objects = getArguments();
        Map map = (Map) objects[0];
        String matchSubject = (String) map.get("Subject");
        user1 = (User) map.get("user");
        String matchLevel = user1.getLevel();


        DF.registerAgent(this, matchSubject, matchLevel);
        System.out.println(getLocalName()+ "--> Installed");

        //First wait the second user in the limit time
        ParallelBehaviour waitSecondUserParllelBehaivour = new ParallelBehaviour(ParallelBehaviour.WHEN_ANY);

        WaitSecondUserBehaivour waitSecondUserBehaivour = new WaitSecondUserBehaivour();
        waitSecondUserParllelBehaivour.addSubBehaviour(waitSecondUserBehaivour);

        DeleteOutOfTimeBehaviour deleteOutOfTimeBehaviour = new DeleteOutOfTimeBehaviour(this, Constant.MATCH_WAIT_TIME_MAX);
        waitSecondUserParllelBehaivour.addSubBehaviour(deleteOutOfTimeBehaviour);

        //
        SequentialBehaviour MatchSequentialBehaviour = new SequentialBehaviour();

        MatchSequentialBehaviour.addSubBehaviour(waitSecondUserParllelBehaivour);
        addBehaviour(MatchSequentialBehaviour);
    }


    /**
     * do something when get the message
     *  wait for second user to join in the game
     */
    private class WaitSecondUserBehaivour extends Behaviour {
        boolean done = false;
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
            ACLMessage message = myAgent.receive(mt);
            if (message != null) {
                String content = message.getContent();
                System.out.println(myAgent.getLocalName() + "--> getRequest ");

                // TODO Auto-generated method stub
                // Get user2
                ACLMessage reply = message.createReply();
                reply.setContent("we get user2, ready to go");
                send(reply);
                done = true;
            } else
            block();
        }

        @Override
        public boolean done(){
            return done;
        }
    }

    private class DeleteOutOfTimeBehaviour extends WakerBehaviour {

        public DeleteOutOfTimeBehaviour(Agent a, long timeout) {
            super(a, timeout);
        }

        @Override
        protected void onWake() {
            System.out.println(myAgent.getLocalName() + "--> wake up");
            myAgent.doDelete();
        }
    }
}
