package inteligentne.oswietlenie.ulicy;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import inteligentne.oswietlenie.ulicy.HighLevelAgents.ZegarSymulacji;
import inteligentne.oswietlenie.ulicy.utils.WaitFrame;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.border.EmptyBorder;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.pmw.tinylog.Logger;

class DateLabelFormatter extends AbstractFormatter {
    private static final long serialVersionUID = 1L;
    private String datePattern = "dd.MM.yyyy";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }

        return "";
    }
}

public class OknoGlowne extends JFrame {
    private static final long serialVersionUID = 1L;
    public static final String CHANGE_SIMULATION_OPTION_STARTED =
        "Change simulation option started";
    private JPanel contentPane;
    private JScrollPane panel;

    public static mxGraph graf;
    public static HashMap<String, Wierzcholek> mapaPowiazanWierzcholkow;
    public static PrzemieszczanieObiektow przemieszczanieObiektow = new PrzemieszczanieObiektow();
    private static boolean pierwszyRaz = true;
    private static boolean symulacjaUruchomiona = false;
    private static boolean uruchomionyScenariusz1 = false;
    public static double procentZachmurzenia;
    public static JLabel referencjaNaLAbelAktualnyCzass;
    public static Calendar aktualnyCzas = Calendar.getInstance();
    public static boolean zmienianieCzasu = false;

    /**
     * Launch the application.
     */
    public static void stworzOknoSymulacji() {
        Thread t = new Thread() {
            @Override
            public void run() {
                System.out.println("TWORZENIE OKNA SYMULACJI");
                OknoGlowne frame;
                try {
                    frame = new OknoGlowne();
                    System.out.println("USTAWIENIE WIDOCZNOSCI OKNA");
                    frame.setVisible(true);
                    frame.stworzGrafReprezentujacy(graf);
                } catch (Exception e) {
                    System.out.println("Exception");
                    e.printStackTrace();
                    System.out.println(e);
                } catch (Error e) {
                    System.out.println("Error");
                    e.printStackTrace();
                    System.out.println(e);
                } catch (Throwable e) {
                    System.out.println("Throwable");
                    System.out.println(e);
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    /**
     * Create the frame.
     */
    public OknoGlowne() throws Exception {
        setTitle("Inteligentne O\u015Bwietlenie Ulicy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 606, 376);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        //
        // GRAF
        //

        //graf = new mxGraph();
        graf = new mxGraphNadpisany();

        mxGraphComponent grafKomponent = new mxGraphComponent(graf);
        grafKomponent.setEnabled(false);
        grafKomponent.setToolTips(true);
        panel = new JScrollPane();
        panel.setViewportView(grafKomponent);
        contentPane.add(panel, BorderLayout.CENTER);

        //
        // KONIEC GRAF
        //

        JPanel panel_1 = new JPanel();
        contentPane.add(panel_1, BorderLayout.WEST);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

        JLabel lblA = new JLabel(" ");
        panel_1.add(lblA);

        JPanel panel_9 = new JPanel();
        panel_1.add(panel_9);
        panel_9.setLayout(new GridLayout(0, 1, 0, 0));

        JLabel lblAktualnyCzas = new JLabel("Aktualny czas:");
        lblAktualnyCzas.setHorizontalAlignment(SwingConstants.CENTER);
        panel_9.add(lblAktualnyCzas);

        StrukturaCzas struktCzas = new StrukturaCzas();
        String dzienLubNoc;

        if (struktCzas.jestDzien) {
            dzienLubNoc = "DZIEN";
        } else {
            dzienLubNoc = "NOC";
        }

        SimpleDateFormat formatDaty = new SimpleDateFormat("dd.M.yyyy   HH:mm:ss");
        String czasNapis = formatDaty.format(struktCzas.getCzas().getTime()) + "   (" + dzienLubNoc + ")";

        JLabel labelAktualnyCzas = new JLabel("dd.M.yyyy   hh:mm:ss   (DZIE\u0143)");
        labelAktualnyCzas.setText(czasNapis);
        labelAktualnyCzas.setHorizontalAlignment(SwingConstants.CENTER);
        referencjaNaLAbelAktualnyCzass = labelAktualnyCzas;
        panel_9.add(labelAktualnyCzas);

        JLabel label_1 = new JLabel(" ");
        panel_1.add(label_1);

        JPanel panel_10 = new JPanel();
        panel_1.add(panel_10);
        panel_10.setLayout(new GridLayout(0, 1, 0, 0));

        JButton btnZmieAktualnyCzas = new JButton("Zmie\u0144 aktualny czas");
        btnZmieAktualnyCzas.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void mouseReleased(MouseEvent e) {
                if (zmienianieCzasu) {
                    return;
                }

                if (!symulacjaUruchomiona) {
                    zmienianieCzasu = true;
                    final JFrame oknoZmianyDaty = new JFrame();
                    oknoZmianyDaty.setBounds(100, 100, 300, 220);
                    oknoZmianyDaty.setTitle("Zmiana daty");
                    JPanel panelZmianyDaty = new JPanel();
                    oknoZmianyDaty.getContentPane().setLayout(new BorderLayout(0, 0));
                    oknoZmianyDaty.getContentPane().add(panelZmianyDaty, BorderLayout.CENTER);


                    oknoZmianyDaty.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            zmienianieCzasu = false;
                        }
                    });

                    panelZmianyDaty.setLayout(new BoxLayout(panelZmianyDaty, BoxLayout.Y_AXIS));

                    JLabel etykietaZmianyDaty = new JLabel("Kliknij przycisk ponizej, aby wybrac date");
                    etykietaZmianyDaty.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panelZmianyDaty.add(etykietaZmianyDaty);

                    JLabel etykietaZmianyDatyPrzerwa = new JLabel(" ");
                    etykietaZmianyDatyPrzerwa.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panelZmianyDaty.add(etykietaZmianyDatyPrzerwa);


                    UtilDateModel model = new UtilDateModel(struktCzas.getCzas().getTime());
                    Properties p = new Properties();
                    p.put("text.today", "Dzin");
                    p.put("text.month", "Miesiac");
                    p.put("text.year", "Rok");
                    JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
                    final JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                    datePicker.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panelZmianyDaty.add(datePicker);
                    panelZmianyDaty.add(etykietaZmianyDatyPrzerwa);

                    JLabel etykietaInfoOZmianieCzasu = new JLabel("Zmiana czasu (hh:mm:ss):");
                    etykietaInfoOZmianieCzasu.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panelZmianyDaty.add(etykietaInfoOZmianieCzasu);
                    panelZmianyDaty.add(etykietaZmianyDatyPrzerwa);

                    JPanel panelZmianyCzasu = new JPanel();
                    panelZmianyCzasu.setLayout(new GridLayout(0, 3));
                    SpinnerNumberModel zmianaGodziny = new SpinnerNumberModel(OknoGlowne.aktualnyCzas.getTime().getHours(), 0, 23, 1);
                    SpinnerNumberModel zmianaMinut = new SpinnerNumberModel(OknoGlowne.aktualnyCzas.getTime().getMinutes(), 0, 59, 1);
                    SpinnerNumberModel zmianaSekund = new SpinnerNumberModel(OknoGlowne.aktualnyCzas.getTime().getSeconds(), 0, 59, 1);
                    panelZmianyCzasu.add(new JSpinner(zmianaGodziny));
                    panelZmianyCzasu.add(new JSpinner(zmianaMinut));
                    panelZmianyCzasu.add(new JSpinner(zmianaSekund));
                    panelZmianyDaty.add(panelZmianyCzasu);
                    panelZmianyDaty.add(etykietaZmianyDatyPrzerwa);
                    JButton przyciskZmianyDaty = new JButton("Zatwierdz zmiane daty");
                    przyciskZmianyDaty.setAlignmentX(Component.CENTER_ALIGNMENT);
                    przyciskZmianyDaty.addActionListener(arg0 -> {
                        Date wybranaData = (Date) datePicker.getModel().getValue();
                        Calendar nowyCzas = Calendar.getInstance();
                        if (wybranaData == null)
                            return;
                        wybranaData.setHours((int) zmianaGodziny.getValue());
                        wybranaData.setMinutes((int) zmianaMinut.getValue());
                        wybranaData.setSeconds((int) zmianaSekund.getValue());
                        nowyCzas.setTime(wybranaData);
                        OknoGlowne.aktualnyCzas = nowyCzas;

                        String poraDnia;
                        StrukturaCzas struktNowyCzas = new StrukturaCzas(nowyCzas.getTime());

                        if (struktNowyCzas.jestDzien) {
                            poraDnia = "DZIEN";
                        } else {
                            poraDnia = "NOC";
                        }

                        SimpleDateFormat formatDaty1 = new SimpleDateFormat("dd.M.yyyy   HH:mm:ss");
                        String czasNapis1 = formatDaty1.format(nowyCzas.getTime()) + "   (" + poraDnia + ")";
                        OknoGlowne.referencjaNaLAbelAktualnyCzass.setText(czasNapis1);
                        zmienianieCzasu = false;

                        AID odbiorca = new AID(Konfiguracja.nazwaAgentaZegarSymulacji, AID.ISLOCALNAME);
                        ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
                        wiadomosc.addReceiver(odbiorca);
                        wiadomosc.setContent(struktNowyCzas.toString());
                        AgentInterfejsu.agentInterfejsu.send(wiadomosc);
                        oknoZmianyDaty.dispose();
                    });
                    panelZmianyDaty.add(przyciskZmianyDaty);
                    oknoZmianyDaty.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Aby zmienic date lub czas musisz zatrzymac symulacje.");
                }
            }
        });
        panel_10.add(btnZmieAktualnyCzas);

        JLabel label_2 = new JLabel(" ");
        panel_1.add(label_2);

        JPanel panel_2 = new JPanel();
        panel_1.add(panel_2);

        JButton btnUruchomSymulacje = new JButton("URUCHOM SYMULACJE");
        btnUruchomSymulacje.addMouseListener(new MouseAdapter() {
            private TimeTracker timeTracker = new TimeTracker();
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!zmienianieCzasu) {
                  timeTracker.start();
                Logger.info("Start simulation");
                    if (!symulacjaUruchomiona) {
                        AID odbiorca = new AID(Konfiguracja.nazwaAgentaZegarSymulacji, AID.ISLOCALNAME);
                        ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
                        wiadomosc.addReceiver(odbiorca);
                        wiadomosc.setContent("URUCHOM");

                        WaitFrame waitFrame = new WaitFrame();
                        if (pierwszyRaz) {
                            waitFrame.showFrame();
//                            try {
//                                Thread.sleep((long) (2 * Konfiguracja.czasOdswiezaniaWMilisekundach));
//                            } catch (InterruptedException e1) {
//                                e1.printStackTrace();
//                            }
                        }
                        AgentInterfejsu.agentInterfejsu.send(wiadomosc);
                        symulacjaUruchomiona = true;

                        if (pierwszyRaz) {
//                            try {
//                                Thread.sleep((long) (2 * Konfiguracja.czasOdswiezaniaWMilisekundach));
//                            } catch (InterruptedException e1) {
//                                e1.printStackTrace();
//                            }
                            pierwszyRaz = false;
                            waitFrame.closeFrame();
                        }
                    }
                  Logger.info("End of starting simulation, time spend: "
                      + timeTracker.getInterval()
                      + "ms");
                } else {
                    JOptionPane.showMessageDialog(null, "Nie mozna uruchomic symulacji podczas zmiany czasu.");
                }
            }
        });
        panel_2.setLayout(new GridLayout(0, 1, 0, 0));
        panel_2.add(btnUruchomSymulacje);

        JLabel lblNewLabel = new JLabel(" ");
        panel_1.add(lblNewLabel);

        JPanel panel_3 = new JPanel();
        panel_1.add(panel_3);

        JButton btnNewButton = new JButton("ZATRZYMAJ SYMULACJE");
        btnNewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (symulacjaUruchomiona) {
                    AID odbiorca = new AID(Konfiguracja.nazwaAgentaZegarSymulacji, AID.ISLOCALNAME);
                    ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
                    wiadomosc.addReceiver(odbiorca);
                    wiadomosc.setContent("ZATRZYMAJ");
                    AgentInterfejsu.agentInterfejsu.send(wiadomosc);
                    symulacjaUruchomiona = false;
                }
            }
        });
        panel_3.setLayout(new GridLayout(0, 1, 0, 0));
        panel_3.add(btnNewButton);

        JLabel lblNewLabel_1 = new JLabel(" ");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(lblNewLabel_1);

        JPanel panel_4 = new JPanel();
        panel_1.add(panel_4);
        panel_4.setLayout(new GridLayout(0, 1, 0, 0));

        JLabel lblWybierz = new JLabel("Szybkosc symulacji:");
        panel_4.add(lblWybierz);
        lblWybierz.setHorizontalAlignment(SwingConstants.CENTER);

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addActionListener(e -> {
            int wybranaOpcja = comboBox.getSelectedIndex();
            int iloscSekund = 1;

            switch (wybranaOpcja) {
                case 0:
                    iloscSekund = 1;
                    break;
                case 1:
                    iloscSekund = 3;
                    break;
                case 2:
                    iloscSekund = 10;
                    break;
                case 3:
                    iloscSekund = 30;
                    break;
                case 4:
                    iloscSekund = 60;
                    break;
                case 5:
                    iloscSekund = 300;
                    break;
                case 6:
                    iloscSekund = 900;
                    break;
                case 7:
                    iloscSekund = 1800;
                    break;
                case 8:
                    iloscSekund = 3600;
                    break;
                case 9:
                    iloscSekund = 7200;
                    break;
            }

            ZegarSymulacji.zegarSymulacji.setZmianaCzasuWSekundach(iloscSekund);
        });
        comboBox.setModel(new DefaultComboBoxModel<>(new String[]{"1 sekunda", "3 sekundy", "10 sekund", "30 sekund", "1 minuta", "5 minut", "15 minut", "30 minut", "1 godzina", "2 godziny"}));
        comboBox.setSelectedIndex(1);
        panel_4.add(comboBox);

        JLabel label = new JLabel(" ");
        panel_1.add(label);

        JPanel panel_6 = new JPanel();
        panel_1.add(panel_6);

        JLabel lblScenariusze = new JLabel("Scenariusze:");
        panel_6.add(lblScenariusze);

        JPanel panel_7 = new JPanel();
        panel_7.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel_1.add(panel_7);
        panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.Y_AXIS));

        JCheckBox checkboxScenariusz1 = new JCheckBox("Samochod osobowy");
        checkboxScenariusz1.addActionListener(e -> {
            if (symulacjaUruchomiona) {
                String tekstWiadomosci;

                if (checkboxScenariusz1.isSelected()) {
                    tekstWiadomosci = "DODAJ_AKTYWNA_TRASE@TRASA_1";
                    uruchomionyScenariusz1 = true;
                } else {
                    tekstWiadomosci = "ZATRZYMAJ_AKTYWNA_TRASE@TRASA_1";
                    uruchomionyScenariusz1 = false;
                }

                Logger.info(CHANGE_SIMULATION_OPTION_STARTED);
                AID odbiorca = new AID(Konfiguracja.nazwaAgentaInterfejsu, AID.ISLOCALNAME);
                ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
                wiadomosc.addReceiver(odbiorca);
                wiadomosc.setContent(tekstWiadomosci);
                AgentInterfejsu.agentInterfejsu.send(wiadomosc);
                try {
                    Thread.sleep(Konfiguracja.czasOdswiezaniaWMilisekundach);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } else {
                checkboxScenariusz1.setSelected(uruchomionyScenariusz1);

                if (uruchomionyScenariusz1) {
                    JOptionPane.showMessageDialog(null, "Aby wylaczyc scenariusz najpierw ponownie uruchom symulacje.");
                } else {
                    JOptionPane.showMessageDialog(null, "Najpierw musisz uruchomi� symulacje.");
                }
            }
        });
        checkboxScenariusz1.setHorizontalAlignment(SwingConstants.LEFT);
        panel_7.add(checkboxScenariusz1);

        JCheckBox checkboxScenariusz2 = new JCheckBox("Zorganizowana demonstracja");
        checkboxScenariusz2.addActionListener(e -> {
            if (symulacjaUruchomiona) {
                Logger.info(CHANGE_SIMULATION_OPTION_STARTED);
                String tekstWiadomosci;

                if (checkboxScenariusz2.isSelected()) {
                    tekstWiadomosci = "DODAJ_AKTYWNA_TRASE@TRASA_2";
                    uruchomionyScenariusz1 = true;
                } else {
                    tekstWiadomosci = "ZATRZYMAJ_AKTYWNA_TRASE@TRASA_2";
                    uruchomionyScenariusz1 = false;
                }

                AID odbiorca = new AID(Konfiguracja.nazwaAgentaInterfejsu, AID.ISLOCALNAME);
                ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
                wiadomosc.addReceiver(odbiorca);
                wiadomosc.setContent(tekstWiadomosci);
                AgentInterfejsu.agentInterfejsu.send(wiadomosc);
                try {
                    Thread.sleep(Konfiguracja.czasOdswiezaniaWMilisekundach);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } else {
                checkboxScenariusz2.setSelected(uruchomionyScenariusz1);

                if (uruchomionyScenariusz1) {
                    JOptionPane.showMessageDialog(null, "Aby wyylczyc scenariusz najpierw ponownie uruchom symulacje.");
                } else {
                    JOptionPane.showMessageDialog(null, "Najpierw musisz uruchomi� symulacje.");
                }
            }
        });
        panel_7.add(checkboxScenariusz2);

        JCheckBox checkboxScenariusz3 = new JCheckBox("Pieszy przy ulicy");
        checkboxScenariusz3.addActionListener(e -> {
            if (symulacjaUruchomiona) {
                Logger.info(CHANGE_SIMULATION_OPTION_STARTED);

                String tekstWiadomosci;

                if (checkboxScenariusz3.isSelected()) {
                    tekstWiadomosci = "DODAJ_AKTYWNA_TRASE@TRASA_3";
                    uruchomionyScenariusz1 = true;
                } else {
                    tekstWiadomosci = "ZATRZYMAJ_AKTYWNA_TRASE@TRASA_3";
                    uruchomionyScenariusz1 = false;
                }

                AID odbiorca = new AID(Konfiguracja.nazwaAgentaInterfejsu, AID.ISLOCALNAME);
                ACLMessage wiadomosc = new ACLMessage(ACLMessage.INFORM);
                wiadomosc.addReceiver(odbiorca);
                wiadomosc.setContent(tekstWiadomosci);
                AgentInterfejsu.agentInterfejsu.send(wiadomosc);
                try {
                    Thread.sleep(Konfiguracja.czasOdswiezaniaWMilisekundach);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } else {
                checkboxScenariusz3.setSelected(uruchomionyScenariusz1);

                if (uruchomionyScenariusz1) {
                    JOptionPane.showMessageDialog(null, "Aby wylaczyc scenariusz najpierw ponownie uruchom symulacje.");
                } else {
                    JOptionPane.showMessageDialog(null, "Najpierw musisz uruchomi� symulacje.");
                }
            }
        });
        panel_7.add(checkboxScenariusz3);

        JPanel panel_8 = new JPanel();
        panel_1.add(panel_8);
        panel_8.setLayout(new GridLayout(0, 1, 0, 0));

        JLabel lblZachmurzenie = new JLabel("Zachmurzenie:");
        lblZachmurzenie.setHorizontalAlignment(SwingConstants.CENTER);
        panel_8.add(lblZachmurzenie);

        JComboBox<String> comboBox_1 = new JComboBox<String>();
        comboBox_1.addActionListener(e -> {

            int wybranaOPcja = comboBox_1.getSelectedIndex();

            switch (wybranaOPcja) {
                case 0:
                    procentZachmurzenia = 0;
                    break;
                case 1:
                    procentZachmurzenia = 0.25;
                    break;
                case 2:
                    procentZachmurzenia = 0.5;
                    break;
                case 3:
                    procentZachmurzenia = 0.75;
                    break;
            }
        });
        comboBox_1.setModel(new DefaultComboBoxModel<>(new String[]{"BRAK", "MA\u0141E", "\u015AREDNIE", "DU\u017BE"}));
        comboBox_1.setSelectedIndex(0);
        panel_8.add(comboBox_1);

        JPanel panel_5 = new JPanel();
        panel_1.add(panel_5);
        panel_5.setLayout(new BorderLayout(0, 0));

        JLabel KONCZACY_LABEL = new JLabel(" ");
        panel_5.add(KONCZACY_LABEL, BorderLayout.NORTH);
    }

    public void stworzGrafReprezentujacy(mxGraph graf) {
        graf.getModel().beginUpdate();

        WczytywanieKonfiguracji wczytywanieKonfiguracji = new WczytywanieKonfiguracji();
        wczytywanieKonfiguracji.wczytajKonfiguracje(graf);
        graf.getModel().endUpdate();

        System.out.println("KONIEC WCZYTYWANIA KONFIGURACJI");
    }
}
