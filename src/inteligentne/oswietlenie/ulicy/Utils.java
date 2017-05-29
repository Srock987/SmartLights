package inteligentne.oswietlenie.ulicy;

import java.util.Map;

public class Utils {
	public static Double min(Double arg1, Double arg2){
		if(null == arg1)
			return arg2;
		if(null == arg2)
			return arg1;
		return arg1<arg2 ? arg1 : arg2;
	}
	
	public static <T> Double getValueOrNull(Map<T, Double> mapa, T klucz){
		if(null != mapa && mapa.containsKey(klucz))
			return mapa.get(klucz);
		return null;
	}
}
