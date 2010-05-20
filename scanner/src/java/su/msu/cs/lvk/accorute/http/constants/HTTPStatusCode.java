package su.msu.cs.lvk.accorute.http.constants;


public class HTTPStatusCode {
    /**
     * The response codes for HTTP, as of version 1.1.
     */

    // REMIND: do we want all these??
    // Others not here that we do want??

    /* 2XX: generally "OK" */

    /**
     * HTTP Status-Code 200: OK.
     */
    public static final String HTTP_OK = "200";

    /**
     * HTTP Status-Code 201: Created.
     */
    public static final String HTTP_CREATED = "201";

    /**
     * HTTP Status-Code 202: Accepted.
     */
    public static final String HTTP_ACCEPTED = "202";

    /**
     * HTTP Status-Code 203: Non-Authoritative Information.
     */
    public static final String HTTP_NOT_AUTHORITATIVE = "203";

    /**
     * HTTP Status-Code 204: No Content.
     */
    public static final String HTTP_NO_CONTENT = "204";

    /**
     * HTTP Status-Code 205: Reset Content.
     */
    public static final String HTTP_RESET = "205";

    /**
     * HTTP Status-Code 206: Partial Content.
     */
    public static final String HTTP_PARTIAL = "206";

    /* 3XX: relocation/redirect */

    /**
     * HTTP Status-Code 300: Multiple Choices.
     */
    public static final String HTTP_MULT_CHOICE = "300";

    /**
     * HTTP Status-Code 301: Moved Permanently.
     */
    public static final String HTTP_MOVED_PERM = "301";

    /**
     * HTTP Status-Code 302: Temporary Redirect.
     */
    public static final String HTTP_MOVED_TEMP = "302";

    /**
     * HTTP Status-Code 303: See Other.
     */
    public static final String HTTP_SEE_OTHER = "303";

    /**
     * HTTP Status-Code 304: Not Modified.
     */
    public static final String HTTP_NOT_MODIFIED = "304";

    /**
     * HTTP Status-Code 305: Use Proxy.
     */
    public static final String HTTP_USE_PROXY = "305";

    /**
     * HTTP Status-Code 307: Temporary Redirect.
     */
    public static final String HTTP_TEMP_REDIR = "307";

    /* 4XX: client error */

    /**
     * HTTP Status-Code 400: Bad Request.
     */
    public static final String HTTP_BAD_REQUEST = "400";

    /**
     * HTTP Status-Code 401: Unauthorized.
     */
    public static final String HTTP_UNAUTHORIZED = "401";

    /**
     * HTTP Status-Code 402: Payment Required.
     */
    public static final String HTTP_PAYMENT_REQUIRED = "402";

    /**
     * HTTP Status-Code 403: Forbidden.
     */
    public static final String HTTP_FORBIDDEN = "403";

    /**
     * HTTP Status-Code 404: Not Found.
     */
    public static final String HTTP_NOT_FOUND = "404";

    /**
     * HTTP Status-Code 405: Method Not Allowed.
     */
    public static final String HTTP_BAD_METHOD = "405";

    /**
     * HTTP Status-Code 406: Not Acceptable.
     */
    public static final String HTTP_NOT_ACCEPTABLE = "406";

    /**
     * HTTP Status-Code 407: Proxy Authentication Required.
     */
    public static final String HTTP_PROXY_AUTH = "407";

    /**
     * HTTP Status-Code 408: Request Time-Out.
     */
    public static final String HTTP_CLIENT_TIMEOUT = "408";

    /**
     * HTTP Status-Code 409: Conflict.
     */
    public static final String HTTP_CONFLICT = "409";

    /**
     * HTTP Status-Code 410: Gone.
     */
    public static final String HTTP_GONE = "410";

    /**
     * HTTP Status-Code 411: Length Required.
     */
    public static final String HTTP_LENGTH_REQUIRED = "411";

    /**
     * HTTP Status-Code 412: Precondition Failed.
     */
    public static final String HTTP_PRECON_FAILED = "412";

    /**
     * HTTP Status-Code 413: Request Entity Too Large.
     */
    public static final String HTTP_ENTITY_TOO_LARGE = "413";

    /**
     * HTTP Status-Code 414: Request-URI Too Large.
     */
    public static final String HTTP_REQ_TOO_LONG = "414";

    /**
     * HTTP Status-Code 415: Unsupported Media Type.
     */
    public static final String HTTP_UNSUPPORTED_TYPE = "415";

    /* 5XX: server error */

    /**
     * HTTP Status-Code 500: Internal Server Error.
     *
     * @deprecated it is misplaced and shouldn't have existed.
     */
    @Deprecated
    public static final String HTTP_SERVER_ERROR = "500";

    /**
     * HTTP Status-Code 500: Internal Server Error.
     */
    public static final String HTTP_INTERNAL_ERROR = "500";

    /**
     * HTTP Status-Code 501: Not Implemented.
     */
    public static final String HTTP_NOT_IMPLEMENTED = "501";

    /**
     * HTTP Status-Code 502: Bad Gateway.
     */
    public static final String HTTP_BAD_GATEWAY = "502";

    /**
     * HTTP Status-Code 503: Service Unavailable.
     */
    public static final String HTTP_UNAVAILABLE = "503";

    /**
     * HTTP Status-Code 504: Gateway Timeout.
     */
    public static final String HTTP_GATEWAY_TIMEOUT = "504";

    /**
     * HTTP Status-Code 505: HTTP Version Not Supported.
     */
    public static final String HTTP_VERSION = "505";


    public static boolean isRedirect(String code) {
        return HTTP_MOVED_PERM.equals(code) ||
                HTTP_MOVED_TEMP.equals(code) ||
                HTTP_SEE_OTHER.equals(code) ||
                HTTP_TEMP_REDIR.equals(code);
    }

    public static boolean isSuccess(String code) {
        return HTTP_OK.equals(code) ||
                HTTP_CREATED.equals(code) ||
                HTTP_ACCEPTED.equals(code);
    }

    public static boolean is5xx(String code) {
        return HTTP_INTERNAL_ERROR.equals(code) ||
                HTTP_NOT_IMPLEMENTED.equals(code) ||
                HTTP_BAD_GATEWAY.equals(code) ||
                HTTP_UNAVAILABLE.equals(code) ||
                HTTP_GATEWAY_TIMEOUT.equals(code) ||
                HTTP_VERSION.equals(code);
    }
}
