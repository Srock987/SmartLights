package inteligentne.oswietlenie.ulicy.EventAgents;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import inteligentne.oswietlenie.ulicy.Konfiguracja;
import inteligentne.oswietlenie.ulicy.LowLevelAgents.ModulSterujacy;
import inteligentne.oswietlenie.ulicy.Utils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ModulAnalizyRuchu extends Agent{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String nazwa = "Modul Analizy Ruchu";
	
    private Map<String, HashMap<String, Double>> mapaOdleglosciObiektow;
    private Map<String, HashMap<String, Double>> mapaPredkosciObiektow;
    private HashMap<String, Double> mapaWspolczynnikow;
	private AID modulSterujacy;

    @Override
    public void setup(){
        zarejestrujUsluge();
        znajdzModulSterujacy();
        mapaWspolczynnikow = new HashMap<>();
        mapaOdleglosciObiektow = new HashMap<>();
        mapaPredkosciObiektow = new HashMap<>();
        addBehaviour(new PobieranieDanych());
        addBehaviour(new UdostepnianieDanych(this, Konfiguracja.czasOdswiezaniaWMilisekundach));
    }

    private void zarejestrujUsluge() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(ModulAnalizyRuchu.nazwa);
        sd.setType(ModulAnalizyRuchu.nazwa);
        dfd.addServices(sd);
        try {
          DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
          fe.printStackTrace(System.out);
        }
    }
    
    private void znajdzModulSterujacy() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(ModulSterujacy.nazwa);
        template.addServices(sd);
        try
        {
          DFAgentDescription[] result = DFService.search(this, template);
          if(result.length == 0)throw new Exception("Nie znaleziono modu�u steruj�cego");
          
          modulSterujacy = result[0].getName();
        }
        catch (Exception e) {
          e.printStackTrace(System.out);
        }
    }
    
    private void analizaDanych() {
    	Set<String> nazwyLatarni = new HashSet<String>();
    	nazwyLatarni.addAll(mapaOdleglosciObiektow.keySet());
    	nazwyLatarni.addAll(mapaPredkosciObiektow.keySet());
    	
        for(String latarnia: nazwyLatarni){
        	Double pieszy = Utils.getValueOrNull(mapaOdleglosciObiektow.get(latarnia), "1");
        	Double pojazd = Utils.getValueOrNull(mapaPredkosciObiektow.get(latarnia), "1");
        	Double odleglosc = Utils.min(pieszy, pojazd);
        	mapaWspolczynnikow.put(latarnia, odleglosc);
        }
    }

	private class PobieranieDanych extends CyclicBehaviour{

		/**
         *
         */
        private static final long serialVersionUID = 1L;

		@Override
		public void action() {
           odebranieDanych();
		}

        @SuppressWarnings("unchecked")
        private void odebranieDanych() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
				try {
					HashMap<String, Double> mapa = (HashMap<String, Double>) msg.getContentObject();
					String nadawca = msg.getSender().getLocalName();
					String numer;
					
					if(Pattern.matches(Konfiguracja.przedrostekAgentaCzujnikaRuchu + ".*", nadawca)){
						numer = nadawca.replace(Konfiguracja.przedrostekAgentaCzujnikaRuchu, "");
						mapaOdleglosciObiektow.put(numer, mapa);
					}else if(Pattern.matches(Konfiguracja.przedrostekAgentaCzujnikaPredkosci + ".*", nadawca)){
						numer = nadawca.replace(Konfiguracja.przedrostekAgentaCzujnikaPredkosci, "");
						mapaPredkosciObiektow.put(numer, mapa);
					}
					else{
						throw new Exception("Nieznany nadawca wiadomo�ci:" + msg.getSender().getName());
					}
					
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            else {
              block();
            }
        }
    }    
    
    
    private class UdostepnianieDanych extends TickerBehaviour{

        public UdostepnianieDanych(Agent a, long period) {
			super(a, period);
		}

		/**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void onTick() {
            try{
            	analizaDanych();
            }catch(Exception e){
            	e.printStackTrace(System.out);
            }
            
            if (modulSterujacy != null) {
			    ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
			    wiadomosc.setPerformative(ACLMessage.INFORM);
			    wiadomosc.addReceiver(modulSterujacy);
			    try {
				    wiadomosc.setContentObject(mapaWspolczynnikow);
			        myAgent.send(wiadomosc);
			    }catch (IOException e) {
			        e.printStackTrace(System.out);
			    }
            }
        }
    }
}