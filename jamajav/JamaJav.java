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

    static final Color goldColour = new Color(0.7f,0.7f,0.98f);
    static final Color clickedColour = new Color(0.9999f,0.9333f,0.9333f);
    static final Color unclickedColour = new Color(0.9333f,0.9333f,0.9333f);

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
            //ClassLoader cl = this.getClass().getClassLoader();
            URL logoURL = JamaJav.class.getResource("/Images/logo.png");
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
        //controlPanel.setBackground(goldColour);
        //outerControlPanel.setBackground(goldColour);

        // Toolbar!!
        ToolBar toolBar = new ToolBar(trackPanel);

        contentPane.add(toolBar,BorderLayout.PAGE_START);
        contentPane.add(outerControlPanel,BorderLayout.WEST);
        contentPane.add(trackPanel,BorderLayout.CENTER);

        // Menus!! 
        MenuBar menuBar = new MenuBar(trackPanel);
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
