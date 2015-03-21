package jamajav;

// For observing:
import java.util.Observer;
import java.util.Observable;

class Stopper extends Observable {

    private boolean isStopped;

    public boolean getValue() {
        return isStopped;
    }

    public void stop() {
        isStopped = true;
        setChanged();
        notifyObservers();
    }

    public void start() {
        isStopped = false;
    }

    Stopper() {
        isStopped = true;
    }
}
