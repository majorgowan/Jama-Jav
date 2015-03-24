package jamajav;

// Swing packages:
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

class Clock extends PlainClock {

    final private int DEFAULT_WIDTH = 200;
    final private int DEFAULT_HEIGHT = 100;

    public Dimension getPreferredSize() {
        return (new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
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
            reset();
            repaint();
        }
    }

    Clock() {
        super();

        setBackground(new Color(0.75f,0.6f,0.1f));

        // start and reset buttons:
        JButton startButton = new JButton("start/stop");
        startButton.setActionCommand("start");
        JButton resetButton = new JButton("reset");

        startButton.addActionListener(this);
        resetButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(resetButton);

        JLabel titleLabel = new JLabel("Timer");
        titleLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel,BoxLayout.LINE_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());

        add(titlePanel,BorderLayout.NORTH);
        add(buttonPanel,BorderLayout.SOUTH);
    }

}
