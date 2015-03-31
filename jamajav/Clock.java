package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

class Clock extends PlainClock {

    final private int DEFAULT_WIDTH = 50;
    final private int DEFAULT_HEIGHT = 50;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }


    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();

        if (ae.getSource() == timer) {
            theTime += 0.001*precision;

            timeLabel.setText(df.format(theTime));
            repaint();

        } else if (comStr.equals("start")) {
            if (timer.isRunning())
                timer.stop();
            else
                timer.start();
        } else if (comStr.equals("reset")) {
            reset();
            repaint();
        }
    }

    Clock() {
        super();

        setBackground(new Color(0.75f,0.6f,0.1f));

        timeLabel.setForeground(JamaJav.darkGoldColour);
        timeLabel.setFont(new Font("SansSerif",Font.BOLD,32));
    }

}
