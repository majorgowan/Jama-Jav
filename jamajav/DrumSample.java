package jamajav;

class DrumSample {

    private byte[] data;
    private String title;
    
    public byte[] getBytes() {
        return data;
    }

    public int getSize() {
        return data.length;
    }

    public String getTitle() {
        return title;
    }

    DrumSample(byte[] d, String t) {
        data = d;
        title = t;
    }

}

