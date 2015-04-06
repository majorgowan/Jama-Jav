package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

class EditorTimeLine extends ActiveTimeLine {

    private int DEFAULT_WIDTH = 250;
    private int DEFAULT_HEIGHT = 44;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    protected void setParams() {
        rad = 3.5;
        lineHeight = 16; 
        barHeight = 24; 
        barThickness = 8; 
        tickLength = 6;

        tickFont = new Font("SansSerif",Font.PLAIN,9);
        endsFont = new Font("SansSerif",Font.BOLD,10);
    }

    EditorTimeLine() {
        super();
    }
}


