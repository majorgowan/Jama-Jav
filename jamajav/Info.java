package jamajav;

// For resizable arrays
import java.util.ArrayList;

// For date
import java.util.Calendar;
import java.text.*;

class Info {

    private String date;
    private String contributor;
    private String location;
    private String title;
    private int runningTime;
    private ArrayList<String> notes;

    public void setTitle(String c) {
        title = c;
    }

    public void setDate(String d) {
        date = d;
    }

    public void setContributor(String c) {
        contributor = c;
    }

    public void setLocation(String l) {
        location = l;
    }

    public void setRunningTime(int t) {
        runningTime = t;
    }

    public void clearNotes() {
        notes.clear();
    }

    public void addNote(String c) {
        notes.add(new String(c));
    }

    public int getNotesSize() {
        return notes.size();
    }

    public String getNote(int i) {
        return notes.get(i);
    }

    public String getAllNotes() {
        String notesString = "<list>";

        for (int i=0; i<notes.size(); i++)
            notesString += "<li>"+notes.get(i);

        return notesString;
    }

    public String getContributor() {
        return contributor;
    }

    public String getLocation() {
        return location;
    }

    public int getRunningTime() {
        return runningTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    Info() {
        notes = new ArrayList<String>(0);
        title = "Track";
        contributor = "You, that's who.";
        runningTime = 0;
        Calendar d = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        date = dateFormat.format(d.getTime());
    }
}


