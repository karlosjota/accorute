package su.msu.cs.lvk.accorute.utils;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.CoreProtocolPNames;


/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 14.12.2010
 * Time: 18:22:52
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientProxyFactory {
    public static AbstractHttpClient create(HttpHost proxy ){
        HttpParams params = new BasicHttpParams();
        // Increase max total connection to 200
        //ConnManagerParams.setMaxTotalConnections(params, 200);
        // Increase default max connection per route to 20
        //ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
        // Increase max connections for localhost:80 to 50
        //HttpHost localhost = new HttpHost("locahost", 80);
        //connPerRoute.setMaxForRoute(new HttpRoute(localhost), 50);
        //ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
        schemeRegistry.register(
                new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        AbstractHttpClient httpClient = new DefaultHttpClient(cm, params);
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        if(proxy != null){
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        httpClient.clearRequestInterceptors();
        httpClient.clearResponseInterceptors();
        return httpClient;
    }
}
