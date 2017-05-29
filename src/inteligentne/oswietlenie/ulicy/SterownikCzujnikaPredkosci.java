package inteligentne.oswietlenie.ulicy;

import inteligentne.oswietlenie.ulicy.HighLevelAgents.CzujnikPredkosci;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.HashMap;
import org.pmw.tinylog.Logger;

public class SterownikCzujnikaPredkosci extends CyclicBehaviour {

    private CzujnikPredkosci czujnik;
    private static final long serialVersionUID = 1L;
	private static final int maxZasiegCzujnika = 500;

    public SterownikCzujnikaPredkosci(CzujnikPredkosci czujnik) {
        this.czujnik = czujnik;
    }

    @Override
	public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = czujnik.receive(mt);
        if (msg != null) {

          Double odleglosc = Double.parseDouble(msg.getContent().replace("ODLEGLOSC_DO_POJAZDU@", ""));

    	  HashMap<String, Double> predkosciObiektow = new HashMap<>();
          if (odleglosc < maxZasiegCzujnika) {
            Logger.info(msg.getContent());

            predkosciObiektow.put("1", odleglosc);
          }
          czujnik.aktualizujOdleglosciObiektow(predkosciObiektow);

        }
        else {
          block();
        }
    }

}