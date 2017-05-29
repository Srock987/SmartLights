package inteligentne.oswietlenie.ulicy.EventAgents;

import inteligentne.oswietlenie.ulicy.Konfiguracja;
import inteligentne.oswietlenie.ulicy.LowLevelAgents.ModulSterujacy;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ModulAnalizyParametrow extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int progZalaczeniaSwiatla = 40;

	public static final String nazwa = "Modul Analizy Paramterow";
	
    private Map<String, Integer> mapaNatezeniaSwiatla;
    private HashMap<String, Double> mapaWspolczynnikow;
    
    private AID modulSterujacy;

    @Override
    public void setup(){
        zarejestrujUsluge();
    	mapaNatezeniaSwiatla = new HashMap<String, Integer>();
        addBehaviour(new PobieranieDanych());
        znajdzModulSterujacy();
        mapaWspolczynnikow = new HashMap<>();
        addBehaviour(new UdostepnianieDanych(this, Konfiguracja.czasOdswiezaniaWMilisekundach));

    }

    private void analizaDanych() {
    	double wspolczynnik;
        for(Entry<String, Integer> entry: mapaNatezeniaSwiatla.entrySet()){
        	if(entry.getValue() > progZalaczeniaSwiatla)
        		wspolczynnik = 0;
        	else
        		wspolczynnik = (progZalaczeniaSwiatla - entry.getValue())*100/40;
        	
    		mapaWspolczynnikow.put(entry.getKey(), new Double(wspolczynnik));
        }
    }
    
    private void zarejestrujUsluge() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(ModulAnalizyParametrow.nazwa);
        sd.setType(ModulAnalizyParametrow.nazwa);
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

    private class PobieranieDanych extends CyclicBehaviour{

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            pobieranieNatezeniaSwiatla();
        }

        private void pobieranieNatezeniaSwiatla() {
            try {
                odebranieDanych();
            } catch (Exception e) {
            	e.printStackTrace(System.out);
            }
        }

        private void odebranieDanych() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                  try {

                    String numer = msg.getSender().getLocalName().replace(Konfiguracja.przedrostekNazwAgentowCzujnikNatezeniaSwiatla, "");
                	Integer wartoscNatezeniaSwiatla = (Integer) msg.getContentObject();
                    mapaNatezeniaSwiatla.put(numer, wartoscNatezeniaSwiatla);
                    
                } catch (UnreadableException e) {
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
            if (modulSterujacy != null) {
	            try{
	            	analizaDanych();
	            }catch(Exception e){
	            	e.printStackTrace(System.out);
	            }
	            
	            ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
	            wiadomosc.setPerformative(ACLMessage.INFORM);
	            wiadomosc.addReceiver(modulSterujacy);
	
	            try {
					wiadomosc.setContentObject(mapaWspolczynnikow);
					myAgent.send(wiadomosc);
				} catch (IOException e) {
					e.printStackTrace(System.out);
				}
	        } else{
	        	znajdzModulSterujacy();
	        }       
        }
    }
}