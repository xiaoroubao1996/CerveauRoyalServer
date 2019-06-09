package SMA;

import Model.Constant;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ProcessBehaviour extends Behaviour {
	private String content;
	private String sendToAgentName;
	private int ACLmessagePerformative = 0;
	MessageTemplate mt;
	private boolean stop = false;

	int step = 0;
	public String answer;

	public ProcessBehaviour(String content,String sendToAgentName, int ACLmessagePerformative) {
		super();
		this.content = content;
		this.sendToAgentName = sendToAgentName;
		if(ACLmessagePerformative == Constant.SMA_GET){
			this.ACLmessagePerformative = ACLMessage.REQUEST;
		}else if (ACLmessagePerformative == Constant.SMA_POST){
			this.ACLmessagePerformative = ACLMessage.PROPOSE;
		} else if (ACLmessagePerformative == Constant.SMA_PUT){
			this.ACLmessagePerformative = ACLMessage.INFORM;
		}else if (ACLmessagePerformative == Constant.SMA_SUBSCRIBE){
			this.ACLmessagePerformative = ACLMessage.SUBSCRIBE;
		}
	}

	@Override
	public void action() {
		switch (step) {
		case 0:
			System.out.println("process: get /user request");
			ACLMessage message = new ACLMessage(ACLmessagePerformative);
			message.setContent(content);
			message.addReplyTo(new AID(sendToAgentName, AID.ISLOCALNAME));
			String convId = String.valueOf(System.currentTimeMillis());
			mt = MessageTemplate.MatchConversationId(convId);
			message.setConversationId(convId);
			message.addReceiver(new AID(Constant.ENVIRONEMENT_NAME, AID.ISLOCALNAME));
			myAgent.send(message);
			step = 1;
			break;
		case 1:
			
			ACLMessage answer = myAgent.receive(mt);
			if (answer != null) {
				System.out.println("gateway :get message from env");
				stopProcess(answer.getContent());
			} else
				block();
			break;
		}
	}

	private void stopProcess(String ans) {
		answer = ans;
		stop = true;
	}

	@Override
	public boolean done() {
		return stop;
	}
}
