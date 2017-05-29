package inteligentne.oswietlenie.ulicy;

import com.mxgraph.view.mxGraph;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class PrzemieszczanieObiektow {
    private ArrayList<Trasa> listaTras = new ArrayList<Trasa>();
    private HashSet<String> listaAktywnychTras = new HashSet<String>();

    private double obliczOdleglosc(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public void dodajTrase(Trasa trasa) {
        listaTras.add(trasa);
    }

    public void dadajAktywnaTrase(String nazwaTrasy, HashMap<String, Wierzcholek> mapaPowiazanWierzcholkow) {
        listaAktywnychTras.add(nazwaTrasy);
        Trasa trasa = null;

        for (int i = 0; i < listaTras.size(); ++i) {
            if (listaTras.get(i).nazwaTrasy.equals(nazwaTrasy)) {
                trasa = listaTras.get(i);
                break;
            }
        }

        if (trasa.listaWezlow.size() > 0) {
            trasa.x = mapaPowiazanWierzcholkow.get(trasa.listaWezlow.get(0)).x;
            trasa.y = mapaPowiazanWierzcholkow.get(trasa.listaWezlow.get(0)).y;
            trasa.nrWierzcholkaDoktoregoDazy = 1;
            trasa.wierzcholekDoKtoregoDazy = mapaPowiazanWierzcholkow.get(trasa.listaWezlow.get(trasa.nrWierzcholkaDoktoregoDazy));
            trasa.deltaX = trasa.wierzcholekDoKtoregoDazy.x - trasa.x;
            trasa.deltaY = trasa.wierzcholekDoKtoregoDazy.y - trasa.y;
            trasa.calkowityDystansMiedzyWierzcholkami = obliczOdleglosc(trasa.x, trasa.y, trasa.wierzcholekDoKtoregoDazy.x, trasa.wierzcholekDoKtoregoDazy.y);
            trasa.poprzedniDystansMiedzyWierzcholkami = trasa.calkowityDystansMiedzyWierzcholkami + 1;
        }

        trasa.usunac = false;

        Wierzcholek nowyWierzcholek = new Wierzcholek();
        nowyWierzcholek.x = (int) Math.round(trasa.x);
        nowyWierzcholek.y = (int) Math.round(trasa.y);
        mapaPowiazanWierzcholkow.put(trasa.nazwaEtykiety, nowyWierzcholek);
    }

    public void usunAktywnaTrase(String nazwaTrasy) {
        for (int i = 0; i < listaTras.size(); ++i) {
            if (listaTras.get(i).nazwaTrasy.equals(nazwaTrasy)) {
                listaTras.get(i).usunac = true;
                break;
            }
        }
    }

    public void przerysujTrasy(mxGraph graf, HashMap<String, Wierzcholek> mapaPowiazanWierzcholkow, AgentInterfejsu agentInterfejsu, int iloscSekund) {
        graf.getModel().beginUpdate();

        for (int i = 0; i < listaTras.size(); ++i) {
            if (listaAktywnychTras.contains(listaTras.get(i).nazwaTrasy)) {
                odrysujPojedynczaTrase(listaTras.get(i), graf, mapaPowiazanWierzcholkow, agentInterfejsu, iloscSekund);
            }
        }

        odrysujLatarnie(graf);
        graf.getModel().endUpdate();
    }

    private void odrysujPojedynczaTrase(Trasa trasa, mxGraph graf, HashMap<String, Wierzcholek> mapaPowiazanWierzcholkow, AgentInterfejsu agentInterfejsu, int iloscSekund) {
        Wierzcholek przemieszczajacy = mapaPowiazanWierzcholkow.get(trasa.nazwaEtykiety);

        if (przemieszczajacy.obiekt != null) {
            graf.removeCells(new Object[]{przemieszczajacy.obiekt});
        }

        if (trasa.usunac) {
            listaAktywnychTras.remove(trasa.nazwaTrasy);
        } else {
            if (trasa.listaWezlow.size() > 0) {
                if ((obliczOdleglosc(trasa.x, trasa.y, trasa.wierzcholekDoKtoregoDazy.x, trasa.wierzcholekDoKtoregoDazy.y) <= 1)
                        || (obliczOdleglosc(trasa.x, trasa.y, trasa.wierzcholekDoKtoregoDazy.x, trasa.wierzcholekDoKtoregoDazy.y) > trasa.poprzedniDystansMiedzyWierzcholkami)) {
                    ++trasa.nrWierzcholkaDoktoregoDazy;

                    if (trasa.nrWierzcholkaDoktoregoDazy >= trasa.listaWezlow.size()) {
                        trasa.nrWierzcholkaDoktoregoDazy = 0;
                    }

                    trasa.x = trasa.wierzcholekDoKtoregoDazy.x;
                    trasa.y = trasa.wierzcholekDoKtoregoDazy.y;
                    trasa.wierzcholekDoKtoregoDazy = mapaPowiazanWierzcholkow.get(trasa.listaWezlow.get(trasa.nrWierzcholkaDoktoregoDazy));
                    trasa.deltaX = trasa.wierzcholekDoKtoregoDazy.x - trasa.x;
                    trasa.deltaY = trasa.wierzcholekDoKtoregoDazy.y - trasa.y;
                    trasa.calkowityDystansMiedzyWierzcholkami = obliczOdleglosc(trasa.x, trasa.y, trasa.wierzcholekDoKtoregoDazy.x, trasa.wierzcholekDoKtoregoDazy.y);
                    trasa.poprzedniDystansMiedzyWierzcholkami = trasa.calkowityDystansMiedzyWierzcholkami + 1;
                } else {
                    trasa.poprzedniDystansMiedzyWierzcholkami = obliczOdleglosc(trasa.x, trasa.y, trasa.wierzcholekDoKtoregoDazy.x, trasa.wierzcholekDoKtoregoDazy.y);
                    double pokonanyDystans = trasa.predkosc * iloscSekund;
                    double procentPokonanejTrasy = pokonanyDystans / trasa.calkowityDystansMiedzyWierzcholkami;
                    double dx = procentPokonanejTrasy * trasa.deltaX;
                    double dy = procentPokonanejTrasy * trasa.deltaY;
                    trasa.x += dx;
                    trasa.y += dy;
                }

                poinformujCzujnikiOOdleglosciOdPojazdu(trasa, mapaPowiazanWierzcholkow, agentInterfejsu);
            } else {
                poinformujCzujnikRuchu(trasa, mapaPowiazanWierzcholkow, agentInterfejsu);
            }

            Wierzcholek nowyWierzcholek = new Wierzcholek();
            nowyWierzcholek.obiekt = graf.insertVertex(graf.getDefaultParent(), trasa.nazwaEtykiety, trasa.nazwaEtykiety, trasa.x + (Konfiguracja.wymiaryLatarni / 4), trasa.y + (Konfiguracja.wymiaryLatarni / 4), Konfiguracja.wymiaryPrzemieszczajacegoSieObiektu, Konfiguracja.wymiaryPrzemieszczajacegoSieObiektu, "endArrow=none");
            nowyWierzcholek.x = (int) Math.round(trasa.x);
            nowyWierzcholek.y = (int) Math.round(trasa.y);

            mapaPowiazanWierzcholkow.put(trasa.nazwaEtykiety, nowyWierzcholek);
        }
    }

    private void odrysujLatarnie(mxGraph graf) {
        Object wezelNadrzedny = graf.getDefaultParent();
        for (Entry<String, Wierzcholek> para : OknoGlowne.mapaPowiazanWierzcholkow.entrySet()) {
            String nazwaLatarni = para.getKey();
            Wierzcholek wierzcholek = para.getValue();
            if (wierzcholek.jestLatarnia) {
                graf.removeCells(new Object[]{wierzcholek.obiekt});
                wierzcholek.obiekt = graf.insertVertex(wezelNadrzedny, wierzcholek.nazwaWierzcholka, ObslugaTresciWierzcholkow.zwrocTrescLatari(nazwaLatarni, wierzcholek.natezenieSwiatla, wierzcholek.mocLatarni), wierzcholek.x, wierzcholek.y, Konfiguracja.wymiaryLatarni, Konfiguracja.wymiaryLatarni, "");
                OknoGlowne.mapaPowiazanWierzcholkow.put(nazwaLatarni, wierzcholek);
            }
        }
        ParsowanieStrukturyGrafu.parsujKrawedzie(WczytywanieKonfiguracji.listaKrawedzi, graf);
    }

    private void poinformujCzujnikiOOdleglosciOdPojazdu(Trasa trasa, HashMap<String, Wierzcholek> mapaPowiazanWierzcholkow, AgentInterfejsu agentInterfejsu) {
        int nrLatarniPrzedPojazdem = trasa.nrWierzcholkaDoktoregoDazy;
        int nrLatarniZaPojazdem = trasa.nrWierzcholkaDoktoregoDazy - 1;
        if (nrLatarniZaPojazdem < 0) {
            nrLatarniZaPojazdem = trasa.listaWezlow.size() - 1;
        }
        double rzeczywisteXPojazdu = trasa.x + (Konfiguracja.wymiaryPrzemieszczajacegoSieObiektu / 2);
        double rzeczywisteYPojazdu = trasa.y + (Konfiguracja.wymiaryPrzemieszczajacegoSieObiektu / 2);
        for (int i = 0; i < Konfiguracja.iloscLatarniWZasieguPojazdu; ++i) {
            int rzeczywisteXLatarniPrzedPojazdem = mapaPowiazanWierzcholkow.get(trasa.listaWezlow.get(nrLatarniPrzedPojazdem)).x + (Konfiguracja.wymiaryLatarni / 2);
            int rzeczywisteYLatarniPrzedPojazdem = mapaPowiazanWierzcholkow.get(trasa.listaWezlow.get(nrLatarniPrzedPojazdem)).y + (Konfiguracja.wymiaryLatarni / 2);
            int rzeczywisteXLatarniZaPojazdem = mapaPowiazanWierzcholkow.get(trasa.listaWezlow.get(nrLatarniZaPojazdem)).x + (Konfiguracja.wymiaryLatarni / 2);
            int rzeczywisteYLatarniZaPojazdem = mapaPowiazanWierzcholkow.get(trasa.listaWezlow.get(nrLatarniZaPojazdem)).y + (Konfiguracja.wymiaryLatarni / 2);
            double odlegloscDoLatarniPrzedPojazdem = obliczOdleglosc(rzeczywisteXPojazdu, rzeczywisteYPojazdu, rzeczywisteXLatarniPrzedPojazdem, rzeczywisteYLatarniPrzedPojazdem);
            double odlegloscDoLatarniZaPojazdem = obliczOdleglosc(rzeczywisteXPojazdu, rzeczywisteYPojazdu, rzeczywisteXLatarniZaPojazdem, rzeczywisteYLatarniZaPojazdem);
            String trescWiadomosciDoLatarniPrzedPojazdem = "ODLEGLOSC_DO_POJAZDU@" + odlegloscDoLatarniPrzedPojazdem;
            String trescWiadomosciDoLatarniZaPojazdem = "ODLEGLOSC_DO_POJAZDU@" + odlegloscDoLatarniZaPojazdem;
            AID odbiorcaLatarniPrzedPojazdem = new AID(Konfiguracja.przedrostekAgentaCzujnikaPredkosci + (nrLatarniPrzedPojazdem + 1), AID.ISLOCALNAME);
            AID odbiorcaLatarniaZaPojazdem = new AID(Konfiguracja.przedrostekAgentaCzujnikaPredkosci + (nrLatarniZaPojazdem + 1), AID.ISLOCALNAME);
            ACLMessage wiadomoscDoLatarniPrzedPojazdem = new ACLMessage(ACLMessage.INFORM);
            ACLMessage wiadomoscDoLatarniZaPojazdem = new ACLMessage(ACLMessage.INFORM);
            wiadomoscDoLatarniPrzedPojazdem.addReceiver(odbiorcaLatarniPrzedPojazdem);
            wiadomoscDoLatarniZaPojazdem.addReceiver(odbiorcaLatarniaZaPojazdem);
            wiadomoscDoLatarniPrzedPojazdem.setContent(trescWiadomosciDoLatarniPrzedPojazdem);
            wiadomoscDoLatarniZaPojazdem.setContent(trescWiadomosciDoLatarniZaPojazdem);
            agentInterfejsu.send(wiadomoscDoLatarniPrzedPojazdem);
            agentInterfejsu.send(wiadomoscDoLatarniZaPojazdem);
            ++nrLatarniPrzedPojazdem;
            --nrLatarniZaPojazdem;

            if (nrLatarniPrzedPojazdem >= trasa.listaWezlow.size()) {
                nrLatarniPrzedPojazdem = 0;
            }

            if (nrLatarniZaPojazdem < 0) {
                nrLatarniZaPojazdem = trasa.listaWezlow.size() - 1;
            }
        }
    }

    private void poinformujCzujnikRuchu(Trasa trasa, HashMap<String, Wierzcholek> mapaPowiazanWierzcholkow, AgentInterfejsu agentInterfejsu) {
        Wierzcholek wierzcholek = mapaPowiazanWierzcholkow.get(trasa.nazwaLatarniDoPoinformowania);
        double odleglosc = obliczOdleglosc(trasa.x, trasa.y, wierzcholek.x, wierzcholek.y);

        AID odbiorca = new AID(Konfiguracja.przedrostekAgentaCzujnikaRuchu + trasa.nrLatarniDoPoinformowania, AID.ISLOCALNAME);
        ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
        wiadomosc.addReceiver(odbiorca);
        wiadomosc.setContent("ODLEGLOSC_DO_LATARNI@" + odleglosc);
        agentInterfejsu.send(wiadomosc);
    }
}