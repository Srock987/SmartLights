package inteligentne.oswietlenie.ulicy;

import java.util.Date;

/**
 * Created by tomeg on 05.02.17.
 */
public class TimeTracker {

  private long milis;

  public void start() {
    milis = new Date().getTime();
  }

  public long getInterval() {
    return new Date().getTime() - milis;
  }
}
