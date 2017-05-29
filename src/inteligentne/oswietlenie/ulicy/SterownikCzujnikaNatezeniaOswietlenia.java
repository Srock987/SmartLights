package inteligentne.oswietlenie.ulicy;

import inteligentne.oswietlenie.ulicy.HighLevelAgents.CzujnikNatezeniaOswietlenia;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

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
		
		if(wiadomosc != null) {
			String strCzas = wiadomosc.getContent();
			StrukturaCzas struktCzas = new StrukturaCzas(strCzas);
			
			if(struktCzas.jestDzien) {
				czujnikNatezeniaOswietlenia.ustawNatezenieOswietlenia((int)Math.round((1 - OknoGlowne.procentZachmurzenia) * Konfiguracja.zakresNatezeniaSwiatla));
			} else {
				czujnikNatezeniaOswietlenia.ustawNatezenieOswietlenia(0);
			}
			
			poinformujOdbiorcow();
		}
	}
}
