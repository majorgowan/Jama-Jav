package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class TitleLabelPanel extends JPanel {

    private int DEFAULT_WIDTH = 150;
    private int DEFAULT_HEIGHT = 30;

    private int displayLength;
    private boolean scrolling = false;
    private String text, displayText;
    private JLabel label;

    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public void setText(String t) {
        text = t;

        FontMetrics fontMetrics = this.getFontMetrics(label.getFont());

        if (fontMetrics.stringWidth(text) > DEFAULT_WIDTH) {
            scrolling = true;
            for (int j = text.length()-1; j > 0; j--) {
                if (fontMetrics.stringWidth(text.substring(0,j)) < DEFAULT_WIDTH) {
                    displayLength = j;
                    break;
                }
            }
            displayText = text + " ... ";
            label.setText(displayText.substring(0,displayLength-1));
        } else {
            scrolling = false;
            displayText = text;
            displayLength = text.length();
            label.setText(text);
        }
            
        //System.out.println("New displayLength : " + displayLength);
    }

    public void update() {
        if (scrolling) {
            displayText = displayText.substring(1) + displayText.charAt(0);
            label.setText(displayText.substring(0,displayLength-1));
        }
    }

    TitleLabelPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        label = new JLabel();
        add(label);
    }

    TitleLabelPanel(String text) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        label = new JLabel();
        add(label);
        setText(text);
    }
}
