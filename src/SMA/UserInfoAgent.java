package SMA;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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

                switch(message.getPerformative()){
                    case ACLMessage.REQUEST:
                        content = message.getContent();
                        System.out.println(myAgent.getLocalName() + "--> getRequest ");

                        reply = message.createReply();
                        reply.setContent("wow");
                        send(reply);
                        break;
                    case ACLMessage.INFORM:
                        content = message.getContent();
                        System.out.println(myAgent.getLocalName() + "--> getRequest ");

                        reply = message.createReply();
                        reply.setContent("wow");
                        send(reply);
                        break;
                    case ACLMessage.PROPOSE:
                        content = message.getContent();
                        System.out.println(myAgent.getLocalName() + "--> getRequest ");

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
