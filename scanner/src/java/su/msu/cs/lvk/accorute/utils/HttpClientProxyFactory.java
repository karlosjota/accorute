package su.msu.cs.lvk.accorute.utils;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 14.12.2010
 * Time: 18:22:52
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientProxyFactory {
    private static Logger logger = Logger.getLogger(HttpClientProxyFactory.class.getName());
    public static AbstractHttpClient create(HttpHost proxy ){
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        try{
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext tlsCtx = SSLContext.getInstance("TLS");
            tlsCtx.init(null,new TrustManager[]{tm},null);
            SSLSocketFactory sf = new SSLSocketFactory(
                    tlsCtx,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
            );
            schemeRegistry.register(
                    new Scheme("https", 443,sf));
        }catch(GeneralSecurityException ex){
            logger.warn("Could not init TLS! HTTPS will be unavailable");
        }
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
        cm.setMaxTotal(1);
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 10000);
        AbstractHttpClient httpClient = new DefaultHttpClient(cm, params);
        if(proxy != null){
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        httpClient.clearRequestInterceptors();
        httpClient.clearResponseInterceptors();
        return httpClient;
    }
}
