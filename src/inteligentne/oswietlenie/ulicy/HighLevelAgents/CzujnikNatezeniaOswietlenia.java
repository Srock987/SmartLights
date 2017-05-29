package inteligentne.oswietlenie.ulicy.HighLevelAgents;

import inteligentne.oswietlenie.ulicy.AgentInterfejsu;
import inteligentne.oswietlenie.ulicy.Konfiguracja;
import inteligentne.oswietlenie.ulicy.EventAgents.ModulAnalizyParametrow;
import inteligentne.oswietlenie.ulicy.SterownikCzujnikaNatezeniaOswietlenia;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("ALL")
public class CzujnikNatezeniaOswietlenia extends Agent {
	private static final long serialVersionUID = 1L;
	private AID modulAnalizyParametrow;
	private int natezenieOswietlenia;
	
	@Override
	protected void setup() {
		System.out.println("URUCHOMIONO AGENTA CZUJNIK NATEZENIA OSWIETLENIA");
		znajdzModulAnalizyParametrow();
		addBehaviour(new SterownikCzujnikaNatezeniaOswietlenia(this, Konfiguracja.czasOdswiezaniaWMilisekundach));
		addBehaviour(new UdostepnieniePomiaru(this, Konfiguracja.czasOdswiezaniaWMilisekundach));
	}
	
	public void ustawNatezenieOswietlenia(int natezenieOswietlenia){
		this.natezenieOswietlenia = natezenieOswietlenia;
	}
	
	private void znajdzModulAnalizyParametrow() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(ModulAnalizyParametrow.nazwa);
        template.addServices(sd);
        try
        {
          DFAgentDescription[] result = DFService.search(this, template);
          if(result.length == 0)
        	  throw new Exception("Nie znaleziono modulu analizy parametrow");
          
          modulAnalizyParametrow = result[0].getName();
          
        }
        catch (Exception e) {
          e.printStackTrace(System.out);
        }
    }

    public class UdostepnieniePomiaru extends TickerBehaviour {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UdostepnieniePomiaru(Agent a, long period) {
			super(a, period);
		}

        @Override
        public void onTick() {
        	if(null != modulAnalizyParametrow){
	            ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
	            wiadomosc.addReceiver(modulAnalizyParametrow);
    			try{
  	              wiadomosc.setContentObject(natezenieOswietlenia);
  	              myAgent.send(wiadomosc);
  				}catch (Exception e) {
  			        e.printStackTrace(System.out);
  				} 
        	}
        	else{
        		znajdzModulAnalizyParametrow();
        	}
            ACLMessage wiadomoscDoGUI = new ACLMessage(ACLMessage.INFORM);
            String numer = myAgent.getLocalName().replace(Konfiguracja.przedrostekNazwAgentowCzujnikNatezeniaSwiatla, "");

            wiadomoscDoGUI.setContent("NATEZENIE_OSWIETLENIA@" + numer + "#" + natezenieOswietlenia);
            wiadomoscDoGUI.addReceiver(AgentInterfejsu.agentInterfejsu.getAID());
            myAgent.send(wiadomoscDoGUI);	
        }
    }
}
