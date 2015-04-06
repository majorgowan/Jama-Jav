package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

class BigTimeLine extends ActiveTimeLine {

    private int DEFAULT_WIDTH = 300;
    private int DEFAULT_HEIGHT = 65;

    public void setRunningTime(double t) {
        super.setRunningTime(t);

        if (t == 0) {
            setToolTip(false);
        } else {
            setToolTip(true);
        }
    }

    private void setToolTip(boolean onoff) {
        if (onoff) {
            setToolTipText("<html>"
                    + "Jam Time Line:<br>"
                    + "Move green and red sliders to<br>"
                    + "set start and end time of playback!"
                    + "</html>");
        } else {
            setToolTipText(null);
        }
    }

    BigTimeLine() {
        super();
    }
}


