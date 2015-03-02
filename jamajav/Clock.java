package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

class Clock extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 200;
    final private int DEFAULT_HEIGHT = 100;

    private int countInSeconds;
    private double theTime;
    private int precision;  // in milliseconds

    private JPanel timerPanel;
    private Timer timer;
    private JLabel timeLabel;

    private final DecimalFormat df = new DecimalFormat("##0.0");

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public double getTime() {
        return theTime;
    }

    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();

        if (ae.getSource() == timer) {
            theTime += 0.001*precision;

            timeLabel.setText(df.format(theTime));
            repaint();

        } else if (comStr.equals("start")) {
            if (timer.isRunning())
                timer.stop();
            else
                timer.start();
        } else if (comStr.equals("reset")) {
            timer.stop();
            theTime = (double)(-countInSeconds);
            timeLabel.setText(df.format(theTime));
            repaint();
        }
    }

    Clock(int countIn, int p) {
        countInSeconds = countIn;
        theTime = (double)(-countInSeconds);
        precision = p;

        // start and reset buttons:
        JButton startButton = new JButton("start");
        JButton resetButton = new JButton("reset");

        startButton.addActionListener(this);
        resetButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(resetButton);

        timerPanel = new JPanel(new FlowLayout());
        timeLabel = new JLabel(df.format(theTime));
        timeLabel.setFont(new Font("SansSerif",Font.BOLD,30));
        // a Timer object which triggers a listener every precision milliseconds
        timer = new Timer(precision, this);
        timerPanel.add(timeLabel);

        JLabel titleLabel = new JLabel("Timer");
        titleLabel.setFont(new Font("SansSerif",Font.BOLD,13));

        setLayout(new BorderLayout());
        add(titleLabel,BorderLayout.NORTH);
        add(timerPanel,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }

}
