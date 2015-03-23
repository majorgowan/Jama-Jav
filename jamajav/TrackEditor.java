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

    private void setCrop() {
        JPanel cropPanel = new JPanel(new FlowLayout());
        JTextField startField = new JTextField("0",3);
        JTextField endField = new JTextField("" + trackData.getInfo().getRunningTime(),3);

        cropPanel.add(new JLabel("From (seconds): "));
        cropPanel.add(startField);

        cropPanel.add(new JLabel("    ")); // crude spacer

        cropPanel.add(new JLabel("To (seconds): "));
        cropPanel.add(endField);

        int result = JOptionPane.showConfirmDialog(null, cropPanel, 
                "Crop interval", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int start = Integer.parseInt(startField.getText());
            int end = Integer.parseInt(endField.getText());
            System.out.println("Cropping from " + start + " seconds to " + end + " seconds!"); 
            // crop it!
            crop(start, end);
            // redraw visualizer and timeLine
            visualizer.setData(trackData.getBytes()); 
            visualizer.repaint();
            timeLine.setRunningTime(trackData.getInfo().getRunningTime());
            timeLine.repaint();
        }
    }

    private void crop(int start, int end) {
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

    private void setFade() {
    }

    private void fade(int start, int end, double fadedecay) {
        // fadedecay can be positive or negative
    }

    private void setMute() {
    }

    private void mute(int start, int end) {
        // kill signal for time interval
    }

    // Audio processing utility routines:
    private int getSixteenBitSample(int high, int low) {
        return (high << 8) + (low & 0x00ff);            
    }
    private byte[] getEightBitPair(int sixteenBitSample) {
        // opposite of: return (high << 8) + (low & 0x00ff);
        // basically must format integer as a 16-bit binary number
        // and split it into the high eight bits and the low eight bits

        byte[] highLow = new byte[2];
        highLow[0] = (byte)((sixteenBitSample >> 8) & 0xff);
        highLow[1] = (byte)(sixteenBitSample & 0xff); 
        return highLow;
    }

    // eight-bit byte representation converted to amplitude of sixteen-bit signal
    public int[] toSixteen(byte[] bytes) {

        int[] toReturn = new int[bytes.length/2];

        int sampleIndex = 0;
        for (int t = 0; t < bytes.length;) {
            int low = (int) bytes[t];
            t++;
            int high = (int) bytes[t];
            t++;
            int sample = getSixteenBitSample(high, low);
            toReturn[sampleIndex] = sample;
            sampleIndex++;
        }
        return toReturn;
    }
    // inverse (could replace with void routine that sets the audioData array
    // but this is probably cleaner)
    public byte[] toEight(int[] sixteen) {
        // inverse of previous routine (must think about it!)

        byte[] toReturn = new byte[2*sixteen.length];
        for (int i = 0; i < sixteen.length; i++) {
            byte[] highLow = getEightBitPair(sixteen[i]);
            toReturn[2*i] = highLow[0];
            toReturn[2*i+1] = highLow[1];
        }
        return toReturn;
    }

    private void saveChanges() {
        // copy modified TrackData to the one belonging to the Track
        // that triggered the TrackEditor
        oldTrackData.putBytes(trackData.getBytes());
        oldTrackData.putInfo(trackData.getInfo());
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

        visualizer = new Visualizer();
        visualizer.setData(trackData.getBytes()); 

        timeLine = new TimeLine();
        timeLine.setRunningTime(info.getRunningTime());

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

