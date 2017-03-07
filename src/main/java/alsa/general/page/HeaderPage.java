package alsa.general.page;

import alsa.general.MyHttpClient;
import alsa.general.Utils;
import alsa.general.exception.ResponseParseError;
import org.apache.http.client.methods.HttpGet;

public abstract class HeaderPage {
    public static final String BASE_URL = "http://generals.mobi/";
    public static final String TWO_DEVICES = "Вы не можете совершать активные действия одновременно с 2х устройств";

    public long cur_health;
    public long health;

    public long cur_energy;
    public long energy;

    public long cur_power;
    public long power;

    public String money;
    public int gold;
    public String level;

    public String cur_level_percent;

    public boolean terrorrist;
    public boolean canDrill;
    public int miningChance;
    public boolean miningTimeEnd;
    public boolean daily;
    public boolean notUsedSkillPoints;
    public boolean twodevices;
    public String error;
    public String message;
    public boolean ban;
    public String response;

    public abstract String getPath();

    protected abstract void update(String response);

    private void updateHeader(String response) {
        if (this.ban = response.contains("Вас забанили")) return;
        this.twodevices = response.contains(TWO_DEVICES);
        this.notUsedSkillPoints = response.contains("!</b>");
        this.money = Utils.between(
                Utils.between(response, "<div class=\"block money\">", "</div>"),
                "<b>", "</b>");
        this.gold = Utils.findGold(response);
                //Utils.between(
                //Utils.between(response, "<div class=\"block uran\">", "</div>"),
                //"<a href=\"/bank/main/gold\"><span></span><b>", "</b>");

        this.level = Utils.between(
                Utils.between(response, "<div class=\"block levels\">", "</div>"),
                "class=\"yellow\">", "!</b>").trim();
        this.cur_level_percent = Utils.between(response, "<div class=\"level\" style=\"width:", "%;\"></div>");

        //parseHealth(response);
        //parseEnergy(response);
        //parseAmmo(response);
        this.terrorrist = response.contains("Шахта будет захвачена");
        this.canDrill = response.contains("shaft/drill");
        if (response.contains("Шансы добычи:"))
            this.miningChance = Utils.findNumber(Utils.between(response, "Шансы добычи:", "%,")).intValue();
        this.miningTimeEnd = response.contains("Сегодня Вы использовали все доступное время.");
        this.error = Utils.onlyText(Utils.between(response, "<div class=\"system_message  error\">", "<!-- /system_messages -->"));
        if (Utils.isBlank(this.error.trim()))
            this.error = Utils.onlyText(Utils.between(response, "<div class=\"system_message  error\">", "<!-- main -->"));
        this.message = Utils.onlyText(Utils.between(response, "<div class=\"system_messages\">", "<!-- /header -->"));
        if (Utils.isBlank(this.message.trim()))
            this.message = Utils.onlyText(Utils.between(response, "<div class=\"system_messages\">", "<!-- main -->"));

    }

    protected void parseAmmo(String response) {
        String ammoBar = Utils.between(response, "<b id=\"ammoValue\" class=\"\">", "<i id=\"ammoTimer\"");
        String current = ammoBar.substring(0, ammoBar.indexOf("</b><b>"));
        String maxAmmo = Utils.between(ammoBar, "</b><b>/", "</b>");
        this.cur_power = Long.parseLong(current.isEmpty() ? "0" : current);
        this.power = Long.parseLong(maxAmmo.isEmpty() ? "0" : maxAmmo);


    }

    protected void parseEnergy(String response) {
        String energyBar = Utils.between(response, "<b id=\"energyValue\" class=\"\">", "<i id=\"energyTimer\"");
        String current = energyBar.substring(0, energyBar.indexOf("</b><b>"));
        String maxEnergy = Utils.between(energyBar, "</b><b>/", "</b>");
        this.cur_energy = Long.parseLong(current.isEmpty() ? "0" : current);
        this.energy = Long.parseLong(maxEnergy.isEmpty() ? "0" : maxEnergy);


    }

    private void parseHealth(String response) {
        String healthBar = Utils.between(response, "<b id=\"healthValue\" class=\"\">", "<i id=\"healthTimer\"");
        String current = healthBar.substring(0, healthBar.indexOf("</b>"));
        String maxHealth = Utils.between(healthBar, "</b><b>/", "</b>");
        this.cur_health = Long.parseLong(current.isEmpty() ? "0" : current);
        this.health = Long.parseLong(maxHealth.isEmpty() ? "0" : maxHealth);

    }

    public String updateFromClient(final MyHttpClient client) throws ResponseParseError, InterruptedException {
        return updateFromClient(client, false);
    }

    public void updateFromResponse(final String response) {
        this.response = response;
        if (response != null) {
            updateHeader(response);
            if (!ban)
                update(response);
        }
    }

    public String updateFromClient(MyHttpClient client, boolean fast) throws ResponseParseError, InterruptedException {
        String response = null;
        try {
            HttpGet request = new HttpGet(BASE_URL + getPath());
            response = client.execute(request, null, fast);
            if (response == null) {
                System.out.println("null");
                throw new Exception("Server returns null on get");
            }
            this.daily = response.contains("/game/getDailyReward");
            if (this.daily) {
                    Gift.daily(client);
                return updateFromClient(client, fast);
            }
            if (response.contains("На сервере ведется работа"))
                System.out.println("На сервере ведется работа");
            if (response.contains("Вход в игру")) {
                System.out.println("Вход в игру");
            }
            updateFromResponse(response);
            return response;
        } catch (InterruptedException t) {
            throw t;
        } catch (Throwable t) {
            throw new ResponseParseError("THE RESPONSE WAS: " + response, t);
        }
    }

    public String safeUpdateFromClient(MyHttpClient client, boolean fast) {
        try {
            return updateFromClient(client, fast);
        } catch (ResponseParseError responseParseError) {
            responseParseError.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String safeUpdateFromClient(MyHttpClient client) {
        return safeUpdateFromClient(client, false);
    }
}
