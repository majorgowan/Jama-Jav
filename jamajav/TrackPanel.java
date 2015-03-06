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
    private NotesPanel notesPanel;

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        if (comStr.equals("New Track")) {
            tracks.add(new Track(metronome, clock, notesPanel));
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
        } else if (comStr.equals("Remove Selected")) {
            for (int i = tracks.size() - 1; i >= 0; i--) {
                //System.out.println("track " + i + " is " + tracks.get(i).isSelected());
                if (tracks.get(i).isSelected()) {
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

    TrackPanel(Metronome m, Clock c, NotesPanel np) {

        metronome = m;
        clock = c;
        notesPanel = np;

        tracks = new ArrayList<Track>(0);
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

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this);

        buttonPanel.add(newTrackButton);
        buttonPanel.add(playSelectedButton);
        buttonPanel.add(removeSelectedButton);
        buttonPanel.add(saveButton);

        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        MainScrollPane scrollPane = new MainScrollPane(mainPanel);

        add(scrollPane,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }
}
