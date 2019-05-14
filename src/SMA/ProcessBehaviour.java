package SMA;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ProcessBehaviour extends Behaviour {
	String content;
	String receiverName;
	MessageTemplate mt;
	private boolean stop = false;
	int step = 0;
	public String answer;

	public ProcessBehaviour(String receiverName, String content) {
		super();
		this.receiverName = receiverName;
		this.content = content;
	}

	@Override
	public void action() {
		switch (step) {
		case 0:
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setContent(content);
			String convId = String.valueOf(System.currentTimeMillis());
			mt = MessageTemplate.MatchConversationId(convId);
			message.setConversationId(convId);
			message.addReceiver(new AID(receiverName, AID.ISLOCALNAME));
			myAgent.send(message);
			step = 1;
			break;
		case 1:
			
			ACLMessage answer = myAgent.receive(mt);
			if (answer != null) {
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
