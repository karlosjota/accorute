/**
 * Created by IntelliJ IDEA.
 * User: Пользователь
 * Date: 10.09.2008
 * Time: 12:15:45
 * To change this template use File | Settings | File Templates.
 */
package su.msu.cs.lvk.accorute.http.constants;

/**
 * The enumeration class, which defines possible locations for a parameter in HTTP request.
 */
public enum ActionParameterLocation {
    /**
     * The parameter is located in the URL. 
     */
    URL,
    /**
     * The parameter is located in the QUERY.
     */
    QUERY,
    /**
     * The parameter is located in the body of the request.
     */
    BODY,
    /**
     * The parameter is located in the headers section of the request (except "Cookie" header).
     */
    HEADER,
    /**
     * The parameter is located in the "Cookie" header of the request.
     */
    COOKIE
}
