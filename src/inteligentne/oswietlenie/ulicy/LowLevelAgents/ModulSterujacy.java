package inteligentne.oswietlenie.ulicy.LowLevelAgents;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import inteligentne.oswietlenie.ulicy.Konfiguracja;
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

public class ModulSterujacy extends Agent{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
	public static final String nazwa = "Modul Sterujacy";
	
    private Map<String, Double> wspolczynnikAnalizyParametrow;
    private Map<String, Double> wspolczynnikAnalizyRuchu;
    private Map<String, Integer> mapaJasnosci;
    private Map<String, AID> mapaLatarnii;

    @Override
    public void setup(){
    	zarejestrujUsluge();
    	mapaJasnosci = new HashMap<>();
    	wyszukajLatarnie();
        addBehaviour(new PobieranieParametrow());
        wspolczynnikAnalizyParametrow = new HashMap<>();
        wspolczynnikAnalizyRuchu = new HashMap<>();
        addBehaviour(new SterowanieLatarniami(this, Konfiguracja.czasOdswiezaniaWMilisekundach));

    }
    
	private void analizaDanych() {
		int min1=200;
		int min2=200;
		
		for(Entry<String, AID> entry: mapaLatarnii.entrySet()){
			Double parametr = wspolczynnikAnalizyParametrow.get(entry.getKey());
			Double wspRuchu = wspolczynnikAnalizyRuchu.get(entry.getKey());
			int wynik;
			if(null == parametr || null == wspRuchu){
				wynik = 0;
			}else{
				wynik = (int) ((wspRuchu + 100) * parametr/500);
				if(wynik < min1 && wynik > 0){
					min1 = wynik;
				}
				else if(wynik < min2 && wynik > 0){
					min2 = wynik;
				}
			}
			mapaJasnosci.put(entry.getKey(), new Integer(wynik));
		}
		for(Entry<String, Integer> entry: mapaJasnosci.entrySet()){
			if(min2 == entry.getValue().intValue()){
				mapaJasnosci.put(entry.getKey(), new Integer((int) (min2 * 0.7)));
			}else if(entry.getValue().intValue() != min1){
				mapaJasnosci.put(entry.getKey(), new Integer(0));
			}
		}

	}
    

    private void wyszukajLatarnie() {
		mapaLatarnii = new HashMap<>();
		
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Latarnia");
        template.addServices(sd);
        try
        {
          DFAgentDescription[] result = DFService.search(this, template);
          if(result.length == 0)throw new Exception("Nie znaleziono latarni");
          
          for(DFAgentDescription agent: result){
        	  mapaLatarnii.put(agent.getName().getLocalName().replace(Konfiguracja.przedrostekNazwyLatarni, ""), agent.getName());
          }
          
        }catch (Exception e) {
        	e.printStackTrace(System.out);
        }
		
	}

    private void zarejestrujUsluge() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(ModulSterujacy.nazwa);
        sd.setType(ModulSterujacy.nazwa);
        dfd.addServices(sd);
        try {
          DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
        	fe.printStackTrace(System.out);
        }
    }

	private class PobieranieParametrow extends CyclicBehaviour{

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
            	String nadawca = msg.getSender().getLocalName();
				try {
					Map<String, Double> mapaWspolczynnikow = (Map<String, Double>) msg.getContentObject();
					
					if(nadawca.equals(Konfiguracja.nazwaModuluAnalizyParametrow)){
						wspolczynnikAnalizyParametrow = mapaWspolczynnikow;
					}else if(nadawca.equals(Konfiguracja.nazwaModuluAnalizyRuchu)){
						wspolczynnikAnalizyRuchu = mapaWspolczynnikow;
					}
					else{
						throw new Exception("Nieznany nadawca wiadomo�ci:" + nadawca);
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
    
    private class SterowanieLatarniami extends TickerBehaviour{

		/**
         *
         */
        private static final long serialVersionUID = 1L;

		public SterowanieLatarniami(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			try{
				analizaDanych();
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
			for(Entry<String, AID> entry: mapaLatarnii.entrySet()){
				try{
	              ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
	              wiadomosc.addReceiver(entry.getValue());
	              wiadomosc.setContentObject(mapaJasnosci.get(entry.getKey()));
	              myAgent.send(wiadomosc);
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}	
		}	
    }
}