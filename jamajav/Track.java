// ref: http://www.developer.com/java/other/article.php/1572251/Java-Sound-Getting-Started-Part-1-Playback.htm

package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For reading and writing files and streams
import java.io.*;

// For sound samples
import javax.sound.sampled.*;

class Track extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 425;
    final private int DEFAULT_HEIGHT = 120;

    private boolean stopPlay = false;
    private boolean stopCapture = false;
    private boolean isCapturing = false;
    private boolean notEmpty = false;

    private byte[] audioData;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;

    private Info info;

    private boolean isClicked = false;
    private Color clickedColor, unclickedColor;

    private JFrame jfrm;

    private Prefs prefs;
    private Metronome metronome;
    private Clock clock; 

    private JButton recordButton;
    private JButton playButton;
    private JButton editButton;
    private JButton infoButton;
    private VolumeSlider slider;

    private TimeLine timeLine;
    private Visualizer visualPanel;
    private Monitor monitor;
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JLabel titleLabel;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        switch (comStr) {
            case ("Rec/Stop") :
                if (!isCapturing) {
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
                editInfo();
                setToolTip();
                break;
        }
    }

    public boolean isSelected() {
        return isClicked;
    }

    public boolean isNotEmpty() {
        return notEmpty;
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
        record();
        notEmpty = true;
    }

    public void stopRecording() {
        System.out.println("Stopping recording . . .");
        stopCapture = true;
        metronome.stop();
        clock.stop();
        isCapturing = false;
    }

    public void startPlaying() {
        if (notEmpty) {
            clock.restart();
            stopPlay = false;
            playback();
        }
    }

    public void stopPlaying() {
        clock.stop();
        stopPlay = true;
    }

    private void editInfo() {
        // open dialog with a infopanel
        final JDialog infoDialog = new JDialog(jfrm, "Edit track info", true);
        infoDialog.setLocationRelativeTo(jfrm);
        infoDialog.getContentPane().setLayout(new BorderLayout());
        infoDialog.getContentPane().add(
                new InfoPanel(info), BorderLayout.CENTER);
        infoDialog.revalidate();
        infoDialog.pack();
        infoDialog.setVisible(true);
    }

    public void setToolTip() {
        String toolTip = "<html><h3>" + info.getTitle() + "<br>";

        toolTip += "by: " + info.getContributor() + "<br>";

        toolTip += "length: " + info.getRunningTime() + "s" + "</h3>";

        toolTip += info.getAllNotes();

        toolTip += "</list><br><br>" + info.getDate();

        toolTip += ", " + info.getLocation();

        visualPanel.setToolTipText(toolTip);

        // probably bad form to put this here, but ...
        titleLabel.setText(info.getTitle());
    }

    public Info getInfo() {
        return info;
    }

    public void putInfo(Info in) {
        info = in;
    }

    public byte[] getBytes() {
        return audioData;
    }

    public void putBytes(byte[] bytes) {
        audioData = bytes;
        notEmpty = true;
        visualPanel.setData(audioData, audioFormat.getFrameSize());

        int runningTime = (int) 
            ((double)(audioData.length) / 
             (double)( 
                 audioFormat.getFrameSize() 
                 * audioFormat.getFrameRate() ) ); 

        timeLine.setRunningTime(runningTime);
        timeLine.repaint();
    }

    // Create and return an AudioFormat object for a given set
    // of format parameters.  If these parameters don't work well for
    // you, try some of the other allowable parameter values, which
    // are shown in comments following the declarations.
    private AudioFormat getAudioFormat(){
        float sampleRate = 8000.0f; //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;  // 8,16
        int channels = 1;           // 1,2
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }

    public void record() {
        try{
            System.out.println("Recording . . . ");
            // Get everything set up for recording
            DataLine.Info dataLineInfo = new DataLine.Info(
                    TargetDataLine.class, audioFormat);

            targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // Create a thread to capture the
            // microphone data and start it
            // running.  It will run until
            // the Stop button is clicked.
            Thread recordThread = new Thread(new RecordThread());
            recordThread.start();
            isCapturing = true;
        } catch (Exception e) {
            System.out.println(e);
            // System.exit(0);   // Don't have to exit, just do nothing!
        }
    }

    class RecordThread extends Thread{

        // An arbitrary-size temporary holding buffer
        // byte tempBuffer[] = new byte[10000];

        byte tempBuffer[] = new byte[1000];
        public void run(){

            int frameSize = targetDataLine.getFormat().getFrameSize();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            stopCapture = false;

            try{
                // Loop until stopCapture is set by another thread that
                while(!stopCapture) {
                    // Read data from the internal buffer of the data line.
                    int cnt = targetDataLine.read(
                            tempBuffer,
                            0,
                            tempBuffer.length);
                    monitor.setData(tempBuffer, frameSize);

                    if(cnt > 0){
                        // Save data in output stream object.
                        byteArrayOutputStream.write(tempBuffer, 0, cnt);
                    }
                }
                byteArrayOutputStream.close();
                stopRecording();

                audioData = byteArrayOutputStream.toByteArray();

                int runningTime = (int) 
                    ((double)(audioData.length) / 
                     (double)( 
                         audioFormat.getFrameSize() 
                         * audioFormat.getFrameRate() ) ); 
                info.setRunningTime(runningTime);

                timeLine.setRunningTime(runningTime);
                timeLine.repaint();
                visualPanel.setData(audioData, frameSize);

                info.resetDate();
                setToolTip();

                System.out.println("Closing targetDataLine . . . ");
                targetDataLine.close();

            } catch (Exception e) {
                System.out.println(e);
                // System.exit(0);
            }
        }
    }

    public void playback() {
        // System.out.println("Playing back . . . ");
        stopPlay = false;
        try {
            InputStream byteArrayInputStream 
                = new ByteArrayInputStream(audioData);

            audioInputStream = 
                new AudioInputStream(
                        byteArrayInputStream,
                        audioFormat,
                        audioData.length/audioFormat.
                        getFrameSize());

            DataLine.Info dataLineInfo = 
                new DataLine.Info(SourceDataLine.class, audioFormat);

            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            FloatControl volume_control = 
                (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);

            volume_control.setValue((float)(
                        -2.5*(10.0-slider.getValue())));

            // Create a thread to play back the data and start it running.  
            // It will run until all the data has been played back.
            Thread playThread = new Thread(new PlayThread());
            playThread.start();
            timeLine.start();

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            // System.exit(0);
        }
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[1000];

        public void run(){

            int frameSize = sourceDataLine.getFormat().getFrameSize();

            try{
                int cnt;
                // Keep looping until the input
                // read method returns -1 for
                // empty stream.
                while (((cnt = audioInputStream.read(
                                    tempBuffer, 0,
                                    tempBuffer.length)) != -1) && (!stopPlay))
                {
                    if (cnt > 0) {
                        // Write data to the internal
                        // buffer of the data line
                        // where it will be delivered
                        // to the speaker.
                        sourceDataLine.write(tempBuffer, 0, cnt);
                        monitor.setData(tempBuffer, frameSize);
                    }
                }

                // Block and wait for internal
                // buffer of the data line to
                // empty.
                sourceDataLine.drain();
                sourceDataLine.close();
                timeLine.stop();
                clock.stop();

            } catch (Exception e) {
                System.out.println(e);
                // e.printStackTrace();
                // System.exit(0);
            }
        }

    } //end inner class PlayThread



    // Track constructor
    Track(JFrame frm, Metronome m, Clock c, Prefs p) {

        jfrm = frm;
        metronome = m;
        clock = c;
        prefs = p;

        audioFormat = getAudioFormat();

        clickedColor = Color.LIGHT_GRAY;
        unclickedColor = getBackground();

        info = new Info();
        info.setContributor(prefs.getUserName());
        info.setLocation(prefs.getUserCity());
        info.setAvatar(prefs.getAvatar());

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        timeLine = new TimeLine();

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

        titlePanel = new JPanel(new FlowLayout());
        titleLabel = new JLabel(info.getTitle());
        titlePanel.add(titleLabel);

        visualPanel = new Visualizer();
        setToolTip();

        monitor = new Monitor();

        JPanel outerVisualPanel = new JPanel(new FlowLayout());
        outerVisualPanel.add(visualPanel);
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
        infoButton.setFont(buttonFont);
        playButton.setFont(buttonFont);

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
