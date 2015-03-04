package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// For sound samples
import java.io.*;
import javax.sound.sampled.*;

class Metronome extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 200;
    final private int DEFAULT_HEIGHT = 120;

    private JPanel signalPanel;
    private JLabel signalLabel; // to be replaced by a picture and sounds

    private JLabel infoLabel;

    private JCheckBox soundCheckBox;

    private int bpMin;       // beats per minute
    private int bpMeas;      // beats per measure

    private Timer timer;
    private int beat;

    private Clip tick, tock;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void getSettings() {
        JPanel mainPanel = new JPanel(new GridLayout(2,1));

        JTextField bpMinField = new JTextField("" + bpMin,4);
        JPanel line1 = new JPanel();
        line1.add(new JLabel("Beats per minute: "));
        line1.add(bpMinField);

        JTextField bpMeasField = new JTextField("" + bpMeas,2);
        JPanel line2 = new JPanel();
        line2.add(new JLabel("Beats per measure: "));
        line2.add(bpMeasField);

        mainPanel.add(line1);
        mainPanel.add(line2);

        int result = JOptionPane.showConfirmDialog(null, mainPanel, 
                "Metronome Settings", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // set new parameters
            setParam(Integer.parseInt(bpMinField.getText()),
                    Integer.parseInt(bpMeasField.getText()));
            // reinit Timer
            timer.setDelay((int)(60000/(bpMin*5)));
        }
    }

    public void setParam(int beatspermin, int beatspermeasure) {
        bpMin = beatspermin;
        bpMeas = beatspermeasure;  
        beat = 0;

        infoLabel.setText("" + bpMin + " bpm "); 
    }

    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();

        if (ae.getSource() == timer) {
            beat++;
            if (beat == 5*bpMeas) {
                signalLabel.setForeground(Color.RED);
                signalLabel.setText("   1   ");
                beat = 0;
                if (soundCheckBox.isSelected()) {
                    if (tock.isRunning())
                        tock.stop();   // Stop the player if it is still running
                    tock.setFramePosition(0); // rewind to the beginning
                    tock.start();     // Start playing
                }
            } else if (beat%5 == 0) {
                signalLabel.setForeground(Color.BLACK);
                signalLabel.setText("   " + (beat/5+1) + "   ");
                if (soundCheckBox.isSelected()) {
                    if (tick.isRunning())
                        tick.stop();   // Stop the player if it is still running
                    tick.setFramePosition(0); // rewind to the beginning
                    tick.start();     // Start playing
                }
            } else {
                signalLabel.setText("           ");
            }
        } else if (comStr.equals("start")) {
            timer.start();
        } else if (comStr.equals("stop")) {
            timer.stop();
            beat = 0;
            signalLabel.setText("    ---    ");
        }
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    Metronome(int beatspermin, int beatspermeasure) {

        setBackground(new Color(0.75f,0.6f,0.1f));

        beat = 0;
        bpMin = beatspermin;
        bpMeas = beatspermeasure;

        // load sound files
        try {
            AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(
                    new File("Sounds/junhui.wav"));
            tick = AudioSystem.getClip();
            tick.open(audioInputStream1);

            AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(
                    new File("Sounds/ding.wav"));

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
//        buttonPanel.add(soundCheckBox);

        signalPanel = new JPanel(new FlowLayout());
        signalLabel = new JLabel("    ---    ");
        signalLabel.setFont(new Font("SansSerif",Font.BOLD,30));
        // a Timer object which triggers a listener every beat
        timer = new Timer((int)(60000/(bpMin*5)), this);
        signalPanel.add(signalLabel);
        signalPanel.add(soundCheckBox);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel,BoxLayout.LINE_AXIS));
        JLabel titleLabel = new JLabel("Metronome");
        infoLabel = new JLabel("" + bpMin + " bpm "); 
        titleLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        infoLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(infoLabel);

        setLayout(new BorderLayout());
        add(titlePanel,BorderLayout.NORTH);
        add(signalPanel,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }

}
