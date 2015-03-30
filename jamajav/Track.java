// ref: http://www.developer.com/java/other/article.php/1572251/Java-Sound-Getting-Started-Part-1-Playback.htm

package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For icon images
import java.awt.image.*;
import java.net.URL;

class Track extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 425;
    final private int DEFAULT_HEIGHT = 130;

    private TrackData trackData;

    private boolean isClicked = false;
    private Color clickedColor, unclickedColor;

    // ancestors
    private JFrame jfrm;
    private TrackPanel trackPanel;

    private Prefs prefs;
    private Metronome metronome;
    private Clock clock; 

    private LeftTrackButtonPanel trackButtonPanel;
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

            case ("recordstop") :
                if (!trackData.isCapturing()) {
                    startRecording();
                } else {
                    stopRecording();
                }
                break;

            case ("playtrack") :
                stopPlaying();
                startPlaying();
                break;

            case ("edittrack") :
                if (isNotEmpty()) {
                    editTrack();
                    trackPanel.refreshBigTimeLine();
                }
                break;

            case ("clonetrack") :
                break;

            case ("editinfo") :
                Info info = editInfo();
                trackData.putInfo(info);
                setToolTip(info);
                break;

            case ("moveup") :
                int jup = trackPanel.whichTrackAmI(this);
                if (jup > 0)
                    trackPanel.swapTracks(jup, jup-1);
                break;

            case ("movedown") :
                int jdown = trackPanel.whichTrackAmI(this);
                if (jdown < trackPanel.getNTracks()-1)
                    trackPanel.swapTracks(jdown, jdown+1);
                break;

            case ("remove") :
                trackPanel.removeTrack(trackPanel.whichTrackAmI(this));
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

    public void startPlaying(double start, double end) {
        if (trackData.isNotEmpty()) {
            clock.setTime(start);
            clock.start();
            disableRecordPlay();
            playback(start, end);
        }
    }

    public void startPlaying() {
        startPlaying(0.0, getRunningTime());
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

    public double getRunningTime() {
        return trackData.getRunningTime();
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

    public void playback(double start, double end) {
        trackData.playback(start, end, slider.getValue(), timeLine);
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

    public void disableRecordPlay() {
        trackButtonPanel.disableRecordPlay();
    }

    public void enableRecordPlay() {
        trackButtonPanel.enableRecordPlay();
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

        // title Panel
        titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel,BoxLayout.LINE_AXIS));
        TrackButton editInfoButton = new TrackButton();
        editInfoButton.addActionListener(this);
        editInfoButton.setActionCommand("editinfo");
        URL imageURL = Track.class.getResource(
                "/Icons/Toolbar/General/EditInfo24.gif");
        editInfoButton.setIcon(new ImageIcon(imageURL));
        editInfoButton.setToolTipText("Edit Track Info");
        titleLabel = new JLabel(info.getTitle());
        titlePanel.add(Box.createRigidArea(new Dimension(20,0)));
        titlePanel.add(editInfoButton);
        titlePanel.add(Box.createRigidArea(new Dimension(20,0)));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(20,0)));
        titlePanel.add(Box.createHorizontalGlue());

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
        mainPanel.add(Box.createRigidArea(new Dimension(0,3)));
        mainPanel.add(titlePanel);
        JPanel outerMonitorPanel = new JPanel(new FlowLayout());
        outerMonitorPanel.add(monitor);

        // Volume slider
        slider = new VolumeSlider(JSlider.VERTICAL, 0, 10, 7);
        JPanel outerSliderPanel = new JPanel(new FlowLayout());
        outerSliderPanel.add(slider);

        trackButtonPanel = new LeftTrackButtonPanel(this);
        add(trackButtonPanel);
        add(Box.createRigidArea(new Dimension(4,0)));
        add(mainPanel);
        add(outerMonitorPanel);
        add(outerSliderPanel);
        add(Box.createRigidArea(new Dimension(4,0)));
        add(new NavTrackButtonPanel(this));

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
