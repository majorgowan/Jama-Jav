package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;

class Visualizer extends JPanel {

    final private int DEFAULT_WIDTH = 250;
    final private int DEFAULT_HEIGHT = 80;

    private double[] data;
    private double maxValue;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // normalize:
        double factor = 0.9*(double)(DEFAULT_HEIGHT)/2.0/maxValue;

        g.setColor(Color.YELLOW);
        for (int i=0; i<data.length; i++)
            g.drawLine(i, 
                    (int)(DEFAULT_HEIGHT/2-factor*Math.abs(data[i])),
                    i, (int)(DEFAULT_HEIGHT/2+factor*Math.abs(data[i])));

        // draw a horizontal line across middle of panel
        g.setColor(Color.BLUE);
        g.drawLine(0, DEFAULT_HEIGHT/2,
                DEFAULT_WIDTH, DEFAULT_HEIGHT/2);

    }

    private int getSixteenBitSample(int high, int low) {
        return (high << 8) + (low & 0x00ff);            
    }

    // based on http://codeidol.com/java/swing/Audio/Build-an-Audio-Waveform-Display/
    public void setData(byte[] bytes, int frameSize) {

        System.out.println(bytes.length + " " + frameSize);
        int[] toReturn = new int[bytes.length];

        int sampleIndex = 0;
        for (int t = 0; t < bytes.length;) {
            int low = (int) bytes[t];
            t++;
            int high = (int) bytes[t];
            t++;
            int sample = getSixteenBitSample(high, low);
            toReturn[sampleIndex] = sample;
            sampleIndex++;
        }

        double factor = toReturn.length / data.length;

        for (int i=0; i < data.length; i++)
            data[i] = (int)toReturn[(int)(factor*i)];

        maxValue = 0.0;
        for (int i=0; i<data.length; i++)
            if (Math.abs(data[i]) > maxValue)
                maxValue = Math.abs(data[i]);

        repaint();
    }

    Visualizer() {
        setBackground(Color.BLACK);
        data = new double[DEFAULT_WIDTH];

        for (int i=0; i<data.length; i++)
            data[i] = 0.0;
        maxValue = 0.0;
    }
}