package task.thread;

import javafx.beans.property.SimpleBooleanProperty;

public class PauseManager {
    private final Object pauseLock;
    private SimpleBooleanProperty isPauseProperty;

    public PauseManager(SimpleBooleanProperty pausePropertyIn)
    {
        pauseLock=new Object();
        isPauseProperty=new SimpleBooleanProperty(false);

        pausePropertyIn.addListener((obsev)-> {
            if (pausePropertyIn.getValue()) {
                isPauseProperty.setValue(true);
            }
            else {
                isPauseProperty.setValue(false);
                synchronized (pauseLock) {
                    pauseLock.notifyAll();
                }
            }
        });
    }

    public boolean checkIfPaused() {return isPauseProperty.get();}
    public Object getPauseLock() {
        return pauseLock;
    }

}
