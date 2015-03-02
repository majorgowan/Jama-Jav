package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

class NotesPanel extends JPanel {

    final private int DEFAULT_WIDTH = 200;
    final private int DEFAULT_HEIGHT = 300;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    NotesPanel() {

        JLabel titleLabel = new JLabel("Notes");
        titleLabel.setFont(new Font("SansSerif",Font.BOLD,13));

        setLayout(new BorderLayout());
        add(titleLabel,BorderLayout.NORTH);
    }

}
