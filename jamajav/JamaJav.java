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
        String comStr = ae.getActionCommand();

        if (comStr.equals("metroset")) {
            metronome.getSettings();
        }
    }

    JamaJav(String[] args) {

        jfrm = new JFrame("Major's Jama Jav");
        JPanel contentPane = (JPanel)jfrm.getContentPane(); 
        contentPane.setLayout(new BorderLayout());

        // left panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.PAGE_AXIS));

        clock = new Clock(4, 100);
        controlPanel.add(clock);

        metronome = new Metronome(100, 4);
        controlPanel.add(metronome);

        JPanel mainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        contentPane.add(controlPanel,BorderLayout.WEST);
        contentPane.add(mainPanel,BorderLayout.CENTER);

        // Menus!!
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu playMenu = new JMenu("Play");
        JMenu helpMenu = new JMenu("Help");

        // File menu
        fileMenu.setMnemonic('F');
        JMenuItem fileNewItem = new JMenuItem("New");
        fileNewItem.addActionListener(this);
        JMenuItem fileOpenItem = new JMenuItem("Open ...");
        fileOpenItem.setActionCommand("Open");
        fileOpenItem.addActionListener(this);
        JMenuItem fileSaveItem = new JMenuItem("Save as ...");
        fileSaveItem.setActionCommand("Save");
        fileSaveItem.addActionListener(this);
        JMenuItem fileWebOpenItem = new JMenuItem("Open from Web ...");
        fileWebOpenItem.setActionCommand("WWW");
        fileWebOpenItem.addActionListener(this);
        JMenuItem fileExitItem = new JMenuItem("Exit");
        fileExitItem.addActionListener(this);
        fileMenu.add(fileNewItem);
        fileMenu.add(fileOpenItem);
        fileMenu.add(fileSaveItem);
        fileMenu.addSeparator();
        fileMenu.add(fileWebOpenItem);
        fileMenu.addSeparator();
        fileMenu.add(fileExitItem);

        // Play menu
        playMenu.setMnemonic('P');
        JMenuItem metronomeItem = new JMenuItem("Metronome settings ...");
        metronomeItem.setActionCommand("metroset");
        metronomeItem.addActionListener(this);
        playMenu.add(metronomeItem);

        // Help menu
        helpMenu.setMnemonic('H');


        menuBar.add(fileMenu);
        menuBar.add(playMenu);
        menuBar.add(helpMenu);

        jfrm.setJMenuBar(menuBar);

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
