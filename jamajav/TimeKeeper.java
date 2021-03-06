package jamajav;

class TimeKeeper {

    private double time = 0.0;

    private TimeKeeper mother = null;

    private PlainClock clock = null;
    private PlainTimeLine timeLine = null;

    private KaraokePanel karaokePanel = null;

    public double getTime() {
        return time;
    }

    public void update(double t) {
        time = t;
        if (mother != null)
            mother.update(time);

        if (clock != null)
            clock.update(time);

        if (timeLine != null)
            timeLine.update(time);

        if (karaokePanel != null)
            karaokePanel.update(time);
    }

    public void reset(double t) {
        time = t;
        if (mother != null)
            mother.reset(t);

        if (clock != null)
            clock.reset(time);

        if (timeLine != null)
            timeLine.reset(time);

        if (karaokePanel != null)
            karaokePanel.reset(time);
    }

    public void addTime(double t) {
        update(time + t);
    }

    public void setClock(PlainClock c) {
        clock = c;
    }

    public void setTimeLine(PlainTimeLine tl) {
        timeLine = tl;
    }

    public void setKaraokePanel(KaraokePanel kopnl) {
        karaokePanel = kopnl;
        karaokePanel.reset(time);
    }

    TimeKeeper() {
        time = 0.0;
    }

    TimeKeeper(double t) {
        time = t;
    }

    TimeKeeper(double t, TimeKeeper moth) {
        this(t);
        mother = moth;
    }
}



    
