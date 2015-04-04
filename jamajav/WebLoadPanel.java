package jamajav;

// Swing packages
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

// For input/output
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

// For resizable arrays
import java.util.ArrayList;

// for getting stuff from web
import java.net.*;

class WebLoadPanel extends JPanel implements ActionListener, ListSelectionListener {

    final private int DEFAULT_WIDTH = 500;
    final private int DEFAULT_HEIGHT = 250;

    private TrackPanel trackPanel;
    private JList fileList;
    private String basePath;
    private ArrayList<Info[]> infoArrayList;

    private JPanel infoHeadPanel;
    private JPanel infoAvatarPanel;

    // for ActionListener interface
    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        switch (comStr) {

            case ("Ok") :
                java.util.List<String> selection = fileList.getSelectedValuesList();
                System.out.println("You chose " + selection.get(0));
                // call openFromWeb in TrackPanel
                trackPanel.openFromWeb(basePath, selection.get(0));
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();
                break;

            case ("Cancel") :
                // System.out.println("You bailed!");
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();
                break;
        }
    }

    // for ListSelectionListener interface
    public void valueChanged(ListSelectionEvent lse) {
        int i = fileList.getSelectedIndex();
        // System.out.println("You just selected the " + i + "th jam!!");
        Info[] infoArray = infoArrayList.get(i);

        double maxRunningTime = 0.0;
        for (int j = 0; j < infoArray.length; j++)
            maxRunningTime = Math.max(maxRunningTime,infoArray[j].getRunningTime());

        // update info panel each time the selection changes
        infoHeadPanel.removeAll();
        infoAvatarPanel.removeAll();
        infoAvatarPanel.revalidate();

        String infoHead = "<html>Tracks: " + infoArray.length + "<br>"
            + "Running time: " + maxRunningTime + " seconds";
        infoHeadPanel.add(new JLabel(infoHead));

        // GridLayout: rows, columns, hgap, vgap
        infoAvatarPanel.setLayout(new GridLayout(3,4,4,4));
        for (int j = 0; j < infoArray.length; j++) {
        
            JLabel avatarLabel = new JLabel();
            avatarLabel.setIcon(
                new ImageIcon(trackPanel.getAvatarImage(infoArray[j].getAvatar())
                    .getScaledInstance(40,40,Image.SCALE_SMOOTH)));
            infoAvatarPanel.add(avatarLabel);
            avatarLabel.setToolTipText(makeToolTip(infoArray[j]));
        }
        revalidate();
    }

    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
    }

    public String makeToolTip(Info info) {
        String toolTip = "<html><h3>" + info.getTitle() + "<br>";
        toolTip += "by: " + info.getContributor() + "<br>";
        toolTip += "length: " + info.getRunningTime() + "s" + "</h3>";
        toolTip += info.getAllNotes();
        toolTip += "</list><br><br>" + info.getDate();
        toolTip += ", " + info.getLocation();

        return toolTip;
    }

    private void parseJJFile(String jjFile) {
        InputStream in = null;
        try {
            URL u = new URL(jjFile + ".jj");
            in = u.openStream();
            in = new BufferedInputStream(in);

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String[] words;

            // throw away Metronome line
            br.readLine();

            words = br.readLine().split(" ");
            int ntracks = Integer.parseInt(words[1]);
            Info[] infoArray = new Info[ntracks];

            for (int i = 0; i < ntracks; i++) {
                br.readLine();    // BEGIN_INFO
                Info inf = new Info();
                inf.setTitle(br.readLine());
                inf.setContributor(br.readLine());
                inf.setAvatar(br.readLine());
                inf.setDate(br.readLine());
                inf.setLocation(br.readLine());
            
                words = br.readLine().split(" ");
                inf.setRunningTime(Double.parseDouble(words[0]));

                words = br.readLine().split(" ");
                int numNotes = Integer.parseInt(words[0]);
                for (int j = 0; j < numNotes; j++) 
                    inf.addNote(br.readLine());

                br.readLine();   // END_INFO
                br.readLine();   // byte_length

                infoArray[i] = inf;
            }
            infoArrayList.add(infoArray);

        } catch (MalformedURLException ex) {
            System.err.println(jjFile + " is not a parseable URL");
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }   
            }   
        }   
    }

    WebLoadPanel(String bp, TrackPanel tp) {

        trackPanel = tp;
        basePath = bp;

        InputStream in = null;
        ArrayList<String> filenames = new ArrayList<String>(0);
        infoArrayList = new ArrayList<Info[]>(0);

        String theWebSite = basePath + "filelist.txt";

        try {
            URL u = new URL(theWebSite);
            in = u.openStream();

            in = new BufferedInputStream(in);

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String filename;
            while ((filename = br.readLine()) != null) {
                // System.out.println(filename);
                filename = filename.split("\\.")[0];
                filenames.add(new String(filename));

                // get list of contributors and their avatars
                parseJJFile(basePath + filename);
            }
        } catch (MalformedURLException ex) {
            System.err.println(theWebSite + " is not a parseable URL");
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }   
            }   
        }   

        fileList = new JList(filenames.toArray());
        fileList.addListSelectionListener(this);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setLayoutOrientation(JList.VERTICAL);
        fileList.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(fileList);
        listScroller.setPreferredSize(new Dimension(280, 200));
        JPanel outerListPanel = new JPanel(new FlowLayout());
        outerListPanel.add(listScroller);

        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BoxLayout(previewPanel,BoxLayout.PAGE_AXIS));
        infoHeadPanel = new JPanel();
        infoAvatarPanel = new JPanel();
        JPanel outerInfoHeadPanel = new JPanel();
        outerInfoHeadPanel.add(infoHeadPanel);
        JPanel outerInfoAvatarPanel = new JPanel();
        outerInfoAvatarPanel.add(infoAvatarPanel);

        previewPanel.add(outerInfoHeadPanel);
        previewPanel.add(outerInfoAvatarPanel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel promptLabel = new JLabel("Choose a Jam from the list!");
        JPanel promptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        promptPanel.add(promptLabel);
        mainPanel.add(promptPanel,BorderLayout.PAGE_START);
        mainPanel.add(outerListPanel,BorderLayout.WEST);
        mainPanel.add(previewPanel,BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(mainPanel,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.PAGE_END);

        fileList.setSelectedIndex(0);
    }

}

