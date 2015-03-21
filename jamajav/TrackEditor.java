package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For sound samples
import javax.sound.sampled.*;

// For observing:
import java.util.Observer;
import java.util.Observable;

class TrackEditor extends JPanel implements ActionListener, Observer {

    private TrackPanel trackPanel;
    // the track to be edited
    private Track track;

    private TrackData trackData, oldTrackData;

    private TimeLine timeLine;
    private Visualizer visualPanel;

    private JButton saveButton;
    private JButton saveAsNewButton;
    private JButton cancelButton;

    public void update(Observable obs, Object arg) {
        // stop "clock" eventually
    }

    public void actionPerformed(ActionEvent ae) {

        String cmdStr = ae.getActionCommand();

        switch (cmdStr) {
            case "preview" :
                trackData.playback(7, timeLine);
                break;
                    
            case "save" :
                saveChanges();
                // exit 
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();

            case "saveasnew" :
                saveAsNew();
                // exit 
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();

            case "cancel" :
                // exit without saving changes
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();
                break;

        }
    }

    private void crop(int start, int end) {

        // convert seconds to bytes

        // truncate byte array accordingly

    }

    private void shift(int seconds) {

        // if seconds is positive, make new byte array longer than the old
        // -> fill with zeros and then old byte array
        // -> replace byte array with new byte array

        // if seconds is negative, make new byte array shorter than old
        // -> fill with remaining part of old byte array
        // -> replace byte array with new byte array

        // update visualizer
    }

    private void fade(int start, int end, double fadedecay) {
        // fadedecay can be positive or negative
    }

    private void mute(int start, int end) {
        // kill signal for time interval
    }

    private void saveChanges() {
        // compute new running time

        // update info object with new running time

        // set bytes of oldTrack to local byte array
    }

    private void saveAsNew() {
        // addNewTrack to trackPanel

        // copy info object of old track to new track

        // update info object with changes
        //  (author stays old author)
        //  (add "copy of " to beginning of title

        // set bytes of new track to local byte array

        // set bytes of oldTrack to local byte array
    }


    TrackEditor(Track oldTrack, TrackPanel tpnl) {

        // need to know trackPanel in case of saving a copy of edited track
        trackPanel = tpnl;
        track = oldTrack;

        oldTrackData = track.getTrackData();
        trackData = new TrackData(oldTrackData);
        trackData.addStopperObserver(this);

        Info info = trackData.getInfo();

        visualPanel = new Visualizer();
        visualPanel.setData(trackData.getBytes(), 
                trackData.getAudioFormat().getFrameSize());

        timeLine = new TimeLine();
        timeLine.setRunningTime(info.getRunningTime());

        JPanel editPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton fadeButton = new JButton("Fade");
        fadeButton.addActionListener(this);
        JButton muteButton = new JButton("Mute interval");
        muteButton.addActionListener(this);
        JButton shiftButton = new JButton("Shift");
        shiftButton.addActionListener(this);
        JButton cropButton = new JButton("Crop");
        cropButton.addActionListener(this);

        editPanel.add(fadeButton);
        editPanel.add(muteButton);
        editPanel.add(shiftButton);
        editPanel.add(cropButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
        JPanel outerVisualPanel = new JPanel(new FlowLayout());
        outerVisualPanel.add(visualPanel);
        JPanel outerTimePanel = new JPanel(new FlowLayout());
        outerTimePanel.add(timeLine);

        mainPanel.add(outerTimePanel);
        mainPanel.add(outerVisualPanel);

        JButton previewButton = new JButton("Preview");
        previewButton.setActionCommand("preview");
        previewButton.addActionListener(this);
        JButton saveButton = new JButton("Save changes");
        saveButton.setActionCommand("save");
        saveButton.addActionListener(this);
        JButton saveAsNewButton = new JButton("Save as new track");
        saveAsNewButton.setActionCommand("saveasnew");
        saveAsNewButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(previewButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(saveAsNewButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(editPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}

