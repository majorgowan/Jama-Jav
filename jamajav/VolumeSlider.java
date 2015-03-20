package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;

public class VolumeSlider extends JSlider {

    final private int DEFAULT_WIDTH = 20;
    final private int DEFAULT_HEIGHT = 100;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    VolumeSlider(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
    }
}
