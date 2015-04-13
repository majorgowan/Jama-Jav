package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For resizable arrays:
import java.util.ArrayList;

// For input/output:
import java.io.*;

// For icon images
import java.awt.image.*;
import java.net.URL;

// For observing:
import java.util.Observer;
import java.util.Observable;

class DrumsEditor extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 480;
    final private int DEFAULT_HEIGHT = 420;

    private TrackPanel trackPanel;
    private Prefs prefs;
    // the track to be edited
    private Track track;

    private TrackData trackData;
    private ArrayList<DrumSample> drumSamples;

    private TimeKeeper timeKeeper;

    private TimeLine timeLine;
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

    private JTextField periodField;
    private JTextField cyclesField;

    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
    }

    public void actionPerformed(ActionEvent ae) {

        for (int i = 0; i < drumLinePanel.size(); i++) {
            if (ae.getSource() == removeButton.get(i)) {
                linesPanel.remove(drumLinePanel.get(i));
                timeField.remove(i);
                volumeField.remove(i);
                removeButton.remove(i);
                previewButton.remove(i);
                drumTypeComboBox.remove(i);
                drumLinePanel.remove(i);
                linesPanel.revalidate();
                linesPanel.repaint();
            }
        }

        String cmdStr = ae.getActionCommand();

        switch (cmdStr) {

            case "adddrum" :
                addDrumLine();
                break;

            case "play" :
                clock.reset(0.0);
                buildPeriod();
                trackData.playback();
                break;

            case "pause" :
                trackData.togglePause();
                break;

            case "playloop" :
                clock.reset(0.0);
                buildFull();
                trackData.playback();
                break;

            case "build" :
                buildPeriod();
                break;

            case "save" :
                buildFull();
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
        Info info = new Info();
        info.setContributor(prefs.getUserName());
        info.setLocation(prefs.getUserCity());
        info.setAvatar(prefs.getAvatar());
        info.setRunningTime(trackData.getRunningTime());
        info.setTitle("Drums");
        // add notes describing track
        info.addNote("Period: " + periodField.getText() + " s");
        for (int i = 0; i < drumLinePanel.size(); i++)
            info.addNote(" " + drumTypeComboBox.get(i).getSelectedItem() + ": "
                    + timeField.get(i).getText() + " s, "
                    + "Vol: " + volumeField.get(i).getText());
        trackData.putInfo(info);
        trackData.setMonitor(new Monitor());
        // add a new track
        trackPanel.addNewTrack();
        // set trackData of new track (reorder in the future!)
        trackPanel.getTrack(trackPanel.getNTracks() - 1).setTrackData(trackData);
        trackData.setMonitor(trackPanel.getTrack(trackPanel.getNTracks() - 1).getMonitor());
        trackPanel.getTrack(trackPanel.getNTracks() - 1).refreshVisualizerAndTimeLine();
        trackPanel.refreshBigTimeLine();
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

        String[] drumTypes = new String[drumSamples.size()];
        for (int i = 0; i < drumTypes.length; i++)
            drumTypes[i] = drumSamples.get(i).getTitle();
        drumTypeComboBox.add(new JComboBox(drumTypes));

        timeField.add(new JTextField(timeDefault,5));
        volumeField.add(new JTextField(volumeDefault,5));
        removeButton.add(makeButton("General","Remove24","Remove Drum"));
        previewButton.add(makeButton("Media","Volume24","Listen"));

        timeField.get(newLineNum).addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    double s = Double.parseDouble(
                        timeField.get(newLineNum).getText());
                    if (s < 0) {
                        throw new TimeOutOfRangeException("low");
                    } else if (s > Double.parseDouble(periodField.getText())) {
                        throw new TimeOutOfRangeException("high");
                    }
                } catch (NumberFormatException nfe) {
                    timeField.get(newLineNum).setText("" + timeDefault);
                } catch (TimeOutOfRangeException toore) {
                    if (toore.getHighLow().equals("low"))
                        timeField.get(newLineNum).setText("0.0");
                    else
                        timeField.get(newLineNum).setText("" +
                            0.5*Double.parseDouble(periodField.getText()));
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
                    volumeField.get(newLineNum).setText("" + volumeDefault);
                } catch (TimeOutOfRangeException toore) {
                    volumeField.get(newLineNum).setText("0.1");
                }
            }
        });

        drumLinePanel.get(newLineNum).setLayout(
                new BoxLayout(drumLinePanel.get(newLineNum),BoxLayout.LINE_AXIS));
        drumLinePanel.get(newLineNum).add(new JLabel("Type: "));
        drumLinePanel.get(newLineNum).add(drumTypeComboBox.get(newLineNum));
        drumLinePanel.get(newLineNum).add(Box.createRigidArea(new Dimension(10,0)));
        drumLinePanel.get(newLineNum).add(new JLabel("Time: "));
        drumLinePanel.get(newLineNum).add(timeField.get(newLineNum));
        drumLinePanel.get(newLineNum).add(Box.createRigidArea(new Dimension(10,0)));
        drumLinePanel.get(newLineNum).add(new JLabel("Volume: "));
        drumLinePanel.get(newLineNum).add(volumeField.get(newLineNum));
        drumLinePanel.get(newLineNum).add(Box.createRigidArea(new Dimension(10,0)));
        drumLinePanel.get(newLineNum).add(previewButton.get(newLineNum));
        drumLinePanel.get(newLineNum).add(Box.createRigidArea(new Dimension(10,0)));
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

        revalidate();
    }

    private void refreshLinesPanel() {
        linesPanel.removeAll();
        for (int i = 0; i < drumLinePanel.size(); i++) {
            linesPanel.add(drumLinePanel.get(i));
        }
    }

    private void loadSamples() {

        InputStream injj = null;
        InputStream inbin = null;

        try {

            URL jjFile = DrumsEditor.class.getResource("/Sounds/Drums/drums.jj");
            URL binFile = DrumsEditor.class.getResource("/Sounds/Drums/drums.bin");

            injj = jjFile.openStream();
            inbin = binFile.openStream();

            injj = new BufferedInputStream(injj);
            inbin = new BufferedInputStream(inbin);

            BufferedReader br = new BufferedReader(new InputStreamReader(injj));

            // skip over jj file until number of tracks
            String[] words;
            do {
                words = br.readLine().split(" ");
            } while (!(words[0].equals("Tracks:")));
            // create appropriate number of tracks,
            int numTracks = Integer.parseInt(words[1]);

            // loop over tracks
            for (int i = 0; i < numTracks; i++) {

                br.readLine(); //INFO_BEGIN
                String title = br.readLine();

                do {
                    words = br.readLine().split(" ");
                } while(!(words[0].equals("Byte_length:")));

                int nbytes = Integer.parseInt(words[1]);

                byte[] bytes = new byte[nbytes];
                // System.out.println("Drum sample " + i + ": Reading " + nbytes + " bytes!");

                // read bytes from bin stream (from Harold book)
                int byteOffset = 0;
                while (byteOffset < nbytes) {
                    int bytesRead = inbin.read(bytes, byteOffset, nbytes - byteOffset);
                    if (bytesRead == -1) 
                        break;
                    byteOffset += bytesRead;
                }
                if (byteOffset != nbytes) {
                    throw new IOException("Only read " + byteOffset
                            + " bytes; Expected " + nbytes + " bytes");
                }

                // create new DrumSample
                drumSamples.add(new DrumSample(bytes, title));
            }

        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            if ((injj != null) || (inbin != null)) {
                try {
                    injj.close();
                    inbin.close();
                } catch (IOException e) {
                    System.out.println("Problem reading web files.");
                    System.out.println(e);
                    e.printStackTrace();
                }   
            }   
        }   
    }

    private int secondsToBytes(double seconds) {
        return (int)(seconds *
                (double)trackData.getAudioFormat().getFrameSize() 
                * (double)trackData.getAudioFormat().getFrameRate());
    }

    private void buildPeriod() {
        double period = Double.parseDouble(periodField.getText());
        int byteLength = secondsToBytes(period);
        byteLength = byteLength + byteLength%2;  // must be even number
        // System.out.println("byteArray has " + byteLength + " bytes!");

        byte[] byteArray = new byte[byteLength];

        // initialize trackData byte Array with zeros
        for (int i = 0; i < byteLength; i++)
            byteArray[i] = (byte)0;

        // loop over number of samples
        int nsamples = drumLinePanel.size();
        // System.out.println(nsamples + " samples!");
        for (int j = 0; j < nsamples; j++) {
            // System.out.println("  Sample " + j);

            int sampleIndex = drumTypeComboBox.get(j).getSelectedIndex();
            byte[] sample = drumSamples.get(sampleIndex).getBytes();
            double startTime = Double.parseDouble(timeField.get(j).getText());
            int startByte = secondsToBytes(startTime);
            startByte = startByte - byteLength%2; // should be even number
            // System.out.println("Starting at byte " + startByte);
            double volumeFactor = Double.parseDouble(volumeField.get(j).getText());

            int[] sampleSixteen = EightSixteen.toSixteen(sample);
            int[] byteArraySixteen = EightSixteen.toSixteen(byteArray);

            int space = byteArraySixteen.length - startByte/2;
            for (int i = 0; i < Math.min(sampleSixteen.length, space); i++)
                byteArraySixteen[startByte/2 + i] 
                    += sampleSixteen[i]*volumeFactor;

            byteArray = EightSixteen.toEight(byteArraySixteen);
            // System.out.println("  Sample " + j + " done!");
        }

        trackData.putBytes(byteArray);

        visualizer.setData(trackData.getBytes()); 
        visualizer.repaint();
        timeLine.setRunningTime(trackData.getRunningTime());
        timeLine.repaint();
    }

    private void buildFull() {
        buildPeriod();

        int ncycles = Integer.parseInt(cyclesField.getText());
        // System.out.println("Building " + ncycles + " cycles!");
        int periodByteLength = trackData.getBytes().length;
        // System.out.println("Each period has " + periodByteLength + " bytes!");

        byte[] periodByteArray = trackData.getBytes();
        byte[] byteArray = new byte[ncycles*periodByteLength];

        for (int i = 0; i < ncycles; i++) 
            for (int j = 0; j < periodByteLength; j++)
                byteArray[i*periodByteLength + j] = periodByteArray[j];

        trackData.putBytes(byteArray);
        timeLine.setRunningTime(trackData.getRunningTime());
        timeLine.repaint();
        visualizer.setData(trackData.getBytes());
        visualizer.repaint();
    }

    DrumsEditor(TrackPanel tpnl, Prefs prfs) {

        // need to know trackPanel in case of saving a copy of edited track
        trackPanel = tpnl;
        prefs = prfs;

        trackData = new TrackData();

        drumSamples = new ArrayList<DrumSample>(0); 

        loadSamples();

        trackData = new TrackData();

        // Visualizer
        visualizer = new Visualizer();

        // time keeping and display
        timeKeeper = new TimeKeeper(0.0);
        timeLine = new TimeLine();
        timeKeeper.setTimeLine(timeLine);
        clock = new PlainClock();
        timeKeeper.setClock(clock);
        trackData.setTimeKeeper(timeKeeper);

        // Drums lines panel
        timeField = new ArrayList<JTextField>(0);
        volumeField = new ArrayList<JTextField>(0);
        drumLinePanel = new ArrayList<JPanel>(0);
        previewButton = new ArrayList<TrackButton>(0);
        removeButton = new ArrayList<TrackButton>(0);
        drumTypeComboBox = new ArrayList<JComboBox>(0);

        linesPanel = new JPanel();
        linesPanel.setLayout(new BoxLayout(linesPanel,BoxLayout.PAGE_AXIS));

        // Add first drumsline:
        addDrumLine();

        JPanel controlPanel = new JPanel();
        JButton addDrumButton = new JButton("Add Drum");
        addDrumButton.setActionCommand("adddrum");
        addDrumButton.addActionListener(this);
        controlPanel.add(addDrumButton);
        controlPanel.add(new JLabel("    "));
        periodField = new JTextField("1.0", 6);
        cyclesField = new JTextField("10", 3);
        controlPanel.add(new JLabel("Repeat period: "));
        controlPanel.add(periodField);
        controlPanel.add(new JLabel("s     "));
        controlPanel.add(new JLabel("Repeat number: "));
        controlPanel.add(cyclesField);

        JPanel outerDrumsPanel = new JPanel(new BorderLayout());
        outerDrumsPanel.add(controlPanel,BorderLayout.NORTH);
        JPanel outerLinesPanel = new JPanel();
        outerLinesPanel.add(linesPanel);
        JScrollPane scrollPane = new JScrollPane(outerLinesPanel);
        outerDrumsPanel.add(scrollPane,BorderLayout.CENTER);

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
        playButton.setToolTipText("Play one period");
        playButton.setActionCommand("play");
        playButton.addActionListener(this);

        JButton playLoopButton = new JButton();
        imageURL = Track.class.getResource(
                "/Icons/Toolbar/Media/PlayAllFromTop24.gif");
        playLoopButton.setIcon(new ImageIcon(imageURL));
        playLoopButton.setToolTipText("Play full drum track");
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
        JButton buildButton = new JButton("Build");
        buildButton.setActionCommand("build");
        buildButton.addActionListener(this);
        JButton saveButton = new JButton("Save");
        saveButton.setActionCommand("save");
        saveButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(buildButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(outerDrumsPanel);
        add(centrePanel);
        add(buttonPanel);
    }
}

