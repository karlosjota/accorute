package su.msu.cs.lvk.accorute.http.constants;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 10.04.2010
 * Time: 23:38:49
 * To change this template use File | Settings | File Templates.
 */
public enum ActionParameterMeaning {
    /**
     * Changing identifier
     */
    IDENTIFIER,

    /**
     * E.g. username,password - never change
     */
    STATICCREDENTIAL,

    /**
     * eg, challenge-response, CAPTCHA.
     * Human can guess its value based only on preceding response (i.e what he sees)
     */
    VOLATILECREDENTIAL,

    /**
     * Session id
     */
    SESSIONTOKEN,

    /**
     * Anti-CSRF
     */
    ONETIMETOKEN,


    /**
     * User can manipulate the value via standard web browser
     */
    USERCONTROLLABLE,

    /**
     * User cannot manipulate with a standard web browser
     */
    AUTOMATIC,
    /**
     * no info
     */
    UNKNOWN
}
