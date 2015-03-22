package jamajav;

// Swing packages
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

// For reading and writing files
import java.io.*;
import javax.imageio.*;
import java.net.URL;

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

        Color goldColour = new Color(0.7f,0.7f,0.98f);

        jfrm = new JFrame("Major's Jama Jav");
        JPanel contentPane = (JPanel)jfrm.getContentPane(); 
        contentPane.setLayout(new BorderLayout());

        // left panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.PAGE_AXIS));

        clock = new Clock();
        clock.setBorder(BorderFactory.createRaisedBevelBorder());
        //clock.setBorder(BorderFactory.createLineBorder(goldColour));
        controlPanel.add(clock);
        controlPanel.add(Box.createRigidArea(new Dimension(0,5)));

        metronome = new Metronome();
        metronome.setBorder(BorderFactory.createRaisedBevelBorder());
        //metronome.setBorder(BorderFactory.createLineBorder(goldColour));
        controlPanel.add(metronome);
        controlPanel.add(Box.createRigidArea(new Dimension(0,5)));

        // add logo to app
        try {
            JPanel logoPanel = new JPanel();
            ClassLoader cl = this.getClass().getClassLoader();
            URL logoURL = cl.getResource("Images/logo.png");
            BufferedImage logoImage;
            logoImage = ImageIO.read(logoURL);
            JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.add(new JLabel(new ImageIcon(logoImage)),
                    BorderLayout.CENTER);
            JPanel imageBackPanel = new JPanel(new BorderLayout());
            imageBackPanel.add(imagePanel,BorderLayout.CENTER);
            imageBackPanel.setBackground(new Color(0.75f,0.6f,0.1f));
            imageBackPanel.setBorder(BorderFactory.createRaisedBevelBorder());
            //imageBackPanel.setBorder(BorderFactory.createLineBorder(goldColour));
            controlPanel.add(Box.createVerticalGlue());
            controlPanel.add(imageBackPanel);
        } catch (IOException e) {
            System.out.println("Logo image not found!");
        }

        prefs = new Prefs("jamajav.cfg");

        trackPanel = new TrackPanel(jfrm, metronome, clock, prefs);
        trackPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        JPanel outerControlPanel = new JPanel();
        outerControlPanel.add(controlPanel);
        outerControlPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        controlPanel.setBackground(goldColour);
        outerControlPanel.setBackground(goldColour);

        contentPane.add(outerControlPanel,BorderLayout.WEST);
        contentPane.add(trackPanel,BorderLayout.CENTER);

        // Menus!!
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu playMenu = new JMenu("Play");
        JMenu helpMenu = new JMenu("Help");

        // File menu
        fileMenu.setMnemonic('F');
        JMenuItem fileNewItem = new JMenuItem("New Jam");
        fileNewItem.setActionCommand("newjam");
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
        fileExitItem.setActionCommand("exit");
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
        JMenuItem playAllItem = new JMenuItem("Play all");
        playAllItem.setActionCommand("playall");
        playAllItem.addActionListener(trackPanel);
        playMenu.add(playAllItem);

        // Help menu
        helpMenu.setMnemonic('H');
        JMenuItem helpInstItem = new JMenuItem("Instructions");
        helpInstItem.addActionListener(trackPanel);
        JMenuItem helpAboutItem = new JMenuItem("About Jama Jav");
        helpAboutItem.setActionCommand("About");
        helpAboutItem.addActionListener(trackPanel);
        helpMenu.add(helpInstItem);
        helpMenu.addSeparator();
        helpMenu.add(helpAboutItem);

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
