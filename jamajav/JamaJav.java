package jamajav;

// Swing packages
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;

// For reading and writing files
import java.io.*;

// For resizable arrays
import java.util.ArrayList;

// For printing arrays to screen
import java.util.Arrays;

// For sound stuff
import javax.sound.sampled.*;


public class JamaJav implements ActionListener {

    private JFrame jfrm;
    private Metronome metronome;
    private Clock clock;

    private ArrayList<JCheckBox> trackCheckBox;


    public void actionPerformed(ActionEvent ae) {
    }

    JamaJav(String[] args) {

        jfrm = new JFrame("Major's Jama Jav");
        JPanel contentPane = (JPanel)jfrm.getContentPane(); 
        contentPane.setLayout(new BorderLayout());

        // left panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.PAGE_AXIS));

        Metronome metronome = new Metronome();
        controlPanel.add(metronome);

        Clock clock = new Clock();
        controlPanel.add(clock);

        JPanel mainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        contentPane.add(controlPanel,BorderLayout.WEST);
        contentPane.add(mainPanel,BorderLayout.CENTER);

        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.pack();
        jfrm.setVisible(true);
    }

    public static void main(String[] args) {
        final String[] commandLineArgs = args;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JamaJav(commandLineArgs);
            }
        });
    }
}
