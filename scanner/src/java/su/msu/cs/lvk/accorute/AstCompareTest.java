package su.msu.cs.lvk.accorute;

import net.sourceforge.htmlunit.corejs.javascript.Parser;
import net.sourceforge.htmlunit.corejs.javascript.ast.AstRoot;
import su.msu.cs.lvk.accorute.utils.AstCompare;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 12.05.12
 * Time: 19:57
 * To change this template use File | Settings | File Templates.
 */
public class AstCompareTest {
    public static void main(String [] args){
        String jqueryHandlerString =
            "function( e ) {" +
                "return typeof jQuery !== \"undefined\" && (!e || jQuery.event.triggered !== e.type) ?" +
                    "jQuery.event.dispatch.apply( eventHandle.elem, arguments ) :" +
                    "undefined;" +
            "}";
        AstRoot jqueryHandlerAstRoot = new Parser().parse(jqueryHandlerString, "", 0);
        String jqMin = "function(a){return typeof f!=\"undefined\"&&(!a||f.event.triggered!==a.type)?f.event.dispatch.apply(i.elem,arguments):b}";
        AstRoot jqMinAstRoot = new Parser().parse(jqMin, "", 0);
        System.out.println(jqueryHandlerAstRoot.debugPrint());
        System.out.println(jqMinAstRoot.debugPrint());
        System.out.println(AstCompare.equalsIgnoringNames(jqMinAstRoot, jqueryHandlerAstRoot));

    }
}
