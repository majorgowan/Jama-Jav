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

    public void restart() {
        timer.stop();
        theTime = (double)(-countInSeconds);
        timer.start();
    }

    public void reset() {
        timer.stop();
        theTime = (double)(-countInSeconds);
        timeLabel.setText(df.format(theTime));
        repaint();
    }

    public void stop() {
        timer.stop();
    }

    public int[] getParam() {
        int[] cparam = new int[2];
        cparam[0] = countInSeconds;
        cparam[1] = precision;
        return cparam;
    }

    public void setParam(int countIn, int p) {
        countInSeconds = countIn;
        precision = p;
        theTime = (double)(-countInSeconds);
        timeLabel.setText(df.format(theTime));
    }

    Clock() {
        setBackground(new Color(0.75f,0.6f,0.1f));

        timeLabel = new JLabel();

        // default (never used ... fix)
        setParam(2, 100);

        // start and reset buttons:
        JButton startButton = new JButton("start/stop");
        startButton.setActionCommand("start");
        JButton resetButton = new JButton("reset");

        startButton.addActionListener(this);
        resetButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(resetButton);

        timerPanel = new JPanel(new FlowLayout());
        timeLabel.setFont(new Font("SansSerif",Font.BOLD,30));
        // a Timer object which triggers a listener every precision milliseconds
        timer = new Timer(precision, this);
        timerPanel.add(timeLabel);

        JLabel titleLabel = new JLabel("Timer");
        titleLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel,BoxLayout.LINE_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());

        setLayout(new BorderLayout());
        add(titlePanel,BorderLayout.NORTH);
        add(timerPanel,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }

}
