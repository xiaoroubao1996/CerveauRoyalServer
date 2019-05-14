package SMA;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.util.leap.Map;


public class testAgent extends Agent{
    protected void setup() {
        System.out.println(getLocalName()+ "--> Installed");
        addBehaviour(new IntermediaireBehaviour());
    }

    /**
     * do something when get the message
     *
     */
    private class IntermediaireBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage message = receive(mt);
            if (message != null) {
                String s = message.getContent();
                System.out.println(myAgent.getLocalName() + "--> getRequest ");

                ACLMessage reply = message.createReply();
                reply.setContent("wow");
                send(reply);
            } else
                block();
        }
    }
}
