package inteligentne.oswietlenie.ulicy.HighLevelAgents;


import java.util.HashMap;
import java.util.Map;

import inteligentne.oswietlenie.ulicy.Konfiguracja;
import inteligentne.oswietlenie.ulicy.SterownikCzujnikaPredkosci;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class CzujnikPredkosci extends Agent{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private HashMap<String, Double> odleglosciObiektow;
    private HashMap<String, Double> predkosciOkiektow;
    private HashMap<String, Double> parametryObiektow;
	private AID modulAnalizyRuchu;

    @Override
    protected void setup(){
        znajdzModulAnalizyRuchu();
        odleglosciObiektow = new HashMap<>();
        predkosciOkiektow = new HashMap<>();
        parametryObiektow = new HashMap<>();
        addBehaviour(new SterownikCzujnikaPredkosci(this));
        addBehaviour(new UdostepnieniePomiaru(this, Konfiguracja.czasOdswiezaniaWMilisekundach));
    }

    public Map<String, Double> pobierzOdleglosci() {
        return odleglosciObiektow;
    }
    
	public void aktualizujOdleglosciObiektow(HashMap<String, Double> odleglosciObiektow) {
        obliczPredkosciObiektow(odleglosciObiektow);
		this.odleglosciObiektow = odleglosciObiektow;
		parametryObiektow.put("1",this.odleglosciObiektow.get("1"));
		parametryObiektow.put("2",predkosciOkiektow.get("2"));
	}

	private void obliczPredkosciObiektow(HashMap<String, Double> noweOdleglosciObiektow){
        if (odleglosciObiektow.size()!=0) {
            predkosciOkiektow.put("2", odleglosciObiektow.get("1") - noweOdleglosciObiektow.get("1"));
        }
    }

    private void znajdzModulAnalizyRuchu() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Modul Analizy Ruchu");
        template.addServices(sd);
        try
        {
          DFAgentDescription[] result = DFService.search(this, template);
          if(result.length == 0)throw new Exception("Nie znaleziono modu�u analizy ruchu");
          
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
		            wiadomosc.setContentObject(parametryObiektow);
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