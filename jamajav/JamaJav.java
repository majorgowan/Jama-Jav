package jamajav;

// Swing packages
import javax.swing.*;
import java.awt.*;


public class JamaJav {

    static final Color goldColour = new Color(0.7f,0.7f,0.98f);
    static final Color darkGoldColour = new Color(0.4f,0.4f,0.68f);
    static final Color clickedColour = new Color(0.9999f,0.8667f,0.8677f);
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
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.LINE_AXIS));

        clock = new Clock();

        metronome = new Metronome();
        //metronome.setBorder(BorderFactory.createRaisedBevelBorder());
        //metronome.setBorder(BorderFactory.createLineBorder(goldColour));
        //controlPanel.add(metronome);
        //controlPanel.add(Box.createRigidArea(new Dimension(0,5)));

        prefs = new Prefs("jamajav.cfg");

        trackPanel = new TrackPanel(args, jfrm, metronome, clock, prefs);
        trackPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        //JPanel outerControlPanel = new JPanel();
        //outerControlPanel.add(controlPanel);
        //outerControlPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        //controlPanel.setBackground(goldColour);
        //outerControlPanel.setBackground(goldColour);

        // Toolbar!!
        ToolBar toolBar = new ToolBar(trackPanel, clock);

        JPanel mainPanel = new JPanel(new BorderLayout());
        //mainPanel.add(outerControlPanel, BorderLayout.PAGE_END);
        mainPanel.add(trackPanel, BorderLayout.CENTER);

        contentPane.add(toolBar,BorderLayout.PAGE_START);
        //contentPane.add(outerControlPanel,BorderLayout.WEST);
        //contentPane.add(trackPanel,BorderLayout.CENTER);
        contentPane.add(mainPanel,BorderLayout.CENTER);

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
