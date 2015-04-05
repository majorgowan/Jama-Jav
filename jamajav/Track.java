// ref: http://www.developer.com/java/other/article.php/1572251/Java-Sound-Getting-Started-Part-1-Playback.htm

package jamajav;

// Swing packages:
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

// For icon images
import java.awt.image.*;
import java.net.URL;

class Track extends JPanel implements ActionListener, ChangeListener {

    private int DEFAULT_WIDTH = 550;
    private int DEFAULT_HEIGHT = 150;

    private TrackData trackData;

    private boolean isClicked = false;

    private final Color clickedColour = JamaJav.clickedColour;
    private final Color unclickedColour = JamaJav.unclickedColour;

    // ancestors
    private JFrame jfrm;
    private TrackPanel trackPanel;

    private Prefs prefs;
    private Metronome metronome;

    private TimeKeeper timeKeeper;

    private LeftTrackButtonPanel trackButtonPanel;
    private VolumeSlider slider;

    private BufferedImage avatarImage;

    private JLabel avatarLabel;
    private TimeLine timeLine;
    private Visualizer visualizer;
    private Monitor monitor;

    private JPanel outerPanel;

    private JPanel titlePanel;
    private TitleLabelPanel titleLabelPanel;
    private int titlePointer;

    private JPanel slimTitlePanel;
    private TitleLabelPanel slimTitleLabelPanel;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    // ActionListener method
    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        switch (comStr) {

            case ("recordstop") :
                trackPanel.toggleMetronome(true);
                if (!trackData.isCapturing()) {
                    timeLine.setRunningTime(0.0);
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

            case ("collapse") :
                collapse();
                break;

            case ("expand") :
                expand();
                break;

            case ("titletimer") :
                titleLabelPanel.update();
                slimTitleLabelPanel.update();
                break;
        }
    }

    // ChangeListener method (for volume slider
    public void stateChanged(ChangeEvent ce) {
        if (ce.getSource() == slider) {
            trackData.setVolume(getVolume());
            //System.out.println("Changing volume to " + getVolume());
        }
    }

    public boolean isSelected() {
        return isClicked;
    }

    public boolean isNotEmpty() {
        return trackData.isNotEmpty();
    }

    public void collapse() {
        displaySlim();
        setSelected(isSelected());
        trackPanel.refreshMainPanel();
    }

    public void expand() {
        displayFat();
        setSelected(isSelected());
        trackPanel.refreshMainPanel();
    }

    public void setSelected(boolean b) {
        Component[] components = this.getComponents();  // get all Components in this container
        Component[] subcomponents;
        Component[] subsubcomponents;
        Component[] subsubsubcomponents;

        isClicked = b;
        if (isClicked) {
            for (Component c : components) 
                if ((c != monitor) && (c != visualizer)) {
                    c.setBackground(clickedColour);

                    if (c instanceof Container) {
                        subcomponents = ((Container)c).getComponents();
                        for (Component csub : subcomponents) 
                            if ((csub != monitor) && (csub != visualizer)) {
                                csub.setBackground(clickedColour);

                                if (csub instanceof Container) {
                                    subsubcomponents = ((Container)csub).getComponents();
                                    for (Component csubsub : subsubcomponents) 
                                        if ((csubsub != monitor) && (csubsub != visualizer)) {
                                            csubsub.setBackground(clickedColour);

                                            if (csubsub instanceof Container) {
                                                subsubsubcomponents = ((Container)csubsub).getComponents();
                                                for (Component csubsubsub : subsubsubcomponents)
                                                    if ((csubsubsub != monitor) && (csubsubsub != visualizer)) {
                                                        csubsubsub.setBackground(clickedColour);
                                                    }
                                            }

                                        }
                                }

                            }
                    }

                }
            timeLine.setBackground(clickedColour);
            titleLabelPanel.setBackground(clickedColour);
        } else {
            for (Component c : components) 
                if ((c != monitor) && (c != visualizer)) {
                    c.setBackground(unclickedColour);

                    if (c instanceof Container) {
                        subcomponents = ((Container)c).getComponents();
                        for (Component csub : subcomponents) 
                            if ((csub != monitor) && (csub != visualizer)) {
                                csub.setBackground(unclickedColour);

                                if (csub instanceof Container) {
                                    subsubcomponents = ((Container)csub).getComponents();
                                    for (Component csubsub : subsubcomponents) 
                                        if ((csubsub != monitor) && (csubsub != visualizer)) {
                                            csubsub.setBackground(unclickedColour);

                                            if (csubsub instanceof Container) {
                                                subsubsubcomponents = ((Container)csubsub).getComponents();
                                                for (Component csubsubsub : subsubsubcomponents)
                                                    if ((csubsubsub != monitor) && (csubsubsub != visualizer)) {
                                                        csubsubsub.setBackground(unclickedColour);
                                                    }
                                            }

                                        }
                                }

                            }
                    }

                }
            timeLine.setBackground(unclickedColour);
            titleLabelPanel.setBackground(unclickedColour);
        }
        repaint();
    }

    public void startRecording() {
        metronome.start();
        trackData.record(visualizer);
        trackData.setNotEmpty(true);
    }

    public void stopRecording() {
        // System.out.println("Stopping recording . . .");
        metronome.stop();
        trackData.stopCapturing();
    }

    public void startPlaying(double start, double end) {
        if (trackData.isNotEmpty()) {
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
        }
    }

    public void stopPlaying() {
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

    public void setAvatar(BufferedImage img) {
        avatarImage = img;
        avatarLabel.setIcon(new ImageIcon(img));
    }

    public BufferedImage getAvatarImage() {
        return avatarImage;
    }

    public TimeLine getTimeLine() {
        return timeLine;
    }

    public void setToolTip(Info info) {
        String toolTip = "<html><h3>" + info.getTitle() + "<br>";

        toolTip += "by: " + info.getContributor() + "<br>";

        toolTip += "length: " + info.getRunningTime() + "s" + "</h3>";

        toolTip += info.getAllNotes();

        toolTip += "</list><br><br>" + info.getDate();

        toolTip += ", " + info.getLocation();

        this.setToolTipText(toolTip);

        // probably wrong place for this but ...
        titleLabelPanel.setText(info.getTitle());
        slimTitleLabelPanel.setText(info.getTitle());
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
        timeLine.setRunningTime(trackData.getRunningTime());
    }

    public void playback(double start, double end) {
        trackData.playback(start, end);
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
        trackData.setTimeKeeper(timeKeeper);
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

    public void displayFat() {
        this.removeAll();
        DEFAULT_HEIGHT = 150;

        avatarLabel.setIcon(new ImageIcon(avatarImage));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

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

        JPanel outerSliderPanel = new JPanel(new FlowLayout());
        slider.makeFat();
        outerSliderPanel.add(slider);

        JPanel rightPanel = new JPanel(new FlowLayout());
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.LINE_AXIS));

        rightPanel.add(Box.createRigidArea(new Dimension(10,0)));
        rightPanel.add(trackButtonPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(10,0)));
        rightPanel.add(mainPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(2,0)));
        rightPanel.add(outerMonitorPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(6,0)));
        rightPanel.add(outerSliderPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(10,0)));
        rightPanel.add(new NavTrackButtonPanel(this));

        outerPanel.removeAll();
        outerPanel.setLayout(new FlowLayout());
        outerPanel.add(avatarLabel);
        outerPanel.add(rightPanel);

        add(outerPanel);
    }

    public void displaySlim() {
        this.removeAll();
        DEFAULT_HEIGHT = 66;

        avatarLabel.setIcon(
                new ImageIcon(avatarImage.getScaledInstance(40,40,Image.SCALE_SMOOTH)));

        JPanel outerAvatarPanel = new JPanel(new FlowLayout());
        outerAvatarPanel.add(avatarLabel);

        JPanel outerTimePanel = new JPanel(new FlowLayout());
        outerTimePanel.add(timeLine);

        JPanel centrePanel = new JPanel();
        centrePanel.setLayout(new BoxLayout(centrePanel,BoxLayout.PAGE_AXIS));
        centrePanel.add(outerTimePanel);
        slider.makeSlim();
        centrePanel.add(slider);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(outerAvatarPanel);
        //leftPanel.add(outerTimePanel);
        //leftPanel.add(outerSliderPanel);
        leftPanel.add(centrePanel);
        leftPanel.add(slimTitlePanel);

        SlimNavTrackButtonPanel slimTrackButtonPanel = new SlimNavTrackButtonPanel(this);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(slimTrackButtonPanel);

        outerPanel.removeAll();
        outerPanel.setLayout(new BoxLayout(outerPanel,BoxLayout.LINE_AXIS));
        outerPanel.add(leftPanel);
        outerPanel.add(Box.createHorizontalGlue());
        outerPanel.add(rightPanel);

        add(outerPanel);
    }

    // Basic Track constructor
    Track(JFrame frm, TrackPanel tpnl, Metronome m, TimeKeeper bigTimeKeeper, Prefs p) {

        jfrm = frm;
        trackPanel = tpnl;

        metronome = m;
        prefs = p;

        setBackground(JamaJav.goldColour);

        // time keeping and display
        timeKeeper = new TimeKeeper(0.0, bigTimeKeeper);
        timeLine = new TimeLine();
        timeKeeper.setTimeLine(timeLine);

        // trackData has the audio stuff and info
        trackData = new TrackData();
        trackData.addStopperObserver(trackPanel);
        trackData.setTimeKeeper(timeKeeper);

        Info info = new Info();
        info.setContributor(prefs.getUserName());
        info.setLocation(prefs.getUserCity());
        info.setAvatar(prefs.getAvatar());
        trackData.putInfo(info);

        // fat title Panel
        titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel,BoxLayout.LINE_AXIS));
        TrackButton editInfoButton = new TrackButton();
        editInfoButton.addActionListener(this);
        editInfoButton.setActionCommand("editinfo");
        URL imageURL = Track.class.getResource(
                "/Icons/Toolbar/General/EditInfo24.gif");
        editInfoButton.setIcon(new ImageIcon(imageURL));
        editInfoButton.setToolTipText("Edit Track Info");

        titleLabelPanel = new TitleLabelPanel(info.getTitle());
        titlePanel.add(Box.createRigidArea(new Dimension(20,0)));
        titlePanel.add(editInfoButton);
        titlePanel.add(Box.createRigidArea(new Dimension(20,0)));
        titlePanel.add(titleLabelPanel);
        titlePanel.add(Box.createRigidArea(new Dimension(20,0)));
        titlePanel.add(Box.createHorizontalGlue());

        // slim title Panel
        slimTitlePanel = new JPanel();
        slimTitlePanel.setLayout(new BoxLayout(slimTitlePanel,BoxLayout.LINE_AXIS));
        slimTitleLabelPanel = new TitleLabelPanel(info.getTitle());
        slimTitlePanel.add(slimTitleLabelPanel);

        // for crawling title if > 25 characters
        Timer titleTimer = new Timer(400, this);
        titleTimer.setActionCommand("titletimer");
        titleTimer.start();
        titlePointer = 0;

        visualizer = new Visualizer();
        setToolTip(info);

        monitor = new Monitor();
        trackData.setMonitor(monitor);

        // Volume slider
        slider = new VolumeSlider(JSlider.VERTICAL, 0, 10, 7);
        slider.addChangeListener(this);

        trackButtonPanel = new LeftTrackButtonPanel(this);

        avatarLabel = new JLabel();

        outerPanel = new JPanel();

        setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

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
