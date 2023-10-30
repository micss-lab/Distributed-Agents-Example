package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CloudAgent extends Agent {

    protected void setup() {
        System.out.println("Agent " + getLocalName() + " started.");
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(receiverBehaviour);
        addBehaviour(parallelBehaviour);
    }

    CyclicBehaviour receiverBehaviour = new CyclicBehaviour() {
        @Override
        public void action() {
            //1° Create a filter
            MessageTemplate template = MessageTemplate.and(
                    MessageTemplate.MatchProtocol("Lora"),
                    MessageTemplate.or(
                            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                            MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
                    )
            );
            //2° Check the mailbox to see if a message that matches the above template is available
            ACLMessage msg = this.myAgent.receive(template);
            if (msg != null) {
                //3. The message is extracted from the mailbox and processed
                String textMessage = msg.getContent();
                System.out.println("textMessage = " + textMessage+ "from "+ msg.getSender());
                ACLMessage msgTx = msg.createReply();
                msgTx.setContent("Hello!, The message is received");
                send(msgTx);
            } else {
                //3.b The behaviour goes to sleep, until a new message arrives in the mailbox
                block();
            }
        }
    };

}
