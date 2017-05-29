package inteligentne.oswietlenie.ulicy;

import java.util.ArrayList;

public class Trasa {
	public String nazwaTrasy;
	public String nazwaEtykiety;
	public ArrayList<String> listaWezlow = new ArrayList<String>();
	public double x;
	public double y;
	public double deltaX;
	public double deltaY;
	public double calkowityDystansMiedzyWierzcholkami;
	public double poprzedniDystansMiedzyWierzcholkami;
	public Wierzcholek wierzcholekDoKtoregoDazy;
	public int nrWierzcholkaDoktoregoDazy;
	public int nrLatarniDoPoinformowania;
	public String nazwaLatarniDoPoinformowania;
	double predkosc;
	boolean usunac = false;
}
