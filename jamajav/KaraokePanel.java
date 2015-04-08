package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class KaraokePanel extends JPanel {

    //    final private int DEFAULT_WIDTH = 210;
    //    final private int DEFAULT_HEIGHT = 165;

    private double theTime;

    private int nextLine;

    private JLabel line1, line2;
    private Karaoke karaoke;

    public void update(double t) {
        if (theTime < t) {
            theTime = t;
            if (nextLine > -1) {
                if (theTime > karaoke.getLine(nextLine).getTime()) {
                    if (nextLine < karaoke.getSize()-1) {
                        putLine(karaoke.getLine(++nextLine));
                    } else {
                        nextLine = -1;
                        putLastLine();
                    }
                }
            }
        }
    }

    public void reset(double t) {
        theTime = t;
        line1.setText(" ");
        line2.setText(" ");
        nextLine = karaoke.find(theTime);
        if (nextLine >= 0)
            putLine(karaoke.getLine(nextLine));

        revalidate();
    }

    private void putLine(KaraokeLine line) {
        // System.out.println("PUTTING NEXT KARAOKE LINE!!!");
        line1.setText(line2.getText());
        line2.setText("" + line.getTime() + ":    "
                + line.getText());
    }

    private void putLastLine() {
        // System.out.println("PUTTING LAST KARAOKE LINE!!!");
        line1.setText(line2.getText());
        line2.setText(" ");
    }

    public void reInit(Karaoke ko) {
        karaoke = ko;

        theTime = 0;
        nextLine = 0;

        line1.setText(" ");
        line2.setText(" ");
    }

    KaraokePanel(Karaoke ko) {

        line1 = new JLabel();
        line2 = new JLabel();

        reInit(ko);

        setBackground(JamaJav.clickedColour);

        line1.setForeground(JamaJav.darkGoldColour);
        line2.setForeground(JamaJav.goldColour);

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
