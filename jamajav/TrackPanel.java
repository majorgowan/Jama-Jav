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
    private ArrayList<JLabel> avatarLabel;

    private static Color goldColour = new Color(0.7f,0.7f,0.98f);
    private static Color highlightColour = new Color(0.8f,0.4f,0.2f);
    private static Color shadowColour = new Color(0.3f,0.1f,0.04f);

    private int ntracks = 0;

    private JFrame parent;
    private ToolBar toolBar;

    private JPanel mainPanel;
    private BigTimeLine bigTimeLine;

    private Metronome metronome;
    private Clock clock;
    private Prefs prefs;

    // avatar list (not mapped to tracks)
    private ArrayList<Avatar> avatars;

    // Observer method for stopping clock when playback or record is finished
    public void update(Observable obs, Object arg) {

        // System.out.println("I THINK WE GOT SOMETHING THERE!");
        for (int i = 0; i < tracks.size(); i++) {
            if (obs == tracks.get(i).getTrackData().getStopPlay()) {
                // loop over all tracks - if any are stopped, then
                // (re)enable the Record and Play buttons
                for (int j = 0; j < tracks.size(); j++) {
                    if (tracks.get(j).getTrackData().getStopPlay().getValue())
                        tracks.get(j).enableRecordPlay();
                }
                if (allStopped()) {
                    clock.stop();
                    bigTimeLine.stop();
                }
            } else if (obs == tracks.get(i).getTrackData().getStopCapture()) {
                // if firing track is stopped recording, stop the clock
                if (tracks.get(i).getTrackData().getStopCapture().getValue()) {
                    clock.stop(); // stop clock (probably redundant)
                    bigTimeLine.stop();
                    // System.out.println("Stopped clock, resetting toolTip");
                    // System.out.println("Running time: " + tracks.get(i).getInfo().getRunningTime());
                    tracks.get(i).resetToolTip(); // reset ToolTip to set running time
                    tracks.get(i).enableRecordPlay();
                }
                refreshBigTimeLine();
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
                refreshBigTimeLine();
                break;

            case ("open") :
                open();
                // set bigTimeLine to full timespan
                refreshBigTimeLine();
                bigTimeLine.setFull();
                // Scroll the TrackPanel to the top to show first Track:
                // (doesn't go quite to the top for some reason but noone will notice)
                mainPanel.scrollRectToVisible(new Rectangle(0,0,0,0));
                break;

            case ("merge") :
                merge();
                refreshBigTimeLine();
                break;

            case ("save") :
                save();
                break;

            case ("addnewtrack") :
                addNewTrack();
                break;

            case("playallfromtop") :
                if (!allStopped()) {
                    allStop();
                    waitASecond(1000);
                }
                // start all tracks playing from the beginning
                for (int i = 0; i < tracks.size(); i++) 
                    tracks.get(i).startPlaying();
                bigTimeLine.setTime(0.0);
                bigTimeLine.start();
                break;

            case ("playall") :
                if (!allStopped()) {
                    allStop();
                    waitASecond(1000);
                }
                // start all tracks playing:
                for (int i = 0; i < tracks.size(); i++) 
                    tracks.get(i).startPlaying(
                            bigTimeLine.getMinTime(), bigTimeLine.getMaxTime());
                bigTimeLine.setTime(bigTimeLine.getMinTime());
                bigTimeLine.start();
                break;

            case ("playselectedfromtop") :
                // play selected tracks from beginning to end
                noneSelected = true;
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected() && tracks.get(i).isNotEmpty()) 
                        noneSelected = false;
                if (!noneSelected) {
                    if (!allStopped()) {
                        allStop();
                        waitASecond(1000);
                    }
                    for (int i = 0; i < tracks.size(); i++) 
                        if (tracks.get(i).isSelected() && tracks.get(i).isNotEmpty()) 
                            tracks.get(i).startPlaying();
                    bigTimeLine.setTime(0.0);
                    bigTimeLine.start(); 
                    clock.restart();
                }
                break;

            case ("playselected") :
                noneSelected = true;
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected() && tracks.get(i).isNotEmpty()) 
                        noneSelected = false;
                if (!noneSelected) {
                    if (!allStopped()) {
                        allStop();
                        waitASecond(1000);
                    }
                    for (int i = 0; i < tracks.size(); i++)
                        if (tracks.get(i).isSelected() && tracks.get(i).isNotEmpty()) 
                            tracks.get(i).startPlaying(
                                    bigTimeLine.getMinTime(), bigTimeLine.getMaxTime());
                    bigTimeLine.setTime(bigTimeLine.getMinTime());
                    bigTimeLine.start();
                }
                break;

            case ("pause") :
                allPause();
                break;

            case ("allstop") :
                allStop();
                break;

            case ("playrecord") :
                // add new track:
                addNewTrack();

                // check if any are playing and if so, stop them
                if (!allStopped()) {
                    allStop();
                    waitASecond(1000);
                }
                // start selected tracks playing:
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected())
                        tracks.get(i).startPlaying();

                bigTimeLine.setTime(0.0);
                bigTimeLine.start();

                // start new track recording
                tracks.get(tracks.size()-1).startRecording();
                toolBar.focusOnStop();
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

            case ("removeselected") :
                for (int i = tracks.size() - 1; i >= 0; i--) {
                    // System.out.println("track " + i + " is " + tracks.get(i).isSelected());
                    if (tracks.get(i).isSelected()) {
                        removeTrack(i);
                    }
                }
                refreshBigTimeLine();
                break;

            case ("moveselectedup") :
                for (int i = 1; i < tracks.size(); i++) {
                    // System.out.println("track " + i + " is " + tracks.get(i).isSelected());
                    if (tracks.get(i).isSelected() && !tracks.get(i-1).isSelected()) {
                        swapTracks(i,i-1);
                    }
                }
                break;

            case ("moveselecteddown") :
                for (int i = tracks.size()-2; i >= 0; i--) {
                    // System.out.println("track " + i + " is " + tracks.get(i).isSelected());
                    if (tracks.get(i).isSelected() && !tracks.get(i+1).isSelected()) {
                        swapTracks(i,i+1);
                    }
                }
                break;

            case ("concatenateselected") :
                noneSelected = true;
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected() && tracks.get(i).isNotEmpty()) {
                        noneSelected = false;
                    }
                if (!noneSelected) {
                    TrackData td = concatenateSelected();
                    Info info = new Info();
                    info.setContributor(prefs.getUserName());
                    info.setLocation(prefs.getUserCity());
                    info.setAvatar(prefs.getAvatar());
                    info.setRunningTime(td.getRunningTime());
                    info.setTitle("Concatenated track");
                    td.putInfo(info);
                    addNewTrack();
                    tracks.get(ntracks-1).setTrackData(td);
                    // set monitor of new track to the TrackData
                    td.setMonitor(tracks.get(ntracks-1).getMonitor());
                    tracks.get(ntracks-1).refreshVisualizerAndTimeLine();
                    refreshAvatars();
                }
                refreshBigTimeLine();
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
                    info.setTitle("Combined track");
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

            case ("instructions") :

                // replace with a proper dialog with two panels with a
                // list of topics and the help information

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

            case ("about") :

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

            case ("exit") :
                System.exit(0);
                break;
        }
    }

    public void refreshBigTimeLine() {
        if (tracks.size() > 0) {
            double running = 0.0;
            for (int i = 0; i < tracks.size(); i++)
                running = Math.max(running, tracks.get(i).getInfo().getRunningTime());

            bigTimeLine.setRunningTime(running);
        } else
            bigTimeLine.setRunningTime(0.0);

        bigTimeLine.rehash();
        bigTimeLine.repaint();
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

        linePanel.get(ntracks-1).setBackground(goldColour);
        linePanel.get(ntracks-1).add(avatarLabel.get(ntracks-1));
        linePanel.get(ntracks-1).add(tracks.get(ntracks-1));
        linePanel.get(ntracks-1)
            .setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        mainPanel.add(linePanel.get(ntracks-1));

        revalidate();
        //Scroll the TrackPanel to the bottom to show the new Track:
        mainPanel.scrollRectToVisible(new Rectangle(0,(int)mainPanel.getPreferredSize().getHeight(),10,10));

        // Make new track selected
        tracks.get(ntracks-1).setSelected(true);
    }

    public void removeTrack(int i) {
        tracks.get(i).stopRecording();
        tracks.get(i).stopPlaying();
        mainPanel.remove(linePanel.get(i));
        tracks.remove(i);
        linePanel.remove(i);
        ntracks--;
        refreshMainPanel();
    }

    public void swapTracks(int i, int j) {
        // swap positions of two tracks (intermediate method for shifting up and down)
        // swap track
        Track tempTrack = tracks.get(i);
        tracks.set(i,tracks.get(j));
        tracks.set(j,tempTrack);

        // swap avatarLabel
        JLabel tempLabel = avatarLabel.get(i);
        avatarLabel.set(i,avatarLabel.get(j));
        avatarLabel.set(j,tempLabel);

        // swap linePanel
        JPanel tempPanel = linePanel.get(i);
        linePanel.set(i,linePanel.get(j));
        linePanel.set(j,tempPanel);

        refreshMainPanel();
    }

    public Track getTrack(int i) {
        return tracks.get(i);
    }

    public int whichTrackAmI(Track trk) {
        return tracks.indexOf(trk);
    }

    private void waitASecond(int milliseconds) {
        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void allStop() {
        for (int i = 0; i < tracks.size(); i++) { 
            tracks.get(i).stopPlaying();
            tracks.get(i).stopRecording();
        }
    }

    private boolean allStopped() {
        boolean result = true;
        for (int j = 0; j < tracks.size(); j++) 
            if (!tracks.get(j).getTrackData().getStopPlay().getValue()) 
                result = false;

        return result;
    }

    private void allPause() {
        // if any are running, then pause them and pause clock
        for (int j = 0; j < tracks.size(); j++) 
            if (!tracks.get(j).getTrackData().getStopPlay().getValue()) 
                tracks.get(j).pausePlaying();

        if (!allStopped()) {
            clock.toggle();
            bigTimeLine.toggle();
        }
    }

    // choose a file
    private String chooseFile(String extension, String fileType, String operation) {
        JFileChooser chooser = new JFileChooser(
                new File(System.getProperty("user.dir")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                fileType, extension);
        chooser.setFileFilter(filter);
        int returnVal = 0;
        if (operation.equals("save") || operation.equals("export"))
            returnVal = chooser.showSaveDialog(
                    SwingUtilities.windowForComponent(this));
        else
            returnVal = chooser.showOpenDialog(
                    SwingUtilities.windowForComponent(this));

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose the file: " +
                    chooser.getSelectedFile().getAbsolutePath());
            return chooser.getSelectedFile().getAbsolutePath();
        } else {
            return "rathernot";
        }
    }

    private void save() {
        // get filename root
        String filename = chooseFile("jj","Jama Jav files","save");
        if (!(filename.equals("rathernot"))) {
            filename = filename.split("\\.")[0];  // strip .jj from filename

            // open two files: root.bin for binary, root.jj for ASCII
            FileWriter asciifw = null;
            FileOutputStream binfos = null;
            try {
                asciifw = new FileWriter(filename + ".jj");
                binfos = new FileOutputStream(filename + ".bin"); 

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
            } finally {
                try {
                    if (asciifw != null) asciifw.close();
                    if (binfos != null) binfos.close();
                } catch(IOException ie) {
                    System.out.println("Error closing jj or bin file");
                }
            }
        }
    }

    private void export(TrackData td) {
        // get filename root
        String filename = chooseFile("wav", "Wave audio files", "export");
        if (!(filename.equals("rathernot"))) {
            filename = filename.split("\\.")[0];
            td.writeToFile(filename);
        }
    }

    private void open() {
        String filename = chooseFile("jj","Jama Jav files","open");
        if (!(filename.equals("rathernot"))) {
            newDoc();
            filename = filename.split("\\.")[0];  // strip .jj from filename

            parent.setTitle("Major's Jama Jav - " + filename);

            System.out.println("Opening " + filename + ".jj"
                    + " and " + filename + ".bin");

            BufferedReader br = null;
            FileInputStream binfis = null;
            try {
                br = new BufferedReader(new FileReader(filename + ".jj"));
                binfis = new FileInputStream(filename + ".bin"); 

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
                int newTracks = Integer.parseInt(words[1]);

                System.out.println("Opening " + newTracks + " tracks.");

                // loop over tracks
                for (int i = 0; i < newTracks; i++) {
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
                    info.setRunningTime(Double.parseDouble(words[0]));

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
                    tracks.get(i).setSelected(false);
                }
            } catch (IOException e) {
                System.out.println("Error reading from " + filename + ".bin or " 
                        + filename + ".jj");
            } finally {
                try {
                    if (br != null) br.close();
                    if (binfis != null) binfis.close();
                } catch(IOException ie) {
                    System.out.println("Error closing jj or bin file");
                }
            }
        }
    }

    private void merge() {
        String filename = chooseFile("jj","Jama Jav files","merge");
        if (!(filename.equals("rathernot"))) {
            filename = filename.split("\\.")[0];  // strip .jj from filename

            System.out.println("Merging tracks from " + filename + ".jj"
                    + " and " + filename + ".bin");

            BufferedReader br = null;
            FileInputStream binfis = null;

            try {
                br = new BufferedReader(new FileReader(filename + ".jj"));
                binfis = new FileInputStream(filename + ".bin");
                // parse jj file

                // discard Metronome parameters (keep settings from existing tracks)
                String[] words = br.readLine().split(" ");

                // get number of additional tracks,
                words = br.readLine().split(" ");
                int newTracks = Integer.parseInt(words[1]);
                int oldTracks = ntracks;

                System.out.println("Adding " + newTracks + " tracks.");

                // loop over new tracks
                for (int i = oldTracks; i < oldTracks + newTracks; i++) {
                    // System.out.println("Adding track number " + i + " of " + (oldTracks+newTracks));
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
                    info.setRunningTime(Double.parseDouble(words[0]));

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
                    tracks.get(i).setSelected(false);
                }
            } catch (IOException e) {
                System.out.println("Error reading from " + filename + ".bin or " 
                        + filename + ".jj");
            } finally {
                try {
                    if (br != null) br.close();
                    if (binfis != null) binfis.close();
                } catch(IOException ie) {
                    System.out.println("Error closing jj or bin file");
                }
            }
        }
    }

    private void newDoc() {
        for (int i = tracks.size()-1; i >= 0; i--) {
            tracks.get(i).stopRecording();
            tracks.get(i).stopPlaying();
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
        // ClassLoader cl = this.getClass().getClassLoader();
        try {
            InputStream is = TrackPanel.class.getResourceAsStream("/Images/Avatars/list.txt");
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

        // find longest selected track:
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

    public TrackData concatenateSelected() {
        // compute length of concatenated track:
        int totalByteLength = 0;
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).isSelected())
                totalByteLength += tracks.get(i).getTrackData().getBytes().length;
        }

        byte[] newBytes = new byte[totalByteLength];

        // copy bytes from each selected track to the newBytes array
        int byteCount = 0;
        for (int i = 0; i < tracks.size(); i++)
            if (tracks.get(i).isSelected()) {
                byte[] oldBytes = tracks.get(i).getTrackData().getBytes();
                for (int j = 0; j < oldBytes.length; j++)
                    newBytes[byteCount + j] = oldBytes[j];
                byteCount += oldBytes.length;
            }

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

        bigTimeLine = new BigTimeLine();

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

        JPanel outerMainPanel = new JPanel(new FlowLayout());
        outerMainPanel.setBackground(goldColour);
        mainPanel.setBackground(goldColour);
        outerMainPanel.add(mainPanel);

        MainScrollPane scrollPane = new MainScrollPane(outerMainPanel);

        add(bigTimeLine,BorderLayout.PAGE_START);
        add(scrollPane,BorderLayout.CENTER);
    }
}

