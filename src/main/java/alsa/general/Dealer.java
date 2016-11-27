package alsa.general;


import alsa.general.model.Cred;
import alsa.general.page.Gift;
import alsa.general.model.Riddle;
import alsa.general.page.VeteranRiddlePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Dealer implements Runnable{

    @Autowired
    Riddle riddle;
    MyHttpClient client;
    VeteranRiddlePage vp;

    public Dealer() {
        client = new MyHttpClient(Cred.AJJTJT, true);

        riddle = new Riddle();

    }
    public void updateRiddle(VeteranRiddlePage page) {
        if (page.gameTimeToStart > 0) {
            riddle.win(Long.parseLong(page.guessed.trim().replace(",", "")));
        } else {
            riddle.init();
            if (page.closestNumberAnnounced) {
                riddle.correct(page.closestNumber);
            }
        }
    }

    @Override
    public void run() {
        vp = new VeteranRiddlePage();

        boolean first = true;
        while (true) {
            try {
                vp.updateFromClient(client, false);
                if (vp.daily) {
                    Gift.daily(client);
                } else {
                    updateRiddle(vp);
                    if (vp.gameTimeToStart == 0 && first) {
                        System.out.print(vp.question + "\n");
                        first = false;
                    }
                    if (vp.gameTimeToStart != 0) {
                        first = true;
                    }
                    System.out.print(toStr(vp) + "\n");
                    if (vp.gameTimeToStart > 0) Utils.sleep(3600);
                    else Utils.sleep(1700);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.sleep(2500);
            }
        }
    }
    public String toStr(VeteranRiddlePage page) {
        return (page.gameTimeToStart > 0 ? page.toString() : riddle.toString() + page.toString());
    }

    public String closestNumberAnnouncer() {
        return null;
    }

    public long gameTimeToStart() {
        return 0;
    }
}
