package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class KaraokePanel extends JPanel {

    //    final private int DEFAULT_WIDTH = 210;
    //    final private int DEFAULT_HEIGHT = 165;

    private double theTime;

    private JLabel line1, line2;
    private Karaoke karaoke;
        
    public void update(double t) {
        if (theTime < t) {
            theTime = t;
        }
    }

    public void reset(double t) {
        theTime = t;
    }

    public void putLine(KaraokeLine line) {
        if (!line2.getText().equals(" "))
            line1.setText(line2.getText());

        line2.setText("" + line.getTime() + ":"
                + line.getText());
    }

    KaraokePanel(Karaoke ko) {

        karaoke = ko;

        theTime = 0;

        line1 = new JLabel(" ");
        line2 = new JLabel(" ");

        line1.setForeground(JamaJav.goldColour);
        line2.setForeground(JamaJav.darkGoldColour);

        Font font = new Font("SansSerif",Font.BOLD,20);
        line1.setFont(font);
        line2.setFont(font);

        JPanel line1Panel = new JPanel(new FlowLayout());
        line1Panel.add(line1);
        JPanel line2Panel = new JPanel(new FlowLayout());
        line2Panel.add(line2);

        setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createLineBorder(JamaJav.goldColour));

        add(line1Panel);
        add(line2Panel);
    }
}
