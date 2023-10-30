package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class EdgeAgent extends Agent {

    protected void setup() {
        System.out.println("Agent " + getLocalName() + " started.");
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(senderBehaviour);
        parallelBehaviour.addSubBehaviour(receiverBehaviour);
        addBehaviour(parallelBehaviour);
    }

    TickerBehaviour senderBehaviour = new TickerBehaviour(this, 5000) {
        @Override
        protected void onTick() {
            //1° Create the message
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);//FIPA performative to choose
            //2° set the sender
            msg.setSender(this.myAgent.getAID());
            //3° set the receiver(s)
            msg.addReceiver(new AID("cloud_agent", AID.ISLOCALNAME));
            msg.setProtocol("Lora");
            //5° set the content of the message, either a String OR a serializable object
            msg.setContent("Hello World");
            //msg.setContentObject(/Serializable object/); You can either use setContent or setContentObject, not both
            send(msg);
            System.out.println("Sent Message = " + msg.getContent());
        }
    };


    CyclicBehaviour receiverBehaviour = new CyclicBehaviour() {
        @Override
        public void action() {
            System.out.println("myAgent = " + myAgent);
            //1° Create a filter
            MessageTemplate template = MessageTemplate.and(
                    MessageTemplate.MatchProtocol("Lora"),
                    MessageTemplate.or(
                            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                            MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
                    )
            );
            //2° Check the mailbox to see if a message that matches the above template is available
            ACLMessage msg = this.myAgent.receive();
            if (msg != null) {
                //3. The message is extracted from the mailbox and processed
                String textMessage = msg.getContent();
                System.out.println("textMessage = " + textMessage+ " from "+ msg.getSender());
            } else {
                //3.b The behaviour goes to sleep, until a new message arrives in the mailbox
                block();
            }
        }
    };

}
