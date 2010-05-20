package su.msu.cs.lvk.accorute.http.utils;


import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;


public class Decoder {

    /**
     * Decodes a URL encoded string. Also decodes html entities.
     * NB: java.net.URLDecoder can only handle (encoded) ASCII strings.
     *
     * @param s       the <code>String</code> to decode
     * @param charset
     * @return the newly decoded <code>String</code>
     * @throws java.io.UnsupportedEncodingException
     *
     */
    public static String urlDecode(String s, String charset) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (Throwable t) {
                        sb.append(c);
                    }
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        // Undo conversion to external encoding
        String result = sb.toString();
        //System.out.println("dec: " + s + " -> " + result);
        if (charset != null)
            result = new String(result.getBytes("ISO-8859-1"), charset);

        // handle html entities in urls (allows 16 bit input from 8 bit forms)
        return htmlEntityDecode(result);

    }

    /**
     * Decodes html entities.
     *
     * @param s the <code>String</code> to decode
     * @return the newly decoded <code>String</code>
     */
    public static String htmlEntityDecode(String s) {

        return StripEntities.stripHTMLEntities(s, ' ');

    }


    public static void decodeQueryString(String qs, String charset, Map<String, String> m) throws UnsupportedEncodingException {

        if (qs == null)
            return;

        StringTokenizer st = new StringTokenizer(qs, "&");
        while (st.hasMoreTokens()) {
            String param = st.nextToken();
            StringTokenizer pst = new StringTokenizer(param, "=");
            String name = urlDecode(pst.nextToken(), charset);
            String val = "";
            if (pst.hasMoreTokens())
                val = urlDecode(pst.nextToken(), charset);
            //System.out.println(name + " -> " + val);
            m.put(name, val);
        }
    }


    public static void main(String[] args) throws Exception {
        String charset = "utf-8";
        String[] s = new String[]{"abcd", " ", "a b", "\u019f \u7624", "a:/b-c\u1234**:", "\uff01\uf897\uffdd", "?a=b&c=d", "-\u8c9d-"};
        System.out.println("started " + new Date().getTime());
        for (int j = 0; j < 5000; ++j) {
            for (int i = 0; i < s.length; ++i) {
                String e = Encoder.urlEncode(s[i], charset);
                if (j == 0)
                    System.out.println(Encoder.unicodeEscape(s[i] + " -> " + e + " -> " + urlDecode(e, charset)));
            }
        }
        System.out.println("finished " + new Date().getTime());
    }

}