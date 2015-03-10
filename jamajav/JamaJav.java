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
    private Prefs prefs;
    private TrackPanel trackPanel;

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

        prefs = new Prefs("jamajav.cfg");

        trackPanel = new TrackPanel(metronome, clock, prefs);
        trackPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        contentPane.add(controlPanel,BorderLayout.WEST);
        contentPane.add(trackPanel,BorderLayout.CENTER);

        // Menus!!
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu playMenu = new JMenu("Play");
        JMenu helpMenu = new JMenu("Help");

        // File menu
        fileMenu.setMnemonic('F');
        JMenuItem fileNewItem = new JMenuItem("New");
        fileNewItem.setActionCommand("new");
        fileNewItem.addActionListener(trackPanel);
        JMenuItem fileOpenItem = new JMenuItem("Open ...");
        fileOpenItem.setActionCommand("open");
        fileOpenItem.addActionListener(trackPanel);
        JMenuItem fileSaveItem = new JMenuItem("Save as ...");
        fileSaveItem.setActionCommand("save");
        fileSaveItem.addActionListener(trackPanel);
        JMenuItem fileWebOpenItem = new JMenuItem("Open from Web ...");
        fileWebOpenItem.setActionCommand("WWW");
        fileWebOpenItem.addActionListener(trackPanel);
        JMenuItem fileEditPrefsItem = new JMenuItem("Edit preferences ...");
        fileEditPrefsItem.setActionCommand("editprefs");
        fileEditPrefsItem.addActionListener(trackPanel);
        JMenuItem fileExitItem = new JMenuItem("Exit");
        fileSaveItem.setActionCommand("exit");
        fileExitItem.addActionListener(trackPanel);
        fileMenu.add(fileNewItem);
        fileMenu.add(fileOpenItem);
        fileMenu.add(fileSaveItem);
        fileMenu.addSeparator();
        fileMenu.add(fileWebOpenItem);
        fileMenu.addSeparator();
        fileMenu.add(fileEditPrefsItem);
        fileMenu.addSeparator();
        fileMenu.add(fileExitItem);

        // Play menu
        playMenu.setMnemonic('P');
        JMenuItem metronomeItem = new JMenuItem("Metronome settings ...");
        metronomeItem.setActionCommand("metroset");
        metronomeItem.addActionListener(trackPanel);
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

        //metronome.getSettings();
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
