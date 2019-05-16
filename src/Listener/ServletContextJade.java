package Listener;

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


             }

         }