package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

class Clock extends JPanel implements ActionListener {

    private int countInSeconds;
    private double theTime;
    private int precision;  // in milliseconds

    private JPanel timerPanel;
    private Timer timer;
    private JLabel timeLabel;

    public double getTime() {
        return theTime;
    }

    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();

        if (ae.getSource() == timer) {
            theTime += 0.001*precision;

            DecimalFormat df = new DecimalFormat("##.#");

            timeLabel.setText(df.format(theTime));
            repaint();


        } else if (comStr.equals("start")) {
            timer.start();
        } else if (comStr.equals("pause")) {
            timer.stop();
        } else if (comStr.equals("stop")) {
            timer.stop();
            theTime = (double)(-countInSeconds);
        }
    }

    Clock(int countIn, int p) {
        countInSeconds = countIn;
        theTime = (double)(-countInSeconds);
        precision = p;

        // start, pause and stop buttons:
        JButton startButton = new JButton("start");
        JButton pauseButton = new JButton("pause");
        JButton stopButton = new JButton("stop");

        startButton.addActionListener(this);
        pauseButton.addActionListener(this);
        stopButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(stopButton);

        timerPanel = new JPanel(new FlowLayout());
        timeLabel = new JLabel("-" + countInSeconds);
        timeLabel.setFont(new Font("SansSerif",Font.BOLD,30));
        // a Timer object which triggers a listener every precision milliseconds
        timer = new Timer(precision, this);
        timerPanel.add(timeLabel);

        this.setLayout(new BorderLayout());
        this.add(timerPanel,BorderLayout.CENTER);
        this.add(buttonPanel,BorderLayout.NORTH);
    }

}
