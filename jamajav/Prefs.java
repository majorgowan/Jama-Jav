package jamajav;

// For input/output
import java.io.*;

class Prefs {

    private String filename;
    private int[] metroset = new int[2];
    private String userName;
    private String userCity;

    private void setDefaults() {

        // default Metronome
        metroset[0] = 120;
        metroset[1] = 4;

        // default username
        userName = "auser";

        // default usercity
        userCity = "Toronto";

    }

    private int[] getMetroSet() {
        return metroset;
    }

    private String getUserName() {
        return userName;
    }

    private String getUserCity() {
        return userCity;
    }

    public void setPrefs(int[] ms, String un, String uc) {
        metroset = ms;
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

            // read userName
            br.readLine();
            String un = br.readLine();

            // read userCity
            br.readLine();
            String uc = br.readLine();
            br.readLine();

            setPrefs(ms, un, uc);

        } catch (IOException ie) {
            System.out.println("No valid preferences file found, using defaults.");
            setDefaults();
        }
    }

    private void writePrefsFile() {
        try (FileWriter fw = new FileWriter(filename)) {

            fw.write("Metronome: " 
                    + metroset[0] + " bpMin "
                    + metroset[1] + " bpMeas\n\n");

            fw.write("User Name:\n");
            fw.write(userName + "\n\n");

            fw.write("User City:\n");
            fw.write(userCity + "\n\n");
        } catch (IOException ie) {
            System.out.println("Problem writing preferences to file.  Aborting.");
        }
    }

    Prefs(String fn) {
        filename = fn;
        readPrefsFile();
    }
}


