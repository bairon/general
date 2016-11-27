package alsa.general;

import alsa.general.exception.ResponseParseError;
import com.sun.org.apache.xml.internal.security.utils.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alsa on 27.11.2016.
 */
public class Utils {
    public static final Pattern p = Pattern.compile("\\d+");
    public static final String SERVER_DATE_FORMAT = "MMM, dd yyyy HH:mm:ss";//Nov, 03 2011 21:27:29
    public static final SimpleDateFormat serverDateFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.ENGLISH);

    public static void sleep(final int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String get(MyHttpClient client, String url) throws ResponseParseError, InterruptedException {
        try {
            return client.execute(new HttpGet(url), null, true);
        } catch (IOException e) {
            throw new ResponseParseError(e);
        }
    }

    public static String post(MyHttpClient client, String url, String... formstrings) throws InterruptedException {
        HttpPost create = new HttpPost(url);
        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
        for (int i = 0; i < formstrings.length; i += 2) {
            formparams.add(new BasicNameValuePair(formstrings[i], formstrings[i + 1]));
        }
        try {
            return client.execute(create, formparams, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isBlank(String s) {
        return s == null || "".equals(s);
    }

    public static String between(final String source, final String starttoken, final String endtoken) {
        int start = source.indexOf(starttoken);
        int end = source.indexOf(endtoken, start + starttoken.length());
        if (start >= 0 && end > start)
            return source.substring(start + starttoken.length(), end);
        else return "";
    }

    public static Long findNumber(String text) {

        Matcher m = p.matcher(text);
        if (m.find()) {
            return Long.parseLong(m.group());
        }
        return null;
    }
    public static String format(long next) {
        return NumberFormat.getNumberInstance(Locale.US).format(next);

    }

    public static String onlyText(String html) {
        StringBuilder sb = new StringBuilder();
        int starttag, endtag = 0;
        do {
            starttag = html.indexOf("<", endtag);
            if (starttag >= 0 && endtag + 1 < starttag)
                sb.append(html.substring(endtag + 1, starttag));
            endtag = starttag >= 0 ? html.indexOf(">", starttag) : -1;
        } while (endtag >= 0);
        return sb.toString();
    }

    public static int findGold(String response) {
        String between = Utils.between(response, "<div class=\"block gold\">", "</div>");
        if (!Utils.isBlank(Utils.between(between, "<a href=\"/bank/gold\"><span></span><b>", "</b>")))
            return Integer.parseInt(Utils.between(between, "<a href=\"/bank/gold\"><span></span><b>", "</b>"));
        else return 0;
    }

}
