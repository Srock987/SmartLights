package inteligentne.oswietlenie.ulicy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//*********************************************************************************
//
//		java -cp libs/jade.jar;bin jade.Boot -gui -agents ZEGAR:inteligentne.oswietlenie.ulicy.HighLevelAgents.ZegarSymulacji
//
//		-gui  <--  OPCJONALNE
//
//		Odpalenie wielu agentow przy jednym wywolaniu:
//
//		-agents NAZWA_AGENTA_1:pelna_nazwa_klasy_1;NAZWA_AGENTA_2:pelna_nazwa_klasy_2;...
//
//*********************************************************************************

public class InteligentneOswietlenieUlicy {
	private static Thread watek;
	private static Process procesKonsoli;
	
	public static void main(String[] args) {
		watek = new Thread() {
			@Override
			public void run() {
				Runtime konsola = Runtime.getRuntime();
				
				try {
					System.out.println("WYWO�ANO KOMEND�");
					procesKonsoli = konsola.exec(Konfiguracja.komendaWywolaniaSymulacji);
					BufferedReader odczyt = new BufferedReader(new InputStreamReader(procesKonsoli.getInputStream()));
					String linia;
					
					while((linia = odczyt.readLine()) != null) {
						System.out.println(linia);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		watek.start();
	}
	
	public void zakoncz() {
		procesKonsoli.destroy();
	}
}
