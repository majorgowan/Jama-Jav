package jamajav;

// For resizable arrays
import java.util.ArrayList;

// For date
import java.util.Calendar;
import java.text.*;

class Info {

    private Calendar date;
    private String contributor;
    private String location;
    private String title;
    private ArrayList<String> notes;

    public void setTitle(String c) {
        title = c;
    }

    public void setContributor(String c) {
        contributor = c;
    }

    public void setLocation(String l) {
        location = l;
    }

    public void addNote(String c) {
        notes.add(new String(c));
    }

    public String getNotes() {
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

    public String getTitle() {
        return title;
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date.getTime());
    }

    Info() {
        notes = new ArrayList<String>(0);
        title = "Track";
        contributor = "You, that's who.";
        date = Calendar.getInstance();
    }

    Info(String tit) {
        notes = new ArrayList<String>(0);
        title = tit;
        date = Calendar.getInstance();
    }
}


