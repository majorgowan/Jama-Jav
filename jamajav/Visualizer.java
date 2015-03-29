package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;

class Visualizer extends JPanel {

    final private int DEFAULT_WIDTH = 250;
    final private int DEFAULT_HEIGHT = 60;

    private int[] data;
    private double maxValue;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // normalize:
        double factor = 0.9*(double)(DEFAULT_HEIGHT)/2.0/maxValue;

        g.setColor(Color.YELLOW);
        for (int i=0; i<data.length; i++)
            g.drawLine(i, 
                    (int)(DEFAULT_HEIGHT/2-factor*data[i]),
                    i, (int)(DEFAULT_HEIGHT/2+factor*data[i]));

        // draw a horizontal line across middle of panel
        g.setColor(Color.BLUE);
        g.drawLine(0, DEFAULT_HEIGHT/2,
                DEFAULT_WIDTH, DEFAULT_HEIGHT/2);

    }

    // based on http://codeidol.com/java/swing/Audio/Build-an-Audio-Waveform-Display/
    public void setData(byte[] bytes) {

        int[] toReturn = EightSixteen.toSixteen(bytes);

        int binSize = toReturn.length/data.length; 

        for (int i=0; i < data.length; i++) {
            data[i] = 0;
            for (int j=0; j < binSize; j++) {
                data[i] += Math.abs(toReturn[binSize*i+j]);
            }
        }

        maxValue = 0.0;
        for (int i=0; i<data.length; i++)
            if (data[i] > maxValue)
                maxValue = data[i];

        repaint();
    }

    Visualizer() {
        setBackground(Color.BLACK);
        data = new int[DEFAULT_WIDTH];

        for (int i=0; i<data.length; i++)
            data[i] = 0;
        maxValue = 0;
    }
}
