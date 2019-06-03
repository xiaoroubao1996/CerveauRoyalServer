package servlet;

import Model.Constant;
import SMA.ProcessBehaviour;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.behaviours.Behaviour;
import jade.wrapper.gateway.JadeGateway;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.logging.Logger;

public class SMAServlet  extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String JSONFromLastRequest = "";
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
//		super.doDelete(req, resp);
        String url = request.getRequestURI();

        String JSON = request.getParameter("JSON");

        int lastIndex = url.lastIndexOf("/");
        String urlREST = url.substring(lastIndex + 1);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = null;
        ProcessBehaviour behaviour = null;
        switch(urlREST){
            case("user"):
                out = response.getWriter();
                behaviour = new ProcessBehaviour(JSON, Constant.USER_INFO_NAME, Constant.SMA_GET);
                activeAgent(behaviour);
                out.println(behaviour.answer);
                out.flush();
                out.close();
                break;

            case("friends"):
                out = response.getWriter();
                behaviour = new ProcessBehaviour(JSON, Constant.USER_INFO_NAME, Constant.SMA_GET);
                activeAgent(behaviour);
                out.println(behaviour.answer);
                out.flush();
                out.close();
                break;

            case("login"):
                out = response.getWriter();
                behaviour = new ProcessBehaviour(JSON, Constant.USER_INFO_NAME, Constant.SMA_SUBSCRIBE);
                activeAgent(behaviour);
                out.println(behaviour.answer);
                out.flush();
                out.close();
                break;
            case("match"):
//                if(!isSameRequestAsLastOne(JSON)){
                    out = response.getWriter();
                    Logger logger = Logger.getLogger(SMAServlet.class.getName());
                    logger.warning(JSON);
                    logger.warning("MyMessage from myservlet!");
                    behaviour = new ProcessBehaviour(JSON, Constant.SEARCH_MATCH_NAME, Constant.SMA_GET);
                    activeAgent(behaviour);
                    out.println(behaviour.answer);
                    out.flush();
                    out.close();
//                }else{
//                    response.sendError(response.SC_ACCEPTED, "same request");
//                }
//                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
//		super.doDelete(req, resp);
        String url = request.getRequestURI();

        String JSON = request.getParameter("JSON");

        int lastIndex = url.lastIndexOf("/");
        String urlREST = url.substring(lastIndex + 1);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = null;
        ProcessBehaviour behaviour = null;
        JsonNode rootNode = null;
        ObjectMapper mapper = new ObjectMapper();

        switch(urlREST){
            case("user"):
                out = response.getWriter();
                behaviour = new ProcessBehaviour(JSON, Constant.USER_INFO_NAME, Constant.SMA_POST);
                activeAgent(behaviour);
                out.println(behaviour.answer);
                out.flush();
                out.close();
                break;
            case("match"):
//                if(!isSameRequestAsLastOne(JSON)){
                    out = response.getWriter();
                    rootNode = mapper.readTree(JSON);
                    String matchId = rootNode.path("matchId").asText();
                    behaviour = new ProcessBehaviour(JSON, matchId, Constant.SMA_POST);
                    activeAgent(behaviour);
                    out.println(behaviour.answer);
                    out.flush();
                    out.close();
//                }else{
//                    response.sendError(response.SC_ACCEPTED, "same request");
//                }
                break;
        }
    }

    private void activeAgent(Behaviour behaviour) {
        try {
            JadeGateway.execute(behaviour);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSameRequestAsLastOne(String JSON){
        if(JSON.equals(JSONFromLastRequest)){
            return true;
        }
        JSONFromLastRequest = JSON;
        return false;
    }

}
