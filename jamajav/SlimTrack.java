// ref: http://www.developer.com/java/other/article.php/1572251/Java-Sound-Getting-Started-Part-1-Playback.htm

package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For icon images
import java.awt.image.*;
import java.net.URL;

class SlimTrack extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 525;
    final private int DEFAULT_HEIGHT = 30;

    // ancestor
    private Track fatTrack;

    private Prefs prefs;

    private boolean isClicked = false;
    private Color clickedColor, unclickedColor;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();
    }

    // Basic Track constructor
    SlimTrack(Track trk) {

        fatTrack = trk;

        clickedColor = Color.LIGHT_GRAY;
        unclickedColor = getBackground();

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    if (fatTrack.isSelected()) {
                        fatTrack.setSelected(false);
                    } else {
                        fatTrack.setSelected(true);
                    }
                }
            }
        });

    }


}
