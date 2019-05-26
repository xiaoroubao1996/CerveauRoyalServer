package Main;

import Model.Constant;
import Model.JadeModel;
import jade.core.ProfileImpl;
import jade.core.Runtime;

public class TestMain {

    private static String MAIN_PROPERTIES_FILE = "properties/main.properties";

    public static void main(String[] args) {
        boot_gui();
    }

    public static void boot_gui() {
        Runtime rt = Runtime.instance();
        ProfileImpl p = null;
        JadeModel jadeModel = new JadeModel();
        try {
            //initialize PrpfileImpl

            jadeModel.setContainer(rt.createMainContainer(jadeModel.getProfileImpl()));

            //put container to search agent
            jadeModel.getContainer().createNewAgent(Constant.SEARCH_MATCH_NAME, "SMA.SearchMatchAgent",null).start();
            jadeModel.getContainer().createNewAgent(Constant.ENVIRONEMENT_NAME, "SMA.EnvTestAgent",null).start();
            jadeModel.getContainer().createNewAgent(Constant.USER_INFO_NAME, "SMA.UserInfoAgent",null).start();
            System.out.println("Jade initialized");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}

