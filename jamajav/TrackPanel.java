package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

// For resizable arrays
import java.util.ArrayList;

// For input/output
import java.io.*;

class TrackPanel extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 400;
    final private int DEFAULT_HEIGHT = 400;

    private ArrayList<Track> tracks;
    private ArrayList<JCheckBox> trackCheckBox;

    public void actionPerformed(ActionEvent ae) {
    }

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    TrackPanel() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton recordButton = new JButton("Record");
        recordButton.addActionListener(this);
        JButton playButton = new JButton("Play");
        playButton.addActionListener(this);

        buttonPanel.add(recordButton);
        buttonPanel.add(playButton);

        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        add(mainPanel,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }
}

