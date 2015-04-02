package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;

class Monitor extends JPanel {

    final private int DEFAULT_WIDTH = 20;
    final private int DEFAULT_HEIGHT = 100;

    private int[] data;
    private double level, maxValue;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int bottom, left, right, top;

        Color lineColor = Color.YELLOW;

        // normalize:
        double factor = (double)(DEFAULT_HEIGHT-8)
            / maxValue / 15000;

        g.setColor(Color.YELLOW);
        bottom = DEFAULT_HEIGHT-4;
        left = DEFAULT_WIDTH/2-4;
        right = DEFAULT_WIDTH/2+4;
        top = bottom - (int)(factor*level);

        g.fillRect(left, top, right-left, bottom-top); 
    }

    // based on http://codeidol.com/java/swing/Audio/Build-an-Audio-Waveform-Display/
    public void setData(byte[] bytes) {

        // System.out.println(bytes.length + " " + frameSize);
        int[] toReturn = EightSixteen.toSixteen(bytes);

        double oldLevel = level;
        level = 0.0;
        for (int i = 0; i < toReturn.length; i++)
            level += Math.pow(toReturn[i],2);
        level = Math.sqrt(level/toReturn.length);

        // "oldLevel" gives a bit of inertia
        level = (2*oldLevel + level) /3;

        repaint();
    }

    Monitor() {
        setBackground(Color.BLACK);
        maxValue = 1;
    }
}
