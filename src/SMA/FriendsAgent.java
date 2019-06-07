package SMA;

import DAO.DAOFactory;
import Model.Constant;
import Model.Friends;
import Model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
//import org.json.simple.JSONObject;
//import sun.awt.Symbol;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FriendsAgent extends Agent{

    private User thisUser;
    private User friend;
    private String matchId;
    private String deviceToken;
    private int subject;
//    public static final String API_GCM = "AIzaSyCAtOoMnNAj2uzqwebB_zrcxY6KciMwdbo";
    public static final String API_GCM = "AAAAUyUzqMQ:APA91bEg6KWwhxMcZjEZDivYAejDNWIfH2zT8N6OKNi-CcFljMLP_tHOl5pk4d_y33miNzFlt1rocVuXVMZZ0bWDTajSV6tzzNfi33N4kk_AcX1hinlPMUWXK9JcsB60Nc0slASL9yMb";

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
                    thisUser = User.read((String) map.get("user"));
                    friend = DAOFactory.getUserDAO().selectByID(id);
                    matchId = (String)map.get("matchAgent");
                    subject = (Integer)map.get("subject");
                    deviceToken = friend.getDeviceToken();
                    if(deviceToken != null) {
                        sendAndroidNotification(deviceToken);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                block();
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

        message.put("to", deviceToken);

        JSONObject dataJson = new JSONObject();

        //dataJson.put("title", "Java");
        dataJson.put("user", thisUser);
        dataJson.put("userId", friend.getId());
        dataJson.put("matchId", matchId);
        dataJson.put("subject", subject);
        //dataJson.put("priority", "high");


        message.put("data", dataJson);

        post.setEntity(new StringEntity(message.toString(), "UTF-8"));
        HttpResponse response = client.execute(post);
        System.out.println(response);
        System.out.println(message);
    }

}
