package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Metronome extends JPanel implements ActionListener {

    private JPanel signalPanel;
    private JLabel signalLabel; // to be replaced by a picture and sounds
    
    private int bpMin;       // beats per minute
    private int bpMeas;      // beats per measure

    private Timer timer;
    private int beat;

    public void setParam(int beatspermin, int beatspermeasure) {
        bpMin = beatspermin;
        bpMeas = beatspermeasure;
        beat = 0;
    }

    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();

        if (ae.getSource() == timer) {
            beat++;
            if (beat == 5*bpMeas) {
                signalLabel.setText(" Boo ");
                beat = 0;
            } else if (beat%5 == 0) {
                signalLabel.setText(" " + beat/5 + " ");
            } else {
                signalLabel.setText("     ");
            }
        } else if (comStr.equals("start")) {
            timer.start();
        } else if (comStr.equals("stop")) {
            timer.stop();
        }
    }

    Metronome(int beatspermin, int beatspermeasure) {

        beat = 0;
        bpMin = beatspermin;
        bpMeas = beatspermeasure;

        // start and stop buttons:
        JButton startButton = new JButton("start");
        JButton stopButton = new JButton("stop");

        startButton.addActionListener(this);
        stopButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        signalPanel = new JPanel(new FlowLayout());
        signalLabel = new JLabel("    ");
        signalLabel.setFont(new Font("SansSerif",Font.BOLD,30));
        // a Timer object which triggers a listener every beat
        timer = new Timer((int)(60000/(bpMin*5)), this);
        signalPanel.add(signalLabel);

        JLabel titleLabel = new JLabel("Metronome");
        titleLabel.setFont(new Font("SansSerif",Font.BOLD,13));

        this.setLayout(new BorderLayout());
        this.add(titleLabel,BorderLayout.NORTH);
        this.add(signalPanel,BorderLayout.CENTER);
        this.add(buttonPanel,BorderLayout.SOUTH);

        this.setBorder(BorderFactory.createRaisedBevelBorder());
    }

}
