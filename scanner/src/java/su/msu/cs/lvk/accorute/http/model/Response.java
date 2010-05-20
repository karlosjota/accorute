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
 * Response.java
 *
 * Created on May 12, 2003, 11:18 PM
 */

package su.msu.cs.lvk.accorute.http.model;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.cookie.CookieOrigin;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.commons.httpclient.cookie.RFC2965Spec;
import su.msu.cs.lvk.accorute.http.constants.HTTPHeader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a HTTP response as sent by an HTTP server
 *
 * @author rdawes
 */
public class Response extends Message {

    private String version = null;
    private String status = null;
    private String message = null;
    private Request request = null;
    private Date date = null;
    private long delay = 0;


    /**
     * Creates a new instance of Response
     */
    public Response() {
        setVersion("HTTP/1.1");
        date = new Date();
    }

    /**
     * Creates a new instance of Response, copied from the supplied Response
     *
     * @param resp The original Response to copy
     */
    public Response(Response resp) {
        this.version = resp.getVersion();
        this.status = resp.getStatusCode();
        this.message = resp.getStatusMessage();
        this.date = resp.date;
        setHeaders(resp.getHeaders());
        setContent(resp.getContent());
    }

    /**
     * get response delay in milliseconds between request submission and response receipt 
     * @return
     */
    public long getDelay() {
        return delay;
    }

    /**
     * set response delay in milliseconds between request submission and response receipt
     * @param delay
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    private Date parseResponseDate(String hdrValue) throws ParseException {
        DateFormat headerDF = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

        return headerDF.parse(hdrValue);
    }

    public Date getForgeDate() {
        String hdrValue = getHeader(HTTPHeader.HTTP_DATE);
        if (hdrValue != null) {
            try {
                date = parseResponseDate(hdrValue);
            } catch (ParseException pex) {
            }
        }

        return date;
    }

    /**
     * Sets the HTTP version supported by the server.
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * returns the HTTP version supported by the server
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * sets the status code of the response.
     *
     * @param status
     */
    public void setStatusCode(String status) {
        this.status = status;
    }

    /**
     * Gets the status code of the Response.
     *
     * @return
     */
    public String getStatusCode() {
        return status;
    }

    /**
     * sets the human-readable status message
     *
     * @param message
     */
    public void setStatusMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the human readable status message
     *
     * @return
     */
    public String getStatusMessage() {
        return message;
    }

    /**
     * Returns the status code and human readable status message
     *
     * @return
     */
    public String getStatusLine() {
        return status + " " + message;
    }

    /**
     * returns a string containing the response, using the RFC specified CRLF of
     * "\r\n" to separate lines.
     *
     * @return
     */
    public String toString() {
        return toString("\r\n");
    }

    /**
     * returns a string containing the response, using the provided string to separate lines.
     *
     * @param crlf
     * @return
     */
    public String toString(String crlf) {
        StringBuffer buff = new StringBuffer();
        buff.append(version).append(" ").append(getStatusLine()).append(crlf);
        buff.append(super.toString(crlf));
        return buff.toString();
    }

    public CookieDescriptor getCookieDescriptor() throws MalformedCookieException {
        //extract only directory part of the path
        String path = request.getURL().getPath() == null || "".equals(request.getURL().getPath()) ? "/" : request.getURL().getPath();
        if (!path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/"));
        }

        CookieOrigin origin = new CookieOrigin(request.getURL().getHost(),
                request.getURL().getPort() > 0 ? request.getURL().getPort() : (request.getURL().getProtocol().equalsIgnoreCase("https") ? 443 : 80),
                path,
                request.getURL().getProtocol().equalsIgnoreCase("https"));

        NamedValue[] headers = getHeaders();
        List<Cookie> result = new ArrayList<Cookie>();
        String headerName = HTTPHeader.HTTP_SET_COOKIE;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].getName().equalsIgnoreCase(HTTPHeader.HTTP_SET_COOKIE) || headers[i].getName().equalsIgnoreCase(HTTPHeader.HTTP_SET_COOKIE2)) {
                RFC2965Spec rfcSpec = new RFC2965Spec();
                Header cookieHeader = new Header(headers[i].getName(), headers[i].getValue());

                Cookie[] cookies = rfcSpec.parse(request.getURL().getHost(),
                        request.getURL().getPort() > 0 ? request.getURL().getPort() : (request.getURL().getProtocol().equalsIgnoreCase("https") ? 443 : 80),
                        request.getURL().getPath(),
                        request.getURL().getProtocol().equalsIgnoreCase("https"),
                        cookieHeader);

                result.addAll((Arrays.asList(cookies)));
                headerName = headers[i].getName();
            }
        }

        return new CookieDescriptor(result, origin, headerName);
    }

    /**
     * associates this Response with the provided Request
     *
     * @param request
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * returns the Request that created this Response
     *
     * @return the request
     */
    public Request getRequest() {
        return request;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Response)) return false;
        Response resp = (Response) obj;
        if (!getVersion().equals(resp.getVersion())) return false;
        if (!getStatusLine().equals(resp.getStatusLine())) return false;
        return super.equals(obj);
    }

}
