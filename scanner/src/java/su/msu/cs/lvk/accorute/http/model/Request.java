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

import org.apache.commons.httpclient.Cookie;
import su.msu.cs.lvk.accorute.http.constants.HTTPContentType;
import su.msu.cs.lvk.accorute.http.constants.HTTPHeader;
import su.msu.cs.lvk.accorute.http.constants.HTTPMethod;
import su.msu.cs.lvk.accorute.http.utils.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
        buff.append(url).append(" ");
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

            setHeader(new NamedValue(HTTPHeader.HTTP_COOKIE,
                    cookies.getSpec().formatCookies(cookies.getCookies().toArray(new Cookie[cookies.getCookies().size()]))));
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
                        result.add(new Cookie(getURL().getHost(), nv.getName().trim(), nv.getValue().trim()));
                    }
                }
           }
        }

        return result;
    }
}
