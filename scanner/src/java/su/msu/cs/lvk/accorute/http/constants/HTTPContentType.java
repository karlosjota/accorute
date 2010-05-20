package su.msu.cs.lvk.accorute.http.constants;

public class HTTPContentType {
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_CSS = "text/css";
    public static final String APPLICATION_XHTML = "application/xhtml+xml";
    public static final String APPLICATION_XML = "application/xml";
    public static final String TEXT_XML = "text/xml";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String APPLICATION_X_WWW_FORM_URL = "application/x-www-form-urlencoded";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public static boolean isHTML(String type) {
        return type != null &&
                (type.startsWith(TEXT_HTML) ||
                        type.startsWith(APPLICATION_XHTML) ||
                        type.startsWith(APPLICATION_XML) ||
                        type.startsWith(TEXT_XML));
    }
}
