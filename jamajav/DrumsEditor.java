package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For resizable arrays:
import java.util.ArrayList;

// For icon images
import java.awt.image.*;
import java.net.URL;

// For observing:
import java.util.Observer;
import java.util.Observable;

class DrumsEditor extends JPanel implements ActionListener {

    private TrackPanel trackPanel;
    // the track to be edited
    private Track track;

    private TrackData trackData;
    private ArrayList<DrumSample> drumSamples;

    private TimeKeeper timeKeeper;

    private PlainTimeLine timeLine;
    private Visualizer visualizer;

    private PlainClock clock;

    // Swing stuff
    private JPanel linesPanel;
    private ArrayList<JTextField> timeField;
    private ArrayList<JTextField> volumeField;
    private ArrayList<JPanel> drumLinePanel;
    private ArrayList<TrackButton> removeButton;
    private ArrayList<TrackButton> previewButton;
    private ArrayList<JComboBox> drumTypeComboBox;

    public void actionPerformed(ActionEvent ae) {

        String cmdStr = ae.getActionCommand();

        switch (cmdStr) {
            case "play" :
                clock.reset(0.0);
                trackData.playback();
                break;

            case "playloop" :
                clock.reset(0.0);
                trackData.playback();
                break;

            case "save" :
                saveAsNew();
                // exit 
                trackData.stopPlaying();
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();
                break;

            case "cancel" :
                // exit without saving changes
                trackData.stopPlaying();
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();
                break;
        }
    }

    private void saveAsNew() {
        // Replace current Info with a copy (adds words "copy of" to title)
        trackData.putInfo(new Info(trackData.getInfo(), "Drums"));
        trackData.setMonitor(new Monitor());
        // add a new track
        trackPanel.addNewTrack();
        // set trackData of new track (reorder in the future!)
        trackPanel.getTrack(trackPanel.getNTracks() - 1).setTrackData(trackData);
        trackData.setMonitor(trackPanel.getTrack(trackPanel.getNTracks() - 1).getMonitor());
        trackPanel.getTrack(trackPanel.getNTracks() - 1).refreshVisualizerAndTimeLine();
    }

    private TrackButton makeButton(String buttonType, String imageName, 
            String toolTipText) {

        String imgLocation = "/Icons/Toolbar/" + buttonType + "/" + imageName + ".gif";
        URL imageURL = ToolBar.class.getResource(imgLocation);

        TrackButton button = new TrackButton();
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        button.setIcon(new ImageIcon(imageURL));

        return button;
    }

    private void addDrumLine() {
        drumLinePanel.add(new JPanel());
        final int newLineNum = drumLinePanel.size()-1;
        final String timeDefault = "0.0";
        final String volumeDefault = "1.0";

        drumTypeComboBox.add(new JComboBox());  // must set list of drumTypes (after jj file etc)
        timeField.add(new JTextField(timeDefault,5));
        volumeField.add(new JTextField(volumeDefault,5));
        removeButton.add(makeButton("General","Remove24","Remove Drum"));
        previewButton.add(makeButton("Media","PlayAll24","Preview"));

        timeField.get(newLineNum).addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    double s = Double.parseDouble(
                        timeField.get(newLineNum).getText());
                    if (s < 0) {
                        throw new TimeOutOfRangeException("low");
                    }
                } catch (NumberFormatException nfe) {
                    timeField.get(newLineNum).setText("" + timeDefault);
                } catch (TimeOutOfRangeException toore) {
                    timeField.get(newLineNum).setText("0.0");
                }
            }
        });

        volumeField.get(newLineNum).addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    double s = Double.parseDouble(
                        volumeField.get(newLineNum).getText());
                    if (s <= 0) {
                        throw new TimeOutOfRangeException("low");
                    }
                } catch (NumberFormatException nfe) {
                    timeField.get(newLineNum).setText("" + volumeDefault);
                } catch (TimeOutOfRangeException toore) {
                    timeField.get(newLineNum).setText("0.1");
                }
            }
        });

        drumLinePanel.get(newLineNum).setLayout(
                new BoxLayout(drumLinePanel.get(newLineNum),BoxLayout.LINE_AXIS));
        drumLinePanel.get(newLineNum).add(new JLabel("Drum Type: "));
        drumLinePanel.get(newLineNum).add(drumTypeComboBox.get(newLineNum));
        drumLinePanel.get(newLineNum).add(Box.createRigidArea(new Dimension(5,0)));
        drumLinePanel.get(newLineNum).add(new JLabel("Time: "));
        drumLinePanel.get(newLineNum).add(timeField.get(newLineNum));
        drumLinePanel.get(newLineNum).add(Box.createRigidArea(new Dimension(5,0)));
        drumLinePanel.get(newLineNum).add(new JLabel("Volume: "));
        drumLinePanel.get(newLineNum).add(volumeField.get(newLineNum));
        drumLinePanel.get(newLineNum).add(Box.createRigidArea(new Dimension(5,0)));
        drumLinePanel.get(newLineNum).add(previewButton.get(newLineNum));
        drumLinePanel.get(newLineNum).add(Box.createRigidArea(new Dimension(5,0)));
        drumLinePanel.get(newLineNum).add(removeButton.get(newLineNum));

        drumLinePanel.get(newLineNum).setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        linesPanel.add(drumLinePanel.get(newLineNum));

        refreshLinesPanel();
        linesPanel.revalidate();

        timeField.get(newLineNum).requestFocusInWindow();
        timeField.get(newLineNum).selectAll();
        linesPanel.scrollRectToVisible(
                new Rectangle(
                    0,(int)linesPanel.getPreferredSize().getHeight(),10,10));
    }

    private void refreshLinesPanel() {
        linesPanel.removeAll();
        for (int i = 0; i < drumLinePanel.size(); i++) {
            linesPanel.add(drumLinePanel.get(i));
        }
    }

    private void loadSamples() {
        // parse jj file
        // load drums.bin file
    }

    DrumsEditor(TrackPanel tpnl) {

        // need to know trackPanel in case of saving a copy of edited track
        trackPanel = tpnl;

        drumSamples = new ArrayList<DrumSample>(0); 
        trackData = new TrackData();

        // Visualizer
        visualizer = new Visualizer();

        // time keeping and display
        timeKeeper = new TimeKeeper(0.0);
        timeLine = new PlainTimeLine();
        timeKeeper.setTimeLine(timeLine);
        clock = new PlainClock();
        timeKeeper.setClock(clock);
        trackData.setTimeKeeper(timeKeeper);

        // Drums lines panel
        timeField = new ArrayList<JTextField>(0);
        volumeField = new ArrayList<JTextField>(0);
        drumLinePanel = new ArrayList<JPanel>(0);
        removeButton = new ArrayList<TrackButton>(0);
        drumTypeComboBox = new ArrayList<JComboBox>(0);

        linesPanel = new JPanel();
        linesPanel.setLayout(new BoxLayout(linesPanel,BoxLayout.PAGE_AXIS));

        JPanel outerDrumsPanel = new JPanel();
        outerDrumsPanel.add(linesPanel);
        JScrollPane scrollPane = new JScrollPane(outerDrumsPanel);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

        JPanel outerVisualPanel = new JPanel(new FlowLayout());
        outerVisualPanel.add(visualizer);
        JPanel outerTimePanel = new JPanel(new FlowLayout());
        outerTimePanel.add(timeLine);

        JPanel playPanel = new JPanel(new FlowLayout());

        JButton playButton = new JButton();
        URL imageURL = Track.class.getResource(
                "/Icons/Toolbar/Media/PlayFromTop24.gif");
        playButton.setIcon(new ImageIcon(imageURL));
        playButton.setToolTipText("play");
        playButton.setActionCommand("playall");
        playButton.addActionListener(this);

        JButton playLoopButton = new JButton();
        imageURL = Track.class.getResource(
                "/Icons/Toolbar/Media/Play24.gif");
        playLoopButton.setIcon(new ImageIcon(imageURL));
        playLoopButton.setToolTipText("Play loop");
        playLoopButton.setActionCommand("playloop");
        playLoopButton.addActionListener(this);

        JButton pauseButton = new JButton();
        imageURL = Track.class.getResource(
                "/Icons/Toolbar/Media/Pause24.gif");
        pauseButton.setIcon(new ImageIcon(imageURL));
        pauseButton.setToolTipText("Pause playback");
        pauseButton.setActionCommand("pause");
        pauseButton.addActionListener(this);

        playPanel.add(playButton);
        playPanel.add(playLoopButton);
        playPanel.add(pauseButton);

        mainPanel.add(outerTimePanel);
        mainPanel.add(outerVisualPanel);
        mainPanel.add(playPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));

        // Centre panel
        JPanel centrePanel = new JPanel(new FlowLayout());
        centrePanel.add(mainPanel);
        centrePanel.add(clock);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addDrumButton = new JButton("Add Drum");
        addDrumButton.setActionCommand("adddrum");
        addDrumButton.addActionListener(this);
        JButton saveButton = new JButton("Save changes");
        saveButton.setActionCommand("save");
        saveButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(addDrumButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.NORTH);
        add(centrePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}

