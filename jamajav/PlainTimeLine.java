package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PlainTimeLine extends JPanel {

    protected double runningTime;
    protected int tickInterval;
    protected double runnerPosition;

    public void setTime(double time) {
        runnerPosition = time;
        repaint();
    }

    public void addTime(double time) {
        runnerPosition += time;
        repaint();
    }

    public double getTime() {
        return runnerPosition;
    }

    public void setRunningTime(double t) {
        runningTime = t;
        // System.out.println("TimeLine running time set at " + runningTime + " seconds");
    }

    PlainTimeLine() {
        runningTime = 0.0;
        tickInterval = 5;
        runnerPosition = 0.0;
    }
}


