package alsa.general.model;

import alsa.general.Utils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Riddle {
    public static long M = 1000000000L;
    public long next = (long) (Math.random() * M);
    public long last = 0;
    public long low = 1;
    public long high = M;
    public Set<Long> corrects = new HashSet<Long>();
    public Set<Long> wrongs = new HashSet<Long>();
    private boolean won;

    public void init() {
        if (won) {
            corrects.clear();
            wrongs.clear();
            last = 0;
            low = 1;
            high = M;
            next = (long) (Math.random() * M);//roundToSameDigits(low, (long) (Math.random() * M), high, 0);
            won = false;
        }
    }

    public synchronized void correct(long number) {
        if (!corrects.contains(number) && !wrongs.contains(number)) {
            if (number > high) {
                high = number + number - high;
                if (high >= M) high = M - 1;
            } else if (number < low) {
                low = number - number + low;
                if (low <= 0) low = 1;
            } else if (last > 0) {
                if (number > last) {
                    low = Math.max(low, (long) Math.ceil(((double) (number + last)) / 2));
                    if (low == last) low++;
                }
                if (number < last) {
                    high = Math.min(high, (long) Math.floor(((double) (number + last)) / 2));
                    if (high == last) high--;
                }
            }
            last = number;
            euristNext();
            corrects.add(number);
        }
    }

    public synchronized void wrong(long number) {
        if (!corrects.contains(number) && !wrongs.contains(number)) {
            if (number > last) {
                high = Math.min(high, (long) Math.floor(((double) (number + last)) / 2));
                if (high == last) high--;
            }
            if (number < last) {
                low = Math.max(low, (long) Math.ceil(((double) (number + last)) / 2));
                if (low == last) low++;
            }
            euristNext();
            wrongs.add(number);
        }
    }

    private void euristNext() {
        if (low == high) next = low;
        if (high < low) {
            high = last + 20;
            low = last - 20;
            if (high > M) high = M;
            if (low < 1) low = 1;
        }

        next = (high + low) / 2;

        if (last == next && next > low) next--;
        if (last == next && next < high) next++;
    }

    private long roundToSameDigits(long low, long next, long high, int digit) {
        int lg = (int) Math.log10(high - low);
        if (next - low < high - next) {
            for (long i = next; i < high; ++i) {
                if (endonsamedigits(i, lg - 1, digit)) return i;
            }
            for (long i = next; i > low; --i)
                if (endonsamedigits(i, lg - 1, digit)) return i;
        } else {
            for (long i = next; i > low; --i)
                if (endonsamedigits(i, lg - 1, digit)) return i;
            for (long i = next; i < high; ++i)
                if (endonsamedigits(i, lg - 1, digit)) return i;
        }
        return next;
    }

    private boolean endonsamedigits(long next, int count, int digit) {
        while (count-- > 0) {
            int nextd = (int) (next % 10);
            next /= 10;
            if (digit == -1) {
                digit = nextd;
            } else if (digit != nextd) return false;
        }
        return true;
    }

    public synchronized long getNext() {
        return next;
    }

    @Override
    public String toString() {
        return "ширина " + Utils.format(high - low);

    }

    public long d() {
        return high - low;
    }

    public synchronized long getLast() {
        return last;
    }

    public synchronized long getLow() {
        return low;
    }

    public synchronized long getHigh() {
        return high;
    }

    public void win(long guessed) {
        low = high = next = last = guessed;
        won = true;
    }

}
