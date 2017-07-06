package inteligentne.oswietlenie.ulicy.utils;

import javax.swing.*;

/**
 * Created by Pawel on 2017-06-24.
 */
public class WaitFrame extends JFrame {

    public void showFrame(){
        setBounds(500, 200, 400, 200);
        setTitle("Rozpoczynanie symulacji");
        fillFrame();
        setVisible(true);
    }

    private void fillFrame(){
        JLabel pleseWaitLabel = new JLabel("Proszę czekać...");
        add(pleseWaitLabel);
    }

    public void closeFrame(){
        setVisible(false);
        dispose();
    }
}
