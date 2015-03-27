// ref: http://www.developer.com/java/other/article.php/1572251/Java-Sound-Getting-Started-Part-1-Playback.htm

package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Track extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 425;
    final private int DEFAULT_HEIGHT = 120;

    private TrackData trackData;

    private boolean isClicked = false;
    private Color clickedColor, unclickedColor;

    // ancestors
    private JFrame jfrm;
    private TrackPanel trackPanel;

    private Prefs prefs;
    private Metronome metronome;
    private Clock clock; 

    private JButton recordButton;
    private JButton playButton;
    private JButton editButton;
    private JButton infoButton;
    private VolumeSlider slider;

    private TimeLine timeLine;
    private Visualizer visualizer;
    private Monitor monitor;
    private JPanel titlePanel;
    private JLabel titleLabel;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        switch (comStr) {
            case ("Rec/Stop") :
                if (!trackData.isCapturing()) {
                    startRecording();
                } else {
                    stopRecording();
                }
                break;

            case ("Play") :
                stopPlaying();
                startPlaying();
                break;

            case ("editinfo") :
                Info info = editInfo();
                trackData.putInfo(info);
                setToolTip(info);
                break;

            case ("edittrack") :
                editTrack();
                break;
        }
    }

    public boolean isSelected() {
        return isClicked;
    }

    public boolean isNotEmpty() {
        return trackData.isNotEmpty();
    }

    public void setSelected(boolean b) {
        isClicked = b;
        if (isClicked) {
            titlePanel.setBackground(clickedColor);
        } else {
            titlePanel.setBackground(unclickedColor);
        }
        repaint();
    }

    public void startRecording() {
        clock.restart();
        metronome.start();
        trackData.record(visualizer, timeLine);
        trackData.setNotEmpty(true);
    }

    public void stopRecording() {
        // System.out.println("Stopping recording . . .");
        metronome.stop();
        clock.stop();
        trackData.stopCapturing();
    }

    public void startPlaying() {
        if (trackData.isNotEmpty()) {
            clock.restart();
            playback();
        }
    }

    public void pausePlaying() {
        if (trackData.isNotEmpty() && (!trackData.getStopPlay().getValue())) {
            trackData.togglePause();
            timeLine.toggle();
        }
    }

    public void stopPlaying() {
        clock.stop();
        trackData.stopPlaying();
    }

    private void editTrack() {
        // System.out.println("Trackdata has " + trackData.getBytes().length + " bytes!");

        // open dialog with a infopanel
        final JDialog editTrackDialog = new JDialog(jfrm, "Track editor", true);
        editTrackDialog.setLocationRelativeTo(jfrm);
        editTrackDialog.getContentPane().setLayout(new BorderLayout());
        editTrackDialog.getContentPane().add(
                new TrackEditor(this, trackPanel), BorderLayout.CENTER);
        editTrackDialog.revalidate();
        editTrackDialog.pack();
        editTrackDialog.setVisible(true);
        // refresh Visualizer and TimeLine in case it's changed
        refreshVisualizerAndTimeLine();
    }

    private Info editInfo() {
        Info info = trackData.getInfo();
        // open dialog with a infopanel
        final JDialog infoDialog = new JDialog(jfrm, "Edit track info", true);
        infoDialog.setLocationRelativeTo(jfrm);
        infoDialog.getContentPane().setLayout(new BorderLayout());
        infoDialog.getContentPane().add(
                new InfoPanel(info), BorderLayout.CENTER);
        infoDialog.revalidate();
        infoDialog.pack();
        infoDialog.setVisible(true);
        return info;
    }

    public void setToolTip(Info info) {
        String toolTip = "<html><h3>" + info.getTitle() + "<br>";

        toolTip += "by: " + info.getContributor() + "<br>";

        toolTip += "length: " + info.getRunningTime() + "s" + "</h3>";

        toolTip += info.getAllNotes();

        toolTip += "</list><br><br>" + info.getDate();

        toolTip += ", " + info.getLocation();

        visualizer.setToolTipText(toolTip);

        // probably bad form to put this here, but ...
        titleLabel.setText(info.getTitle());
    }

    public void resetToolTip() {
        setToolTip(trackData.getInfo());
    }

    public Info getInfo() {
        return trackData.getInfo();
    }

    public int getVolume() {
        return slider.getValue();
    }

    public void putInfo(Info in) {
        trackData.putInfo(in);
    }

    public byte[] getBytes() {
        return trackData.getBytes();
    }

    public void putBytes(byte[] bytes) {
        trackData.putBytes(bytes);
        visualizer.setData(bytes);
        timeLine.setRunningTime(trackData.getInfo().getRunningTime());
    }

    public void playback() {
        trackData.playback(slider.getValue(), timeLine);
    }

    public void putTrackData(TrackData td) {
        trackData = td;
    }

    public void refreshVisualizerAndTimeLine() {
        visualizer.setData(trackData.getBytes());
        visualizer.repaint();
        timeLine.setRunningTime(trackData.getInfo().getRunningTime());
        timeLine.repaint();
        resetToolTip();
        revalidate();
    }
    
    public TrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(TrackData td) {
        trackData = td;
        trackData.addStopperObserver(trackPanel);
    }

    public Monitor getMonitor() {
        return monitor;
    }

    // Basic Track constructor
    Track(JFrame frm, TrackPanel tpnl, Metronome m, Clock c, Prefs p) {

        jfrm = frm;
        trackPanel = tpnl;

        metronome = m;
        clock = c;
        prefs = p;

        clickedColor = Color.LIGHT_GRAY;
        unclickedColor = getBackground();

        // trackData has the audio stuff and info
        trackData = new TrackData();
        trackData.addStopperObserver(trackPanel);

        Info info = new Info();
        info.setContributor(prefs.getUserName());
        info.setLocation(prefs.getUserCity());
        info.setAvatar(prefs.getAvatar());
        trackData.putInfo(info);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        timeLine = new TimeLine();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

        titlePanel = new JPanel(new FlowLayout());
        titleLabel = new JLabel(info.getTitle());
        titlePanel.add(titleLabel);

        visualizer = new Visualizer();
        setToolTip(info);

        monitor = new Monitor();
        trackData.setMonitor(monitor);

        JPanel outerVisualPanel = new JPanel(new FlowLayout());
        outerVisualPanel.add(visualizer);
        JPanel outerTimePanel = new JPanel(new FlowLayout());
        outerTimePanel.add(timeLine);
        mainPanel.add(outerTimePanel);
        mainPanel.add(outerVisualPanel);
        mainPanel.add(titlePanel);
        JPanel outerMonitorPanel = new JPanel(new FlowLayout());
        outerMonitorPanel.add(monitor);

        // Buttons
        Font buttonFont = new Font("SansSerif",Font.BOLD,10);

        recordButton = new JButton("Rec/Stop");
        playButton = new JButton("Play");
        editButton = new JButton("Edit");
        editButton.setActionCommand("edittrack");
        infoButton = new JButton("Edit info");
        infoButton.setActionCommand("editinfo");

        recordButton.addActionListener(this);
        playButton.addActionListener(this);
        editButton.addActionListener(this);
        infoButton.addActionListener(this);

        recordButton.setFont(buttonFont);
        playButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        infoButton.setFont(buttonFont);

        slider = new VolumeSlider(JSlider.VERTICAL, 0, 10, 7);
        slider.setFont(buttonFont);
        JPanel outerSliderPanel = new JPanel(new FlowLayout());
        outerSliderPanel.add(slider);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4,1));

        buttonPanel.add(recordButton);
        buttonPanel.add(playButton);
        buttonPanel.add(editButton);
        buttonPanel.add(infoButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

        add(buttonPanel);
        add(Box.createRigidArea(new Dimension(5,0)));
        add(mainPanel);
        add(outerMonitorPanel);
        add(outerSliderPanel);

        // create border for Track
        setBorder(BorderFactory.createRaisedSoftBevelBorder());

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    if (isSelected()) {
                        setSelected(false);
                    } else {
                        setSelected(true);
                    }
                }
            }
        });

    }


}
