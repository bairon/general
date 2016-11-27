package alsa.general.page;

import alsa.general.Utils;

public class VeteranRiddlePage extends HeaderPage {
    public long gameTimeToStart;
    public boolean wrongNumber;
    public boolean closestNumberAnnounced;
    public long closestNumber;
    public boolean gameCanRiddle;
    public String gameWinner;
    public String guessed;
    public String question;
    public long timeToTry;
    public boolean wrongCaptcha;
    public String closestNumberAnnouncer;
    public boolean fault;
    public String captchaPath;

    @Override
    public String getPath() {
        return "officersclub/veteransRiddle";
    }

    @Override
    protected void update(String response) {
        this.fault = response.contains("Не удалось завершить операцию.");
        if (response.contains("Время до начала новой игры: ")) {
            this.gameTimeToStart = Utils.findNumber(Utils.between(response, "Время до начала новой игры: ", "</p>"));
            this.gameWinner = Utils.between(response, "Победитель предыдущей игры:", "</p>");
            this.guessed = Utils.between(response, "Загаданное число:", "</p>");
            this.closestNumberAnnouncer = gameWinner;
            this.closestNumberAnnounced = true;
        }
        else {
            this.gameTimeToStart = 0;
            this.question = Utils.between(response, "<p class=\"center yellow padd_bot_5 padd_top_5\">", "</p>");

        }
        this.wrongNumber = response.contains("message padd_top_10 padd_bot_10 red");
        this.closestNumberAnnounced = response.contains("Самое близкое число назвал");
        parseTimeToTry(response);
        this.wrongCaptcha = response.contains("Проверочный текст введен неверно.");
        if (closestNumberAnnounced) {
            this.closestNumberAnnouncer = Utils.between(response, "Самое близкое число назвал ", ", это:");
            String formattedNumber = Utils.between(response, "это: <b class=\"yellow\">", "</b>");
            String unformattedNumber = formattedNumber.replace(",", "");
            try {
                this.closestNumber = Long.parseLong(unformattedNumber);
            } catch (NumberFormatException e) {
                closestNumber = 500000000;
            }
        }
        this.gameCanRiddle = response.contains("captcha/simple-php-captcha.php");
        this.captchaPath = Utils.between(response, "<img src=\"/captcha/simple-php-captcha.php", "\" alt");

    }

    private void parseTimeToTry(String response) {
        String timeTT = Utils.between(response, "Время до следующей попытки: \n" +
                "            <b class=\"yellow\">", "</b> секунд");
        if (!timeTT.isEmpty()) {
            this.timeToTry = Long.parseLong(timeTT);
        } else {
            this.timeToTry = 1;
        }
        if (timeToTry == 0) timeToTry++;

    }

    @Override
    public String toString() {
        return gameTimeToStart == 0 ?
                " ближайшее " + Utils.format(closestNumber)  + (closestNumberAnnouncer == null ? " " : " " + closestNumberAnnouncer):
                "победитель: " + gameWinner + " число " + guessed + " время " + gameTimeToStart;
    }
}
