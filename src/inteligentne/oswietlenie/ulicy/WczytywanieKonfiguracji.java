package inteligentne.oswietlenie.ulicy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import com.mxgraph.view.mxGraph;

public class WczytywanieKonfiguracji {
	private Scanner odczyt;
	public static ArrayList<String> listaKrawedzi = new ArrayList<String>();
	public static int lacznaIloscLatarni;
	
	private void wczytajKonfiguracjeDoTablic(ArrayList<String> listaWierzcholkow, ArrayList<String> listaKrawedzi, ArrayList<String> listaTras) {
		File plik = new File(Konfiguracja.nazwaPLikuKonfiguracji);
		
		try {
			odczyt = new Scanner(plik);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(odczyt.hasNextLine()) {
			String linia = odczyt.nextLine();
			
			if(linia.startsWith("WIERZCHOLEK")) {
				listaWierzcholkow.add(linia);
			} else if(linia.startsWith("KRAWEDZ")) {
				listaKrawedzi.add(linia);
			} else if(linia.startsWith("TRASA")) {
				listaTras.add(linia);
			}
		}
		
		odczyt.close();
	}
	
	void wczytajKonfiguracje(mxGraph graf) {
		System.out.println("WCZYTYWANIE KONFIGURACJI");
		ArrayList<String> listaWierzcholkow = new ArrayList<String>();
		ArrayList<String> listaTras = new ArrayList<String>();
		
		wczytajKonfiguracjeDoTablic(listaWierzcholkow, listaKrawedzi, listaTras);
		
		ParsowanieStrukturyGrafu parsowanieStrukturyGrafu = new ParsowanieStrukturyGrafu();
		
		System.out.println("PARSOWANIE WIERZCHO£KÓW");
		parsowanieStrukturyGrafu.parsujWierzchlki(listaWierzcholkow, graf);
		System.out.println("PARSOWANIE KRAWÊDZI");
		ParsowanieStrukturyGrafu.parsujKrawedzie(listaKrawedzi, graf);
		OknoGlowne.mapaPowiazanWierzcholkow = parsowanieStrukturyGrafu.pobierzMapePowiazanWierzcholkow();
		System.out.println("PARSOWANIE TRAS");
		parsowanieStrukturyGrafu.parsujTrasy(listaTras, OknoGlowne.przemieszczanieObiektow);
		System.out.println("KONIEC PARSOWANIE TRAS");
	}
}
