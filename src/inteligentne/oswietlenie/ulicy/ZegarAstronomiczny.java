package inteligentne.oswietlenie.ulicy;

import java.util.Calendar;
import java.util.Date;

public class ZegarAstronomiczny{

	private static double szerokoscGeograficzna = Konfiguracja.szerokoscGeograficzna;
	private static double dlugoscGeograficzna = Konfiguracja.dlugoscGeograficzna;

	
	
	public static boolean jestNoc(Calendar biezacaData){
		
		Date godzinaWschodu = godzinaWschodu(biezacaData);
		Date godzinaZachodu = godzinaZachodu(biezacaData);
		
		return !(biezacaData.getTime().after(godzinaWschodu) && (biezacaData.getTime().before(godzinaZachodu)));
	}

	public static Date godzinaWschodu(Calendar data){
		return obliczGodzine(PoraDnia.WSCHOD, data);
	}

	public static Date godzinaZachodu(Calendar data){
		return obliczGodzine(PoraDnia.ZACHOD, data);
	}
	
	private static Date obliczGodzine(PoraDnia poraDnia, Calendar data){
		
		int R = data.get(Calendar.YEAR);
		int M = data.get(Calendar.MONTH)+1;
		int D = data.get(Calendar.DAY_OF_MONTH);
		double Req = -0.833;
		double J = 367*R-(int) (7*(R+ (int) ((M+9)/12))/4)+ (int) (275*M/9)+D-730531.5;
		double Cent = J/36525;
		double L = (4.8949504201433+628.331969753199*Cent)%6.28318530718;
		double G = (6.2400408+628.3019501*Cent)%6.28318530718;
		double O = 0.409093-0.0002269*Cent;
		double F = 0.033423*Math.sin(G)+0.00034907*Math.sin(2*G);
		double E = 0.0430398*Math.sin(2*(L+F)) - 0.00092502*Math.sin(4*(L+F)) - F;
		double A = Math.asin(Math.sin(O)*Math.sin(L+F));
		double C = (Math.sin(0.017453293*Req)-Math.sin(0.017453293*szerokoscGeograficzna)*Math.sin(A))/ 
				(Math.cos(0.017453293*szerokoscGeograficzna)*Math.cos(A));
		double czas = 0;
		switch(poraDnia){
			case ZACHOD: czas = 
					(Math.PI - (E+0.017453293*dlugoscGeograficzna-Math.acos(C)))*57.29577951/15;
				break;
			case WSCHOD: czas = 
					(Math.PI - (E+0.017453293*dlugoscGeograficzna+Math.acos(C)))*57.29577951/15;
				break;
		}
		return updateCalendar(data, czas);
	}
	
	private static Date updateCalendar(final Calendar data, double czas) {
		Calendar clone = (Calendar) data.clone();
		int godzina = (int) czas;
		clone.set(Calendar.HOUR_OF_DAY, godzina +2);//+2 dla czasu letniego
		clone.set(Calendar.MINUTE,(int) ((czas - godzina)*60));
		clone.set(Calendar.SECOND, 0);
		return clone.getTime();
	}

	
	public enum PoraDnia{
		WSCHOD,ZACHOD;
	}
	
}




