package alsa.general;

import alsa.general.model.Cred;
import org.apache.http.Consts;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alsa on 27.11.2016.
 */
public class MyHttpClient {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";
    protected static final ResponseHandler<String> responseHandler = new BasicResponseHandler();
    private boolean logging = true;
    public CloseableHttpClient client;
    private CookieStore cookieStore = new BasicCookieStore();
    public Cred cred;


    public MyHttpClient(Cred cred) {
        this.cred = cred;
        init();
    }

    public MyHttpClient(Cred cred, boolean logging) {
        this.cred = cred;
        this.logging = logging;
        init();
    }

    private void init() {
        client = HttpClientBuilder.create()
                .addInterceptorFirst(new RequestAcceptEncoding())
                .addInterceptorLast(new ResponseContentEncoding())
                .setDefaultCookieStore(cookieStore)
                .setUserAgent(USER_AGENT)
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build())
                .setRedirectStrategy(new HttpPostRedirectStrategy(logging))
                .build();
    }

    public String execute(HttpUriRequest request, List<BasicNameValuePair> formparams) throws IOException, InterruptedException {
        return execute(request, formparams, false);
    }

    public String execute(HttpUriRequest request, List<BasicNameValuePair> formparams, boolean fast) throws IOException, InterruptedException {
        String response = null;
        if (!fast) Thread.sleep(500);
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.addHeader("Accept-Encoding", "gzip, deflate");
        request.addHeader("Accepta-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
        request.addHeader("Referer", "http://generals.mobi/game/index");
        try {
            if (formparams != null) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
                ((HttpPost) request).setEntity(entity);
            }
            if (logging)
                System.out.print((cred == null ? "" : "[" + cred.getUsername() + "]") + " sending " + request.getURI() + " formparams: " + (formparams == null ? "no" : Arrays.deepToString(formparams.toArray())) + "\n");
            CloseableHttpResponse execute = client.execute(request);
            boolean sessionExpired = false;
            try {
                response = responseHandler.handleResponse(execute);
            } catch (ClientProtocolException e) {
                execute.close();
            }
            if (response != null && response.contains("j_spring_security_check")) return response;
            if (response != null) {
                sessionExpired = response.contains("Логин:") || response.contains("/authorization/login/");
            }

            if (sessionExpired) {
                System.out.print("Логин " + cred.getUsername() + "\n");
                login();
                response = execute(request, formparams, fast);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            if (e.getCause() instanceof org.apache.http.client.CircularRedirectException && e.getCause().getMessage().contains("/authorization/logout"))
                cookieStore.clear();
            return execute(request, formparams, fast);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void login() {
        try {
            HttpPost authorization = new HttpPost("http://generals.mobi/authorization/login/");
            execute(authorization, null);

            HttpPost login = new HttpPost("http://passport.vmmo.ru/j_spring_security_check");
            List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
            formparams.add(new BasicNameValuePair("openid.return_to", "http://vmmo.generals.mobi/authorization/login/"));
            formparams.add(new BasicNameValuePair("openid.realm", "http://vmmo.generals.mobi"));
            formparams.add(new BasicNameValuePair("openid.mode", "checkid_setup"));
            formparams.add(new BasicNameValuePair("openid.identity", "http://passport.vmmo.ru/pages/IndexPage"));
            formparams.add(new BasicNameValuePair("_spring_security_remember_me", "true"));
            formparams.add(new BasicNameValuePair("j_username", cred.getUsername()));
            formparams.add(new BasicNameValuePair("j_password", cred.getPassword()));
            execute(login, formparams);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public CloseableHttpResponse execute(HttpGet request) throws IOException {
        if (logging) System.out.print("Sending " + request.getURI() + "\n");
        return client.execute(request);
    }

}
