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

    private ArrayList<Track> tracks;
    private ArrayList<JCheckBox> trackCheckBoxes;
    private ArrayList<JPanel> linePanel;
    private int ntracks = 0;

    private JPanel mainPanel;

    private Metronome metronome;
    private Clock clock;

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        if (comStr.equals("New Track")) {
            tracks.add(new Track(metronome, clock));
            trackCheckBoxes.add(new JCheckBox("",true));
            linePanel.add(new JPanel());
            ntracks++;

            System.out.println("adding track ... now " + ntracks + " tracks");

            tracks.get(ntracks-1)
                .setBorder(BorderFactory.createRaisedBevelBorder());

            linePanel.get(ntracks-1).add(trackCheckBoxes.get(ntracks-1));
            linePanel.get(ntracks-1).add(tracks.get(ntracks-1));
            mainPanel.add(linePanel.get(ntracks-1));
            mainPanel.add(Box.createRigidArea(new Dimension(0,10)));
            revalidate();
        } else if (comStr.equals("Remove Selected")) {
            for (int i = tracks.size() - 1; i >= 0; i--)
                if (trackCheckBoxes.get(i).isSelected()) {
                    mainPanel.remove(linePanel.get(i));
                    tracks.remove(i);
                    trackCheckBoxes.remove(i);
                    linePanel.remove(i);
                    ntracks--;
                }
            repaint();
        }
    }

    TrackPanel(Metronome m, Clock c) {

        metronome = m;
        clock = c;

        tracks = new ArrayList<Track>(0);
        trackCheckBoxes = new ArrayList<JCheckBox>(0);
        linePanel = new ArrayList<JPanel>(0);

        setBackground(new Color(0.75f,0.6f,0.1f));
        setLayout(new BorderLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
        //mainPanel.setLayout(new FlowLayout());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton newTrackButton = new JButton("New Track");
        newTrackButton.addActionListener(this);

        JButton playSelectedButton = new JButton("Play Selected");
        playSelectedButton.addActionListener(this);

        JButton removeSelectedButton = new JButton("Remove Selected");
        removeSelectedButton.addActionListener(this);

        buttonPanel.add(newTrackButton);
        buttonPanel.add(playSelectedButton);
        buttonPanel.add(removeSelectedButton);

        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        MainScrollPane scrollPane = new MainScrollPane(mainPanel);

        add(scrollPane,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }
}

