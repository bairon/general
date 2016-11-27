package alsa.general;

/**
 * Created by alsa on 27.11.2016.
 */
public class RiddleRequest {
    public long number;
    public boolean result;

    public RiddleRequest() {
    }

    public RiddleRequest(long number, boolean result) {
        this.number = number;
        this.result = result;
    }

    @Override
    public String toString() {
        return "{" +
                number +
                ", " + result +
                '}';
    }
}
