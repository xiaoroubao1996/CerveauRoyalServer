package Model;

import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;

public class JadeModel {
    private ProfileImpl profileImpl;
    private static AgentContainer container = null;

    public JadeModel(){
        profileImpl = new ProfileImpl();
        profileImpl.setParameter("main", "true");
        profileImpl.setParameter("gui", "true");
        profileImpl.setParameter("platform-id", "cerveauroyal");
        profileImpl.setParameter("local-port", "1098");
    }

    public static AgentContainer getContainer() {
        return container;
    }

    public static void setContainer(AgentContainer container) {
        JadeModel.container = container;
    }

    public ProfileImpl getProfileImpl() {
        return profileImpl;
    }

    public void setProfileImpl(ProfileImpl profileImpl) {
        this.profileImpl = profileImpl;
    }
}
