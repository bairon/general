package alsa.general.page;

import alsa.general.MyHttpClient;
import alsa.general.Utils;
import alsa.general.exception.ResponseParseError;

public class Gift {
    public static String daily(MyHttpClient client) throws InterruptedException, ResponseParseError {
        return Utils.get(client, HeaderPage.BASE_URL + "game/getDailyReward");
    }
}
