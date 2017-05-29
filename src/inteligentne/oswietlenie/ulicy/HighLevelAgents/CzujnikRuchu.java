package inteligentne.oswietlenie.ulicy.HighLevelAgents;

import java.util.HashMap;
import java.util.Map;

import inteligentne.oswietlenie.ulicy.Konfiguracja;
import inteligentne.oswietlenie.ulicy.SterownikCzujnikaRuchu;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class CzujnikRuchu extends Agent{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private HashMap<String, Double> odleglosciObiektow;
	private AID modulAnalizyRuchu;

    @Override
    protected void setup(){
    	znajdzModulAnalizyRuchu();
    	odleglosciObiektow = new HashMap<>();
        addBehaviour(new SterownikCzujnikaRuchu(this));
        addBehaviour(new UdostepnieniePomiaru(this, Konfiguracja.czasOdswiezaniaWMilisekundach));
    }

    public Map<String, Double> pobierzOdleglosci() {
        return odleglosciObiektow;
    }
    
	public void aktualizujOdleglosciObiektow(HashMap<String, Double> odleglosciObiektow) {
		this.odleglosciObiektow = odleglosciObiektow;
		
	}

    private void znajdzModulAnalizyRuchu() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Modul Analizy Ruchu");
        template.addServices(sd);
        try
        {
          DFAgentDescription[] result = DFService.search(this, template);
          if(result.length == 0)
        	  throw new Exception("Nie znaleziono modu�u analizy ruchu");
          
          modulAnalizyRuchu = result[0].getName();
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
        	if(null != modulAnalizyRuchu){
	            ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
	            wiadomosc.addReceiver(modulAnalizyRuchu);
        		try{
    	            wiadomosc.setContentObject(odleglosciObiektow);
    	            myAgent.send(wiadomosc);
    			} catch (Exception e) {
    				e.printStackTrace(System.out);
    			} 
        	}
        	else{
        		znajdzModulAnalizyRuchu();
        	}
        }
    }
}