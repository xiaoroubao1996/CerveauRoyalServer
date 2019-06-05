package SMA;

import DAO.DAOFactory;
import Model.Constant;
import Model.Friends;
import Model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import servlet.SMAServlet;
//import sun.awt.Symbol;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static DAO.DAOFactory.getFriendDAO;

public class FriendsAgent extends Agent{

    private User this_user;
    private User friend;
    private AID MatchID;
    private String deviceToken;
    public static final String API_GCM = "AIzaSyCAtOoMnNAj2uzqwebB_zrcxY6KciMwdbo";
    public static final String SENDER_ID = "285662560372";
    private String ANDROID_NOTIFICATION_URL = "https://fcm.googleapis.com/fcm/send";
    //public static final String API_GCM = "AIzaSyDXVkHh-crxkYY73J7OvpLOa3mzufFIlfk";

    protected void setup() {
        System.out.println(getLocalName()+ "--> Installed");
        DF.registerAgent(this, Constant.FRIEND_NAME, getLocalName(),"");

        friend = null;
        deviceToken = null;
        ObjectMapper mapper = new ObjectMapper();
//        try {
//            Map<String, Object> map = mapper.readValue((String) getArguments()[0], Map.class);
//            Integer idFriend = (Integer) map.get("userId");
//            friend = DAOFactory.getUserDAO().selectByID(idFriend);
//            MatchID = new AID((String) map.get("matchAgent"), AID.ISLOCALNAME);
//            System.out.println("Got aid de match :" +  MatchID);
//            if (friend != null) addBehaviour(new sendInvitationBehaviour());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        addBehaviour(new waitToPushNotificationBehaviour());
        addBehaviour(new waitMsgBehaviour());
    }

    private class waitToPushNotificationBehaviour extends CyclicBehaviour {

        @Override
        public void action() {

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
            ACLMessage message = myAgent.receive(mt);
            if (message != null && message.getSender().getLocalName().equals(Constant.SEARCH_MATCH_NAME)) {
                System.out.println("Get ready to push notification");
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = null;
                try {
                    map = mapper.readValue(message.getContent(), Map.class);
                    Integer id = (Integer) map.get("userId");
                    this_user = User.read((String) map.get("user"));
                    friend = DAOFactory.getUserDAO().selectByID(id);
                    MatchID = new AID((String) map.get("matchAgent"), AID.ISLOCALNAME);
                    deviceToken = friend.getDeviceToken();
                    if(deviceToken != null) {
                        sendAndroidNotification(deviceToken);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class waitMsgBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage message = receive();
            if (message != null) {
                if (message.getSender().getLocalName().equals(Constant.ENVIRONEMENT_NAME)) {
                    String content = null;
                    ObjectMapper mapper = new ObjectMapper();
                    ACLMessage reply = null;
                    switch(message.getPerformative()) {
                        //get reply from env
                        case ACLMessage.SUBSCRIBE:
                            try {
                                Map<String, Object> map = mapper.readValue(message.getContent(), Map.class);
                                boolean s = (Boolean) map.get("success");
                                ACLMessage result = new ACLMessage(ACLMessage.INFORM);
                                result.addReceiver(MatchID);
                                Map<String, Object> map2 = new HashMap<String, Object>();
                                if (s) {
                                    map2.put("success", true);
                                } else map2.put("success", false);
                                String jsonStr = mapper.writeValueAsString(map);
                                result.setContent(jsonStr);
                                send(result);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        //get friend list
                        case ACLMessage.REQUEST:
                            content = message.getContent();
                            reply = message.createReply();
                            try {
                                JsonNode rootNode = mapper.readTree(content); // read Json
                                int userId = rootNode.path("id").asInt();
                                //get friends

                                ArrayList<Friends> friends = DAOFactory.getFriendDAO().selectByUserID(userId);
                                ArrayList<User> userList = new ArrayList<>();
                                for(Friends friend :  friends){
                                    userList.add(DAOFactory.getUserDAO().selectByID(friend.getUser2Id()));
                                }
                                Map<String, Object> map = new HashMap<String, Object>();
                                String jsonStr = mapper.writeValueAsString(userList);
                                map.put("success",true);
                                map.put("friends",jsonStr);
                                jsonStr = mapper.writeValueAsString(map);
                                reply.setContent(jsonStr);

                            } catch (IOException e) {
                                e.printStackTrace();
                                reply = message.createReply();
                                String jsonStr = "{\"success\" : false }";
                                reply.setContent(jsonStr);
                            }
                            send(reply);
                            break;

                        //add friend
                        case ACLMessage.PROPOSE:
                            content = message.getContent();
                            reply = message.createReply();
                            try {
                                JsonNode rootNode = mapper.readTree(content); // read Json
                                int userId = rootNode.path("id").asInt();
                                String email = rootNode.path("email").asText();
                                //get friends
                                User user1 = DAOFactory.getUserDAO().selectByID(userId);
                                User user2 = DAOFactory.getUserDAO().selectByEmail(email);
                                if(user1!= null && user2 != null) {
                                    Friends friend = new Friends(user1, user2);
                                    if (DAOFactory.getFriendDAO().add(friend)) {
                                        ArrayList<Friends> friends = DAOFactory.getFriendDAO().selectByUserID(userId);
                                        ArrayList<User> userList = new ArrayList<>();
                                        for(Friends newFriend :  friends){
                                            userList.add(DAOFactory.getUserDAO().selectByID(newFriend.getUser2Id()));
                                        }
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        String jsonStr = mapper.writeValueAsString(userList);
                                        map.put("success",true);
                                        map.put("friends",jsonStr);
                                        jsonStr = mapper.writeValueAsString(map);
                                        reply.setContent(jsonStr);
                                    }
                                }else{
                                    String jsonStr = "{\"success\" : false }";
                                    reply.setContent(jsonStr);
                                }
                            } catch (IOException e) {
                                String jsonStr = "{\"success\" : false }";
                                reply.setContent(jsonStr);
                            } catch (SQLException e) {
                                String jsonStr = "{\"success\" : false }";
                                reply.setContent(jsonStr);
                            }
                            send(reply);
                            break;
                    }
                }
            }else{
                block();
            }
        }
    }


    private void sendAndroidNotification(String deviceToken) throws IOException {

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(ANDROID_NOTIFICATION_URL);
        post.setHeader("Content-type", "application/json");
        post.setHeader("Authorization", "key="+API_GCM);

        JSONObject message = new JSONObject();
        message.put("user", this_user);
        message.put("userID", friend.getId());
        message.put("MatchID", MatchID);
        message.put("to", deviceToken);
        message.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("title", "Java");

        message.put("notification", notification);

        post.setEntity(new StringEntity(message.toString(), "UTF-8"));
        HttpResponse response = client.execute(post);
        System.out.println(response);
        System.out.println(message);
    }

}
