package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PlainTimeLine extends JPanel {

    protected double runningTime;
    protected int tickInterval;
    protected double runnerPosition;

    public void update(double time) {
        if (runnerPosition < time) {
            runnerPosition = time;
            setRunningTime(Math.max(runningTime, runnerPosition));
            repaint();
        }
    }

    public void reset(double time) {
        runnerPosition = time;
        setRunningTime(Math.max(runningTime, runnerPosition));
        repaint();
    }

    public void setRunningTime(double t) {
        runningTime = t;
    }

    PlainTimeLine() {
        tickInterval = 5;
        reset(0.0);
    }
}


