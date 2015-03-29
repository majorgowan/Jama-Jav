package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.URL;

class LeftTrackButtonPanel extends TrackButtonPanel {

    private TrackButton recordStopButton, playButton;

    public void disableRecordPlay() {
        recordStopButton.setEnabled(false);
        playButton.setEnabled(false);
    }

    public void enableRecordPlay() {
        recordStopButton.setEnabled(true);
        playButton.setEnabled(true);
    }

    LeftTrackButtonPanel(Track trck) {

        super(trck);

        // Buttons ...
        recordStopButton = makeButton(
                "Media", "RecordStop24", "recordstop", "Record / Stop", "Rec/Stop"); 
        playButton = makeButton(
                "Media", "PlayFromTop24", "playtrack", "Play Track", "Play"); 
        TrackButton stopButton = makeButton(
                "Media", "Stop24", "allstop", "Stop Playing", "Stop"); 

        TrackButton editTrackButton = makeButton(
                "General", "EditTrack24", "edittrack", 
                "Edit Track", "Edit");
        TrackButton cloneButton = makeButton(
                "General", "Clone24", "clonetrack", 
                "Clone Track", "Clone");

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.PAGE_AXIS));
        
        leftPanel.add(recordStopButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0,5)));
        leftPanel.add(playButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0,5)));
        leftPanel.add(editTrackButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0,5)));
        leftPanel.add(cloneButton);

        setLayout(new FlowLayout());
        add(leftPanel);
    }
}

