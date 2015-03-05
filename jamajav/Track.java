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

    private boolean stopCapture = false;
    private boolean isCapturing = false;

    private ByteArrayOutputStream byteArrayOutputStream;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;

    private Notes notes;
    private boolean isClicked = false;
    private Color clickedColor, unclickedColor;

    private Metronome metronome;
    private Clock clock;
    private NotesPanel notesPanel;

    private Visualizer visualPanel;
    private JCheckBox trackCheckBox;
    private JButton recordButton;
    private JButton noteButton;
    private JButton playButton;
    private VolumeSlider slider;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        if (comStr.equals("Rec/Stop")) {
            if (!isCapturing) {
                clock.restart();
                metronome.start();
                record();
            } else {
                stopRecording();
                metronome.stop();
                clock.reset();
                isCapturing = false;
            }
        } else if (comStr.equals("Play")) {
            playback();
        } else if (comStr.equals("Add note")) {
            String newNote = getNote();
            if (!newNote.equals("no comment"))
                notes.addComment(newNote);
        }
    }

    public boolean isSelected() {
        return isClicked;
    }

    private String getNote() {
        JTextArea noteArea = new JTextArea(5,40);
        JScrollPane jsp = new JScrollPane(noteArea);

        int result = JOptionPane.showConfirmDialog(null, jsp, 
                "Add a note", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION)
            return noteArea.getText();
        else
            return "no comment";

    }

    // will put more things in the constructor later (probably
    // a Notes object will be involved
    Track(Metronome m, Clock c, NotesPanel np) {

        metronome = m;
        clock = c;
        notesPanel = np;

        clickedColor = Color.LIGHT_GRAY;
        unclickedColor = getBackground();

        notes = new Notes();

        setLayout(new FlowLayout());

        visualPanel = new Visualizer();

        Font buttonFont = new Font("SansSerif",Font.BOLD,10);

        recordButton = new JButton("Rec/Stop");
        noteButton = new JButton("Add note");
        playButton = new JButton("Play");

        slider = new VolumeSlider(JSlider.HORIZONTAL, 0, 10, 10);
        recordButton.addActionListener(this);
        noteButton.addActionListener(this);
        playButton.addActionListener(this);

        recordButton.setFont(buttonFont);
        noteButton.setFont(buttonFont);
        playButton.setFont(buttonFont);
        slider.setFont(buttonFont);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4,1));

        buttonPanel.add(recordButton);
        buttonPanel.add(noteButton);
        buttonPanel.add(playButton);
        buttonPanel.add(slider);

        add(buttonPanel);
        add(Box.createRigidArea(new Dimension(5,0)));
        add(visualPanel);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (isClicked) {
                    isClicked = false;
                    setBackground(unclickedColor);
                }    else {
                    isClicked = true;
                    setBackground(clickedColor);
                    notesPanel.showNotes(notes);
                }
            }
        });

    }

    public void stopRecording() {
        System.out.println("Stopping recording . . .");
        stopCapture = true;
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
        System.out.println("Recording . . . ");
        try{
            // Get everything set up for recording
            audioFormat = getAudioFormat();
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
            System.exit(0);
        }
    }

    class RecordThread extends Thread{

        // An arbitrary-size temporary holding buffer
        byte tempBuffer[] = new byte[10000];
        public void run(){
            byteArrayOutputStream = new ByteArrayOutputStream();
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
                visualPanel.setData(byteArrayOutputStream.toByteArray());

            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    public void playback() {
        System.out.println("Playing back . . . ");
        try {
            byte audioData[] = byteArrayOutputStream.toByteArray();

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

            // Create a thread to play back the data and start it running.  
            // It will run until all the data has been played back.

            Thread playThread = new Thread(new PlayThread());
            playThread.start();

        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
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
                while ((cnt = audioInputStream.read(
                                tempBuffer, 0,
                                tempBuffer.length)) != -1) {
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

            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }

    } //end inner class PlayThread

}
