package su.msu.cs.lvk.accorute.http.utils;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;
import su.msu.cs.lvk.accorute.http.constants.HTTPContentType;
import su.msu.cs.lvk.accorute.http.constants.HTTPHeader;
import su.msu.cs.lvk.accorute.http.constants.HTTPMethod;
import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.http.model.NamedValue;
import su.msu.cs.lvk.accorute.http.model.Request;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class Utils {
    public static void collectAttributes(ArrayList<String> values, String attrName, Object[] xpathResult) {
        for (int i = 0; i < xpathResult.length; i++) {
            if (xpathResult[i] instanceof TagNode) {
                TagNode link = (TagNode) xpathResult[i];

                String reference = Decoder.htmlEntityDecode(link.getAttributeByName(attrName));
                if (!values.contains(reference)) {
                    values.add(reference);
                }
            }
        }
    }

    public static URL getBaseDocumentURL(TagNode root, Conversation conversation) throws MalformedURLException {
        return getBaseDocumentURL(root, conversation.getRequest().getURL());
    }

    public static URL getBaseDocumentURL(TagNode root, URL BaseUrl) throws MalformedURLException {
        URL result = BaseUrl;

        try {
            ArrayList<String> values = new ArrayList<String>();
            Object[] links = root.evaluateXPath("//base[@href]");
            collectAttributes(values, "href", links);

            if (values.size() > 0) result = new URL(values.get(0));
        } catch (XPatherException xpex) {
            throw new IllegalStateException("Impossible exception here: " + xpex.getMessage());
        }

        return result;
    }

    public static String readFile(File filename) throws IOException {
        String lineSep = System.getProperty("line.separator");
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String nextLine = "";
        StringBuffer sb = new StringBuffer();
        while ((nextLine = br.readLine()) != null) {
            sb.append(nextLine);
            //
            // note:
            //   BufferedReader strips the EOL character
            //   so we add a new one!
            //
            sb.append(lineSep);
        }
        return sb.toString();
    }

    public static String getCharset(byte[] bytes) {
        nsDetector det = new nsDetector(nsPSMDetector.ALL);
        CharsetListener listener = new CharsetListener();
        det.Init(listener);

        boolean isAscii = det.isAscii(bytes, bytes.length);
        // DoIt if non-ascii and not done yet.
        if (!isAscii)
            det.DoIt(bytes, bytes.length, false);
        det.DataEnd();
        if (isAscii) return "ASCII";

        return listener.getCharset();
    }

    private static class CharsetListener implements nsICharsetDetectionObserver {

        private String charset = null;

        public void Notify(String charset) {
            this.charset = charset;
        }

        public String getCharset() {
            return this.charset;
        }

    }


    public static Request convertPostToGet(Request post) {
        if (!HTTPMethod.POST.equals(post.getMethod()))
            throw new IllegalStateException("Request method must be 'POST', was '" + post.getMethod() + "'");

        NamedValue[] headers = post.getHeaders();
        NamedValue contentType = NamedValue.findOne(HTTPHeader.HTTP_CONTENT_TYPE, headers);
        if (contentType == null || !HTTPContentType.APPLICATION_X_WWW_FORM_URL.equals(contentType.getValue()))
            throw new IllegalStateException("Invalid 'Content-Type' header");

        Request get = new Request();
        get.setMethod(HTTPMethod.GET);
        URL uri = post.getURL();
        try {
            String query = new String(post.getContent(), "ASCII");
            if (post.getURL().getQuery() != null) query += post.getURL().getQuery() + "&" + query;
            uri = new URI(uri.getProtocol(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), query, uri.getRef()).toURL();
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException("Invalid uri syntax : " + use.getMessage(), use);
        } catch (MalformedURLException use) {
            throw new IllegalArgumentException("Invalid uri syntax : " + use.getMessage(), use);
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException("ASCII cannot be an unsupported encoding");
        }

        get.setURL(uri);
        get.setVersion(post.getVersion());

        headers = NamedValue.delete(HTTPHeader.HTTP_CONTENT_TYPE, headers);
        headers = NamedValue.delete(HTTPHeader.HTTP_CONTENT_LENGTH, headers);
        get.setHeaders(headers);

        get.setNoBody();

        return get;
    }

}
