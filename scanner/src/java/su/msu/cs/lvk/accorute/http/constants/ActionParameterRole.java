package su.msu.cs.lvk.accorute.http.constants;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.04.2010
 * Time: 20:38:33
 * To change this template use File | Settings | File Templates.
 */
public enum ActionParameterRole {
    // Example: www.ex.com/1.asp?action=moveMoney&from=1&to=2&comment=Hey&SESSSIONID=12345
    /**
     * Authentication, referer etc.
     * SESSSIONID in our example
     */
    TECHNICAL,
    /**
     * Denotes the type of the action.
     * action in our example
     */
    ACTION_TYPE,
    /**
     * Parameters to the action
     * from and to in the example
     */
    ACTION_SUBJECT,
    /**
     * Additional data; the contents of such fields are of no importance to the business logic tier.
     */
    ADDITIONAL,
    /**
     * no info
     */
    UNKNOWN
}
