package Llistener;

import Model.Constant;
import jade.wrapper.AgentContainer;
import jade.core.ProfileImpl;
import jade.core.Runtime;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextJade implements ServletContextListener {

              // 实现其中的销毁函数

              public void contextDestroyed(ServletContextEvent sce) {

                 System.out.println("this is last destroyeed");

             }

             // 实现其中的初始化函数，当有事件发生时即触发

             public void contextInitialized(ServletContextEvent sce) {
                    // open main console gui
                     // properties: main=true; gui = true;
                     Runtime rt = Runtime.instance();
                     ProfileImpl p = null;
                     try {
                         //initialize PrpfileImpl
                         p = new ProfileImpl();
                         p.setParameter("main", "true");
                         p.setParameter("gui", "false");
                         p.setParameter("platform-id", "cerveauroyal");
                         p.setParameter("local-port", "1098");


                         AgentContainer cc = rt.createMainContainer(p);
                         cc.createNewAgent("testAgent", "SMA.testAgent",null).start();
                         System.out.println("Jade initialized");
                     } catch (Exception ex) {
                         ex.printStackTrace();
                     }

//                 ServletContext sct=sce.getServletContext();
//
//                 Map<Integer,String> depts=new HashMap<Integer,String>();
//
//                 Connection connection=null;
//
//                 PreparedStatement pstm=null;
//
//                 ResultSet rs=null;
//
//                 try{
//
//                         connection=ConnectTool.getConnection();
//
//                         String sql="select deptNo,dname from dept";
//
//                         pstm=connection.prepareStatement(sql);
//
//                         rs=pstm.executeQuery();
//
//                         while(rs.next()){
//
//                                 depts.put(rs.getInt(1), rs.getString(2));
//
//                             }
//
//                         // 将所取到的值存放到一个属性键值对中
//
//                         sct.setAttribute("dept", depts);
//
//                         System.out.println("======listener test is beginning=========");
//
//                     }catch(Exception e){
//
//                         e.printStackTrace();
//
//                     }finally{
//
//                         ConnectTool.releasersc(rs, pstm, connection);
//
//                     }

             }

         }