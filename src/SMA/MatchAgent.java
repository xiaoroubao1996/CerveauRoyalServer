package SMA;

import Model.Constant;
import Model.Question;
import Model.User;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MatchAgent extends Agent {
	private User user1;
	private User user2;
	private int user1Score;
	private int user2Score;
	private String matchSubject;
	private ArrayList<Question> questionsList;
	private String questionsJson;
	private ACLMessage reply; 

	protected void setup() {
		System.out.println(getLocalName() + "--> Installed");

		Object[] objects = getArguments();
		Map map = (Map) objects[0];

		matchSubject = (String) map.get("Subject");
		user1 = (User) map.get("user");
		// TODO: User.getLevel()
		String matchLevel = user1.getLevel();

		// register this MatchAgent into the DF
		DF.registerAgent(this, matchSubject, matchLevel);


		// The main behaviour in this Agent
		SequentialBehaviour MatchSequentialBehaviour = new SequentialBehaviour();
		
		// 1
		// First sub-behaviour in the main sequential behaviour
		ParallelBehaviour waitSecondUserParllelBehaivour = new ParallelBehaviour(ParallelBehaviour.WHEN_ANY);

		//start 1.1 waitUser2SendQuesSequential
		SequentialBehaviour waitUser2SendQuesSequential = new SequentialBehaviour();
		//1.1.1 waitSecondUserBehaivour
		WaitSecondUserBehaivour waitSecondUserBehaivour = new WaitSecondUserBehaivour();
		waitUser2SendQuesSequential.addSubBehaviour(waitSecondUserParllelBehaivour);
		//1.1.2 requestDataBehaviour
		RequestDataBehaviour requestDataBehaviour = new RequestDataBehaviour();
		waitUser2SendQuesSequential.addSubBehaviour(requestDataBehaviour);
		//1.1.3 replyWithDataBehaviour
		ReplyWithDataBehaviour replyWithDataBehaviour = new ReplyWithDataBehaviour();
		waitUser2SendQuesSequential.addSubBehaviour(replyWithDataBehaviour);
		//end 1.1
		waitSecondUserParllelBehaivour.addSubBehaviour(waitUser2SendQuesSequential);
		
		
		//start 1.2
		DeleteOutOfTimeBehaviour deleteOutOfTimeBehaviour = new DeleteOutOfTimeBehaviour(this,Constant.MATCH_WAIT_TIME_MAX);
		//end 1.2
		waitSecondUserParllelBehaivour.addSubBehaviour(deleteOutOfTimeBehaviour);
		
		//end 1
		MatchSequentialBehaviour.addSubBehaviour(waitSecondUserParllelBehaivour);
		
		
		
		// 2
		// Second sub-behaviour in the main sequential behaviour
		CountingBehaviour countingBehaviour = new CountingBehaviour();
		//end 2
		MatchSequentialBehaviour.addSubBehaviour(countingBehaviour);
		
		
		//3
		// Third sub-behaviour in the main sequential behaviour
		GameOverBehaviour gameOverBehaviour = new GameOverBehaviour();
		//end 3
		MatchSequentialBehaviour.addSubBehaviour(gameOverBehaviour);

		
		addBehaviour(MatchSequentialBehaviour);
	}
	
	private class GameOverBehaviour extends Behaviour{

		@Override
		public void action() {
			Boolean user1Win;
			Boolean user2Win;
			if(user1Score > user2Score) {
				user1Win = true;
				user2Win = false;
			}else if (user1Score == user2Score) {
				user1Win = true;
				user2Win = true;
			}else {
				user1Win = false;
				user2Win = true;
			}
			

			//update the score of user 1 and user 2
			//{userId:xx, subject:xxx,win:true}
			ACLMessage m = new ACLMessage(ACLMessage.INFORM);
			m.addReceiver(new AID("UserInfoAgent", AID.ISLOCALNAME));
			m.setContent("{\"userId\":\""+user1.getId()+
					"\", \"subject\": \""+matchSubject+
					"\",\"isWinner\":\""+user1Win+"\"}");
			myAgent.send(m);
			
			ACLMessage m = new ACLMessage(ACLMessage.INFORM);
			m.addReceiver(new AID("UserInfoAgent", AID.ISLOCALNAME));
			m.setContent("{\"userId\":\""+user2.getId()+
					"\", \"subject\": \""+matchSubject+
					"\",\"isWinner\":\""+user2Win+"\"}");
			myAgent.send(m);
			
			myAgent.doDelete();			
		
		}

		@Override
		public boolean done() {
			return false;
		}
	}
	
	private class CountingBehaviour extends Behaviour{

		int iterator = 0 ;
		int step = 0;
		long endTime = System.currentTimeMillis() + 15000;		
		int user1Res;
		int user2Res;
		int correctRes;
		String msgJson;
		
		@Override
		public void action() {
			switch(step) {
			case'0':
				//waiting for the reply from both users
				//15 s for each question
				while(endTime > System.currentTimeMillis()) {
					correctRes = questionsList.get(iterator).getAnswer();
					//TODO: doubleCheck the sender and performative
					MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchSender(new AID("EnvAgent", AID.ISLOCALNAME)));
					ACLMessage message1 = receive(mt1);
					
					MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchSender(new AID("EnvAgent", AID.ISLOCALNAME)));
					ACLMessage message2 = receive(mt2);
					
					if (message1 != null && message2 != null) {
						msgJson = message1.getContent();
						checkMsg(msgJson);

						msgJson = message2.getContent();
						checkMsg(msgJson);		
						
						step++;
					} else {
						block();
					}
				}
				break;
			case'1':
				//return the option chosen by another user as well as the score of another user
				//{"msgTo":"id", "opponent": "id","score":"x", "choice": "x"}
				ACLMessage m = new ACLMessage(ACLMessage.INFORM);
				m.addReceiver(new AID("EnvAgent", AID.ISLOCALNAME));
				m.setContent("{\"msgTo\":\""+user2.getId()+
						"\", \"opponent\": \""+user1.getId()+"\",\"score\":\""+user1Score+"\", \"choice\": \""+user1Res+"\"}");
				myAgent.send(m);
				
				ACLMessage m = new ACLMessage(ACLMessage.INFORM);
				m.addReceiver(new AID("EnvAgent", AID.ISLOCALNAME));
				m.setContent("{\"msgTo\":\""+user1.getId()+
						"\", \"opponent\": \""+user2.getId()+"\",\"score\":\""+user2Score+"\", \"choice\": \""+user2Res+"\"}");
				myAgent.send(m);
				
				iterator++;
				break;
			}
			
		}
		
		//get the response of a specific user
		private void checkMsg(String msg) {
            JsonNode rootNode = mapper.readTree(msg); 
			//{UserId: xxx, Option: 2, Score: xxx}
			if(json.getString("UserId") == user1.getId()) {
				user1Res = Integer.parseInt(rootNode.path("Option").asText());
				user1Score = Integer.parseInt(rootNode.path("Score").asText());
			}else {
				user2Res = Integer.parseInt(rootNode.path("Option").asText());
				user2Score = Integer.parseInt(rootNode.path("Score").asText());
			}
			
		}

		@Override
		public boolean done() {
			Boolean isDone = false;
			if(iterator == 10) {
				isDone = true;
			}
			return isDone;
		}
		
	}

	private class RequestDataBehaviour extends Behaviour {
		int step = 0;
		Boolean isDone = false;
		
		@Override
		public void action() {
			switch (step) {
			case '0':
				// send request to QuestionDataAgent
				// request 10 questions in one time
				ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
				// TODO: doubleCheck the AID of questionDataAgent
				m.addReceiver(new AID("questionDataAgent", AID.ISLOCALNAME));
				m.setContent("{\"Subjet\":\"" + matchSubject + "\"}");
				myAgent.send(m);
				step = 1;
				break;
			case '1':
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
						MessageTemplate.MatchSender(new AID("questionDataAgent", AID.ISLOCALNAME)));
				ACLMessage message = receive(mt);
				
				if (message != null) {
					questionsJson = message.getContent();
					//TODO
					questionsList = questionJsonToObjectList(questionsJson);
					isDone = true;
				} else {
					block();
				}
			}

		}

		@Override
		public boolean done() {
			return isDone;
		}

	}

	
	private class ReplyWithDataBehaviour extends OneShotBehaviour {

		@Override
		public void action() {
			// reply to the Env "we get user2 and we get Data, ready to go"
			// TODO: in SearchMatchAgent set this message replyTo EnvAgent
			// TODO: User.getUserId()
			reply.setContent(generateReplyJson(user1.getUserId(), user2.getUserId(), questionsJson));
			send(reply);	
		}
		
	} 
	
	/**
	 * wait for second user to join in the game do something when get the message
	 */
	private class WaitSecondUserBehaivour extends Behaviour {
		boolean done = false;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				System.out.println(myAgent.getLocalName() + "--> getRequest ");
				// the JadeMsg from EnvAgent -> SearchMatchAgent -> MatchAgent
				ACLMessage reply = message.createReply();
				String content = message.getContent();

				// Get user2 info and add into this room
				// TODO: content is a json of a user object
				// TODO: user2 = userJsonToObject(Content);

				done = true;
			} else
				block();
		}

		@Override
		public boolean done() {
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

	private String generateReplyJson(String user1Id, String user2Id, String message) {
		String jsonString;
		String msgJson;
		msgJson = message.substring(1, message.length());
		jsonString = "{\"User1\":\"" + user1Id + "\",\"User2\":\"" + user2Id + "\","+msgJson+"}";
		return jsonString;
	}

}
