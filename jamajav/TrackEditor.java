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
    private Visualizer visualizer;

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

            case "stop" :
                trackData.stopPlaying();
                break;

            case "fade" :
                setFade();
                break;

            case "mute" :
                setMute();
                break;

            case "shift" :
                setShift();
                break;

            case "crop" :
                setCrop();
                break;

            case "save" :
                saveChanges();
                // exit 
                trackData.stopPlaying();
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();
                break;

            case "saveasnew" :
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

    private void setCrop() {
        JPanel cropPanel = new JPanel(new FlowLayout());
        JTextField startField = new JTextField("0",4);
        JTextField endField = new JTextField("" + trackData.getInfo().getRunningTime(),4);

        cropPanel.add(new JLabel("From (seconds): "));
        cropPanel.add(startField);

        cropPanel.add(new JLabel("    ")); // crude spacer

        cropPanel.add(new JLabel("To (seconds): "));
        cropPanel.add(endField);

        startField.requestFocusInWindow();

        int result = JOptionPane.showConfirmDialog(null, cropPanel, 
                "Crop interval", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            double start = Double.parseDouble(startField.getText());
            double end = Double.parseDouble(endField.getText());
            // System.out.println("Cropping from " + start + " seconds to " + end + " seconds!"); 
            // crop it!
            crop(start, end);
            // redraw visualizer and timeLine
            visualizer.setData(trackData.getBytes()); 
            visualizer.repaint();
            timeLine.setRunningTime(trackData.getInfo().getRunningTime());
            timeLine.repaint();
        }
    }

    private void crop(double start, double end) {
        // convert seconds to bytes
        int startByte = (int)(start 
                * trackData.getAudioFormat().getFrameSize() 
                * trackData.getAudioFormat().getFrameRate());
        int endByte = (int)(end 
                * trackData.getAudioFormat().getFrameSize() 
                * trackData.getAudioFormat().getFrameRate()) + 1;

        int cutBytes = endByte - startByte + 1;
        // System.out.println("startByte: " + startByte + ", endBytes:" + endByte);
        // System.out.println("Cropping " + cutBytes + " bytes!");

        // truncate byte array accordingly
        byte[] oldBytes = trackData.getBytes();
        byte[] newBytes 
            = new byte[oldBytes.length - cutBytes]; 

        // System.out.println("Old byte array has length " + oldBytes.length);
        // System.out.println("New byte array has length " + newBytes.length);
        for (int i = 0; i < startByte-1; i++)
            newBytes[i] = oldBytes[i];
        for (int i = startByte; i < newBytes.length; i++)
            newBytes[i] = oldBytes[i+cutBytes];

        trackData.putBytes(newBytes);

        // new running time:
        trackData.getInfo().setRunningTime((int)
                ((double)newBytes.length 
                 / (double)(trackData.getAudioFormat().getFrameRate()
                     *trackData.getAudioFormat().getFrameSize())));
    }

    private void setShift() {
        JPanel shiftPanel = new JPanel(new FlowLayout());
        JTextField secondsField = new JTextField("0",4);

        shiftPanel.add(new JLabel("Shift by (seconds): "));
        shiftPanel.add(secondsField);

        secondsField.requestFocusInWindow();

        int result = JOptionPane.showConfirmDialog(null, shiftPanel, "Shift track", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            double seconds = Double.parseDouble(secondsField.getText());
            // System.out.println("Shifting by " + seconds + " seconds!"); 
            // shift it!
            shift(seconds);
            // redraw visualizer and timeLine
            visualizer.setData(trackData.getBytes()); 
            visualizer.repaint();
            timeLine.setRunningTime(trackData.getInfo().getRunningTime());
            timeLine.repaint();
        }
    }

    private void shift(Double seconds) {

        // convert seconds to bytes
        int shiftBytes = (int)(seconds 
                * trackData.getAudioFormat().getFrameSize() 
                * trackData.getAudioFormat().getFrameRate());

        byte[] oldBytes = trackData.getBytes();
        byte[] newBytes 
            = new byte[oldBytes.length + shiftBytes]; 

        // if shiftBytes is positive, pad with zeros and then copy
        // otherwise just copy necessary bytes
        if (shiftBytes > 0) {
            for (int i = 0; i < shiftBytes; i++)
                newBytes[i] = (byte)0;
            for (int i = shiftBytes; i < newBytes.length; i++)
                newBytes[i] = oldBytes[i-shiftBytes];
        } else if (shiftBytes < 0) {
            for (int i = 0; i < newBytes.length; i++)
                newBytes[i] = oldBytes[i-shiftBytes];
        }

        trackData.putBytes(newBytes);

        // new running time:
        trackData.getInfo().setRunningTime((int)
                ((double)newBytes.length 
                 / (double)(trackData.getAudioFormat().getFrameRate()
                     *trackData.getAudioFormat().getFrameSize())));
    }

    private void setFade() {
        JPanel fadePanel = new JPanel(new FlowLayout());
        JTextField startField = new JTextField("0",3);
        JTextField endField = new JTextField("" 
                + Math.min(5,trackData.getInfo().getRunningTime()),3);

        JRadioButton fadeInButton = new JRadioButton("Fade in");
        JRadioButton fadeOutButton = new JRadioButton("Fade out");
        fadeInButton.setSelected(true);
        ButtonGroup fadeGroup = new ButtonGroup();
        fadeGroup.add(fadeInButton);
        fadeGroup.add(fadeOutButton);

        JPanel buttonPanel = new JPanel(new GridLayout(2,1));
        buttonPanel.add(fadeInButton);
        buttonPanel.add(fadeOutButton);

        fadePanel.add(new JLabel("From (seconds): "));
        fadePanel.add(startField);

        fadePanel.add(new JLabel("    ")); // crude spacer

        fadePanel.add(new JLabel("To (seconds): "));
        fadePanel.add(endField);

        fadePanel.add(buttonPanel);
        startField.requestFocusInWindow();

        int result = JOptionPane.showConfirmDialog(null, fadePanel, 
                "Fade in/out", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            double start = Double.parseDouble(startField.getText());
            double end = Double.parseDouble(endField.getText());
            // System.out.println("Fading from " + start + " seconds to " + end + " seconds!"); 
            // get state of radiobuttons:
            boolean inout = true;
            if (fadeOutButton.isSelected())
                inout = false;
            // fade it!
            fade(start, end, inout);
            // redraw visualizer and timeLine
            visualizer.setData(trackData.getBytes()); 
            visualizer.repaint();
        }
    }

    private void fade(double start, double end, boolean inout) {
        double seconds = Math.abs(end - start);

        int frameRate = (int) trackData.getAudioFormat().getFrameRate();
        // compute number of frames to fade over
        int fadeFrames = (int)(seconds * frameRate); 
        int startFade = (int)(Math.min(start,end) * frameRate);
        int endFade = (int)(Math.max(start,end) * frameRate);

        // System.out.println("Fading from byte " + startFade + " to byte " + endFade + "!"); 

        // decay from full volume to exp(-2) smaller
        double coeff = -2.0 / (double)fadeFrames;

        // convert bytes to 16-bit integers:
        int[] sixteen = EightSixteen.toSixteen(trackData.getBytes());

        // fade
        if (inout)    // fade in
            for (int i = startFade; i < endFade; i++)
                sixteen[i] = (int)
                    (Math.exp((double)(endFade - i)*coeff)*sixteen[i]);
        else          // fade out
            for (int i = startFade; i < endFade; i++)
                sixteen[i] = (int)
                    (Math.exp((double)(i - startFade)*coeff)*sixteen[i]);

        // convert integers back to bytes and copy to trackData
        trackData.putBytes(EightSixteen.toEight(sixteen));
    }

    private void setMute() {
        JPanel mutePanel = new JPanel(new FlowLayout());
        JTextField startField = new JTextField("0",3);
        JTextField endField = new JTextField("" + trackData.getInfo().getRunningTime(),3);

        mutePanel.add(new JLabel("From (seconds): "));
        mutePanel.add(startField);

        mutePanel.add(new JLabel("    ")); // crude spacer

        mutePanel.add(new JLabel("To (seconds): "));
        mutePanel.add(endField);

        startField.requestFocusInWindow();

        int result = JOptionPane.showConfirmDialog(null, mutePanel, 
                "Mute interval", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            double start = Double.parseDouble(startField.getText());
            double end = Double.parseDouble(endField.getText());
            // System.out.println("Muting from " + start + " seconds to " + end + " seconds!"); 
            // mute it!
            mute(start, end);
            // redraw visualizer and timeLine
            visualizer.setData(trackData.getBytes()); 
            visualizer.repaint();
        }
    }

    private void mute(double start, double end) {
        // convert seconds to bytes
        int startByte = (int)(start 
                * trackData.getAudioFormat().getFrameSize() 
                * trackData.getAudioFormat().getFrameRate());
        int endByte = (int)(end 
                * trackData.getAudioFormat().getFrameSize() 
                * trackData.getAudioFormat().getFrameRate()) + 1;

        int cutBytes = endByte - startByte + 1;

        byte[] oldBytes = trackData.getBytes();

        for (int i = startByte; i < endByte; i++)
            oldBytes[i] = (byte)0;

        trackData.putBytes(oldBytes);
    }

    private void saveChanges() {
        // copy modified TrackData to the one belonging to the Track
        // that triggered the TrackEditor
        oldTrackData.putBytes(trackData.getBytes());
        oldTrackData.putInfo(trackData.getInfo());
    }

    private void saveAsNew() {
        // Replace current Info with a copy (adds words "copy of" to title)
        trackData.putInfo(new Info(trackData.getInfo()));
        // add a new track
        trackPanel.addNewTrack();
        // set trackData of new track (reorder in the future!)
        trackPanel.getTrack(trackPanel.getNTracks() - 1).setTrackData(trackData);
        trackPanel.getTrack(trackPanel.getNTracks() - 1).refreshVisualizerAndTimeLine();
        // clumsy, but necessary because new Track will otherwise have default avatar
        // instead of author of original track being copied
        trackPanel.refreshAvatars();
    }


    TrackEditor(Track oldTrack, TrackPanel tpnl) {

        // need to know trackPanel in case of saving a copy of edited track
        trackPanel = tpnl;
        track = oldTrack;

        oldTrackData = track.getTrackData();
        trackData = new TrackData(oldTrackData);
        trackData.addStopperObserver(this);

        // override default copy constructor appending "copy of" to info
        // of copy (will only do that if "save as new track" is
        // clicked
        trackData.putInfo(oldTrackData.getInfo());

        visualizer = new Visualizer();
        visualizer.setData(trackData.getBytes()); 

        timeLine = new TimeLine();
        timeLine.setRunningTime(trackData.getInfo().getRunningTime());

        // Edit panel
        JPanel editPanel = new JPanel(new FlowLayout());
        JButton fadeButton = new JButton("Fade");
        fadeButton.addActionListener(this);
        fadeButton.setActionCommand("fade");
        JButton muteButton = new JButton("Mute interval");
        muteButton.addActionListener(this);
        muteButton.setActionCommand("mute");
        JButton shiftButton = new JButton("Shift");
        shiftButton.addActionListener(this);
        shiftButton.setActionCommand("shift");
        JButton cropButton = new JButton("Crop");
        cropButton.addActionListener(this);
        cropButton.setActionCommand("crop");
        editPanel.add(fadeButton);
        editPanel.add(muteButton);
        editPanel.add(shiftButton);
        editPanel.add(cropButton);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

        JPanel outerVisualPanel = new JPanel(new FlowLayout());
        outerVisualPanel.add(visualizer);
        JPanel outerTimePanel = new JPanel(new FlowLayout());
        outerTimePanel.add(timeLine);

        JPanel playPanel = new JPanel(new FlowLayout());
        JButton previewButton = new JButton("Preview");
        previewButton.setActionCommand("preview");
        previewButton.addActionListener(this);
        JButton stopButton = new JButton("Stop");
        stopButton.setActionCommand("stop");
        stopButton.addActionListener(this);

        playPanel.add(previewButton);
        playPanel.add(stopButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0,15)));
        mainPanel.add(outerTimePanel);
        mainPanel.add(outerVisualPanel);
        mainPanel.add(playPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,15)));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save changes");
        saveButton.setActionCommand("save");
        saveButton.addActionListener(this);
        JButton saveAsNewButton = new JButton("Save as new track");
        saveAsNewButton.setActionCommand("saveasnew");
        saveAsNewButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);

        buttonPanel.add(saveButton);
        buttonPanel.add(saveAsNewButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(editPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}

