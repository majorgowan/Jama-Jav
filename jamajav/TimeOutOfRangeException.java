package jamajav;

class TimeOutOfRangeException extends Exception {

    private String highlow;

    public String toString() {
        return ("Exception: Number out of range.  Too " + highlow + ".");
    }

    public String getHighLow() {
        return highlow;
    }

    TimeOutOfRangeException(String hl) {
        highlow = hl;
    }
}
        
