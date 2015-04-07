package jamajav;

// For resizable arrays:
import java.util.ArrayList;

class Karaoke {

    private ArrayList<KaraokeLine> lines;

    public void addLine(double t, String txt) {
        lines.add(new KaraokeLine(t, txt));
    }

    public void removeLine(int i) {
        lines.remove(i);
    }

    public KaraokeLine getLine(int i) {
        return lines.get(i);
    }

    public int getSize() {
        return lines.size();
    }

    public int find(double time) {
        for (int i = 0; i < lines.size(); i++)
            if (lines.get(i).getTime() >= time)
                return i;

        return -1;
    }

    public void clear() {
        lines.clear();
    }

    Karaoke() {
        // nothing to do yet
        lines = new ArrayList<KaraokeLine>(0);
    }

    // copy constructor
    Karaoke(Karaoke ko) {
        this();
        for (int i = 0; i < ko.getSize(); i++) {
            KaraokeLine kol = ko.getLine(i);
            addLine(kol.getTime(), kol.getText());
        }
    }
}

