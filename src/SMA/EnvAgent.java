package SMA;

import Model.Constant;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.gateway.JadeGateway;

public class EnvAgent extends Agent{
	protected void setup() {
        DF.registerAgent(this, Constant.ENVIRONEMENT_NAME, getLocalName());
        addBehaviour(new EnvBehaviour());
	}

    /**
     * do something when get the message
     *  REQUEST: get
     *  INFORM: put(update)
     *  PROPOSE: post(create)
     */
    private class EnvBehaviour extends CyclicBehaviour {
        private int step = 0;
        MessageTemplate mt;
        ACLMessage message;
        @Override
        public void action() {
//            switch (step) {
//                case 0:
//                    message = receive();
//                    if (message.getSender().getName().equals(Constant.JADEGATEWAY_NAME)) {
//                        String convId = String.valueOf(System.currentTimeMillis());
//                        mt = MessageTemplate.MatchConversationId(convId);
//                        message.setConversationId(convId);
//                        step++;
//                    }
//                    break;
//                case 1:
//                    ACLMessage answer = myAgent.receive(mt);
//                    if (answer != null) {
//                        message
//                    } else
//                        block();
//                    break;
//            }
                message = receive();
                if(message != null){
                if (message.getSender().getName().equals(Constant.JADEGATEWAY_NAME)) {
                    //get message from gateway
                    ACLMessage messageToSMA = message.createReply();
                    messageToSMA.setPerformative(message.getPerformative());
                    messageToSMA.setContent(message.getContent());
                    messageToSMA.setConversationId(message.getConversationId());
                    send(messageToSMA);
                }else{
                    //get message from SMA
                    message.clearAllReceiver();
                    message.addReceiver();
                }
            }else{
                    block();
            }
        }
    }

}
