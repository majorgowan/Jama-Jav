package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;

// for formatting numbers:
import java.text.DecimalFormat;

class PlainClock extends JPanel {

    final private int DEFAULT_WIDTH = 80;
    final private int DEFAULT_HEIGHT = 50;

    protected double theTime = 0.0;

    protected JLabel timeLabel;

    protected final DecimalFormat df = new DecimalFormat("##0.0");

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void update(double t) {
        if (theTime < t) {
            theTime = t;
            timeLabel.setText(df.format(theTime));
            //System.out.println("Clock should be showing " + theTime);
            repaint();
        }
    }

    public void reset(double t) {
        theTime = t;
        timeLabel.setText(df.format(theTime));
        repaint();
    }

    PlainClock() {
        theTime = 0.0;
        timeLabel = new JLabel(df.format(theTime));

        JPanel timerPanel = new JPanel(new FlowLayout());
        timeLabel.setFont(new Font("SansSerif",Font.BOLD,30));

        timerPanel.add(timeLabel);

        setLayout(new BorderLayout());
        add(timerPanel,BorderLayout.CENTER);
    }

}
