package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For sound samples
import java.io.*;
import javax.sound.sampled.*;
import java.net.URL;

// For icon images
import java.awt.image.*;

class Metronome extends JPanel implements ActionListener {

    //    final private int DEFAULT_WIDTH = 210;
    //    final private int DEFAULT_HEIGHT = 165;

    private JLabel signalLabel; // to be replaced by a picture and sounds

    private JTextField bpmField;
    private JTextField bpMeasField;
    private JTextField offsetField;

    private JCheckBox soundCheckBox;

    private int bpMin;           // beats per minute
    private int bpMeas;          // beats per measure
    private double offset = 0.0; // time in seconds to wait before starting

    private Timer timer;
    private int beat;
    private double theTime;
    private boolean isStarted = false;

    private double precision = 0.001;
    private Clip tick, tock;

    private JButton startStopButton;
    private URL stopImageURL, goImageURL;

    /*    public Dimension getPreferredSize() {
          return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }
    */

    public void setParam(int beatspermin, int beatspermeasure) {
        //System.out.println("Setting parameters " + beatspermin + " " + beatspermeasure);
        bpMin = beatspermin;
        bpMeas = beatspermeasure;  
        beat = -1;

        bpmField.setText("" + bpMin); 
        bpMeasField.setText("" + bpMeas); 
        signalLabel.setText("   0" + " / " + bpMeas + "   ");

        // reinit Timer
        precision = (60000.0/(double)(10*bpMin));
        timer.setDelay((int)precision);
    }

    public void setOffset(double off) {
        offset = off;
        offsetField.setText("" + offset);

        isStarted = false;
        // start one measure before "offset"
        theTime = - offset + 60.0*(double)(bpMeas)/(double)(bpMin);
    }

    public double getOffset() {
        return offset;
    }

    public int[] getParam() {
        int[] params = new int[3];
        params[0] = bpMin;
        params[1] = bpMeas;

        return params;
    }

    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();

        if (ae.getSource() == timer) {
            if (isStarted) {
                beat++;
                if ((beat == 0) || (beat == 10*bpMeas)) {
                    signalLabel.setForeground(Color.RED);
                    signalLabel.setText("   1" + " / " + bpMeas + "   ");
                    beat = 0;
                    if (soundCheckBox.isSelected()) {
                        if (tock.isRunning())
                            tock.stop();   // Stop the player if it is still running
                        tock.setFramePosition(0); // rewind to the beginning
                        tock.start();     // Start playing
                    }
                } else if (beat%10 == 0) {
                    signalLabel.setForeground(Color.BLACK);
                    signalLabel.setText("   " + (beat/10+1) + " / " + bpMeas + "   ");
                    if (soundCheckBox.isSelected()) {
                        if (tick.isRunning())
                            tick.stop();   // Stop the player if it is still running
                        tick.setFramePosition(0); // rewind to the beginning
                        tick.start();     // Start playing
                    }
                } else {
                    //signalLabel.setText("     " + " / " + bpMeas + "   ");
                }
            } else { // if not started
                theTime += precision/1000;  // precision is in milliseconds
                if (theTime >= 0.0)
                    isStarted = true;
            }
        } else {

            switch (comStr) {

                case ("startstop") :
                    if (timer.isRunning()) {
                        stop();
                    } else {
                        theTime = 0.0; // override offset if in manual mode
                        start();
                    }
                    break;

                case ("bpm") :
                    try {
                        int b = Integer.parseInt(bpmField.getText());
                        setParam(b, bpMeas);
                    } catch (NumberFormatException nfe) {
                        bpmField.setText("" + bpMin);
                    }
                    break;

                case ("bpMeas") :
                    try {
                        int bb = Integer.parseInt(bpMeasField.getText());
                        setParam(bpMin, bb);
                    } catch (NumberFormatException nfe) {
                        bpMeasField.setText("" + bpMeas);
                    }
                    break;

                case ("offset") :
                    try {
                        double ofs = Double.parseDouble(offsetField.getText());
                        setOffset(ofs);
                    } catch (NumberFormatException nfe) {
                        offsetField.setText("" + offset);
                    }
                    break;

            }
        }
    }

    public void start() {
        timer.start();
        startStopButton.setIcon(new ImageIcon(stopImageURL));
    }

    public void stop() {
        timer.stop();
        startStopButton.setIcon(new ImageIcon(goImageURL));
        beat = -1;
        // start one measure before "offset"
        theTime = - offset + 60.0*(double)(bpMeas)/(double)(bpMin);
        isStarted = false;
        signalLabel.setForeground(Color.BLACK);
        signalLabel.setText("   0" + " / " + bpMeas + "   ");
    }

    Metronome() {

        setBackground(JamaJav.unclickedColour);

        signalLabel = new JLabel();

        // a Timer object which triggers a listener every beat
        timer = new Timer(100, this);

        // load sound files
        try {
            // get sounds from jar file (not sure why URL is appropriate but it works)
            // ClassLoader cl = this.getClass().getClassLoader();
            URL sound1 = Metronome.class.getResource("/Sounds/junhui.wav");
            URL sound2 = Metronome.class.getResource("/Sounds/ding.wav");

            AudioInputStream audioInputStream1 
                = AudioSystem.getAudioInputStream(sound1);

            tick = AudioSystem.getClip();
            tick.open(audioInputStream1);

            AudioInputStream audioInputStream2 
                = AudioSystem.getAudioInputStream(sound2);

            tock = AudioSystem.getClip();
            tock.open(audioInputStream2);

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        // start/stop button:
        startStopButton = new JButton();
        startStopButton.addActionListener(this);
        startStopButton.setActionCommand("startstop");
        stopImageURL = Track.class.getResource(
                "/Icons/Toolbar/Media/StopSign24.gif");
        goImageURL = Track.class.getResource(
                "/Icons/Toolbar/Media/GoSign24.gif");
        startStopButton.setIcon(new ImageIcon(goImageURL));
        startStopButton.setToolTipText("Start/Stop Metronome");

        // checkbox to enable/disable sound
        soundCheckBox = new JCheckBox("sound enabled", true);

        signalLabel.setFont(new Font("SansSerif",Font.BOLD,24));

        JLabel titleLabel = new JLabel("Metronome");
        titleLabel.setFont(new Font("SansSerif",Font.BOLD,13));

        JLabel bpmLabel = new JLabel("bpm");
        bpmField = new JTextField("" + bpMin, 2);
        bpmLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        bpmField.setFont(new Font("SansSerif",Font.BOLD,13));

        JLabel bpMeasLabel = new JLabel("/ 4 time");
        bpMeasField = new JTextField("" + bpMeas, 1);
        bpMeasLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        bpMeasField.setFont(new Font("SansSerif",Font.BOLD,13));

        JLabel offsetLabel = new JLabel("offset");
        offsetField = new JTextField("" + offset, 3);
        JLabel offsetSecLabel = new JLabel("s");
        offsetLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        offsetField.setFont(new Font("SansSerif",Font.BOLD,13));
        offsetSecLabel.setFont(new Font("SansSerif",Font.BOLD,13));

        bpmField.setActionCommand("bpm");
        bpmField.addActionListener(this);
        bpmField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    int b = Integer.parseInt(bpmField.getText());
                    setParam(b, bpMeas);
                } catch (NumberFormatException nfe) {
                    bpmField.setText("" + bpMin);
                }
            }
        });

        bpMeasField.setActionCommand("bpMeas");
        bpMeasField.addActionListener(this);
        bpMeasField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    int b = Integer.parseInt(bpMeasField.getText());
                    setParam(bpMin, b);
                } catch (NumberFormatException nfe) {
                    bpMeasField.setText("" + bpMeas);
                }
            }
        });

        offsetField.setActionCommand("offset");
        offsetField.addActionListener(this);
        offsetField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    double ofs = Double.parseDouble(offsetField.getText());
                    setOffset(ofs);
                } catch (NumberFormatException nfe) {
                    offsetField.setText("" + offset);
                }
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.LINE_AXIS));

        JPanel titlePanel = new JPanel(new GridLayout(2,1));
        titlePanel.add(titleLabel);
        titlePanel.add(soundCheckBox);

        mainPanel.add(titlePanel);
        mainPanel.add(Box.createHorizontalGlue());

        JPanel signalPanel = new JPanel();
        signalPanel.add(signalLabel);
        mainPanel.add(signalPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startStopButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createHorizontalGlue());

        JPanel bpmPanel = new JPanel();
        bpmPanel.add(bpmField);
        bpmPanel.add(bpmLabel);

        JPanel bpMeasPanel = new JPanel();
        bpMeasPanel.add(bpMeasField);
        bpMeasPanel.add(bpMeasLabel);

        JPanel offsetPanel = new JPanel();
        offsetPanel.add(offsetLabel);
        offsetPanel.add(offsetField);
        offsetPanel.add(offsetSecLabel);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.add(bpmPanel);
        fieldsPanel.add(bpMeasPanel);
        fieldsPanel.add(offsetPanel);

        mainPanel.add(fieldsPanel);

        setLayout(new BorderLayout());
        add(mainPanel);
    }

}
