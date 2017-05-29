package inteligentne.oswietlenie.ulicy;

import com.mxgraph.view.mxGraph;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class ParsowanieStrukturyGrafu {
    private static HashMap<String, Wierzcholek> mapaPowiazanWierzcholkow = new HashMap<String, Wierzcholek>();

    public void parsujWierzchlki(ArrayList<String> listaWierzcholkow, mxGraph graf) {
        Object wezelNadrzedny = graf.getDefaultParent();

        for (int i = 0; i < listaWierzcholkow.size(); ++i) {
            String[] listaTokenow = listaWierzcholkow.get(i).split(" ");

            int x = Integer.parseInt(listaTokenow[1]);
            int y = Integer.parseInt(listaTokenow[2]);
            String nazwaWierzholka = listaTokenow[3];

            Wierzcholek wierzcholek = new Wierzcholek();
            wierzcholek.natezenieSwiatla = 100;
            wierzcholek.mocLatarni = 0;
            wierzcholek.obiekt = graf.insertVertex(wezelNadrzedny, nazwaWierzholka, ObslugaTresciWierzcholkow.zwrocTrescLatari(nazwaWierzholka, wierzcholek.natezenieSwiatla,
                    wierzcholek.mocLatarni), x, y, Konfiguracja.wymiaryLatarni, Konfiguracja.wymiaryLatarni, "");
            wierzcholek.x = x;
            wierzcholek.y = y;
            wierzcholek.nazwaWierzcholka = nazwaWierzholka;
            wierzcholek.jestLatarnia = true;

            if (wierzcholek.obiekt == null) {
                System.out.println("Error while creating vertex: " + nazwaWierzholka);
            }

            mapaPowiazanWierzcholkow.put(nazwaWierzholka, wierzcholek);
        }

        WczytywanieKonfiguracji.lacznaIloscLatarni = listaWierzcholkow.size();
    }

    public static void parsujKrawedzie(ArrayList<String> listaKrawedzi, mxGraph graf) {
        Object wezelNadrzedny = graf.getDefaultParent();

        for (int i = 0; i < listaKrawedzi.size(); ++i) {
            String[] listaTokenow = listaKrawedzi.get(i).split(" ");
            String nazwa1 = listaTokenow[1];
            String nazwa2 = listaTokenow[2];
            Wierzcholek wierzcholek1 = mapaPowiazanWierzcholkow.get(nazwa1);
            Wierzcholek wierzcholek2 = mapaPowiazanWierzcholkow.get(nazwa2);
            if ((wierzcholek1 == null) || (wierzcholek2 == null)) {
                System.out.println("Error while searching vertex");
            }
            Object obj = graf.insertEdge(wezelNadrzedny, null, "", wierzcholek1.obiekt, wierzcholek2.obiekt, "endArrow=none");
            if (obj == null) {
                System.out.println("Error while creating edge between " + wierzcholek1.nazwaWierzcholka + " and " + wierzcholek2.nazwaWierzcholka);
            }
        }
    }

    public void parsujTrasy(ArrayList<String> listaTras, PrzemieszczanieObiektow przemieszczanieObiektow) {
        for (int i = 0; i < listaTras.size(); ++i) {
            String[] listaTokenow = listaTras.get(i).split(" ");

            String nazwaTrasy = listaTokenow[1];
            String typPoruszajacegoSieObiektu = listaTokenow[2];
            String nazwaEtykiety = listaTokenow[3];

            Trasa trasa = new Trasa();
            trasa.nazwaTrasy = nazwaTrasy;
            trasa.nazwaEtykiety = nazwaEtykiety;

            if (typPoruszajacegoSieObiektu.equals("SAMOCHOD")) {
                trasa.predkosc = Konfiguracja.predkoscSamochodu;
            } else if (typPoruszajacegoSieObiektu.equals("PIESZY")) {
                trasa.predkosc = Konfiguracja.predkoscPieszego;
            }

            for (int k = 4; k < listaTokenow.length; ++k) {
                if (listaTokenow[k].startsWith(Konfiguracja.przedrostekNazwyLatarni)) {
                    trasa.listaWezlow.add(listaTokenow[k]);
                } else {
                    if (k == 4) {
                        trasa.x = Integer.parseInt(listaTokenow[k]);
                    } else if (k == 5) {
                        trasa.y = Integer.parseInt(listaTokenow[k]);
                    } else if (k == 6) {
                        trasa.nrLatarniDoPoinformowania = Integer.parseInt(listaTokenow[k]);
                        trasa.nazwaLatarniDoPoinformowania = Konfiguracja.przedrostekNazwyLatarni + trasa.nrLatarniDoPoinformowania;
                    }
                }
            }

            przemieszczanieObiektow.dodajTrase(trasa);
        }
    }

    public HashMap<String, Wierzcholek> pobierzMapePowiazanWierzcholkow() {
        return mapaPowiazanWierzcholkow;
    }
}
