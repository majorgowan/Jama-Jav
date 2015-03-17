package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For sound samples
import java.io.*;
import javax.sound.sampled.*;
import java.net.URL;

class Metronome extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 200;
    final private int DEFAULT_HEIGHT = 150;

    private JPanel signalPanel;
    private JLabel signalLabel; // to be replaced by a picture and sounds

    private JTextField bpmField;
    private JTextField bpMeasField;

    private JCheckBox soundCheckBox;

    private int bpMin;       // beats per minute
    private int bpMeas;      // beats per measure

    private Timer timer;
    private int beat;

    private Clip tick, tock;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void setParam(int beatspermin, int beatspermeasure) {
        //System.out.println("Setting parameters " + beatspermin + " " + beatspermeasure);
        bpMin = beatspermin;
        bpMeas = beatspermeasure;  
        beat = 0;

        bpmField.setText("" + bpMin); 
        bpMeasField.setText("" + bpMeas); 
        signalLabel.setText("   0" + " / " + bpMeas + "   ");
        
        // reinit Timer
        timer.setDelay((int)(60000/(bpMin*5)));
    }

    public int[] getParam() {
        int[] params = new int[2];
        params[0] = bpMin;
        params[1] = bpMeas;

        return params;
    }

    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();

        if (ae.getSource() == timer) {
            beat++;
            if (beat == 5*bpMeas) {
                signalLabel.setForeground(Color.RED);
                signalLabel.setText("   1" + " / " + bpMeas + "   ");
                beat = 0;
                if (soundCheckBox.isSelected()) {
                    if (tock.isRunning())
                        tock.stop();   // Stop the player if it is still running
                    tock.setFramePosition(0); // rewind to the beginning
                    tock.start();     // Start playing
                }
            } else if (beat%5 == 0) {
                signalLabel.setForeground(Color.BLACK);
                signalLabel.setText("   " + (beat/5+1) + " / " + bpMeas + "   ");
                if (soundCheckBox.isSelected()) {
                    if (tick.isRunning())
                        tick.stop();   // Stop the player if it is still running
                    tick.setFramePosition(0); // rewind to the beginning
                    tick.start();     // Start playing
                }
            } else {
                signalLabel.setText("     " + " / " + bpMeas + "   ");
            }
        } else if (comStr.equals("start")) {
            timer.start();
        } else if (comStr.equals("stop")) {
            stop();
        } else if (comStr.equals("bpm")) {
            int b = Integer.parseInt(bpmField.getText());
            setParam(b, bpMeas);
        } else if (comStr.equals("bpMeas")) {
            int b = Integer.parseInt(bpMeasField.getText());
            setParam(bpMin, b);
        }
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
        beat = -1;
        signalLabel.setForeground(Color.BLACK);
        signalLabel.setText("   0" + " / " + bpMeas + "   ");
    }

    Metronome() {

        setBackground(new Color(0.75f,0.6f,0.1f));

        signalLabel = new JLabel();

        // a Timer object which triggers a listener every beat
        timer = new Timer(100, this);

        // load sound files
        try {
            // get sounds from jar file (not sure why URL is appropriate but it works)
            ClassLoader cl = this.getClass().getClassLoader();
            URL sound1 = cl.getResource("Sounds/junhui.wav");
            URL sound2 = cl.getResource("Sounds/ding.wav");

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

        // start and stop buttons:
        JButton startButton = new JButton("start");
        JButton stopButton = new JButton("stop");

        startButton.addActionListener(this);
        stopButton.addActionListener(this);

        // checkbox to enable/disable sound
        soundCheckBox = new JCheckBox("sound enabled", true);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        signalPanel = new JPanel(new BorderLayout());
        signalLabel.setFont(new Font("SansSerif",Font.BOLD,30));
        signalPanel.add(signalLabel, BorderLayout.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel,BoxLayout.LINE_AXIS));
        JLabel titleLabel = new JLabel("Metronome");
        titleLabel.setFont(new Font("SansSerif",Font.BOLD,13));

        JPanel bpmPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel bpmLabel = new JLabel("bpm");
        bpmField = new JTextField("" + bpMin, 2);
        bpmLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        bpmField.setFont(new Font("SansSerif",Font.BOLD,13));
        bpmPanel.add(bpmField);
        bpmPanel.add(bpmLabel);
        
        JPanel bpMeasPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel bpMeasLabel = new JLabel("/ 4 time");
        bpMeasField = new JTextField("" + bpMeas, 1);
        bpMeasLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        bpMeasField.setFont(new Font("SansSerif",Font.BOLD,13));
        bpMeasPanel.add(bpMeasField);
        bpMeasPanel.add(bpMeasLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        infoPanel.add(bpmPanel);
        infoPanel.add(bpMeasPanel);
        JPanel outerInfoPanel = new JPanel(new FlowLayout());
        outerInfoPanel.add(infoPanel);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(soundCheckBox);

        bpmField.setActionCommand("bpm");
        bpmField.addActionListener(this);
        bpmField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                int b = Integer.parseInt(bpmField.getText());
                setParam(b, bpMeas);
            }
        });

        bpMeasField.setActionCommand("bpMeas");
        bpMeasField.addActionListener(this);
        bpMeasField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                int b = Integer.parseInt(bpMeasField.getText());
                setParam(bpMin, b);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(titlePanel,BorderLayout.NORTH);
        mainPanel.add(outerInfoPanel,BorderLayout.EAST);
        mainPanel.add(signalPanel,BorderLayout.WEST);
        mainPanel.add(buttonPanel,BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel);
    }

}
