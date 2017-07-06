package inteligentne.oswietlenie.ulicy;

import javafx.scene.paint.Color;

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

	public static String getColorIntensityString(int power){
	    double powerValue = power/100d;
	    double blue = 1.0d - powerValue;
	    if (blue > 1.0d){
	    	blue = 1.0d;
		}else if (blue < 0.0d){
	    	blue = 0.0d;
		}
		Color color = new Color(1,1,blue,0);
		return toRGBCode(color);
	}

    public static String toRGBCode( Color color )
    {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }
}
