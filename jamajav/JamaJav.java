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


public class JamaJav {

    private JFrame jfrm;
    private Clock clock;
    private Metronome metronome;
    private TrackPanel mainPanel;

    JamaJav(String[] args) {

        jfrm = new JFrame("Major's Jama Jav");
        JPanel contentPane = (JPanel)jfrm.getContentPane(); 
        contentPane.setLayout(new BorderLayout());

        // left panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.PAGE_AXIS));

        clock = new Clock(2, 100);
        clock.setBorder(BorderFactory.createRaisedBevelBorder());
        controlPanel.add(clock);

        metronome = new Metronome(120, 3);
        metronome.setBorder(BorderFactory.createRaisedBevelBorder());
        controlPanel.add(metronome);

        mainPanel = new TrackPanel(metronome, clock);
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());

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
        fileNewItem.addActionListener(mainPanel);
        JMenuItem fileOpenItem = new JMenuItem("Open ...");
        fileOpenItem.setActionCommand("open");
        fileOpenItem.addActionListener(mainPanel);
        JMenuItem fileSaveItem = new JMenuItem("Save as ...");
        fileSaveItem.setActionCommand("save");
        fileSaveItem.addActionListener(mainPanel);
        JMenuItem fileWebOpenItem = new JMenuItem("Open from Web ...");
        fileWebOpenItem.setActionCommand("WWW");
        fileWebOpenItem.addActionListener(mainPanel);
        JMenuItem fileExitItem = new JMenuItem("Exit");
        fileExitItem.addActionListener(mainPanel);
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
        metronomeItem.addActionListener(mainPanel);
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

        metronome.getSettings();
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
