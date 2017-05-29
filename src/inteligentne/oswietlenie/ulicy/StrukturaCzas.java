package inteligentne.oswietlenie.ulicy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StrukturaCzas {
	public Calendar czas = Calendar.getInstance();
	public boolean jestDzien;
	
	public StrukturaCzas() {
	}
	
	public StrukturaCzas(Date data) {
		czas.setTime(data);
		jestDzien = !(ZegarAstronomiczny.jestNoc(czas));
	}
	
	public StrukturaCzas(String dataString) {
		String[] prefiksIData = dataString.split("@");
		
		if(prefiksIData.length == 2) {
			if(prefiksIData[0].equals("CZAS")) {
				String[] rozdzielone = prefiksIData[1].split("#");
				jestDzien = Boolean.parseBoolean(rozdzielone[0]);
				
				SimpleDateFormat formatDaty = new SimpleDateFormat("dd.M.yyyy HH:mm:ss");
				try {
					Date data = formatDaty.parse(rozdzielone[1]);
					czas.setTime(data);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public String toString() {
		SimpleDateFormat formatDaty = new SimpleDateFormat("dd.M.yyyy HH:mm:ss");
		String dataString = "CZAS@" + Boolean.toString(jestDzien) + "#";
		dataString += formatDaty.format(czas.getTime());
		return dataString;
	}

	public Calendar getCzas() {
		return czas;
	}
}
