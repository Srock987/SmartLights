package inteligentne.oswietlenie.ulicy;

import jade.core.Agent;

public class AgentInterfejsu extends Agent {
	private static final long serialVersionUID = 1L;
	public static AgentInterfejsu agentInterfejsu;
	
	@Override
	protected void setup() {
		System.out.println("URUCHOMIONO AGENTA INTERFEJSU");
		agentInterfejsu = this;
		OknoGlowne.stworzOknoSymulacji();
		addBehaviour(new ObslugaKomunikatowInterfejsu(this, Konfiguracja.czasOdswiezaniaWMilisekundach));
	}
}
