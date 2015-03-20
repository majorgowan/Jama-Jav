package jamajav;

// Swing packages:
import javax.swing.*;
import javax.swing.filechooser.*;
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

class TrackPanel extends JPanel implements ActionListener {

    private ArrayList<Track> tracks;
    private ArrayList<JPanel> linePanel;

    private int ntracks = 0;

    private JFrame parent;

    private JPanel mainPanel;

    private JButton allStopButton;   // must be global to receive focus!

    private Metronome metronome;
    private Clock clock;
    private Prefs prefs;

    private ArrayList<Avatar> avatars;
    private ArrayList<JLabel> avatarLabel;

    // ActionListener method
    public void actionPerformed(ActionEvent ae) {
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

            case ("New Track") :
                addNewTrack();
                break;

            case ("Remove Selected") :
                for (int i = tracks.size() - 1; i >= 0; i--) {
                    //System.out.println("track " + i + " is " + tracks.get(i).isSelected());
                    if (tracks.get(i).isSelected()) {
                        removeTrack(i);
                    }
                }
                refreshMainPanel();
                break;

            case ("Play Selected") :
                for (int i = 0; i < tracks.size(); i++) 
                    if (tracks.get(i).isSelected() &&
                            tracks.get(i).isNotEmpty()) 
                        tracks.get(i).playback();
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
                allStopButton.requestFocusInWindow();
                break;

            case ("allstop") :
                allStop();
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

    private void addNewTrack() {
        ntracks++;
        tracks.add(new Track(parent, metronome, clock, prefs));
        linePanel.add(new JPanel());
        avatarLabel.add(new JLabel(
                    new ImageIcon(avatars.get(findAvatarIndex(prefs.getAvatar())).getImage())));

        //System.out.println("adding track ... now " + ntracks + " tracks");

        tracks.get(ntracks-1)
            .setBorder(BorderFactory.createRaisedBevelBorder());

        linePanel.get(ntracks-1).add(avatarLabel.get(ntracks-1));
        linePanel.get(ntracks-1).add(tracks.get(ntracks-1));
        linePanel.get(ntracks-1)
            .setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        mainPanel.add(linePanel.get(ntracks-1));
        revalidate();
    }

    private void removeTrack(int i) {
        tracks.get(i).stopRecording();
        tracks.get(i).stopPlaying();
        mainPanel.remove(linePanel.get(i));
        tracks.remove(i);
        linePanel.remove(i);
        ntracks--;
    }

    private void allStop() {
        for (int i = 0; i < tracks.size(); i++) { 
            tracks.get(i).stopPlaying();
            tracks.get(i).stopRecording();
        }
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
                    + mP[1] + " bpMeas\n");

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

                System.out.println("Metronome settings: " 
                        + bpMin + " bpMin "
                        + "and " + bpMeas + " bpMeas.");

                // create appropriate number of tracks,
                words = br.readLine().split(" ");
                int ntracks = Integer.parseInt(words[1]);

                System.out.println("Opening " + ntracks + " tracks.");

                // loop over tracks
                for (int i = 0; i < ntracks; i++) {
                    addNewTrack();

                    Info in = tracks.get(i).getInfo();
                    br.readLine(); // INFO_BEGIN
                    in.setTitle(br.readLine());
                    in.setContributor(br.readLine());
                    in.setAvatar(br.readLine());

                    avatarLabel.get(i).setIcon(
                            new ImageIcon(avatars.get(findAvatarIndex(in.getAvatar())).getImage()));

                    in.setDate(br.readLine());
                    in.setLocation(br.readLine());

                    words = br.readLine().split(" ");
                    in.setRunningTime(Integer.parseInt(words[0]));

                    words = br.readLine().split(" ");
                    int numNotes = Integer.parseInt(words[0]);
                    for (int j = 0; j < numNotes; j++)
                        in.addNote(br.readLine());

                    br.readLine(); // INFO_END
                    tracks.get(i).setToolTip();

                    // read number of bytes
                    words = br.readLine().split(" ");
                    int nbytes = Integer.parseInt(words[1]);
                    byte[] bytes = new byte[nbytes];

                    // read bytes from bin file
                    int bytesread = binfis.read(bytes);
                    System.out.println("Track " + i + ":"
                            + " read " + bytesread
                            + " bytes");
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
        repaint();
    }

    private void initAvatars() {
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            InputStream is = cl.getResourceAsStream("Images/Avatars/list.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s.split("\\.")[0]);
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

        setBackground(new Color(0.75f,0.6f,0.1f));
        setLayout(new BorderLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));

        // create and add buttons to buttonPanel:
        JPanel buttonPanel = new JPanel(new GridLayout(2,4));

        JButton newTrackButton = new JButton("New Track");
        newTrackButton.addActionListener(this);

        JButton selectAllButton = new JButton("(Un)select All");
        selectAllButton.setActionCommand("selectall");
        selectAllButton.addActionListener(this);

        JButton playSelectedButton = new JButton("Play Selected");
        playSelectedButton.addActionListener(this);

        JButton playRecordButton = new JButton("Play Sel + Rec new");
        playRecordButton.setActionCommand("playrecord");
        playRecordButton.addActionListener(this);

        allStopButton = new JButton("All Stop!");
        allStopButton.setActionCommand("allstop");
        allStopButton.addActionListener(this);

        JButton removeSelectedButton = new JButton("Remove Selected");
        removeSelectedButton.addActionListener(this);

        buttonPanel.add(newTrackButton);
        buttonPanel.add(selectAllButton);
        buttonPanel.add(playRecordButton);
        buttonPanel.add(removeSelectedButton);
        buttonPanel.add(playSelectedButton);
        buttonPanel.add(allStopButton);

        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        JPanel outerMainPanel = new JPanel(new FlowLayout());
        outerMainPanel.add(mainPanel);

        MainScrollPane scrollPane = new MainScrollPane(outerMainPanel);

        add(scrollPane,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
    }
}

