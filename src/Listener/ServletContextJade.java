package Listener;

import Model.JadeModel;
import jade.wrapper.AgentContainer;
import jade.core.ProfileImpl;
import jade.core.Runtime;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextJade implements ServletContextListener {

              public void contextDestroyed(ServletContextEvent sce) {

                 System.out.println("this is last destroyeed");

             }

             public void contextInitialized(ServletContextEvent sce) {
                // open main console gui
                 // properties: main=true; gui = true;
                 Runtime rt = Runtime.instance();
                 JadeModel jadeModel = new JadeModel();
                     try {
                         //initialize PrpfileImpl

                         jadeModel.setContainer(rt.createMainContainer(jadeModel.getProfileImpl()));

                         //put container to search agent
                         jadeModel.getContainer().createNewAgent("searchMatchAgent", "SMA.searchMatchAgent",null).start();
                         System.out.println("Jade initialized");
                     } catch (Exception ex) {
                         ex.printStackTrace();
                     }


             }

         }