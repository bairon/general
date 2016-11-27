package alsa.general;

/**
 * Created by alsa on 27.11.2016.
 */
public class RiddleRequest {
    public long number;
    public boolean result;

    @Override
    public String toString() {
        return "{" +
                number +
                ", " + result +
                '}';
    }
}
