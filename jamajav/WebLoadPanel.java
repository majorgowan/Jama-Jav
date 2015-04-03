package jamajav;

// Swing packages
import javax.swing.*;
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

class WebLoadPanel extends JPanel implements ActionListener {

    final private int DEFAULT_WIDTH = 300;
    final private int DEFAULT_HEIGHT = 250;

    TrackPanel trackPanel;
    JList fileList;
    String basePath;
    ArrayList<Info[]> infoArrayList;

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
                System.out.println("You bailed!");
                SwingUtilities.windowForComponent(this).setVisible(false);
                SwingUtilities.windowForComponent(this).dispose();
                break;
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
    }

    private void parseJJFile(String jjFile) {
        InputStream in = null;
        try {
            URL u = new URL(jjFile);
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
                System.out.println(filename);
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
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setLayoutOrientation(JList.VERTICAL);
        fileList.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(fileList);
        listScroller.setPreferredSize(new Dimension(230, 200));
        JPanel outerListPanel = new JPanel(new FlowLayout());
        outerListPanel.add(listScroller);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel promptLabel = new JLabel("Choose a Jam from the list!");
        JPanel promptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        promptPanel.add(promptLabel);
        mainPanel.add(promptPanel,BorderLayout.PAGE_START);
        mainPanel.add(outerListPanel);

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
    }

}

