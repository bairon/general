package alsa.general;

/**
 * Created by alsa on 27.11.2016.
 */
public class RiddleResponse {
    public long next;
    public long low;
    public long high;
    public long last;
    public String announce;
    public long time;

    public RiddleResponse(long next, long low, long high, long last, String announce, long time) {
        this.next = next;
        this.low = low;
        this.high = high;
        this.last = last;
        this.announce = announce;
        this.time = time;
    }
    @Override
    public String toString() {
        return time == 0 ? "{" +
                //"нижняя = " + low +
                //", верхняя = " + high +
                ", D = " + Utils.format(high - low) +
                ", ближайшее = " + Utils.format(last) +
                ", назвал = " + announce  +
                ", следующее = " + Utils.format(next) + "}" :
                "победитель " + announce +
                        " число " + Utils.format(last) +
                        " время до следующей игры " + time;
    }
}
