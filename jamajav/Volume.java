package jamajav;

class Volume {

    private int value = 7;

    public void setValue(int val) {
        // value must be integer between 0 and 10
        value = Math.max(0,Math.min(val,10));
    }

    public int getValue() {
        return value;
    }
}
