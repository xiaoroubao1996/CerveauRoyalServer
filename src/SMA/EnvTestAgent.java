package SMA;

import Model.Constant;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class EnvTestAgent extends Agent{
	protected void setup() {
        System.out.println(getLocalName()+ "--> Installed");
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
        ACLMessage messageFromGateway;
        @Override
        public void action() {
            ACLMessage message = null;
            switch (step) {
                case 0:
                    message = receive();
                    if(message != null){
//                    //get message from gateway
                    messageFromGateway = message;
                    System.out.println("env: get message from gate way");
                    ACLMessage messageToSMA = message.createReply();
                    messageToSMA.setPerformative(message.getPerformative());
                    messageToSMA.setContent(message.getContent());
                    messageToSMA.setConversationId(message.getConversationId());
                    send(messageToSMA);
                        step++;
                    }else{
                        block();
                    }

                    break;
                case 1:
                    ACLMessage answer = myAgent.receive(mt);
                    if (answer != null) {
                        System.out.println(answer.getContent());
                        step = 0;
                    } else
                        block();
                    break;
            }
        }
//            ACLMessage message = receive();
//            if(message != null){
//                // TODO
//                System.out.println(getLocalName() + " received message from someone");
//                if (message.getSender().getName().equals(Constant.JADEGATEWAY_NAME)) {
//                    //get message from gateway
//                    messageFromGateway = message;
//                    System.out.println("env: get message from gate way");
//                    ACLMessage messageToSMA = message.createReply();
//                    messageToSMA.setPerformative(message.getPerformative());
//                    messageToSMA.setContent(message.getContent());
//                    messageToSMA.setConversationId(message.getConversationId());
//                    send(messageToSMA);
//                }else{
//                    //get message from SMA
//                    System.out.println(messageFromGateway.getContent());
//                    message.clearAllReceiver();
//                    message.addReceiver(messageFromGateway.getSender()); // TODO
//                    send(message);
//                }
//            }else{
//                    block();
//                }
//        }
    }

}
