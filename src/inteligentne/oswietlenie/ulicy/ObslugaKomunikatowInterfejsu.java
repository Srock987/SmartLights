package inteligentne.oswietlenie.ulicy;

import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.pmw.tinylog.Logger;

public class ObslugaKomunikatowInterfejsu extends TickerBehaviour {
  private static final long serialVersionUID = 1L;
  private AgentInterfejsu agentInterfejsu;
  private Calendar poprzedniCzas;
  private TimeTracker timeTracker = new TimeTracker();

  public ObslugaKomunikatowInterfejsu(AgentInterfejsu agentInterfejsu, long okres) {
    super(agentInterfejsu, okres);
    this.agentInterfejsu = agentInterfejsu;
    poprzedniCzas = Calendar.getInstance();
  }

  @Override
  protected void onTick() {
    while (true) {
      ACLMessage wiadomosc = agentInterfejsu.receive();

      if (wiadomosc != null) {
        timeTracker.start();
        String tekstWiadomosci = wiadomosc.getContent();

        if (tekstWiadomosci.startsWith("CZAS@")) {
          StrukturaCzas struktCzas = new StrukturaCzas(tekstWiadomosci);

          long obecnaLiczbaSekund = struktCzas.getCzas().getTime().getTime() / 1000;
          long poprzedniaLiczbaSekund = poprzedniCzas.getTime().getTime() / 1000;
          int roznicaCzasu = (int) (obecnaLiczbaSekund - poprzedniaLiczbaSekund);
          poprzedniCzas = struktCzas.getCzas();

          if (roznicaCzasu < 0) {
            roznicaCzasu = 1;
          }

          OknoGlowne.przemieszczanieObiektow.przerysujTrasy(OknoGlowne.graf,
              OknoGlowne.mapaPowiazanWierzcholkow, agentInterfejsu, roznicaCzasu);

          if (OknoGlowne.referencjaNaLAbelAktualnyCzass != null) {
            String dzienLubNoc;

            if (struktCzas.jestDzien) {
              dzienLubNoc = "DZIEN";
            } else {
              dzienLubNoc = "NOC";
            }

            SimpleDateFormat formatDaty = new SimpleDateFormat("dd.M.yyyy   HH:mm:ss");
            String czasNapis =
                formatDaty.format(struktCzas.getCzas().getTime()) + "   (" + dzienLubNoc + ")";
            OknoGlowne.referencjaNaLAbelAktualnyCzass.setText(czasNapis);
            OknoGlowne.aktualnyCzas = struktCzas.getCzas();
          }
        } else if (tekstWiadomosci.startsWith("DODAJ_AKTYWNA_TRASE@")) {
          String[] rozdzielone = tekstWiadomosci.split("@");

          if (rozdzielone.length == 2) {
            String nazwaTrasy = rozdzielone[1];
            System.out.println("ON: " + nazwaTrasy);
            OknoGlowne.przemieszczanieObiektow.dadajAktywnaTrase(nazwaTrasy,
                OknoGlowne.mapaPowiazanWierzcholkow);
          }
          Logger.info("Change simulation option finished");
        } else {
          if (tekstWiadomosci.startsWith("ZATRZYMAJ_AKTYWNA_TRASE@")) {

            String[] rozdzielone = tekstWiadomosci.split("@");

            if (rozdzielone.length == 2) {
              String nazwaTrasy = rozdzielone[1];
              System.out.println("OFF: " + nazwaTrasy);
              OknoGlowne.przemieszczanieObiektow.usunAktywnaTrase(nazwaTrasy);
            }
            Logger.info("Change simulation option finished");
          } else {
            if (tekstWiadomosci.startsWith("MOC_LATARNI@")) {
              String[] rozdzielone = tekstWiadomosci.split("@");

              if (rozdzielone.length == 2) {
                String[] nrLatarniIMoc = rozdzielone[1].split("#");

                if (nrLatarniIMoc.length == 2) {
                  int nrLatarni = Integer.parseInt(nrLatarniIMoc[0]);
                  int mocLatarni = Integer.parseInt(nrLatarniIMoc[1]);

                  Wierzcholek wierzcholek = OknoGlowne.mapaPowiazanWierzcholkow.get(
                      Konfiguracja.przedrostekNazwyLatarni + nrLatarni);
                  wierzcholek.mocLatarni = mocLatarni;
                }
              }
            } else if (tekstWiadomosci.startsWith("NATEZENIE_OSWIETLENIA@")) {
              String[] rozdzielone = tekstWiadomosci.split("@");
              if (rozdzielone.length == 2) {
                String[] nrLatarniINatezenie = rozdzielone[1].split("#");

                if (nrLatarniINatezenie.length == 2) {
                  int nrLatarni = Integer.parseInt(nrLatarniINatezenie[0]);
                  int natezenieSwiatla = Integer.parseInt(nrLatarniINatezenie[1]);
                  //System.out.println(nrLatarni);
                  try {
                    Wierzcholek wierzcholek = OknoGlowne.mapaPowiazanWierzcholkow.get(
                        Konfiguracja.przedrostekNazwyLatarni + nrLatarni);
                    wierzcholek.natezenieSwiatla = natezenieSwiatla;
                  } catch (Exception e) {
                    e.printStackTrace(System.out);
                  }
                }
              }
            }
          }
        }
      } else {
        break;
      }
    }
  }
}
