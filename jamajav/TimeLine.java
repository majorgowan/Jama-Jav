package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class TimeLine extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 250;
    final private int DEFAULT_HEIGHT = 9;

    private int runningTime;
    private int tickInterval;
    private double runnerPosition;
    private Timer timer;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void setRunningTime(int t) {
        runningTime = t;
    }

    public void start() {
        runnerPosition = 0;
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == timer) {
            runnerPosition += 0.1;
            repaint();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // normalize:
        double factor = 0.;
        if (runningTime != 0)
            factor = DEFAULT_WIDTH/runningTime;

        // draw a horizontal line across middle of panel
        g.setColor(Color.BLUE);
        g.drawLine(0, DEFAULT_HEIGHT/2+1,
                DEFAULT_WIDTH, DEFAULT_HEIGHT/2+1);

        // draw a vertical line at tickInterval intervals
        for (int i = 0; i <= runningTime/tickInterval; i++)
            g.drawLine((int)(factor*i*tickInterval), 0,
                    (int)(factor*i*tickInterval), DEFAULT_HEIGHT);

        double rad = 3.5;
        g.fillOval((int)(factor*runnerPosition-rad), 
                (int)(DEFAULT_HEIGHT/2+1-rad), 
                (int)(2*rad), (int)(2*rad));
    }

    TimeLine() {
        runningTime = 0;
        tickInterval = 5;
        runnerPosition = 0.0;

        setBackground(Color.WHITE);

        timer = new Timer(100, this);
    }
}


