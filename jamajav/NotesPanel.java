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

    private JLabel text;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void showNotes(Notes notes) {
        text.setText(notes.getText());
        revalidate();
    }

    NotesPanel() {
        setBackground(new Color(0.95f,0.9f,0.8f));

        Font textFont = new Font("SansSerif",Font.PLAIN,10);
        text = new JLabel(" ");
        text.setVerticalTextPosition(JLabel.TOP);
        text.setFont(textFont);

        JLabel titleLabel = new JLabel("Notes");
        titleLabel.setFont(new Font("SansSerif",Font.BOLD,13));

        setLayout(new BorderLayout());
        add(titleLabel,BorderLayout.NORTH);

        add(text,BorderLayout.CENTER);
    }

}
