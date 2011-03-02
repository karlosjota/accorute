/***********************************************************************
 *
 * $CVSHeader$
 *
 * This file is part of WebScarab, an Open Web Application Security
 * Project utility. For details, please see http://www.owasp.org/
 *
 * Copyright (c) 2002 - 2004 Rogan Dawes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Getting Source
 * ==============
 *
 * Source for this application is maintained at Sourceforge.net, a
 * repository for free software projects.
 *
 * For details, please see http://www.sourceforge.net/projects/owasp
 *
 */

/*
 * Request.java
 *
 * Created on May 12, 2003, 11:12 PM
 */

package su.msu.cs.lvk.accorute.http.model;

import com.gargoylesoftware.htmlunit.WebRequest;
import org.apache.commons.lang.NotImplementedException;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import su.msu.cs.lvk.accorute.http.constants.HTTPHeader;
import su.msu.cs.lvk.accorute.http.constants.HTTPMethod;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a request that can be sent to an HTTP server.
 *
 * @author rdawes
 */
public class Request extends Message {

    private String method = HTTPMethod.GET;
    private URL url = null;
    private String version = "HTTP/1.1";

    /**
     * Creates a new instance of Request
     */
    public Request() {
    }

    /**
     * Creates a new Request, which is a copy of the supplied Request
     *
     * @param req the request to copy
     */
    public Request(Request req) {
        method = req.getMethod();
        url = req.getURL();
        version = req.getVersion();
        setHeaders(req.getHeaders());
        if (req.getContent() == null) {
            setNoBody();
        } else {
            setContent(req.getContent());
        }
    }

    public Request(WebRequest req) {
        method = req.getHttpMethod().toString();
        url = req.getUrl();
        //TODO: the rest is not copied!!!!
    }
    
    /**
     * Sets the request method
     *
     * @param method the method of the request (automatically converted to uppercase)
     */
    public void setMethod(String method) {
        this.method = method.toUpperCase();
    }

    /**
     * gets the Request method
     *
     * @return the request method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the Request URL
     *
     * @param url the url
     */
    public void setURL(URL url) {
        this.url = url;
    }

    /**
     * Gets the Request URL
     *
     * @return the request url
     */
    public URL getURL() {
        return url;
    }

    /**
     * Sets the HTTP version supported
     *
     * @param version the version of the request. Automatically converted to uppercase.
     */
    public void setVersion(String version) {
        this.version = version.toUpperCase();
    }

    /**
     * gets the HTTP version
     *
     * @return the version of the request
     */
    public String getVersion() {
        return version;
    }

    /**
     * returns a string representation of the Request, using a CRLF of "\r\n"
     *
     * @return a string representation of the Request, using a CRLF of "\r\n"
     */
    public String toString() {
        return toString("\r\n");
    }

    /**
     * returns a string representation of the Request, using the supplied string to
     * separate lines
     *
     * @param crlf the string to use to separate lines (usually CRLF)
     * @return a string representation of the Request
     */
    public String toString(String crlf) {
        if (method == null || url == null || version == null) {
            return "Unitialized Request!";
        }
        StringBuffer buff = new StringBuffer();
        buff.append(method).append(" ");
        buff.append(url.getFile()).append(" ");//TODO: temporary fix
        buff.append(version).append(crlf);
        buff.append(super.toString(crlf));
        return buff.toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Request)) return false;
        Request req = (Request) obj;
        if (!getMethod().equals(req.getMethod())) return false;
        if (!getURL().equals(req.getURL())) return false;
        if (!getVersion().equals(req.getVersion())) return false;
        return super.equals(req);
    }

    public void setCookies(CookieDescriptor cookies) {
        deleteHeader(HTTPHeader.HTTP_COOKIE);

        if (cookies.getCookies().size() > 0) {
            List<Header> headers = cookies.getSpec().formatCookies(cookies.getCookies());
            String val = "";
            for(Header h: headers){
                val = val + h.getValue() + ";";
            }
            val = val.substring(0,val.length() - 1);
            setHeader("Cookie",val);
        }
    }

    public List<Cookie> getCookies() {
        NamedValue[] headers = getHeaders();
        List<Cookie> result = new ArrayList<Cookie>();
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].getName().equalsIgnoreCase(HTTPHeader.HTTP_COOKIE)) {
                NamedValue[] nvs = NamedValue.splitNamedValues(headers[i].getValue(), ";", "=");

                for(NamedValue nv : nvs) {
                    if (!nv.getName().startsWith("$")) {
                        result.add(new BasicClientCookie(nv.getName().trim(), nv.getValue().trim()));
                    }
                }
           }
        }
        return result;
    }
    public WebRequest genWebRequest(){
        WebRequest req = new WebRequest(getURL());
        for(NamedValue header: getHeaders()){
            req.setAdditionalHeader(header.getName(),header.getValue());
        }
        if(getMethod().equalsIgnoreCase("GET")){
            req.setHttpMethod(com.gargoylesoftware.htmlunit.HttpMethod.GET);
        }else if(getMethod().equalsIgnoreCase("POST")){
            req.setHttpMethod(com.gargoylesoftware.htmlunit.HttpMethod.POST);
        }
        //TODO: add more code here!
        return req;
    }
    public HttpUriRequest genHTTPClientMethod(){
        HttpUriRequest res;
        URI uri;
        try{
           uri = new URI(url.toString());
        }catch(URISyntaxException ex){
            throw new RuntimeException(ex);
        }
        if(method.equalsIgnoreCase("GET")){
            res = new HttpGet(uri);
        }else if(method.equalsIgnoreCase("POST")){
            HttpPost postMeth = new HttpPost(uri);
            String [] params = URLDecoder.decode(getCharsetDecodedBody().toString()).split("&");
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            for(String param:params){
                String [] namevalue = param.split("=",2);
                nvps.add(new BasicNameValuePair(namevalue[0], namevalue[1]));
            }
            try{
                postMeth.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            }catch(UnsupportedEncodingException ex){
                throw new RuntimeException(ex);
            }
            res = postMeth;
        }else{
            throw new NotImplementedException("cannot convert " + method + " method request to httpclient HttpMethod");
        }
        boolean hashost = false;
        if(super.headers != null){
            for(NamedValue v: super.headers){
                if(v.getName().equalsIgnoreCase("host"))
                    hashost = true;
                res.addHeader(v.getName(),v.getValue());
            }
        }
        if(!hashost){
            res.addHeader("Host",getURL().getHost());
        }
        return res;
    }
}
