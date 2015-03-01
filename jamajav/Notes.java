package jamajav;

class Notes {

    private String contributor;
    private String comments;

    public void setContributor(String c) {
        contributor = c;
    }

    public void addComment(String c) {
        comments += "<li>" + c;
    }

    Notes() {
        comments = "<html><list>";
    }
}


