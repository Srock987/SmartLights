package inteligentne.oswietlenie.ulicy.LowLevelAgents;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import inteligentne.oswietlenie.ulicy.Konfiguracja;
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

public class ModulSterujacy extends Agent {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String nazwa = "Modul Sterujacy";

    private Map<String, Double> wspolczynnikAnalizyParametrow;
    private HashMap<String, HashMap<String, Double>> wspolczynnikAnalizyRuchu;
    private Map<String, Integer> mapaJasnosci;
    private Map<String, AID> mapaLatarnii;

    @Override
    public void setup() {
        zarejestrujUsluge();
        mapaJasnosci = new HashMap<>();
        wyszukajLatarnie();
        addBehaviour(new PobieranieParametrow());
        wspolczynnikAnalizyParametrow = new HashMap<>();
        wspolczynnikAnalizyRuchu = new HashMap<>();
        addBehaviour(new SterowanieLatarniami(this, Konfiguracja.czasOdswiezaniaWMilisekundach));

    }

    private void analizaDanych() {
        Double nextMax = 0.0;
        Double nextMax2 = 0.0;
        Double previousMax = 0.0;
        String keyNextMax = null;
        String keyNextMax2 = null;
        String keyPreviousMax = null;
        for (Entry<String, AID> entry : mapaLatarnii.entrySet()) {
            Double parametr = wspolczynnikAnalizyParametrow.get(entry.getKey());
            Double distance = Utils.getDoubleOrNull(wspolczynnikAnalizyRuchu, entry.getKey(), "1");
            Double speed = Utils.getDoubleOrNull(wspolczynnikAnalizyRuchu, entry.getKey(), "2");
            Double wynik;

            if (null == parametr || null == distance) {
                wynik = 0.0;
            } else {
                wynik = ((((500 - distance)) * parametr) / 500);
                if (speed > 0) {
                    if (wynik > nextMax && wynik > 0) {
                        nextMax = wynik;
                        keyNextMax = entry.getKey();
                    } else if (wynik > nextMax2 && wynik > 0) {
                        nextMax2 = wynik;
                        keyNextMax2 = entry.getKey();
                    }
                } else if (speed < 0) {
                    if (wynik > previousMax && wynik > 0 && speed < 0) {
                        previousMax = wynik;
                        keyPreviousMax = entry.getKey();
                    }
                }
                mapaJasnosci.put(entry.getKey(), new Integer(0));
            }


            if (keyNextMax != null) {
                nextMax = nextMax * 1.2d;
                mapaJasnosci.put(keyNextMax, nextMax.intValue());
            }
            if (keyNextMax2 != null) {
                nextMax2 = nextMax2 * 1.2d;
                mapaJasnosci.put(keyNextMax2, nextMax2.intValue());
            }

            if (keyPreviousMax != null) {
                previousMax = previousMax * 0.8d;
                mapaJasnosci.put(keyPreviousMax, previousMax.intValue());
            }
        }
    }


    private void wyszukajLatarnie() {
        mapaLatarnii = new HashMap<>();

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Latarnia");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length == 0) throw new Exception("Nie znaleziono latarni");

            for (DFAgentDescription agent : result) {
                mapaLatarnii.put(agent.getName().getLocalName().replace(Konfiguracja.przedrostekNazwyLatarni, ""), agent.getName());
            }

        } catch (Exception e) {
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
        } catch (FIPAException fe) {
            fe.printStackTrace(System.out);
        }
    }

    private class PobieranieParametrow extends CyclicBehaviour {

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

                    if (nadawca.equals(Konfiguracja.nazwaModuluAnalizyParametrow)) {
                        HashMap<String, Double> mapaWspolczynnikow = (HashMap<String, Double>) msg.getContentObject();
                        wspolczynnikAnalizyParametrow = mapaWspolczynnikow;
                    } else if (nadawca.equals(Konfiguracja.nazwaModuluAnalizyRuchu)) {
                        HashMap<String, HashMap<String, Double>> mapaWspolczynnikow = (HashMap<String, HashMap<String, Double>>) msg.getContentObject();
                        wspolczynnikAnalizyRuchu = mapaWspolczynnikow;
                    } else {
                        throw new Exception("Nieznany nadawca wiadomo�ci:" + nadawca);
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            } else {
                block();
            }
        }
    }

    private class SterowanieLatarniami extends TickerBehaviour {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public SterowanieLatarniami(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            try {
                analizaDanych();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            for (Entry<String, AID> entry : mapaLatarnii.entrySet()) {
                try {
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