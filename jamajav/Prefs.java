package jamajav;

// For input/output
import java.io.*;

class Prefs {

    private String filename;
    private Metronome metronome;
    private Clock clock;
    private String userName;
    private String userCity;

    private void setDefaults() {

        // default Metronome
        metronome.setParam(120,4);

        // default Clock
        clock.setParam(0,100);

        // default username
        userName = "auser";

        // default usercity
        userCity = "Toronto";

    }

    public int[] getMetroSet() {
        return metronome.getParam();
    }

    public int[] getClockParam() {
        return clock.getParam();
    }

    public String getUserName() {
        return userName;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setPrefs(int[] ms, int[] cparam, String un, String uc) {
        metronome.setParam(ms[0],ms[1]);
        clock.setParam(cparam[0],cparam[1]);
        userName = un;
        userCity = uc;
    }

    private void readPrefsFile() {
        try (BufferedReader br 
                = new BufferedReader(new FileReader(filename)) )
        {
            // read metronome settings
            String[] words = br.readLine().split(" ");
            int[] ms = new int[2];
            ms[0] = Integer.parseInt(words[1]);
            ms[1] = Integer.parseInt(words[3]);
            br.readLine();

            // read clock settings
            words = br.readLine().split(" ");
            int[] cparam = new int[2];
            cparam[0] = Integer.parseInt(words[2]);
            cparam[1] = Integer.parseInt(words[4]);
            br.readLine();

            // read userName
            br.readLine();  // discard "User Name:"
            String un = br.readLine();
            br.readLine();  // discard blank line

            // read userCity
            br.readLine(); // discard "User City:"
            String uc = br.readLine();
            br.readLine();  // discard blank line

            setPrefs(ms, cparam, un, uc);

        } catch (IOException ie) {
            System.out.println("No valid preferences file found, using defaults.");
            setDefaults();
        }
    }

    public void writePrefsFile() {
        try (FileWriter fw = new FileWriter(filename)) {

            int[] metroset = metronome.getParam();
            fw.write("Metronome: " 
                    + metroset[0] + " bpMin "
                    + metroset[1] + " bpMeas\n\n");

            int[] cparam = clock.getParam();
            fw.write("Clock: " 
                    + "count-in: " + cparam[0] + " "
                    + "precision: " + cparam[1] + "\n\n");

            fw.write("User Name:\n");
            fw.write(userName + "\n\n");

            fw.write("User City:\n");
            fw.write(userCity + "\n\n");
        } catch (IOException ie) {
            System.out.println("Problem writing preferences to file.  Aborting.");
        }
    }

    Prefs(String fn, Metronome m, Clock c) {

        filename = fn;
        metronome = m;
        clock = c;
        readPrefsFile();
    }
}


