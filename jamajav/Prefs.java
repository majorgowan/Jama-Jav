package jamajav;

// For input/output
import java.io.*;

class Prefs {

    private int[] metroset = new int[2];
    private String filename;
    private String userName;
    private String userCity;
    private String avatar;
    private boolean showMetronome;
    private boolean showKaraoke;
    private boolean autoSelectNewTrack;

    private void setDefaults() {

        // default Metronome
        metroset[0] = 120;
        metroset[1] = 4;

        // default username
        userName = "auser";

        // default usercity
        userCity = "Toronto";

        // default avatar
        avatar = "tenor";

        showMetronome = false;
        showKaraoke = false;
        autoSelectNewTrack = false;
    }

    public int[] getMetroSet() {
        return metroset;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserCity() {
        return userCity;
    }

    public String getAvatar() {
        return avatar;
    }

    public boolean getShowMetronome() {
        return showMetronome;
    }

    public boolean getShowKaraoke() {
        return showKaraoke;
    }

    public boolean getAutoSelectNewTrack() {
        return autoSelectNewTrack;
    }

    public void setPrefs(int[] ms, String un, String uc, String av) {
        metroset = ms;
        userName = un;
        userCity = uc;
        avatar = av;
        showMetronome = true;
        showKaraoke = true;
        autoSelectNewTrack = true;
    }

    public void setPrefs(int[] ms, String un, String uc, String av,
            boolean sM, boolean sK, boolean aSNT) {
        metroset = ms;
        userName = un;
        userCity = uc;
        avatar = av;
        showMetronome = sM;
        showKaraoke = sK;
        autoSelectNewTrack = aSNT;
    }

    public void setAvatar(String av) {
        avatar = av;
    }

    private void readPrefsFile() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            // read metronome settings
            String[] words = br.readLine().split(" ");
            int[] ms = new int[2];
            ms[0] = Integer.parseInt(words[1]);
            ms[1] = Integer.parseInt(words[3]);
            br.readLine();

            // read userName
            br.readLine();  // discard "User Name:"
            String un = br.readLine();
            br.readLine();  // discard blank line

            // read userCity
            br.readLine(); // discard "User City:"
            String uc = br.readLine();
            br.readLine();  // discard blank line

            // read avatar
            br.readLine(); // discard "User City:"
            String av = br.readLine();
            br.readLine();  // discard blank line

            // get boolean values
            String line = br.readLine(); // discard information line
            if (line == null)
                setPrefs(ms, un, uc, av);
            else {
                boolean sMet = Boolean.parseBoolean(br.readLine());
                boolean sKar = Boolean.parseBoolean(br.readLine());
                boolean selectNewTrack = Boolean.parseBoolean(br.readLine());
                br.readLine();
                setPrefs(ms, un, uc, av, sMet, sKar, selectNewTrack);
            }

        } catch (IOException ie) {
            //System.out.println(ie);
            //ie.printStackTrace();
            System.out.println("No valid preferences file found, using defaults.");
            setDefaults();
        } finally {
            try {
                if (br != null) br.close();
            } catch(IOException ie) {
                System.out.println("Error closing Prefs file");
            }
        }
    }

    public void writePrefsFile() {
        FileWriter fw = null;
        try {
            fw = new FileWriter(filename);

            fw.write("Metronome: "
                    + metroset[0] + " bpMin "
                    + metroset[1] + " bpMeas\n\n");

            fw.write("User Name:\n");
            fw.write(userName + "\n\n");

            fw.write("User City:\n");
            fw.write(userCity + "\n\n");

            fw.write("Avatar:\n");
            fw.write(avatar + "\n\n");

            fw.write("ShowMetronome, ShowKaraoke, AutoSelectNewTrack:\n");
            fw.write(showMetronome + "\n");
            fw.write(showKaraoke + "\n");
            fw.write(autoSelectNewTrack + "\n\n");
        } catch (IOException ie) {
            System.out.println(ie);
            ie.printStackTrace();
            System.out.println("Problem writing preferences to file.  Aborting.");
        } finally {
            try {
                if (fw != null) fw.close();
            } catch(IOException ie) {
                System.out.println("Error closing Prefs file");
            }
        }
    }

    Prefs(String fn) {
        filename = fn;
        readPrefsFile();
    }

    // copy constructor
    Prefs(Prefs p) {
        metroset = p.getMetroSet();
        userName = p.getUserName();
        userCity = p.getUserCity();
        avatar = p.getAvatar();
    }
}
