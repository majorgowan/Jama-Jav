package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;

public class VolumeSlider extends JSlider {

    private int DEFAULT_WIDTH = 20;
    private int DEFAULT_HEIGHT = 100;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void makeSlim() {
        DEFAULT_WIDTH = 250;
        DEFAULT_HEIGHT = 15;
        setOrientation(SwingConstants.HORIZONTAL);
    }

    public void makeFat() {
        DEFAULT_WIDTH = 20;
        DEFAULT_HEIGHT = 100;
        setOrientation(SwingConstants.VERTICAL);
    }

    VolumeSlider(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
    }
}
