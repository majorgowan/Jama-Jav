package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

class PlainClock extends JPanel implements ActionListener {

    protected double theTime = 0.0;
    protected int precision = 100;  // in milliseconds

    protected Timer timer;
    protected JLabel timeLabel;

    protected final DecimalFormat df = new DecimalFormat("##0.0");

    public double getTime() {
        return theTime;
    }

    public void actionPerformed(ActionEvent ae) {

        String comStr = ae.getActionCommand();

        if (ae.getSource() == timer) {
            theTime += 0.001*precision;

            timeLabel.setText(df.format(theTime));
            repaint();
        }
    }

    public void restart() {
        timer.stop();
        theTime = 0.0;
        timer.start();
    }

    public void reset() {
        timer.stop();
        theTime = 0.0;
        timeLabel.setText(df.format(theTime));
        repaint();
    }

    public void stop() {
        timer.stop();
    }

    PlainClock() {
        setBackground(new Color(0.75f,0.6f,0.1f));

        timeLabel = new JLabel(df.format(theTime));

        JPanel timerPanel = new JPanel(new FlowLayout());
        timeLabel.setFont(new Font("SansSerif",Font.BOLD,30));
        // a Timer object which triggers a listener every precision milliseconds
        timer = new Timer(precision, this);
        timerPanel.add(timeLabel);

        setLayout(new BorderLayout());
        add(timerPanel,BorderLayout.CENTER);
    }

}
