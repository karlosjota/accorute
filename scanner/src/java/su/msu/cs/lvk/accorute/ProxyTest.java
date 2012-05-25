package su.msu.cs.lvk.accorute;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpRequestFilter;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 03.05.12
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
public class ProxyTest {
    public static void main(String [] args){
        HttpRequestFilter filter = new HttpRequestFilter() {
            public void filter(HttpRequest httpRequest) {
                System.out.println(httpRequest.toString());
            }
        };
        final HttpProxyServer server = new DefaultHttpProxyServer(8088, filter);
        server.start();
    }
}
