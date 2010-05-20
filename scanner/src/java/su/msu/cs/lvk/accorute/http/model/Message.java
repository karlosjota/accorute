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
 * Message.java
 *
 * Created on May 12, 2003, 11:10 PM
 */

package su.msu.cs.lvk.accorute.http.model;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import su.msu.cs.lvk.accorute.http.constants.HTTPContentType;
import su.msu.cs.lvk.accorute.http.constants.HTTPHeader;
import su.msu.cs.lvk.accorute.http.utils.Decoder;
import su.msu.cs.lvk.accorute.http.utils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Message is a class that is used to represent the bulk of an HTTP message, namely
 * the headers, and (possibly null) body. Messages should not be instantiated
 * directly, but should rather be created by a derived class, namely Request or
 * Response.
 *
 * @author rdawes
 */
public class Message implements Serializable{
    public EntityID getCtxID() {
        return ctxID;
    }

    public void setCtxID(EntityID ctxID) {
        this.ctxID = ctxID;
    }

    private EntityID ctxID = EntityID.NOT_INITIALIZED;


    private List<NamedValue> headers = null;
    private byte[] content = null;

    /**
     * Message is a class that is used to represent the bulk of an HTTP message, namely
     * the headers, and (possibly null) body. Messages should not be instantiated
     * directly, but should rather be created by a derived class, namely Request or
     * Response.
     */
    public Message() {
    }


    /**
     * Returns a String representation of the message, *including* the message body.
     *
     * @return The string representation of the message
     */
    public String toString() {
        return toString("\r\n");
    }

    /**
     * Returns a String representation of the message, *including* the message body.
     * Lines of the header are separated by the supplied "CarriageReturnLineFeed" string.
     *
     * @param crlf The required line separator string
     * @return a String representation of the Message.
     */
    public String toString(String crlf) {
        StringBuffer buff = new StringBuffer();
        NamedValue[] headers = getHeaders();
        if (headers != null) {
            for (int i = 0; i < headers.length; i++) {
                NamedValue nv = headers[i];
                if (nv.getName().equalsIgnoreCase(HTTPHeader.HTTP_TRANSFER_ENCODING) && nv.getValue().indexOf("chunked") > -1) {
                    buff.append("X-").append(nv.getName()).append(": ").append(nv.getValue()).append(crlf);
                } else if (nv.getName().equalsIgnoreCase(HTTPHeader.HTTP_CONTENT_ENCODING) && nv.getValue().indexOf("gzip") > -1) {
                    buff.append("X-").append(nv.getName()).append(": ").append(nv.getValue()).append(crlf);
                } else {
                    buff.append(nv.getName()).append(": ").append(nv.getValue()).append(crlf);
                }
            }
        }
        byte[] content = getContent();
        if (content != null) {
            buff.append(HTTPHeader.HTTP_CONTENT_LENGTH + ": ").append(Integer.toString(content.length)).append(crlf);
        }
        buff.append(crlf);
        if (content != null) {
            buff.append(this.getTotallyDecodedBody());
        }
        return buff.toString();
    }

    /**
     * sets the value of a header. This overwrites any previous values of headers with the same name.
     *
     * @param name  the name of the header (without a colon)
     * @param value the value of the header
     */
    public void setHeader(String name, String value) {
        setHeader(new NamedValue(name, value.trim()));
    }

    public void setHeader(NamedValue header) {
        if (headers == null) {
            headers = new ArrayList<NamedValue>();
        } else {
            for (int i = 0; i < headers.size(); i++) {
                NamedValue nv = headers.get(i);
                if (nv.getName().equalsIgnoreCase(header.getName())) {
                    headers.set(i, header);
                    return;
                }
            }
        }
        headers.add(header);
    }

    /**
     * Adds a header with the specified name and value. This preserves any previous
     * headers with the same name, and adds another header with the same name.
     *
     * @param name  the name of the header (without a colon)
     * @param value the value of the header
     */
    public void addHeader(String name, String value) {
        addHeader(new NamedValue(name, value.trim()));
    }

    public void addHeader(NamedValue header) {
        if (headers == null) {
            headers = new ArrayList<NamedValue>();
        }
        headers.add(header);
    }

    /**
     * Removes a header
     *
     * @param name the name of the header (without a colon)
     * @return the value of the header that was deleted
     */
    public String deleteHeader(String name) {
        if (headers == null) {
            return null;
        }
        for (int i = 0; i < headers.size(); i++) {
            NamedValue nv = headers.get(i);
            if (nv.getName().equalsIgnoreCase(name)) {
                headers.remove(i);
                return nv.getValue();
            }
        }
        return null;
    }

    /**
     * Returns an array of header names
     *
     * @return an array of the header names
     */
    public String[] getHeaderNames() {
        if (headers == null || headers.size() == 0) {
            return new String[0];
        }
        String[] names = new String[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            NamedValue nv = headers.get(i);
            names[i] = nv.getName();
        }
        return names;
    }

    /**
     * Returns the value of the requested header
     *
     * @param name the name of the header (without a colon)
     * @return the value of the header in question (null if the header did not exist)
     */
    public String getHeader(String name) {
        if (headers == null) {
            return null;
        }
        for (int i = 0; i < headers.size(); i++) {
            NamedValue nv = headers.get(i);
            if (nv.getName().equalsIgnoreCase(name)) {
                return nv.getValue();
            }
        }
        return null;
    }

    /**
     * Returns all the values of the requested header, if there are multiple items
     *
     * @param name the name of the header (without a colon)
     * @return the values of the header in question (null if the header did not exist)
     */
    public List<String> getHeaders(String name) {
        List<String> values = new ArrayList<String>();
        if (headers == null) return values;
        for (int i = 0; i < headers.size(); i++) {
            NamedValue nv = headers.get(i);
            if (nv.getName().equalsIgnoreCase(name)) {
                values.add(nv.getValue());
            }
        }
        
        return values;
    }

    /**
     * returns the header names and their values
     *
     * @return an array of NamedValue's representing the names and values
     *         of the headers
     */
    public NamedValue[] getHeaders() {
        if (headers == null || headers.size() == 0) {
            return new NamedValue[0];
        }
        return headers.toArray(new NamedValue[headers.size()]);
    }

    /**
     * sets the headers
     *
     * @param headers   a two dimensional array of Strings, where table[i][0] is the header name and
     *                table[i][1] is the header value
     */
    public void setHeaders(NamedValue[] headers) {
        if (this.headers == null) {
            this.headers = new ArrayList<NamedValue>();
        } else {
            this.headers.clear();
        }
        for (int i = 0; i < headers.length; i++) {
            addHeader(headers[i]);
        }
    }

    /**
     * getContent returns the message body that accompanied the request.
     * if the message was read from an InputStream, it reads the content from
     * the InputStream and returns a copy of it.
     * If the message body was chunked, or gzipped (according to the headers)
     * it returns the unchunked and unzipped content.
     *
     * @return Returns a byte array containing the message body
     */
    public byte[] getContent() {
        if (content != null) {
            return content;
        } else {
            return new byte[0];
        }
    }

    /**
     * sets the message to not have a body. This is typical for a CONNECT request or
     * response, which should not read any body.
     */
    public void setNoBody() {
        content = null;
    }

    /**
     * Sets the content of the message body. If the message headers indicate that the
     * content is gzipped, the content is automatically compressed
     *
     * @param bytes a byte array containing the message body
     */
    public void setContent(byte[] bytes) {
        // discard whatever is pending in the content stream
        content = bytes;
        setHeader(new NamedValue(HTTPHeader.HTTP_CONTENT_LENGTH, Integer.toString(content.length)));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Message)) return false;
        Message mess = (Message) obj;
        NamedValue[] myHeaders = getHeaders();
        NamedValue[] thoseHeaders = mess.getHeaders();
        if (myHeaders.length != thoseHeaders.length) return false;
        for (int i = 0; i < myHeaders.length; i++) {
            if (!myHeaders[i].getName().equalsIgnoreCase(thoseHeaders[i].getName())) return false;
            if (!myHeaders[i].getValue().equals(thoseHeaders[i].getValue())) return false;
        }
        byte[] myContent = getContent();
        byte[] thatContent = mess.getContent();
        return Arrays.equals(myContent, thatContent);
    }

    public String getEncodingFromHeader() {

        String contenttype = this.getHeader(HTTPHeader.HTTP_CONTENT_TYPE);
        if (contenttype == null || contenttype.indexOf("charset=") < 0) return "ISO-8859-1";

        String charset = contenttype.substring(contenttype.indexOf("charset") + "charset=".length());
        return charset.trim();

    }


    public String getEncodingFromHTML() {
        String result = "ISO-8859-1";

        if (HTTPContentType.isHTML(this.getHeader(HTTPHeader.HTTP_CONTENT_TYPE)) &&
                this.getContent() != null) {
            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode node;

            try {
                String content = new String(this.getContent(), getEncodingFromHeader());

                node = cleaner.clean(content);
            } catch (IOException ioex) {
                return result;
            }

            try {
                Object[] links = node.evaluateXPath("//meta[@http-equiv]");

                for (int i = 0; i < links.length; i++) {
                    if (links[i] instanceof TagNode) {
                        TagNode link = (TagNode) links[i];

                        if (HTTPHeader.HTTP_CONTENT_TYPE.equalsIgnoreCase(link.getAttributeByName("http-equiv"))) {
                            String contenttype = link.getAttributeByName("content");

                            if (contenttype == null || contenttype.indexOf("charset=") < 0) return result;

                            String charset = contenttype.substring(contenttype.indexOf("charset") + "charset=".length());
                            result = charset.trim();
                        }
                    }
                }
            } catch (XPatherException xpex) {
            }
        }

        return result;
    }

    public String getMessageEncoding() {
        String result = getEncodingFromHeader();

        if (HTTPContentType.isHTML(this.getHeader(HTTPHeader.HTTP_CONTENT_TYPE))) {
            String htmlEncoding = getEncodingFromHTML();

            if (!"ISO-8859-1".equals(htmlEncoding)) {
                result = htmlEncoding;
            }
        }

        return result;
    }

    //converts to proper encoding
    //replaces entities
    public String getTotallyDecodedBody() {
        String content = null;
        try {
            //get encoding from the header or meta-tag
            content = new String(this.getContent(), getMessageEncoding());
        } catch (UnsupportedEncodingException uecx1) {
            try {
                //try to guess encoding (chardet)
                String charset = Utils.getCharset(this.getContent());

                content = charset == null ? new String(this.getContent()) : new String(this.getContent(), charset);
            } catch (UnsupportedEncodingException uecx2) {
                //fail to default
                try {
                    content = new String(this.getContent(), "ISO-8859-1");
                } catch (UnsupportedEncodingException uecx3) {
                    content = new String(this.getContent());
                }
            }
        }

        return HTTPContentType.isHTML(getHeader(HTTPHeader.HTTP_CONTENT_TYPE)) ? Decoder.htmlEntityDecode(content) : content;
    }

    //converts to proper encoding
    //does not replace entities
    public String getCharsetDecodedBody() {
        String content = null;
        try {
            //get encoding from the header or meta-tag
            content = new String(this.getContent(), getMessageEncoding());
        } catch (UnsupportedEncodingException uecx1) {
            try {
                //try to guess encoding (chardet)
                String charset = Utils.getCharset(this.getContent());

                content = charset == null ? new String(this.getContent()) : new String(this.getContent(), charset);
            } catch (UnsupportedEncodingException uecx2) {
                //fail to default
                try {
                    content = new String(this.getContent(), "ISO-8859-1");
                } catch (UnsupportedEncodingException uecx3) {
                    content = new String(this.getContent());
                }
            }
        }

        return content;
    }
}
