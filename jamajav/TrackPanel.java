package jamajav;

// Swing packages:
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;

// for formatting numbers:
import java.text.DecimalFormat;

// For resizable arrays:
import java.util.ArrayList;

// For input/output:
import java.io.*;

// For finding avatar files:
import java.net.URL;
import java.net.URISyntaxException;

// For observing:
import java.util.Observable;
import java.util.Observer;

class TrackPanel extends JPanel implements ActionListener, Observer {

    private ArrayList<Track> tracks;
    private ArrayList<JPanel> linePanel;

    private static Color goldColour = new Color(0.7f,0.7f,0.98f);
    private static Color highlightColour = new Color(0.8f,0.4f,0.2f);
    private static Color shadowColour = new Color(0.3f,0.1f,0.04f);

    private int ntracks = 0;

    private JFrame parent;
    private ToolBar toolBar;

    private JPanel mainPanel;

    private Metronome metronome;
    private Clock clock;
    private Prefs prefs;

    private ArrayList<Avatar> avatars;
    private ArrayList<JLabel> avatarLabel;

    // Observer method for stopping clock when playback or record is finished
    public void update(Observable obs, Object arg) {

        // System.out.println("I THINK WE GOT SOMETHING THERE!");
        for (int i = 0; i < tracks.size(); i++) {
            if (obs == tracks.get(i).getTrackData().getStopPlay()) {
                // loop over all tracks - if all are stopped playing
                // then stop clock
                boolean allStopped = true;
                for (int j = 0; j < tracks.size(); j++) {
                    if (!tracks.get(j).getTrackData().getStopPlay().getValue())
                        allStopped = false;
                }
                if (allStopped)
                    clock.stop();
            } else if (obs == tracks.get(i).getTrackData().getStopCapture()) {
                // if firing track is stopped recording, stop the clock
                if (tracks.get(i).getTrackData().getStopCapture().getValue()) {
                    clock.stop(); // stop clock (probably redundant)
                    // System.out.println("Stopped clock, resetting toolTip");
                    // System.out.println("Running time: " + tracks.get(i).getInfo().getRunningTime());
                    tracks.get(i).resetToolTip(); // reset ToolTip to set running time
                }
            }
        }
    }

    // ActionListener method
    public void actionPerformed(ActionEvent ae) {

        boolean noneSelected;

        String comStr = ae.getActionCommand();

        switch (comStr) {
            case ("newjam") :
                newDoc();
                break;

            case ("save") :
                save();
                break;

            case ("open") :
                open();
                // Scroll the TrackPanel to the top to show first Track:
                // (doesn't go quite to the top for some reason but noone will notice)
                mainPanel.scrollRectToVisible(new Rectangle(0,0,0,0));
                break;

            case ("editprefs") :
                // open dialog with a prefspanel
                final JDialog prefsDialog = new JDialog(parent, "Edit preferences", true);
                prefsDialog.setLocationRelativeTo(parent);
                prefsDialog.getContentPane().setLayout(new BorderLayout());
                prefsDialog.getContentPane().add(
                        new PrefsPanel(prefs, avatars, prefsDialog), BorderLayout.CENTER);
                prefsDialog.revalidate();
                prefsDialog.pack();
                prefsDialog.setVisible(true);
                break;

            case ("exit") :
                System.exit(0);
                break;

            case ("addnewtrack") :
                addNewTrack();
                break;

            case ("removeselected") :
                for (int i = tracks.size() - 1; i >= 0; i--) {
                    // System.out.println("track " + i + " is " + tracks.get(i).isSelected());
                    if (tracks.get(i).isSelected()) {
                        removeTrack(i);
                    }
                }
                refreshMainPanel();
                break;

            case ("playselected") :
                noneSelected = true;
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected() && tracks.get(i).isNotEmpty()) {
                        noneSelected = false;
                        tracks.get(i).playback();
                    }
                if (!noneSelected)
                    clock.restart();
                break;

            case ("combineselected") :
                noneSelected = true;
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected() && tracks.get(i).isNotEmpty()) {
                        noneSelected = false;
                    }
                if (!noneSelected) {
                    TrackData td = combineSelected();
                    Info info = new Info();
                    info.setContributor(prefs.getUserName());
                    info.setLocation(prefs.getUserCity());
                    info.setAvatar(prefs.getAvatar());
                    info.setRunningTime(td.getRunningTime());
                    td.putInfo(info);
                    addNewTrack();
                    tracks.get(ntracks-1).setTrackData(td);
                    // set monitor of new track to the TrackData
                    td.setMonitor(tracks.get(ntracks-1).getMonitor());
                    tracks.get(ntracks-1).refreshVisualizerAndTimeLine();
                    refreshAvatars();
                }
                break;

            case ("exportselected") :
                noneSelected = true;
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected() && tracks.get(i).isNotEmpty()) {
                        noneSelected = false;
                    }
                if (!noneSelected) {
                    TrackData td = combineSelected();
                    export(td);
                }
                break;

            case ("selectall") :
                boolean getState = false;    // none are selected
                for (int i = 0; i < tracks.size(); i++)
                    if (tracks.get(i).isSelected())
                        getState = true;

                if (getState)               // if any are selected, unselect-all
                    for (int i = 0; i < tracks.size(); i++)
                        tracks.get(i).setSelected(false);
                else                        // else unselect-all
                    for (int i = 0; i < tracks.size(); i++)
                        tracks.get(i).setSelected(true);
                break;

            case ("playrecord") :
                // add new track:
                addNewTrack();

                // start selected tracks playing:
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected())
                        tracks.get(i).startPlaying();

                // start new track recording
                tracks.get(tracks.size()-1).startRecording();
                toolBar.focusOnStop();
                break;

            case ("allstop") :
                allStop();
                break;

            case ("pause") :
                allPause();
                break;

            case ("playall") :
                // start all tracks playing:
                for (int i = 0; i < tracks.size(); i++) 
                    tracks.get(i).startPlaying();
                break;

            case ("Instructions") :

                JDialog helpDialog = new JDialog(parent,"How to use Jama Jav");

                JLabel helpText = new JLabel("<html><p align=left>"
                        + "Have fun.<br><br>"
                        );

                helpDialog.setLayout(new BorderLayout());

                helpDialog.add(helpText);
                helpDialog.setLocationRelativeTo(parent);
                helpDialog.getRootPane().setBorder(
                        BorderFactory.createEmptyBorder(30,30,30,30));
                helpDialog.pack();
                helpDialog.setVisible(true);

                break;

            case ("About") :

                JDialog aboutDialog = new JDialog(parent,"About Jama Jav");

                JLabel aboutText = new JLabel("<html><p align=center>"
                        + "The Major's Jama Jav<br><br><br>"
                        + "Copyright (c) 2015<br>"
                        + "by Mark D. Fruman<br>"
                        );

                aboutDialog.setLayout(new BorderLayout());

                aboutDialog.add(aboutText);
                aboutDialog.setLocationRelativeTo(parent);
                aboutDialog.getRootPane().setBorder(
                        BorderFactory.createEmptyBorder(30,30,30,30));
                aboutDialog.pack();
                aboutDialog.setVisible(true);
                break;

        }
    }

    private void refreshMainPanel() {

        mainPanel.removeAll();

        for (int i=0; i<ntracks; i++) {
            mainPanel.add(linePanel.get(i));
        }
        mainPanel.revalidate();
        repaint();
    }

    public void refreshAvatars() {
        for (int i = 0; i < tracks.size(); i++)
            avatarLabel.get(i).setIcon(
                    new ImageIcon(avatars.get(
                            findAvatarIndex(
                                tracks.get(i).getInfo().getAvatar())).getImage()));
        revalidate();
    }

    public int getNTracks() {
        return tracks.size();
    }

    public void setToolBar(ToolBar tb) {
        toolBar = tb;
    }

    public void addNewTrack() {
        ntracks++;
        tracks.add(new Track(parent, this, metronome, clock, prefs));
        linePanel.add(new JPanel());
        avatarLabel.add(new JLabel(
                    new ImageIcon(avatars.get(findAvatarIndex(prefs.getAvatar())).getImage())));

        avatarLabel.get(ntracks-1)
            .setBorder(BorderFactory
                    .createEtchedBorder(EtchedBorder.RAISED, highlightColour, shadowColour));

        // System.out.println("adding track ... now " + ntracks + " tracks");

        tracks.get(ntracks-1)
            .setBorder(BorderFactory.createRaisedBevelBorder());

        linePanel.get(ntracks-1).setBackground(goldColour);
        linePanel.get(ntracks-1).add(avatarLabel.get(ntracks-1));
        linePanel.get(ntracks-1).add(tracks.get(ntracks-1));
        linePanel.get(ntracks-1)
            .setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        mainPanel.add(linePanel.get(ntracks-1));

        revalidate();
        //Scroll the TrackPanel to the bottom to show the new Track:
        mainPanel.scrollRectToVisible(new Rectangle(0,(int)mainPanel.getPreferredSize().getHeight(),10,10));
    }

    private void removeTrack(int i) {
        tracks.get(i).stopRecording();
        tracks.get(i).stopPlaying();
        mainPanel.remove(linePanel.get(i));
        tracks.remove(i);
        linePanel.remove(i);
        ntracks--;
    }

    public Track getTrack(int i) {
        return tracks.get(i);
    }

    private void allStop() {
        for (int i = 0; i < tracks.size(); i++) { 
            tracks.get(i).stopPlaying();
            tracks.get(i).stopRecording();
        }
    }

    private void allPause() {
        boolean allStopped = true;
        for (int j = 0; j < tracks.size(); j++) {
            if (!tracks.get(j).getTrackData().getStopPlay().getValue()) {
                tracks.get(j).pausePlaying();
                allStopped = false;
            }
        }
        // if any are running, then pause clock
        if (!allStopped) 
            clock.toggle();
    }

    private void save() {
        // get filename root
        String filename = JOptionPane.showInputDialog(
                "Please enter a filename");

        // open two files: root.bin for binary, root.jj for ASCII
        try (FileWriter asciifw = new FileWriter(filename + ".jj");
                FileOutputStream binfos 
                = new FileOutputStream(filename + ".bin") ) {

            // write Metronome settings to ASCII file
            int[] mP = metronome.getParam();
            asciifw.write("Metronome: " 
                    + mP[0] + " bpMin "
                    + mP[1] + " bpMeas "
                    + "offset " + metronome.getOffset() + "\n");

            // write number of tracks to ASCII file
            asciifw.write("Tracks: " + tracks.size() + "\n");

            // loop over tracks
            for (int i = 0; i < tracks.size(); i++) {
                Info in = tracks.get(i).getInfo();
                asciifw.write("TRACK_" + i + "_INFO_BEGIN\n");
                asciifw.write(in.getTitle() + "\n");
                asciifw.write(in.getContributor() + "\n");
                asciifw.write(in.getAvatar() + "\n");
                asciifw.write(in.getDate() + "\n");
                asciifw.write(in.getLocation() + "\n");
                asciifw.write(in.getRunningTime() + " seconds\n");
                asciifw.write(in.getNotesSize() + " notes\n");
                for (int j = 0; j < in.getNotesSize(); j++)
                    asciifw.write(in.getNote(j) + "\n");
                asciifw.write("INFO_END\n");

                // get byte array from track
                byte[] bytes = tracks.get(i).getBytes();

                // write byte-array length to jj file
                asciifw.write("Byte_length: " + bytes.length + "\n");

                // write bytes to binary file
                binfos.write(bytes);

                parent.setTitle("Major's Jama Jav - " + filename);
            }
        } catch (IOException e) {
            System.out.println("Error reading from file " 
                    + filename + ".jj" + " or " + filename + ".bin");
        }
    }

    private void export(TrackData td) {
        // get filename root
        String filename = JOptionPane.showInputDialog(
                "Please enter a filename");

        td.writeToFile(filename);
    }

    // choose a jj file
    private String chooseFile() {
        JFileChooser chooser = new JFileChooser(
                new File(System.getProperty("user.dir")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Jama Jav files", "jj");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(
                SwingUtilities.windowForComponent(this));
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getAbsolutePath());
            return chooser.getSelectedFile().getAbsolutePath();
        } else {
            return "rathernot";
        }
    }

    private void open() {
        String filename = chooseFile();
        if (!(filename.equals("rathernot"))) {
            newDoc();
            filename = filename.split("\\.")[0];  // strip .jj from filename

            parent.setTitle("Major's Jama Jav - " + filename);

            System.out.println("Opening " + filename + ".jj"
                    + " and " + filename + ".bin");
            try (BufferedReader br 
                    = new BufferedReader(new FileReader(filename + ".jj"));
                    FileInputStream binfis 
                    = new FileInputStream(filename + ".bin") ) 
            { 

                // parse jj file

                // get and set Metronome parameters
                String[] words = br.readLine().split(" ");
                int bpMin = Integer.parseInt(words[1]);
                int bpMeas = Integer.parseInt(words[3]);
                metronome.setParam(bpMin, bpMeas);
                if (words.length >= 7) {
                    double offset = Double.parseDouble(words[6]);
                    metronome.setOffset(offset);
                } else
                    metronome.setOffset(0.0);

                // create appropriate number of tracks,
                words = br.readLine().split(" ");
                int ntracks = Integer.parseInt(words[1]);

                System.out.println("Opening " + ntracks + " tracks.");

                // loop over tracks
                for (int i = 0; i < ntracks; i++) {
                    addNewTrack();

                    Info info = tracks.get(i).getInfo();
                    br.readLine(); // INFO_BEGIN
                    info.setTitle(br.readLine());
                    info.setContributor(br.readLine());
                    info.setAvatar(br.readLine());

                    avatarLabel.get(i).setIcon(
                            new ImageIcon(avatars.get(findAvatarIndex(info.getAvatar())).getImage()));

                    info.setDate(br.readLine());
                    info.setLocation(br.readLine());

                    words = br.readLine().split(" ");
                    info.setRunningTime(Integer.parseInt(words[0]));

                    words = br.readLine().split(" ");
                    int numNotes = Integer.parseInt(words[0]);
                    for (int j = 0; j < numNotes; j++)
                        info.addNote(br.readLine());

                    br.readLine(); // INFO_END
                    tracks.get(i).setToolTip(info);

                    // read number of bytes
                    words = br.readLine().split(" ");
                    int nbytes = Integer.parseInt(words[1]);
                    byte[] bytes = new byte[nbytes];

                    // read bytes from bin file
                    int bytesread = binfis.read(bytes);
                    // System.out.println("Track " + i + ":"
                    //         + " read " + bytesread
                    //         + " bytes");
                    tracks.get(i).putBytes(bytes);
                }
            } catch (IOException e) {
                System.out.println("Error reading from " + filename + ".MMM");
            }
        }
    }

    private void newDoc() {
        for (int i = tracks.size()-1; i >= 0; i--) {
            mainPanel.remove(linePanel.get(i));
            linePanel.remove(i);
            tracks.remove(i);
            ntracks--;
        }

        parent.setTitle("Major's Jama Jav");

        // reset metronome to saved preferences
        int[] metroset = prefs.getMetroSet();
        metronome.setParam(metroset[0], metroset[1]);
        metronome.setOffset(0.0);
        // reset clock
        clock.reset();

        repaint();
    }

    private void initAvatars() {
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            InputStream is = cl.getResourceAsStream("Images/Avatars/list.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s;
            while ((s = br.readLine()) != null) {
                // System.out.println(s.split("\\.")[0]);
                avatars.add(new Avatar(s.split("\\.")[0]));
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private int findAvatarIndex(String name) {
        for (int i = 0; i < avatars.size(); i++)
            if (avatars.get(i).getName().equals(name)) {
                return i;
            }

        return 0;
    }

    public TrackData combineSelected() {

        // find longest selected track
        int maxDataLength = 0;
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).isSelected())
                maxDataLength
                    = Math.max(tracks.get(i).getTrackData().getBytes().length, maxDataLength);
        }

        // byte array for combined track
        byte[] newBytes = new byte[0];

        for (int i = 0; i < tracks.size(); i++)
            if (tracks.get(i).isSelected())
                newBytes 
                    = EightSixteen.addEights(
                            newBytes, 
                            1.0,
                            tracks.get(i).getTrackData().getBytes(), 
                            (double)(tracks.get(i).getVolume())/10.0);

        TrackData newTrackData = new TrackData();
        newTrackData.putBytes(newBytes);

        return newTrackData;

    }

    TrackPanel(JFrame jfrm, Metronome m, Clock c, Prefs p) {

        parent = jfrm;
        metronome = m;
        clock = c;
        prefs = p;

        tracks = new ArrayList<Track>(0);
        linePanel = new ArrayList<JPanel>(0);
        avatarLabel = new ArrayList<JLabel>(0);

        // initialize Avatars
        avatars = new ArrayList<Avatar>(0);
        initAvatars();

        // create an empty Jam by default
        newDoc();

        // GUI Stuff ...
        setLayout(new BorderLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

        JPanel outerMainPanel = new JPanel(new FlowLayout());
        outerMainPanel.setBackground(goldColour);
        mainPanel.setBackground(goldColour);
        outerMainPanel.add(mainPanel);

        MainScrollPane scrollPane = new MainScrollPane(outerMainPanel);

        add(scrollPane,BorderLayout.CENTER);
    }
}

