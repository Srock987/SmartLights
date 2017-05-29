package inteligentne.oswietlenie.ulicy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Loger {
    private static Map<String, Logger> loggerMap = new HashMap<String, Logger>();

    private static Logger getLogger(String nazwa){
        if(loggerMap.containsKey(nazwa))
            return loggerMap.get(nazwa);

        Logger logger = Logger.getLogger(nazwa);
        try {
            FileHandler fh = new FileHandler("logs/" + nazwa + ".txt");
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.addHandler(fh);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        loggerMap.put(nazwa, logger);
        return logger;

    }

    public static void info(String nazwa, String log){
        getLogger(nazwa).info(log);
    }

    public static void error(String nazwa, Exception e){
        getLogger(nazwa).log(Level.SEVERE, e.getMessage(), e);
    }
}