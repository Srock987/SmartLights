package inteligentne.oswietlenie.ulicy;

import inteligentne.oswietlenie.ulicy.HighLevelAgents.CzujnikNatezeniaOswietlenia;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SterownikCzujnikaNatezeniaOswietlenia extends TickerBehaviour {
    private static final long serialVersionUID = 1L;
    private CzujnikNatezeniaOswietlenia czujnikNatezeniaOswietlenia;

    public SterownikCzujnikaNatezeniaOswietlenia(CzujnikNatezeniaOswietlenia czujnikNatezeniaOswietlenia, long okres) {
        super(czujnikNatezeniaOswietlenia, okres);
        this.czujnikNatezeniaOswietlenia = czujnikNatezeniaOswietlenia;
    }

    private void poinformujOdbiorcow() {
        //
        //TO DO
        //
    }

    @Override
    protected void onTick() {
        ACLMessage wiadomosc = czujnikNatezeniaOswietlenia.receive();

        if (wiadomosc != null) {
            String strCzas = wiadomosc.getContent();
            StrukturaCzas struktCzas = new StrukturaCzas(strCzas);

            Calendar currentDate = struktCzas.getCzas();
            Calendar sunUpDate = Calendar.getInstance();
            sunUpDate.setTime(ZegarAstronomiczny.godzinaWschodu(struktCzas.getCzas()));
            Calendar sunDownDate = Calendar.getInstance();
            sunDownDate.setTime(ZegarAstronomiczny.godzinaZachodu(struktCzas.getCzas()));

            float currentMinute = currentDate.get(Calendar.HOUR_OF_DAY) * 60 + currentDate.get(Calendar.MINUTE);
            float sunUp = sunUpDate.get(Calendar.HOUR_OF_DAY) * 60 + sunUpDate.get(Calendar.MINUTE);
            float sunDown = sunDownDate.get(Calendar.HOUR_OF_DAY) * 60 + sunDownDate.get(Calendar.MINUTE);

            float wspolczynnikSwiatla;

            if (Math.abs(currentMinute - sunDown) < 45) {
                wspolczynnikSwiatla = (45 + (sunDown - currentMinute)) / 90;
            } else if (Math.abs(currentMinute - sunUp) < 45)
                wspolczynnikSwiatla = (45 + (currentMinute - sunUp)) / 90;
            else
                wspolczynnikSwiatla = -1;

            if (wspolczynnikSwiatla != -1)
                czujnikNatezeniaOswietlenia.ustawNatezenieOswietlenia((int)
                        Math.round((1 - OknoGlowne.procentZachmurzenia) * Konfiguracja.zakresNatezeniaSwiatla * wspolczynnikSwiatla));
            else if (struktCzas.jestDzien) {

                    czujnikNatezeniaOswietlenia.ustawNatezenieOswietlenia((int)
                            Math.round((1 - OknoGlowne.procentZachmurzenia) * Konfiguracja.zakresNatezeniaSwiatla));
            } else {
                    czujnikNatezeniaOswietlenia.ustawNatezenieOswietlenia(0);
            }

            poinformujOdbiorcow();
        }
    }
}
