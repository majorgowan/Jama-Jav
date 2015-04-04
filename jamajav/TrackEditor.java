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

class TrackEditor extends JPanel implements ActionListener {

    private TrackPanel trackPanel;
    // the track to be edited
    private Track track;

    private TrackData trackData, oldTrackData;
    private byte[] bufferData;

    private TimeKeeper timeKeeper;

    private TimeLine timeLine;
    private Visualizer visualizer;

    private JButton pasteButton;

    private PlainClock clock;

    public void actionPerformed(ActionEvent ae) {

        String cmdStr = ae.getActionCommand();

        switch (cmdStr) {
            case "preview" :
                clock.reset(0.0);
                trackData.playback();
                break;

            case "stop" :
                trackData.stopPlaying();
                break;

            case "select" :
                setSelect();
                break;

            case "paste" :
                setPaste();
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

    private void setSelect() {
        JPanel selectPanel = new JPanel(new FlowLayout());
        JTextField startField = new JTextField("0",4);
        JTextField endField = new JTextField("" + trackData.getInfo().getRunningTime(),4);

        selectPanel.add(new JLabel("From (seconds): "));
        selectPanel.add(startField);

        selectPanel.add(new JLabel("    ")); // crude spacer

        selectPanel.add(new JLabel("To (seconds): "));
        selectPanel.add(endField);

        startField.requestFocusInWindow();

        String[] options = new String[] {"Cut", "Copy", "Cancel"};
        int result = JOptionPane.showOptionDialog(null, selectPanel, 
                "Select interval", JOptionPane.DEFAULT_OPTION, 
                JOptionPane.PLAIN_MESSAGE, 
                null, options, options[2]);

        if (result != 2) {
            double start = Double.parseDouble(startField.getText());
            double end = Double.parseDouble(endField.getText());
            select(start, end);
            pasteButton.setEnabled(true);
            if (result == 0) {
                mute(start, end);
                // redraw visualizer and timeLine
                visualizer.setData(trackData.getBytes()); 
                visualizer.repaint();
                timeLine.setRunningTime(trackData.getRunningTime());
                timeLine.repaint();
            }
        }
    }

    private void select(double start, double end) {
        // convert seconds to bytes
        int startByte = (int)(start 
                * trackData.getAudioFormat().getFrameSize() 
                * trackData.getAudioFormat().getFrameRate());
        int endByte = (int)(end 
                * trackData.getAudioFormat().getFrameSize() 
                * trackData.getAudioFormat().getFrameRate()) + 1;

        byte[] oldData = trackData.getBytes();
        bufferData = new byte[endByte-startByte+1];

        for (int i = startByte; i <= endByte; i++)
            bufferData[i-startByte] = oldData[i];
    }

    private void setPaste() {
        JPanel pastePanel = new JPanel(new FlowLayout());
        JTextField secondsField = new JTextField("0",4);

        pastePanel.add(new JLabel("Paste at (seconds): "));
        pastePanel.add(secondsField);

        secondsField.requestFocusInWindow();

        int result = JOptionPane.showConfirmDialog(null, pastePanel, 
                "Paste", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            double seconds = Double.parseDouble(secondsField.getText());
            paste(seconds);
            // redraw visualizer and timeLine
            visualizer.setData(trackData.getBytes()); 
            visualizer.repaint();
            timeLine.setRunningTime(trackData.getRunningTime());
            timeLine.repaint();
        }
    }

    private void paste(double seconds) {

        // convert seconds to bytes
        int pasteAtByte = (int)(seconds 
                * trackData.getAudioFormat().getFrameSize() 
                * trackData.getAudioFormat().getFrameRate());

        byte[] oldBytes = trackData.getBytes();
        byte[] newBytes 
            = new byte[Math.max(oldBytes.length, pasteAtByte+bufferData.length)]; 

        // copy first part of data
        for (int i = 0; i < Math.min(pasteAtByte, oldBytes.length); i++)
            newBytes[i] = oldBytes[i];

        // if pasteAtByte is past end of oldBytes
        for (int i = oldBytes.length; i < pasteAtByte; i++)
            newBytes[i] = (byte)0;

        // paste buffer data
        for (int i = pasteAtByte; i < pasteAtByte+bufferData.length; i++)
            newBytes[i] = bufferData[i-pasteAtByte];

        // copy last part of data
        for (int i = pasteAtByte+bufferData.length; i < oldBytes.length; i++)
            newBytes[i] = oldBytes[i];

        // put data
        trackData.putBytes(newBytes);

        // new running time:
        trackData.getInfo().setRunningTime(
                trackData.getRunningTime());
        timeLine.setRunningTime(trackData.getRunningTime());
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
            timeLine.setRunningTime(trackData.getRunningTime());
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
        trackData.getInfo().setRunningTime(
                trackData.getRunningTime());
        timeLine.setRunningTime(trackData.getRunningTime());
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

    private void shift(double seconds) {

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
        trackData.getInfo().setRunningTime(
                trackData.getRunningTime());
        timeLine.setRunningTime(trackData.getRunningTime());
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
        track.getTimeLine().setRunningTime(trackData.getRunningTime());
    }

    private void saveAsNew() {
        // Replace current Info with a copy (adds words "copy of" to title)
        trackData.putInfo(new Info(trackData.getInfo()));
        trackData.setMonitor(new Monitor());
        // add a new track
        trackPanel.addNewTrack();
        // set trackData of new track (reorder in the future!)
        trackPanel.getTrack(trackPanel.getNTracks() - 1).setTrackData(trackData);
        trackData.setMonitor(trackPanel.getTrack(trackPanel.getNTracks() - 1).getMonitor());
        trackPanel.getTrack(trackPanel.getNTracks() - 1).refreshVisualizerAndTimeLine();
    }


    TrackEditor(Track oldTrack, TrackPanel tpnl) {

        // need to know trackPanel in case of saving a copy of edited track
        trackPanel = tpnl;
        track = oldTrack;

        oldTrackData = track.getTrackData();
        trackData = new TrackData(oldTrackData);

        // override default copy constructor appending "copy of" to info
        // of copy (will only do that if "save as new track" is
        // clicked
        trackData.putInfo(oldTrackData.getInfo());

        // Visualizer
        visualizer = new Visualizer();
        visualizer.setData(trackData.getBytes()); 

        // time keeping and display
        timeKeeper = new TimeKeeper(0.0);
        timeLine = new TimeLine();
        timeLine.setRunningTime(trackData.getRunningTime());
        timeKeeper.setTimeLine(timeLine);
        clock = new PlainClock();
        timeKeeper.setClock(clock);
        trackData.setTimeKeeper(timeKeeper);

        // Outer Edit panel
        JPanel outerEditPanel = new JPanel();
        outerEditPanel.setLayout(new BoxLayout(outerEditPanel, BoxLayout.PAGE_AXIS));

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

        // Select panel
        JPanel selectPanel = new JPanel(new FlowLayout());
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(this);
        selectButton.setActionCommand("select");
        // pasteButton is a class variable so it can be
        // disabled and enabled when the buffer is not empty
        pasteButton = new JButton("Paste");
        pasteButton.setEnabled(false);
        pasteButton.addActionListener(this);
        pasteButton.setActionCommand("paste");
        selectPanel.add(selectButton);
        selectPanel.add(pasteButton);

        outerEditPanel.add(editPanel);
        outerEditPanel.add(Box.createRigidArea(new Dimension(0,10)));
        outerEditPanel.add(selectPanel);

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
        add(outerEditPanel, BorderLayout.NORTH);
        add(centrePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}

