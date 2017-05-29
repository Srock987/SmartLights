package inteligentne.oswietlenie.ulicy;

import java.util.Calendar;

import inteligentne.oswietlenie.ulicy.HighLevelAgents.ZegarSymulacji;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class PostepCzasuSymulacji extends TickerBehaviour {
	private static final long serialVersionUID = 1L;
	private ZegarSymulacji zegarSymulacji;

	private void poinformujAgentowONowymCzasie(Calendar nowyCzas) {
		for(int i = 0; i < zegarSymulacji.getListaAgentowDoPoinformowania().size(); ++i) {
			AID odbiorca = zegarSymulacji.getListaAgentowDoPoinformowania().get(i);
			ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
			wiadomosc.addReceiver(odbiorca);
			StrukturaCzas stuktCzas = new StrukturaCzas(nowyCzas.getTime());
			wiadomosc.setContent(stuktCzas.toString());
			zegarSymulacji.send(wiadomosc);
		}
	}

	public PostepCzasuSymulacji(ZegarSymulacji zegarSymulacji, long okres) {
		super(zegarSymulacji, okres);
		this.zegarSymulacji = zegarSymulacji;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onTick() {
		ACLMessage wiadomosc = zegarSymulacji.receive();
		
		if(wiadomosc != null) {
			if(wiadomosc.getContent().equals("URUCHOM")) {
				zegarSymulacji.zegarUruchomiony = true;
			} else if(wiadomosc.getContent().equals("ZATRZYMAJ")) {
				zegarSymulacji.zegarUruchomiony = false;
			} else if(wiadomosc.getContent().startsWith("CZAS@")) {
				zegarSymulacji.setAktualnyCzas(new StrukturaCzas(wiadomosc.getContent()).getCzas());
			}
		}
		
		if(zegarSymulacji.zegarUruchomiony) {
			Calendar nowyCzas = zegarSymulacji.getAktualnyCzas();
			nowyCzas.add(Calendar.SECOND, zegarSymulacji.getZmianaCzasuWSekundach());
			zegarSymulacji.setAktualnyCzas(nowyCzas);
	
			poinformujAgentowONowymCzasie(nowyCzas);
//			System.out.println(zegarSymulacji.getAktualnyCzas().getTime().getMinutes());
		}
	}
}
