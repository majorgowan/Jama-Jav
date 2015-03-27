package jamajav;

// For input/output
import java.io.*;

class Prefs {

    private int[] metroset = new int[2];
    private String filename;
    private String userName;
    private String userCity;
    private String avatar;

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

    public void setPrefs(int[] ms, String un, String uc, String av) {
        metroset = ms;
        userName = un;
        userCity = uc;
        avatar = av;
    }

    public void setAvatar(String av) {
        avatar = av;
    }

    private void readPrefsFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
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

            setPrefs(ms, un, uc, av);

        } catch (IOException ie) {
            System.out.println(ie);
            ie.printStackTrace();
            System.out.println("No valid preferences file found, using defaults.");
            setDefaults();
        }
    }

    public void writePrefsFile() {
        try {
            FileWriter fw = new FileWriter(filename);

            fw.write("Metronome: " 
                    + metroset[0] + " bpMin "
                    + metroset[1] + " bpMeas\n\n");

            fw.write("User Name:\n");
            fw.write(userName + "\n\n");

            fw.write("User City:\n");
            fw.write(userCity + "\n\n");

            fw.write("Avatar:\n");
            fw.write(avatar + "\n\n");
        } catch (IOException ie) {
            System.out.println(ie);
            ie.printStackTrace();
            System.out.println("Problem writing preferences to file.  Aborting.");
        }
    }

    Prefs(String fn) {
        filename = fn;
        readPrefsFile();
    }
}


