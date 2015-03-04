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

    final private int DEFAULT_WIDTH = 500;
    final private int DEFAULT_HEIGHT = 500;

    private ArrayList<Track> tracks;
    private int ntracks = 0;

    private JPanel mainPanel;

    private Metronome metronome;
    private Clock clock;

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        if (comStr.equals("New Track")) {
            tracks.add(new Track(metronome, clock));
            ntracks++;

            System.out.println("adding track ... now " + ntracks + " tracks");

            tracks.get(ntracks-1)
                .setBorder(BorderFactory.createRaisedBevelBorder());

            mainPanel.add(tracks.get(ntracks-1));
            mainPanel.add(Box.createRigidArea(new Dimension(0,10)));
            revalidate();
        }

    }

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    TrackPanel(Metronome m, Clock c) {

        metronome = m;
        clock = c;

        tracks = new ArrayList<Track>(0);

        setBackground(new Color(0.75f,0.6f,0.1f));
        setLayout(new BorderLayout());

        mainPanel = new JPanel();
        //mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
        mainPanel.setLayout(new FlowLayout());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton newTrackButton = new JButton("New Track");
        newTrackButton.addActionListener(this);

        buttonPanel.add(newTrackButton);

        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        add(mainPanel,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }
}

