package inteligentne.oswietlenie.ulicy;

public class Konfiguracja {
    public static final double dlugoscGeograficzna = 19.57;
    public static final double szerokoscGeograficzna = 50.03;
	public static int czasOdswiezaniaWMilisekundach = 1000;
    public static double zakresNatezeniaSwiatla = 150;
    public static String nazwaAgentaZegaraAstronomicznego = "AgentZegarAstronomiczny";
    public static int przyrostCzasu = 10;
    public static double maxZasiegPodczerwieni = 100;
    public static int maxPredkoscCzlowieka = 10;
    public static double prawdRuchuCzlowieka = 0.3;
    public static int iloscLatarniWZasieguPojazdu = 3;
    public static String nazwaPLikuKonfiguracji = "mapa_grafu.txt";
    public static int wymiaryLatarni = 50;
    public static int wymiaryPrzemieszczajacegoSieObiektu = 25;
    public static double predkoscSamochodu = 0.55;		//pikseli na sekunde
    public static double predkoscPieszego = 0.15;		//pikseli na sekunde

    //
    //PRZEDROSTKI I KOMENDY DO WYWOLANIA Z KONSOLI
    //

    public static String nazwaAgentaZegarSymulacji = "ZEGAR_SYMULACJI";
    public static String komendaWywolaniaSymulacji = "java -cp libs/jade.jar;libs/jgraphx-2.0.0.1.jar;libs/jdatepicker-1.3.4.jar;bin jade.Boot -gui -agents " + Konfiguracja.nazwaAgentaZegarSymulacji + ":inteligentne.oswietlenie.ulicy.HighLevelAgents.ZegarSymulacji";
    public static String przedrostekNazwAgentowCzujnikNatezeniaSwiatla = "CZUJNIK_NATEZENIA_SWIATLA_";
    public static String pelnaNazwaKlasyAgentaCzujnikaNatezeniaSwiatla = "inteligentne.oswietlenie.ulicy.HighLevelAgents.CzujnikNatezeniaOswietlenia";
    public static String pelnaNazwaKlasyAgentaCzujnikaRuchu = "inteligentne.oswietlenie.ulicy.HighLevelAgents.CzujnikRuchu";
    public static String pelnaNazwaKlasyAgentaCzujnikaPredkosci = "inteligentne.oswietlenie.ulicy.HighLevelAgents.CzujnikPredkosci";
    public static String nazwaAgentaInterfejsu = "AGENT_INTERFEJSU";
    public static String pelnaNazwaAgentaInterfejsu = "inteligentne.oswietlenie.ulicy.AgentInterfejsu";
    public static String nazwaModuluAnalizyParametrow = "MODUL_ANALIZY_PARAMETROW";
    public static String pelnaNazwaModuluAnalizyParametrow = "inteligentne.oswietlenie.ulicy.EventAgents.ModulAnalizyParametrow";
    public static String nazwaModuluAnalizyRuchu = "MODUL_ANALIZY_RUCHU";
    public static String pelnaNazwaModuluAnalizyRuchu = "inteligentne.oswietlenie.ulicy.EventAgents.ModulAnalizyRuchu";
    public static String nazwaModuluSterujacego = "MODUL_STERUJACY";
    public static String pelnaNazwaModuluSterujacego = "inteligentne.oswietlenie.ulicy.LowLevelAgents.ModulSterujacy";
    public static String przedrostekNazwyLatarni = "LATARNIA_";
    public static String pelnaNazwaAgentaLatarni = "inteligentne.oswietlenie.ulicy.LowLevelAgents.Latarnia";
    public static String przedrostekAgentaCzujnikaPredkosci = "CZUJNIK_PREDKOSCI_";
    public static String przedrostekAgentaCzujnikaRuchu = "CZUJNIK_RUCHU_"; 
    
    //
    //KONFIGURACJA SYMULACJI
    //

    public static int iloscUplywajacychSekundPrzyOdswiezaniu = 60;
    public static int iloscLatarni = 5;
}