package inteligentne.oswietlenie.ulicy;

import inteligentne.oswietlenie.ulicy.HighLevelAgents.CzujnikRuchu;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.HashMap;
import org.pmw.tinylog.Logger;

public class SterownikCzujnikaRuchu extends CyclicBehaviour {

    private CzujnikRuchu czujnik;
    private static final long serialVersionUID = 1L;
    private static final int maxZasiegCzujnika = 500;

    public SterownikCzujnikaRuchu(CzujnikRuchu czujnik) {
        this.czujnik = czujnik;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = czujnik.receive(mt);
        if (msg != null) {
            Logger.info(msg.getContent());

            Double odleglosc =
                Double.parseDouble(msg.getContent().replace("ODLEGLOSC_DO_LATARNI@", ""));
            HashMap<String, Double> odleglosciObiektow = new HashMap<>();

            if (odleglosc < maxZasiegCzujnika) {
                odleglosciObiektow.put("1", odleglosc);
            }
            System.out.println(myAgent.getLocalName() + ":" + odleglosc);
            czujnik.aktualizujOdleglosciObiektow(odleglosciObiektow);
        } else {
            block();
        }
    }

}