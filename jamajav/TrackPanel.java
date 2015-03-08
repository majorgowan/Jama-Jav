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
    private ArrayList<JPanel> linePanel;
    private int ntracks = 0;

    private JPanel mainPanel;

    private Metronome metronome;
    private Clock clock;

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        if (comStr.equals("New Track")) {
            addNewTrack();
        } else if (comStr.equals("Remove Selected")) {
            for (int i = tracks.size() - 1; i >= 0; i--) {
                //System.out.println("track " + i + " is " + tracks.get(i).isSelected());
                if (tracks.get(i).isSelected()) {
                    tracks.get(i).stopRecording();
                    tracks.get(i).stopPlaying();
                    mainPanel.remove(linePanel.get(i));
                    tracks.remove(i);
                    linePanel.remove(i);
                    ntracks--;
                    //System.out.println("   Removing track " + i);
                }
            }
            refreshMainPanel();
        } else if (comStr.equals("Play Selected")) {
            for (int i = 0; i < tracks.size(); i++) 
                if (tracks.get(i).isSelected() &&
                        tracks.get(i).isNotEmpty()) 
                    tracks.get(i).playback();
        } else if (comStr.equals("playrecord")) {
            // add new track:
            addNewTrack();

            // start selected tracks playing:
            for (int i = 0; i < tracks.size(); i++) 
                if (tracks.get(i).isSelected())
                    tracks.get(i).startPlaying();

            // start new track recording
            tracks.get(tracks.size()-1).startRecording();
        } else if (comStr.equals("allstop")) {
            allStop();
        }
    }

    private void refreshMainPanel() {

        mainPanel.removeAll();

        for (int i=0; i<ntracks; i++) {
            mainPanel.add(linePanel.get(i));
        }
        mainPanel.revalidate();
        repaint();
    }

    private void addNewTrack() {
        tracks.add(new Track(metronome, clock));
        linePanel.add(new JPanel());
        ntracks++;

        //System.out.println("adding track ... now " + ntracks + " tracks");

        tracks.get(ntracks-1)
            .setBorder(BorderFactory.createRaisedBevelBorder());

        linePanel.get(ntracks-1).add(tracks.get(ntracks-1));
        linePanel.get(ntracks-1)
            .setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        mainPanel.add(linePanel.get(ntracks-1));
        revalidate();
    }

    private void allStop() {
            for (int i = 0; i < tracks.size(); i++) { 
                tracks.get(i).stopPlaying();
                tracks.get(i).stopRecording();
            }
    }

    TrackPanel(Metronome m, Clock c) {

        metronome = m;
        clock = c;

        tracks = new ArrayList<Track>(0);
        linePanel = new ArrayList<JPanel>(0);

        setBackground(new Color(0.75f,0.6f,0.1f));
        setLayout(new BorderLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

        // create and add buttons to buttonPanel:
        JPanel buttonPanel = new JPanel(new GridLayout(2,4));

        JButton newTrackButton = new JButton("New Track");
        newTrackButton.addActionListener(this);

        JButton playSelectedButton = new JButton("Play Selected");
        playSelectedButton.addActionListener(this);

        JButton playRecordButton = new JButton("Play Sel / Rec new");
        playRecordButton.setActionCommand("playrecord");
        playRecordButton.addActionListener(this);

        JButton allStopButton = new JButton("All Stop!");
        allStopButton.setActionCommand("allstop");
        allStopButton.addActionListener(this);

        JButton removeSelectedButton = new JButton("Remove Selected");
        removeSelectedButton.addActionListener(this);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this);

        buttonPanel.add(newTrackButton);
        buttonPanel.add(playSelectedButton);
        buttonPanel.add(playRecordButton);
        buttonPanel.add(removeSelectedButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(allStopButton);

        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        MainScrollPane scrollPane = new MainScrollPane(mainPanel);

        add(scrollPane,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }
}

