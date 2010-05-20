package su.msu.cs.lvk.accorute.http.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

/**
 * Utility class for HTML form encoding. This class contains static methods
 * for converting a String to the <CODE>application/x-www-form-urlencoded</CODE> MIME
 * format. For more information about HTML form encoding, consult the HTML
 * <A HREF="http://www.w3.org/TR/html4/">specification</A>.
 * <p/>
 * <p/>
 * When encoding a String, the following rules apply:
 * <p/>
 * <p/>
 * <ul>
 * <li>The alphanumeric characters &quot;<code>a</code>&quot; through
 * &quot;<code>z</code>&quot;, &quot;<code>A</code>&quot; through
 * &quot;<code>Z</code>&quot; and &quot;<code>0</code>&quot;
 * through &quot;<code>9</code>&quot; remain the same.
 * <li>The special characters &quot;<code>.</code>&quot;,
 * &quot;<code>-</code>&quot;, &quot;<code>*</code>&quot;, and
 * &quot;<code>_</code>&quot; remain the same.
 * <li>The space character &quot;<code>&nbsp;</code>&quot; is
 * converted into a plus sign &quot;<code>+</code>&quot;.
 * <li>All other characters are unsafe and are first converted into
 * one or more bytes using some encoding scheme. Then each byte is
 * represented by the 3-character string
 * &quot;<code>%<i>xy</i></code>&quot;, where <i>xy</i> is the
 * two-digit hexadecimal representation of the byte.
 * The recommended encoding scheme to use is UTF-8. However,
 * for compatibility reasons, if an encoding is not specified,
 * then the default encoding of the platform is used.
 * </ul>
 * <p/>
 * <p/>
 * For example using UTF-8 as the encoding scheme the string &quot;The
 * string &#252;@foo-bar&quot; would get converted to
 * &quot;The+string+%C3%BC%40foo-bar&quot; because in UTF-8 the character
 * &#252; is encoded as two bytes C3 (hex) and BC (hex), and the
 * character @ is encoded as one byte 40 (hex).
 *
 * @author Herb Jellinek
 * @version 1.25, 12/03/01
 * @since JDK1.0
 */
public class Encoder {
    static BitSet dontNeedEncoding;
    static final int caseDiff = ('a' - 'A');
    static String dfltEncName = null;

    static {

        /* The list of characters that are not encoded has been
       * determined as follows:
       *
       * RFC 2396 states:
       * -----
       * Data characters that are allowed in a URI but do not have a
       * reserved purpose are called unreserved.  These include upper
       * and lower case letters, decimal digits, and a limited set of
       * punctuation marks and symbols.
       *
       * unreserved  = alphanum | mark
       *
       * mark        = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
       *
       * Unreserved characters can be escaped without changing the
       * semantics of the URI, but this should not be done unless the
       * URI is being used in a context that does not allow the
       * unescaped character to appear.
       * -----
       *
       * It appears that both Netscape and Internet Explorer escape
       * all special characters from this list with the exception
       * of "-", "_", ".", "*". While it is not clear why they are
       * escaping the other characters, perhaps it is safest to
       * assume that there might be contexts in which the others
       * are unsafe if not escaped. Therefore, we will use the same
       * list. It is also noteworthy that this is consistent with
       * O'Reilly's "HTML: The Definitive Guide" (page 164).
       *
       * As a last note, Intenet Explorer does not encode the "@"
       * character which is clearly not unreserved according to the
       * RFC. We are being consistent with the RFC in this matter,
       * as is Netscape.
       *
       */

        dontNeedEncoding = new BitSet(256);
        int i;
        for (i = 'a'; i <= 'z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            dontNeedEncoding.set(i);
        }
        dontNeedEncoding.set(' '); /* encoding a space to a + is done
	 * in the encode() method */
        dontNeedEncoding.set('-');
        dontNeedEncoding.set('_');
        dontNeedEncoding.set('.');
        dontNeedEncoding.set('*');

        /*dfltEncName = (String)AccessController.doPrivileged (
          new GetPropertyAction("file.encoding")
      );*/
        dfltEncName = "ISO-8859-1";
    }

    /**
     * You can't call the constructor.
     */
    private Encoder() {
    }

    /**
     * Translates a string into <code>x-www-form-urlencoded</code>
     * format. This method uses the platform's default encoding
     * as the encoding scheme to obtain the bytes for unsafe characters.
     *
     * @param s <code>String</code> to be translated.
     * @return the translated <code>String</code>.
     * @deprecated The resulting string may vary depending on the platform's
     *             default encoding. Instead, use the encode(String,String)
     *             method to specify the encoding.
     */
    public static String urlEncode(String s) {

        String str = null;

        try {
            str = urlEncode(s, dfltEncName);
        } catch (UnsupportedEncodingException e) {
            // The system should always have the platform default
        }

        return str;
    }


    /**
     * Rewrite of insanely inefficient j2se1.4 URL Encoder...
     * NB: j2se 1.3 URLEncoder can't handle certain chars, eg, \u8c9d
     * <p/>
     * Translates a string into <code>application/x-www-form-urlencoded</code>
     * format using a specific encoding scheme. This method uses the
     * supplied encoding scheme to obtain the bytes for unsafe
     * characters.
     * <p/>
     * <em><strong>Note:</strong> The <a href=
     * "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
     * World Wide Web Consortium Recommendation</a> states that
     * UTF-8 should be used. Not doing so may introduce
     * incompatibilites.</em>
     * <p/>
     *
     * @param s   <code>String</code> to be translated.
     * @param enc The name of a supported
     *            <a href="../lang/package-summary.html#charenc">character
     *            encoding</a>.
     * @return the translated <code>String</code>.
     * @throws UnsupportedEncodingException If the named encoding is not supported
     * @see URLDecoder#decode(java.lang.String, java.lang.String)
     */
    public static String urlEncode(String s, String enc)
            throws UnsupportedEncodingException {

        int encBytesPerChar = 10; // should virtually eliminate any buffer regrowth
        StringBuffer out = new StringBuffer(encBytesPerChar * s.length());

        // Conversion to external encoding
        //System.out.println("enc: " + s + " -> " + result);
        if (enc != null)
            s = new String(s.getBytes(enc), "ISO-8859-1");

        for (int i = 0; i < s.length(); i++) {
            int c = (int) s.charAt(i);
            if (dontNeedEncoding.get(c)) {
                if (c == ' ') {
                    c = '+';
                }
                //System.out.println("Storing: " + c);
                out.append((char) c);
                continue;
            }
            out.append('%');
            char ch = Character.forDigit((c >> 4) & 0xF, 16);
            // converting to use uppercase letter as part of
            // the hex value if ch is a letter.
            if (Character.isLetter(ch)) {
                ch -= caseDiff;
            }
            out.append(ch);
            ch = Character.forDigit(c & 0xF, 16);
            if (Character.isLetter(ch)) {
                ch -= caseDiff;
            }
            out.append(ch);
        }

        return (out.toString());
    }


    /**
     * To escape and quote a String. Escapes \n, \r, \ and " chars.
     *
     * @param s the string to escape
     * @return a string that can be safely placed in double quotes
     *         to build a Java String, Nova query, etc
     */
    static public String quotedEscape(String s) {
        return quotedEscape(s, false);
    }

    /**
     * To escape and quote a String. Escapes \n, \r, \ and " chars.
     *
     * @param s           the string to escape
     * @param quoteResult whether to wrap the result string in double quotes
     * @return a string that can be safely placed in double quotes
     *         to build a Java String, Nova query, etc
     */
    static public String quotedEscape(String s, boolean quoteResult) {
        StringBuffer sb = new StringBuffer(s.length() + 20);
        if (quoteResult)
            sb.append("\"");
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\n') {
                sb.append("\\n");
                continue;
            }
            if (c == '\r') {
                sb.append("\\r");
                continue;
            }
            if (c == '\\' || c == '"')
                sb.append('\\');
            sb.append(c);
        }
        if (quoteResult)
            sb.append("\"");
        return sb.toString();
    }

    /**
     * Encodes <>"'& characters
     *
     * @param s  string to be html encoded
     * @param sb string buffer to hold results
     */
    public static StringBuffer htmlEncode(String s, StringBuffer sb) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    //sb.append("&apos;");	// only understood by Mozilla
                    sb.append("&#39;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb;
    }

    /**
     * Encodes <>"'& characters
     *
     * @param s string to be html encoded
     */
    public static String htmlEncode(String s) {
        StringBuffer buf = new StringBuffer();
        return htmlEncode(s, buf).toString();
    }

    /**
     * Converts a character to a unicode escape sequence.
     */
    public static String unicodeEscape(char c) {
        StringBuffer b = new StringBuffer();
        if (c >= 30 && c <= 126)
            return "" + c;
        b.append("\\u");
        String u = Integer.toHexString(c);
        switch (u.length()) {
            case 1:
                u = "000" + u;
                break;
            case 2:
                u = "00" + u;
                break;
            case 3:
                u = "0" + u;
                break;
        }
        b.append(u);
        return b.toString();
    }

    /**
     * Converts a String to a unicode escaped String.
     */
    public static String unicodeEscape(String s) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            b.append(unicodeEscape(s.charAt(i)));
        }
        return b.toString();
    }

    /**
     * Returns the MD5 hash of a String.
     *
     * @param str Description of the Parameter
     * @return Description of the Return Value
     */
    public static String hashMD5(String str) {
        return hashMD5(str.getBytes());
    }

    public static String hashMD5(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(bytes);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // it's got to be there
        }
        return toHexString(md.digest());
    }


    /**
     * Returns the SHA hash of a String.
     *
     * @param str Description of the Parameter
     * @return Description of the Return Value
     */
    public static String hashSHA(String str) {
        byte[] b = str.getBytes();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
            md.update(b);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // it's got to be there
        }
        return toHexString(md.digest());
    }

    /**
     * Converts a byte array into a hexadecimal String.
     *
     * @param b a byte array to be converted
     * @return a hexadecimal (lower-case-based) String representation of the
     *         byte array
     */
    public static String toHexString(byte[] b) {
        if (null == b)
            return null;
        int len = b.length;
        byte[] hex = new byte[len << 1];
        for (int i = 0, j = 0; i < len; i++, j += 2) {
            hex[j] = (byte) ((b[i] & 0xF0) >> 4);
            hex[j] += 10 > hex[j] ? 48 : 87;
            hex[j + 1] = (byte) (b[i] & 0x0F);
            hex[j + 1] += 10 > hex[j + 1] ? 48 : 87;
        }
        return new String(hex);
    }
}