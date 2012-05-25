package su.msu.cs.lvk.accorute.utils;

import net.sourceforge.htmlunit.corejs.javascript.Token;
import net.sourceforge.htmlunit.corejs.javascript.ast.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 12.05.12
 * Time: 19:34
 * To change this template use File | Settings | File Templates.
 */
public class AstCompare {
    static class RecordingNodeVisitor implements NodeVisitor{
        final Collection<AstNode> coll;
        RecordingNodeVisitor(Collection<AstNode> thecoll) {
            coll = thecoll;
        }
        public boolean visit(AstNode node) {
            if(node.getType() != Token.COMMENT && node.getType() != Token.EMPTY){
                coll.add(node);
                return true;
            }
            return false;
        }
    }
    public static boolean equalsIgnoringNames(AstRoot sample, AstRoot template){
        final ArrayList<AstNode> sampleNodeList = new ArrayList<AstNode>();
        final ArrayList<AstNode> templateNodeList = new ArrayList<AstNode>();
        sample.visit(new RecordingNodeVisitor(sampleNodeList));
        template.visit(new RecordingNodeVisitor(templateNodeList));
        if(sampleNodeList.size() != templateNodeList.size())
            return false;
        Map<String, String> namesCorrespondence = new HashMap<String, String>();
        for(int i = 0 ; i < templateNodeList.size(); i++){
            AstNode sampleNode = sampleNodeList.get(i);
            AstNode templateNode = templateNodeList.get(i);
            if(!(
                    (sampleNode.getType() == templateNode.getType())
                    ||sampleNode.getType()==Token.SHEQ &&  templateNode.getType()==Token.EQ
                    ||sampleNode.getType()==Token.EQ &&  templateNode.getType()==Token.SHEQ
                    ||sampleNode.getType()==Token.SHNE &&  templateNode.getType()==Token.NE
                    ||sampleNode.getType()==Token.NE &&  templateNode.getType()==Token.SHNE
            )){
                return false;
            }
            if(sampleNode.getType() == Token.NAME){
                String sampleName = ((Name) sampleNode).getIdentifier();
                String templateName = ((Name) templateNode).getIdentifier();
                if(namesCorrespondence.containsKey(templateName)){
                    if(!namesCorrespondence.get(templateName).equals(sampleName))
                        return false;
                    continue;
                }
                namesCorrespondence.put(sampleName, templateName);
            }else if(sampleNode.getType() == Token.STRING){
                if(!((StringLiteral) sampleNode).getValue().equals(((StringLiteral) templateNode).getValue()))
                    return false;
            }
        }
        return true;
    }
}
