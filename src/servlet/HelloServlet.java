package servlet;

import SMA.ProcessBehaviour;
import jade.core.behaviours.Behaviour;
import jade.wrapper.gateway.JadeGateway;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author zqw
 *
 * 继承HttpServlet，重写
 */
public class HelloServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		super.doDelete(req, resp);
		System.out.println("处理Get请求。。。");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();

		ProcessBehaviour behaviour = new ProcessBehaviour("testAgent", "this is content");
		activeAgent(behaviour);

		out.println(behaviour.answer);
		out.println("<strong>Hello servlet</strong>");
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		super.doGet(request, response);
		System.out.println("处理Post请求。。。");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		out.println("<strong>Hello servlet</strong>");
		out.flush();
		out.close();
	}

	private void activeAgent(Behaviour behaviour) {
		try {
			JadeGateway.execute(behaviour);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
