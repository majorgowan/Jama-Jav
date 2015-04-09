package jamajav;

class ChatEntry {

    private String text;
    private String avatar;
    private String contributor;
    private String location;
    private String date;

    public String getText() {
        return text;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getContributor() {
        return contributor;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    ChatEntry(String txt, String av, String contrib, String loc, String dte) {
        text = txt;
        avatar = av;
        contributor = contrib;
        location = loc;
        date = dte;
    }
}
