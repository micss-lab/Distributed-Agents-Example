package agent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Initializer {
    /**
     * IP (or host) of the main container
     */
    private static String MAIN_CONTAINER_IP; // localhost
    static {
        try {
            MAIN_CONTAINER_IP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    /**
     * Port to use to contact the AMS
     */
    private static int PLATFORM_PORT=1099;
    /**
     * ID (name) of the platform instance
     */
    private static String PLATFORM_ID="Main-Platform";
    /**
     * Name of the container
     */
    private static String CONTAINER_NAME="Laptop-Container";
    /**
     * MTPS Protocol address
     */
    private static String MTPS_PROTOCOL=String.format("jade.mtp.http.MessageTransportProtocol(http://%s:7778)", MAIN_CONTAINER_IP);

    public static void main(String[] args) throws UnknownHostException {

        Runtime runtime = Runtime.instance();
        String source = MAIN_CONTAINER_IP;
        String target = "192.168.0.138";

        ProfileImpl profileImp = new ProfileImpl(target, 1099, null, false);
        profileImp.setParameter(Profile.LOCAL_HOST, source);
        profileImp.setParameter(Profile.CONTAINER_NAME, "Edge-Container");
//        profileImp.setParameter(Profile.MTPS, MTPS_PROTOCOL);
        AgentContainer agentContainer = runtime.createAgentContainer(profileImp);


        if (agentContainer != null) {
            AgentController agentController;
            try {
                agentController = agentContainer.createNewAgent("edge_agent", EdgeAgent.class.getName(), new Object[]{});
                agentController.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
