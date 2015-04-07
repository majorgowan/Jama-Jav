package jamajav;

class KaraokeLine {

    private double entryTime;
    private String text;

    public double getTime() {
        return entryTime;
    }

    public String getText() {
        return text;
    }

    public void set(double t, String txt) {
        entryTime = t;
        text = txt;
    }

    KaraokeLine(double t, String txt) {
        set(t, txt);
    }
}
