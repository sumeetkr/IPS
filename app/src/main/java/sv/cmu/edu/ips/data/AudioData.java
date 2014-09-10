package sv.cmu.edu.ips.data;

/**
 * Created by sumeet on 9/10/14.
 */
public class AudioData {
    private long time;
    private short [] rawAudio;
    int length;
    int index;

    public AudioData(long time, short[] rawAudio){
        this.time = time;
        this.rawAudio = rawAudio;
        this.length = rawAudio.length;

    }

    public short [] getRawAudio() {
        return rawAudio;
    }
    void setRawAudio(short [] rawAudio) {
        this.rawAudio = rawAudio;
    }

    public long getTime() {
        return time;
    }

    void setTime(long time) {
        this.time = time;
    }

}
