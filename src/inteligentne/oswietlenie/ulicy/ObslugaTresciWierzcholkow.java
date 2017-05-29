package inteligentne.oswietlenie.ulicy;

public class ObslugaTresciWierzcholkow {
    public static String zwrocTrescLatari(String idLatarni, int wartoscNatezeniaSwiatla, int mocLatarni) {
        return "ID : " + idLatarni + "\n\n\n\n\n\nSwiatlo dzienne: " + wartoscNatezeniaSwiatla + "\nMoc: " + mocLatarni;
    }

    public static String zwrocKomunikatPodpowiedzi(String trescWierzcholka) {
        String[] tablica = trescWierzcholka.split("\n");
        String podpowiedz = "";

        if (tablica.length == 2) {
            String napisNatezenieSwiatla = tablica[0];
            String napisMocLatarni = tablica[1];
            if ((napisNatezenieSwiatla.length() > 3) && (napisMocLatarni.length() > 3)) {
                napisNatezenieSwiatla = napisNatezenieSwiatla.substring(3);
                napisMocLatarni = napisMocLatarni.substring(3);
                podpowiedz = "<html>Wartosc natezenia swiatla: " + napisNatezenieSwiatla + "<br>Moc latarni: " + napisMocLatarni + "</html>";
            }
        }

        return podpowiedz;
    }
}
