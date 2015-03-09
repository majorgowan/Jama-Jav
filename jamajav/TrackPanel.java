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

        switch (comStr) {
            case ("New") :
                newDoc();
                break;

            case ("save") :
                save();
                break;

            case ("New Track") :
                addNewTrack();
                break;

            case ("metroset") :
                metronome.getSettings();
                break;

            case ("Remove Selected") :
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
                break;

            case ("Play Selected") :
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected() &&
                            tracks.get(i).isNotEmpty()) 
                        tracks.get(i).playback();
                break;

            case ("selectall") :
                boolean getState = false;    // none are selected
                for (int i = 0; i < tracks.size(); i++)
                    if (tracks.get(i).isSelected())
                        getState = true;

                if (getState)               // if any are selected, unselect-all
                    for (int i = 0; i < tracks.size(); i++)
                        tracks.get(i).setSelected(false);
                else                        // else select-all
                    for (int i = 0; i < tracks.size(); i++)
                        tracks.get(i).setSelected(true);
                break;

            case ("playrecord") :
                // add new track:
                addNewTrack();

                // start selected tracks playing:
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected())
                        tracks.get(i).startPlaying();

                // start new track recording
                tracks.get(tracks.size()-1).startRecording();
                break;

            case ("allstop") :
                allStop();
                break;
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

    private void save() {

        // get filename root

        // open two files: root.bin for binary, root.jj for ASCII

        // write number of tracks to ASCII file

        // loop over tracks

        //      getInfo(), write to jj file
        //      getBytes(), write byte-array length to jj file
        //      write bytes to binary file
        //      write ENDINFO to jj file

        // close files
    }

    private void open() {

        // FileChooser: list jj files

        // open bin and jj files

        // parse jj file, create appropriate number of tracks,

        // loop over tracks

        //      construct Info object with information from jj file
        //      tracks.get(i).putInfo(info);

        //      read number of bytes from jj file
        //      read bytes from bin file
        //      tracks.get(i).putBytes(bytes);

        // close files
    }

    private void newDoc() {
        for (int i = tracks.size()-1; i >= 0; i++) {
            remove(linePanel.get(i));
            linePanel.remove(i);
            tracks.remove(i);
        }
        repaint();
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

        JButton selectAllButton = new JButton("(Un)select All");
        selectAllButton.setActionCommand("selectall");
        selectAllButton.addActionListener(this);

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

        buttonPanel.add(newTrackButton);
        buttonPanel.add(selectAllButton);
        buttonPanel.add(playRecordButton);
        buttonPanel.add(removeSelectedButton);
        buttonPanel.add(playSelectedButton);
        buttonPanel.add(allStopButton);

        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        MainScrollPane scrollPane = new MainScrollPane(mainPanel);

        add(scrollPane,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }
}

