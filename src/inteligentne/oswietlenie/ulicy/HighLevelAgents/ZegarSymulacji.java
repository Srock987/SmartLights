package inteligentne.oswietlenie.ulicy.HighLevelAgents;

import inteligentne.oswietlenie.ulicy.Konfiguracja;
import inteligentne.oswietlenie.ulicy.PostepCzasuSymulacji;
import inteligentne.oswietlenie.ulicy.TimeTracker;
import inteligentne.oswietlenie.ulicy.WczytywanieKonfiguracji;
import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;
import java.util.Calendar;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;

public class ZegarSymulacji extends Agent {
  private static final long serialVersionUID = 1L;
  private Calendar aktualnyCzas;
  private int zmianaCzasuWSekundach;
  private ArrayList<AID> listaAgentowDoPoinformowania;
  public boolean zegarUruchomiony = false;
  public static ZegarSymulacji zegarSymulacji = null;
  private TimeTracker timeTracker = new TimeTracker();

  private void dodajAgentaInterfejsu() {
    AgentContainer kontener = getContainerController();

    try {
      AgentController kontroler = kontener.createNewAgent(Konfiguracja.nazwaAgentaInterfejsu,
          Konfiguracja.pelnaNazwaAgentaInterfejsu, null);
      kontroler.start();
    } catch (StaleProxyException e) {
      e.printStackTrace();
    }

    AID odbiorca = new AID(Konfiguracja.nazwaAgentaInterfejsu, AID.ISLOCALNAME);
    listaAgentowDoPoinformowania.add(odbiorca);
  }

  private void dodajAgentowCzujnikowNatezeniaDoPoinformowania() {
    dodajAgentaInterfejsu();
    try {
      Thread.sleep((long) (Konfiguracja.czasOdswiezaniaWMilisekundach * 1.5));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    dodajLatarnie();
    dodajModuly();
    dodajCzujniki();

    //tu dodac do listy jakich agentow poinformowac przy kazdym wywolaniu
  }

  private void dodajLatarnie() {
    AgentContainer kontener = getContainerController();

    for (int i = 1; i <= WczytywanieKonfiguracji.lacznaIloscLatarni; ++i) {
      String nazwaAgenta = Konfiguracja.przedrostekNazwyLatarni + Integer.toString(i);
      try {
        AgentController kontroler =
            kontener.createNewAgent(nazwaAgenta, Konfiguracja.pelnaNazwaAgentaLatarni, null);
        kontroler.start();
      } catch (StaleProxyException e) {
        e.printStackTrace();
      }

      //			AID odbiorca = new AID(nazwaAgenta, AID.ISLOCALNAME);
      //			listaAgentowDoPoinformowania.add(odbiorca);

    }
  }

  private void dodajCzujniki() {
    AgentContainer kontener = getContainerController();

    for (int i = 1; i <= WczytywanieKonfiguracji.lacznaIloscLatarni; ++i) {

      dodajCzujnik(Konfiguracja.przedrostekAgentaCzujnikaPredkosci,
          Konfiguracja.pelnaNazwaKlasyAgentaCzujnikaPredkosci, i, kontener);
      dodajCzujnik(Konfiguracja.przedrostekAgentaCzujnikaRuchu,
          Konfiguracja.pelnaNazwaKlasyAgentaCzujnikaRuchu, i, kontener);
      dodajCzujnik(Konfiguracja.przedrostekNazwAgentowCzujnikNatezeniaSwiatla,
          Konfiguracja.pelnaNazwaKlasyAgentaCzujnikaNatezeniaSwiatla, i, kontener);
    }
  }

  private void dodajCzujnik(String przedrosteknazwyAgenta,
      String pelnaNazwaKlasyAgenta, int index, AgentContainer kontener) {
    String nazwaAgenta = przedrosteknazwyAgenta + Integer.toString(index);
    try {
      AgentController kontroler = kontener.createNewAgent(nazwaAgenta, pelnaNazwaKlasyAgenta, null);
      kontroler.start();
    } catch (StaleProxyException e) {
      e.printStackTrace();
    }
    if (pelnaNazwaKlasyAgenta.equals(Konfiguracja.pelnaNazwaKlasyAgentaCzujnikaNatezeniaSwiatla)) {
      AID odbiorca = new AID(nazwaAgenta, AID.ISLOCALNAME);
      listaAgentowDoPoinformowania.add(odbiorca);
    }
  }

  private void dodajModuly() {
    AgentContainer kontener = getContainerController();

    try {
      AgentController kontroler = kontener.createNewAgent(Konfiguracja.nazwaModuluSterujacego,
          Konfiguracja.pelnaNazwaModuluSterujacego, null);
      kontroler.start();

      kontroler = kontener.createNewAgent(Konfiguracja.nazwaModuluAnalizyParametrow,
          Konfiguracja.pelnaNazwaModuluAnalizyParametrow, null);
      kontroler.start();

      kontroler = kontener.createNewAgent(Konfiguracja.nazwaModuluAnalizyRuchu,
          Konfiguracja.pelnaNazwaModuluAnalizyRuchu, null);
      kontroler.start();
    } catch (StaleProxyException e) {
      e.printStackTrace();
    }

    //		AID modulSterujacy = new AID(Konfiguracja.nazwaModuluSterujacego, AID.ISLOCALNAME);
    //		listaAgentowDoPoinformowania.add(modulSterujacy);

    //		AID modulAnalizyParametrow = new AID(Konfiguracja.nazwaModuluAnalizyParametrow, AID.ISLOCALNAME);
    //		listaAgentowDoPoinformowania.add(modulAnalizyParametrow);

    //		AID modulAnalizyRuchu = new AID(Konfiguracja.nazwaModuluAnalizyRuchu, AID.ISLOCALNAME);
    //		listaAgentowDoPoinformowania.add(modulAnalizyRuchu);

  }

  @Override
  protected void setup() {
    Configurator.defaultConfig()
        .formatPattern(
            "{date:yyyy-MM-dd HH:mm:ss.SSS} [{thread}] {class}.{method}()\\n{level}: {message}")
        .activate();
    timeTracker.start();
    Logger.info("Start simulation initialization");

    System.out.println("URUCHOMIONO AGENTA ZEGAR SYMULACJI");
    zegarSymulacji = this;

    aktualnyCzas = Calendar.getInstance();
    zmianaCzasuWSekundach = Konfiguracja.iloscUplywajacychSekundPrzyOdswiezaniu;
    listaAgentowDoPoinformowania = new ArrayList<AID>();
    dodajAgentowCzujnikowNatezeniaDoPoinformowania();

    addBehaviour(new PostepCzasuSymulacji(this, Konfiguracja.czasOdswiezaniaWMilisekundach));
    Logger.info(
        "End of simulation initialization, time spend initialization = "
            + timeTracker.getInterval()
            + " ms");
  }

  public Calendar getAktualnyCzas() {
    return aktualnyCzas;
  }

  public void setAktualnyCzas(Calendar aktualnyCzas) {
    this.aktualnyCzas = aktualnyCzas;
  }

  public ArrayList<AID> getListaAgentowDoPoinformowania() {
    return listaAgentowDoPoinformowania;
  }

  public int getZmianaCzasuWSekundach() {
    return zmianaCzasuWSekundach;
  }

  public void setZmianaCzasuWSekundach(int zmianaCzasuWSekundach) {
    this.zmianaCzasuWSekundach = zmianaCzasuWSekundach;
  }
}
