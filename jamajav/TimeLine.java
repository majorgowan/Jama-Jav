package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class TimeLine extends PlainTimeLine {

    final private int DEFAULT_WIDTH = 250;
    final private int DEFAULT_HEIGHT = 19;

    private static Font tickFont = new Font("SansSerif",Font.PLAIN,9);

    private BigTimeLine bigTimeLine = null;
    private PlainClock clock = null;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tickLength = 7;
        int lineMiddle = (int)(2*getHeight()/3);
        int tickTop = (int)(lineMiddle - (tickLength+1)/2);

        // normalize:
        double factor = 0.0;
        if (runningTime != 0.0)
            factor = (double)getWidth()/runningTime;

        // draw a horizontal line across middle of panel
        g.setColor(Color.BLUE);
        g.drawLine(0, lineMiddle,
                getWidth(), lineMiddle);

        // draw a vertical line at tickInterval intervals
        g.setFont(tickFont);
        for (int i = 0; i <= runningTime/tickInterval; i++) {
            g.drawLine((int)(factor*i*tickInterval), tickTop,
                    (int)(factor*i*tickInterval), tickTop+tickLength);
            if (i%2 == 0)
                g.drawString("" + i*tickInterval,
                        (int)(factor*i*tickInterval)+1, 
                        (int)(tickTop-1));
        }

        double rad = 3.5;
        g.fillOval((int)(factor*runnerPosition-rad), 
                (int)(lineMiddle-rad), 
                (int)(2*rad), (int)(2*rad));
    }

    public void setTime(double time) {
        super.setTime(time);
        if (bigTimeLine != null) {
            bigTimeLine.setTime(time);
            clock.setTime(time);
        }
    }

    public void addTime(double time) {
        super.addTime(time);
        if (bigTimeLine != null) {
            bigTimeLine.setTime(runnerPosition);
            clock.setTime(runnerPosition);
        }
    }

    TimeLine() {
        super();
        //setBackground(Color.WHITE);
    }

    TimeLine(BigTimeLine btl, PlainClock pc) {
        super();
        bigTimeLine = btl;
        clock = pc;
    }
}


