package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PlainTimeLine extends JPanel implements ActionListener {

    protected double runningTime;
    protected int tickInterval;
    protected double runnerPosition;
    protected Timer timer;

    public void setTime(double time) {
        runnerPosition = time;
    }

    public void setRunningTime(double t) {
        runningTime = t;
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void toggle() {
        if (timer.isRunning())
            timer.stop();
        else
            timer.start();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == timer) {
            runnerPosition += 0.1;
            repaint();
        }
    }

    PlainTimeLine() {
        runningTime = 0.0;
        tickInterval = 5;
        runnerPosition = 0.0;
        timer = new Timer(100, this);
    }
}


