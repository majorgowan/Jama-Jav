// ref: http://www.developer.com/java/other/article.php/1572251/Java-Sound-Getting-Started-Part-1-Playback.htm

package jamajav;

// For reading and writing files and streams
import java.io.*;

// For sound samples
import javax.sound.sampled.*;

// For observing:
import java.util.Observable;
import java.util.Observer;

class TrackData {

    private Stopper stopPlay = new Stopper();
    private Stopper stopCapture = new Stopper();
    private boolean isCapturing = false;
    private boolean notEmpty = false;

    private byte[] audioData;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;

    private Info info;

    private Monitor monitor;

    // eventually don't put this stuff here.  Have the
    // components "listen" for change in "stopPlay" 
    // and "isCapturing" and then
    // request the byteArray bzw. runningTime from
    // the TrackData object
    private Visualizer visualizer;
    private TimeLine timeLine;

    public void setNotEmpty(boolean ne) {
        notEmpty = ne;
    }

    public void stopPlaying() {
        stopPlay.stop();
    }

    public Stopper getStopPlay() {
        return stopPlay;
    }

    public void stopCapturing() {
        stopCapture.stop();
    }

    public Stopper getStopCapture() {
        return stopCapture;
    }

    public void addStopperObserver(Observer obs) {
        stopCapture.addObserver(obs);
        stopPlay.addObserver(obs);
    }

    public boolean isCapturing() {
        return isCapturing;
    }

    public boolean isNotEmpty() {
        return notEmpty;
    }

    public byte[] getBytes() {
        return audioData;
    }

    public void putBytes(byte[] bytes) {
        audioData = bytes;
        notEmpty = true;
    }

    public int getRunningTime() {
        return (int)((double)(audioData.length) / 
                (double)(audioFormat.getFrameSize() * audioFormat.getFrameRate())); 
    }

    public Info getInfo() {
        return info;
    }

    public void putInfo(Info in) {
        info = in;
    }

    public void setMonitor(Monitor mon) {
        monitor = mon;
    }

    // Create and return an AudioFormat object for a given set
    // of format parameters.  If these parameters don't work well for
    // you, try some of the other allowable parameter values, which
    // are shown in comments following the declarations.
    public AudioFormat getAudioFormat(){
        float sampleRate = 8000.0f; //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;  // 8,16
        int channels = 1;           // 1,2
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }

    public void record(Visualizer vis, TimeLine tl) {
        visualizer = vis;
        timeLine = tl;
        try{
            // System.out.println("Recording . . . ");
            // Get everything set up for recording
            DataLine.Info dataLineInfo = new DataLine.Info(
                    TargetDataLine.class, audioFormat);

            targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // Create a thread to capture the
            // microphone data and start it
            // running.  It will run until
            // the Stop button is clicked.
            Thread recordThread = new Thread(new RecordThread());
            recordThread.start();
            isCapturing = true;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            // System.exit(0);   // Don't have to exit, just do nothing!
        }
    }

    class RecordThread extends Thread{

        // An arbitrary-size temporary holding buffer
        // byte tempBuffer[] = new byte[10000];

        byte tempBuffer[] = new byte[1000];
        public void run(){

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            stopCapture.start();

            try{
                // Loop until stopCapture is set by another thread that
                while(!stopCapture.getValue()) {
                    // Read data from the internal buffer of the data line.
                    int cnt = targetDataLine.read(
                            tempBuffer,
                            0,
                            tempBuffer.length);
                    if (monitor != null)
                        monitor.setData(tempBuffer);

                    if(cnt > 0){
                        // Save data in output stream object.
                        byteArrayOutputStream.write(tempBuffer, 0, cnt);
                    }
                }
                byteArrayOutputStream.close();

                audioData = byteArrayOutputStream.toByteArray();

                info.setRunningTime(getRunningTime());
                info.resetDate();

                timeLine.setRunningTime(getRunningTime());
                timeLine.repaint();
                visualizer.setData(audioData);

                //System.out.println("Closing targetDataLine . . . ");
                targetDataLine.close();
                stopCapturing();
                isCapturing = false;

            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                // System.exit(0);
            }
        }
    }

    public void playback(int volume, TimeLine tl) {
        timeLine = tl;
        timeLine.start();
        // System.out.println("Playing back . . . ");
        stopPlay.start();
        try {
            InputStream byteArrayInputStream 
                = new ByteArrayInputStream(audioData);

            audioInputStream = 
                new AudioInputStream(
                        byteArrayInputStream,
                        audioFormat,
                        audioData.length/audioFormat.
                        getFrameSize());

            DataLine.Info dataLineInfo = 
                new DataLine.Info(SourceDataLine.class, audioFormat);

            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            FloatControl volume_control = 
                (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);

            volume_control.setValue((float)(-2.5*(10.0-volume)));

            // Create a thread to play back the data and start it running.  
            // It will run until all the data has been played back.
            Thread playThread = new Thread(new PlayThread());
            playThread.start();

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            // System.exit(0);
        }
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[1000];

        public void run(){

            try{
                int cnt;
                // Keep looping until the input
                // read method returns -1 for
                // empty stream.
                while (((cnt = audioInputStream.read(
                                    tempBuffer, 0,
                                    tempBuffer.length)) != -1) && (!stopPlay.getValue()))
                {
                    if (cnt > 0) {
                        // Write data to the internal
                        // buffer of the data line
                        // where it will be delivered
                        // to the speaker.
                        sourceDataLine.write(tempBuffer, 0, cnt);
                        if (monitor != null)
                            monitor.setData(tempBuffer);
                    }
                }

                // Block and wait for internal
                // buffer of the data line to
                // empty.
                sourceDataLine.drain();
                sourceDataLine.close();
                timeLine.stop();
                stopPlay.stop();

            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                // System.exit(0);
            }
        }

    } //end inner class PlayThread

    public void writeToFile(String filename) {

        InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);

        audioInputStream = 
            new AudioInputStream(
                    byteArrayInputStream,
                    audioFormat,
                    audioData.length/audioFormat.getFrameSize());

        try {

            AudioSystem.write(audioInputStream, 
                    AudioFileFormat.Type.WAVE, 
                    new File(filename + ".wav"));

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            // System.exit(0);
        }
    }

    // Basic TrackData constructor
    TrackData() {
        audioFormat = getAudioFormat();
    }

    // Copy constructor
    TrackData(TrackData td) {
        this();
        this.audioData = new byte[td.getBytes().length];
        for (int i = 0; i < audioData.length; i++)
            this.audioData[i] = td.getBytes()[i];

        // copy constructor of Info adds "copy of" to title
        this.info = new Info(td.getInfo());

        if (td.isNotEmpty())
            this.setNotEmpty(true);
    }
}
