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

    final private int DEFAULT_WIDTH = 400;
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

    private Visualizer visualPanel;
    private JButton recordButton;
    private JButton infoButton;
    private JButton playButton;
    private VolumeSlider slider;
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
            mainPanel.setBackground(clickedColor);
            titlePanel.setBackground(clickedColor);
        } else {
            mainPanel.setBackground(unclickedColor);
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
        clock.reset();
        isCapturing = false;
    }

    public void startPlaying() {
        if (notEmpty) {
            stopPlay = false;
            playback();
        }
    }

    public void stopPlaying() {
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

        toolTip += "by: " + info.getContributor() + "</h3>";

        toolTip += info.getAllNotes();

        toolTip += "</list><br><br>" + info.getDate();

        toolTip += ", " + info.getLocation();

        visualPanel.setToolTipText(toolTip);
                
        // probably bad form to put this here, but ...
        titleLabel.setText(info.getTitle());
    }

    // constructor
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

        setLayout(new BorderLayout());
        mainPanel = new JPanel(new FlowLayout());
        titlePanel = new JPanel(new FlowLayout());
        titleLabel = new JLabel(info.getTitle());
        titlePanel.add(titleLabel);

        visualPanel = new Visualizer();
        setToolTip();

        Font buttonFont = new Font("SansSerif",Font.BOLD,10);

        recordButton = new JButton("Rec/Stop");
        infoButton = new JButton("Edit info");
        infoButton.setActionCommand("editinfo");
        playButton = new JButton("Play");

        slider = new VolumeSlider(JSlider.HORIZONTAL, 0, 10, 10);
        recordButton.addActionListener(this);
        infoButton.addActionListener(this);
        playButton.addActionListener(this);

        recordButton.setFont(buttonFont);
        infoButton.setFont(buttonFont);
        playButton.setFont(buttonFont);
        slider.setFont(buttonFont);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4,1));

        buttonPanel.add(recordButton);
        buttonPanel.add(infoButton);
        buttonPanel.add(playButton);
        buttonPanel.add(slider);

        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
        mainPanel.add(visualPanel);

        add(mainPanel,BorderLayout.CENTER);
        add(titlePanel,BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (isSelected()) {
                    setSelected(false);
                } else {
                    setSelected(true);
                }
            }
        });

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
        visualPanel.setData(audioData, getAudioFormat().getFrameSize());
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
            Thread RecordThread = new Thread(new RecordThread());
            RecordThread.start();
            isCapturing = true;
        } catch (Exception e) {
            System.out.println(e);
            // System.exit(0);   // Don't have to exit, just do nothing!
        }
    }

    class RecordThread extends Thread{

        // An arbitrary-size temporary holding buffer
        byte tempBuffer[] = new byte[10000];
        public void run(){
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
                    if(cnt > 0){
                        // Save data in output stream object.
                        byteArrayOutputStream.write(tempBuffer, 0, cnt);
                    }
                }
                byteArrayOutputStream.close();

                audioData = byteArrayOutputStream.toByteArray();
                visualPanel.setData(audioData,
                        targetDataLine.getFormat().getFrameSize());

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

        } catch (Exception e) {
            System.out.println(e);
            // System.exit(0);
        }
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        public void run(){
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
                    }
                }

                // Block and wait for internal
                // buffer of the data line to
                // empty.
                sourceDataLine.drain();
                sourceDataLine.close();
                stopPlay = false;

            } catch (Exception e) {
                System.out.println(e);
                // System.exit(0);
            }
        }

    } //end inner class PlayThread

}
