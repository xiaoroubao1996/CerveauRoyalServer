package SMA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DAO.DAOFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Constant;
import Model.JadeModel;
import Model.Question;
import Model.User;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.StaleProxyException;

public class MatchAgent extends Agent {
	private User user1;
	private User user2;
	private int user1Score;
	private int user2Score;
	private Integer opponentId;
	private Boolean withUser;
	private String matchSubject;
	private String matchId;
	private ArrayList<Question> questionsList;
	private String questionsJson;
	private ACLMessage MessageToReplyUser1;
	private ACLMessage MessageToReplyUser2;
	private ObjectMapper mapper = new ObjectMapper();

	protected void setup() {
		System.out.println(getLocalName() + "--> Installed");

		user1Score = 0;
        user2Score = 0;
        opponentId = 0 ;

		Object[] objects = getArguments();
		Map map = (Map) objects[0];

		matchSubject = (String) map.get("subject");
		user1 = (User) map.get("user");
		withUser = (Boolean) map.get("withUser");
		opponentId = (Integer) map.get("userId");
		
		
        MessageToReplyUser1 = (ACLMessage) map.get("MessageToReplyUser1");
        MessageToReplyUser1 = MessageToReplyUser1.createReply();
		String matchLevel = String.valueOf(user1.getRank());

		// register this MatchAgent into the DF
		DF.registerAgent(this, matchSubject, matchLevel);
        matchId = this.getName();
        
		// The main behaviour in this Agent
		SequentialBehaviour MatchSequentialBehaviour = new SequentialBehaviour();

		// 1
		// First sub-behaviour in the main sequential behaviour
		ParallelBehaviour waitSecondUserParllelBehaivour = new ParallelBehaviour(ParallelBehaviour.WHEN_ANY);

		//start 1.1 waitUser2SendQuesSequential
		SequentialBehaviour waitUser2SendQuesSequential = new SequentialBehaviour();
		//1.1.1 waitSecondUserBehaivour
		WaitSecondUserBehaivour waitSecondUserBehaivour = new WaitSecondUserBehaivour();
		waitUser2SendQuesSequential.addSubBehaviour(waitSecondUserBehaivour);
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
	


	private String generateDataJson(User user, Boolean withUser, String userId) {
		Map<String, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        try {
            map.put("user",user.toJSON());
            map.put("withUser", withUser);
            map.put("userId", userId);
            jsonString = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
//            jsonString = "{\"success\": false}";
        }
		return jsonString;
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
			//{user: user}

			user1.updateMatchResult(user1Win,matchSubject);
			user2.updateMatchResult(user2Win,matchSubject);


			ACLMessage m = new ACLMessage(ACLMessage.INFORM);
			m.addReceiver(new AID(Constant.USER_INFO_NAME, AID.ISLOCALNAME));
			m.setContent(user1.toJSON());
			myAgent.send(m);

			m = new ACLMessage(ACLMessage.INFORM);
			m.addReceiver(new AID(Constant.USER_INFO_NAME, AID.ISLOCALNAME));
			m.setContent(user2.toJSON());
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
		long endTime = System.currentTimeMillis() + 20000;
		int user1Res = 0;
		int user2Res = 0;
		int correctRes;
		String msgJson;
        ACLMessage message1 = null;
        ACLMessage message2 = null;
        boolean stop = false;

		@Override
		public void action() {
			switch(step) {
			case 0:
				//waiting for the reply from both users
				//20 s for each question
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchSender(new AID(Constant.ENVIRONEMENT_NAME, AID.ISLOCALNAME)));

				if(endTime > System.currentTimeMillis()) {
					//correctRes = questionsList.get(iterator).getAnswer();
					//TODO: doubleCheck the sender and performative

					if(message1 == null){
						message1 = receive(mt);

					}
					if(message2 == null){
						message2 = receive(mt);
					}

					if (message1 != null && message2 != null) {
						msgJson = message1.getContent();
						checkMsg(msgJson);

						msgJson = message2.getContent();
						checkMsg(msgJson);
						step++;
						break;
					} else {
						block();
					}

				//which means one of the users lose his connection
				}else{
					step++;
					iterator = 10;
					stop = true;
				}
				break;
			case 1:
				//return the option chosen by another user as well as the score of another user
				//{"id":id,"matchId":matchId, "index": Index,"answer": answer. "score":score, "stop", stop}

				//To user1
                ACLMessage m = message1.createReply();
                m.setPerformative(ACLMessage.INFORM);
				m.addReceiver(new AID(Constant.ENVIRONEMENT_NAME, AID.ISLOCALNAME));

				String newMessageContent = message2.getContent();
				newMessageContent = newMessageContent.substring(0,newMessageContent.length() - 1); // delete "}"
				newMessageContent = newMessageContent + ", \"stop\": " + stop + "}";

//				m.setContent("{\"id\":\""+user2.getId()+
//						"\", \"roomId\": \""+matchId+"\",\"score\":\""+user1Score+"\", \"choice\": \""+user1Res+"\"}");
				m.setContent(newMessageContent);
				myAgent.send(m);

				//To user2
				m = message2.createReply();
				m.setPerformative(ACLMessage.INFORM);
				m.addReceiver(new AID(Constant.ENVIRONEMENT_NAME, AID.ISLOCALNAME));

				newMessageContent = message1.getContent();
				newMessageContent = newMessageContent.substring(0,newMessageContent.length() - 1); // delete "}"
				newMessageContent = newMessageContent + ", \"stop\": " + stop + "}";
				m.setContent(newMessageContent);
				myAgent.send(m);

				iterator++;
                user1Res = 0;
                user2Res = 0;
                message1 = null;
                message2 = null;
                endTime = System.currentTimeMillis() + 20000;
                step = 0;
				break;
			}

		}

		//get the response of a specific user
		private void checkMsg(String msg) {
            JsonNode rootNode;
			try {
				rootNode = mapper.readTree(msg);
				//{UserId: xxx, Option: 2, Score: xxx}
				if(rootNode.path("UserId").asInt() == user1.getId()) {
					user1Res = rootNode.path("Option").asInt();
					user1Score = rootNode.path("Score").asInt();
				}else {
					user2Res = rootNode.path("Option").asInt();
					user2Score = rootNode.path("Score").asInt();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public boolean done() {
			boolean isDone = false;
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
			case 0:
				// send request to QuestionDataAgent
				// request 10 questions in one time
				ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
				// TODO: doubleCheck the AID of questionDataAgent
				m.addReceiver(new AID(Constant.QUESTION_NAME, AID.ISLOCALNAME));
				m.setContent("{\"Subjet\":\"" + matchSubject + "\"}");
				myAgent.send(m);
				step = 1;
				break;
			case 1:
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
						MessageTemplate.MatchSender(new AID("questionDataAgent", AID.ISLOCALNAME)));
				ACLMessage message = receive(mt);

				if (message != null) {
					questionsJson = message.getContent();

                    try {
                        questionsList = mapper.readValue(questionsJson, new TypeReference<List<Question>>() {});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
//			reply.setContent(generateReplyJson(String.valueOf(user1.getId()), String.valueOf(user2.getId()), questionsJson));
//			send(reply);
            // the JadeMsg from EnvAgent -> SearchMatchAgent -> MatchAgent
            MessageToReplyUser1.setContent(generateReplyJson(user1, user2));
            MessageToReplyUser2.setContent(generateReplyJson(user2, user1));

            send(MessageToReplyUser1);
            send(MessageToReplyUser2);
		}


		private String generateReplyJson(User userSelf, User opponent) {
			Map<String, Object> map = new HashMap<>();


            String jsonString = null;
            try {
                String questionsJSON = mapper.writeValueAsString(questionsList);
                map.put("success",true);
                map.put("opponent", opponent.toJSON());
                map.put("questions",questionsJSON);
                map.put("matchId", matchId);
                map.put("withFriend", DAOFactory.getFriendDAO().user1IsFriendOfUser2(userSelf, opponent));
                map.put("subject", matchSubject);
                jsonString = mapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                jsonString = "{\"success\": false}";
            }
			return jsonString;
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

				if(withUser == true) {		
					ObjectMapper mapper = new ObjectMapper();
					JsonNode rootNode = null;
					// read Json
					try {
						rootNode = mapper.readTree(message.getContent());
						Boolean success = rootNode.path("success").asBoolean();
						//case 1-1: play the game with an user and the user accept the request
						if(success == true && opponentId != 0) {
							//get the user2 into this room then goto get the questionDataBehaviour
							user2 = DAOFactory.getUserDAO().selectByID(opponentId);
							MessageToReplyUser2 = message.createReply();
							
						}else {
						//case 1-2: play the game with an user and the user REFUSE the request
							//tell the user1 that request FALSE and delete this room
							String jsonString = "{\"success\": false}";
				            MessageToReplyUser1.setContent(jsonString);
				            send(MessageToReplyUser1);
							myAgent.doDelete();
						}
						//case 2: play the game with an user but the user does NOT reply 
						//deleteOutOfTimeBehaviour will be launched and end this MatchAgent
						//if the Friend response the request after the delete of MatchAgent, 
						//TODO: Android app need to treat the OnErruer state. 
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					
					
				}else {
					//reply
	                MessageToReplyUser2 = message.createReply();

					// Get user2 info and add into this room, content is a json of a user object
	                String userString = message.getContent();
					user2 = User.read(userString);
				}
				
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
            String jsonString = "{\"success\": false}";
            MessageToReplyUser1.setContent(jsonString);
            send(MessageToReplyUser1);
			myAgent.doDelete();
		}
	}


}
