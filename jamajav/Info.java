package jamajav;

// For resizable arrays
import java.util.ArrayList;

class Info {

    private String contributor;
    private String title;
    private ArrayList<String> notes;

    public void setTitle(String c) {
        title = c;
    }

    public void setContributor(String c) {
        contributor = c;
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

    public String getTitle() {
        return title;
    }

    Info() {
        notes = new ArrayList<String>(0);
        title = "Track";
    }

    Info(String tit) {
        notes = new ArrayList<String>(0);
        title = tit;
    }
}


