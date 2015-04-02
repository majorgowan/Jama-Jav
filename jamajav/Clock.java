package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;

// for formatting numbers:
import java.text.DecimalFormat;

class Clock extends PlainClock {

    final private int DEFAULT_WIDTH = 50;
    final private int DEFAULT_HEIGHT = 50;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    Clock() {
        super();

        setBackground(new Color(0.75f,0.6f,0.1f));

        timeLabel.setForeground(JamaJav.darkGoldColour);
        timeLabel.setFont(new Font("SansSerif",Font.BOLD,32));
    }

}
