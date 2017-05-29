package inteligentne.oswietlenie.ulicy.LowLevelAgents;

import inteligentne.oswietlenie.ulicy.AgentInterfejsu;
import inteligentne.oswietlenie.ulicy.Konfiguracja;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.pmw.tinylog.Logger;

public class Latarnia extends Agent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer intensywnoscSwiatla;
	private String numer;
	
	@Override
	protected void setup(){
		rejestracjaAgenta();
		setIntensywnoscSwiatla(new Integer(0));
		numer = getLocalName().replace(Konfiguracja.przedrostekNazwyLatarni, "");
		addBehaviour(new OdbieranieWiadomosci());
	}

	private void rejestracjaAgenta() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getLocalName());
        sd.setType("Latarnia");
        dfd.addServices(sd);
        try {
          DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
          fe.printStackTrace(System.out);
        }
		
	}

	public Integer getIntensywnoscSwiatla() {
		return intensywnoscSwiatla;
	}

	public void setIntensywnoscSwiatla(Integer intensywnoscSwiatla) {
		this.intensywnoscSwiatla = intensywnoscSwiatla;
	}

	private class OdbieranieWiadomosci extends CyclicBehaviour{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {

							try {
                	intensywnoscSwiatla = (Integer) msg.getContentObject();

                } catch (UnreadableException e) {
                    e.printStackTrace(System.out);
                }
            }
            else {
              block();
            }
            przekazIntensywnoscDoGUI();
		}

		private void przekazIntensywnoscDoGUI() {
            ACLMessage wiadomoscDoGUI = new ACLMessage(ACLMessage.INFORM);
            wiadomoscDoGUI.setContent("MOC_LATARNI@" + numer + "#" + intensywnoscSwiatla);
            wiadomoscDoGUI.addReceiver(AgentInterfejsu.agentInterfejsu.getAID());
            myAgent.send(wiadomoscDoGUI);			
		}
		
	}
}
