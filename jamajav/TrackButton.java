package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;

class TrackButton extends JButton {

    final private int DEFAULT_WIDTH = 26;
    final private int DEFAULT_HEIGHT = 24;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }
    
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}

