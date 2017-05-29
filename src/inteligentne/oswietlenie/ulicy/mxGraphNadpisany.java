package inteligentne.oswietlenie.ulicy;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class mxGraphNadpisany extends mxGraph {
	@Override
	public String getToolTipForCell(Object obiekt) {
		String trescKomunikatu = "";
		
		if(obiekt instanceof mxCell) {
			mxCell wierzcholek = (mxCell)obiekt;
			Object obiektWierzcholka = wierzcholek.getValue();
			
			if(obiektWierzcholka instanceof String) {
				trescKomunikatu = ObslugaTresciWierzcholkow.zwrocKomunikatPodpowiedzi((String)obiektWierzcholka);
			}
		}
		
		return trescKomunikatu;
	}
}
