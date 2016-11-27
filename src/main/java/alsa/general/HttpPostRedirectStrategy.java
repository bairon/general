package alsa.general;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

public class HttpPostRedirectStrategy extends org.apache.http.impl.client.DefaultRedirectStrategy {
    public static final HttpPostRedirectStrategy INSTANCE = new HttpPostRedirectStrategy();
    private boolean logging;

    public HttpPostRedirectStrategy() {
    }

    public HttpPostRedirectStrategy(boolean logging) {
        this.logging = logging;
    }

    @Override
    protected boolean isRedirectable(String method) {
        return super.isRedirectable(method) || method.equalsIgnoreCase(org.apache.http.client.methods.HttpPost.METHOD_NAME);
    }

    @Override
    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        HttpUriRequest redirect = super.getRedirect(request, response, context);
        if (logging) System.out.print("Redirect to " + redirect.getRequestLine() + "\n");
        return redirect;
    }
}
