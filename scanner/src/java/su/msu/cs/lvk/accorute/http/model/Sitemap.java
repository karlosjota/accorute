package su.msu.cs.lvk.accorute.http.model;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import su.msu.cs.lvk.accorute.WebAppProperties;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.04.2010
 * Time: 20:23:32
 * To change this template use File | Settings | File Templates.
 */
public class Sitemap {
    private long nextNodeID = 01l;
    private final Map<EntityID, SitemapNode>  nodeById = new HashMap<EntityID, SitemapNode>();
    private final SitemapNode entryNode = genNode();
    private final SitemapNode invalidNode = genNode();
    private final SitemapNode exitNode = genNode();

    public SitemapNode getInvalidNode() {
        return invalidNode;
    }

    public SitemapNode getExitNode() {
        return exitNode;
    }

    public SitemapNode getEntryNode() {
        return entryNode;
    }

    @Override
    public String toString() {
        return "Sitemap{" +
                "\nctxID=" + ctxID +
                ",\nNodes=" + actionDepGraph.vertexSet() +
                ",\nEdges=" + actionDepGraph.edgeSet() +
                ",\ninvalidNodeID=" + invalidNode.getNodeID().getId() +
                ",\nexitNodeID=" + exitNode.getNodeID().getId() +
                ",\nentryNodeID=" + entryNode.getNodeID().getId() +
                "\n}";
    }

    public static class SitemapNode{
        public EntityID getNodeID() {
            return nodeID;
        }

        public Set<HttpAction> getActions() {
            return httpActions;
        }

        public void setActions(Collection<HttpAction> httpActions) {
            this.httpActions.clear();
            addActions(httpActions);
        }
        public boolean addAction(HttpAction action) {
            return this.httpActions.add(action);
        }
        public boolean addActions(Collection<HttpAction> httpActions) {
            return this.httpActions.addAll(httpActions);
        }
        public boolean addInbound(Conversation c){
            return inConversation.add(c);
        }
        public boolean addPage(HtmlPage p){
            return pages.add(p);
        }
        public boolean responseEqual(Conversation c){
            if(inConversation.size() == 0)
                return false;
            return WebAppProperties.getInstance().getrEqD().ResponseEquals(inConversation.get(0).getResponse() ,c.getResponse());
        }

        public boolean pageEqual(HtmlPage p){
            if(inConversation.size() == 0 || pages.size() == 0)
                return false;
            return WebAppProperties.getInstance().getPageEqDec().pagesEqual(pages.get(0),p);
        }

        private final Set<HttpAction> httpActions = new HashSet<HttpAction>();
        private final List<Conversation> inConversation  = new ArrayList<Conversation> ();
        private final List<HtmlPage> pages  = new ArrayList<HtmlPage> ();

        private final EntityID nodeID;

        SitemapNode(EntityID id) {
            this.nodeID = id;
        }
        boolean equals(SitemapNode other){
            return other.getActions().equals(httpActions);
        }

        @Override
        public String toString() {
            return "\nSitemapNode{" +
                    "nodeID=" + nodeID.getId() +
                    ", pages=" + pages +
                    ", HTTPactions=" + httpActions +
                    /*", inConversation=" + inConversation +*/
                    '}';
        }
    }
    synchronized public SitemapNode genNode(){
        EntityID eid = new EntityID(nextNodeID);
        SitemapNode n = new SitemapNode(eid);
        nodeById.put(eid, n);
        nextNodeID ++;
        return n;
    }
    public SitemapNode getNodeByID(EntityID id){
         return nodeById.get(id);
    }
    public SitemapNode createNode(Conversation conv, HtmlPage p){
        SitemapNode n = genNode();
        n.addInbound(conv);
        n.addPage(p);
        return n;
    }
    public SitemapNode getNode(HtmlPage p){
        SitemapNode n = null;
        for(Map.Entry<EntityID, SitemapNode> entry : nodeById.entrySet()){
            if(entry.getValue().pageEqual(p)){
                n = entry.getValue();
                break;
            }
        }
        if(n != null){
            n.addPage(p);
        }
        return n;
    }
    public SitemapNode getNode(Conversation conv){
        SitemapNode n = null;
        for(Map.Entry<EntityID, SitemapNode> entry : nodeById.entrySet()){
            if(entry.getValue().responseEqual(conv)){
                n = entry.getValue();
                break;
            }
        }
        if(n != null){
            n.addInbound(conv);
        }
        return n;
    }
    public static class SitemapEdge extends DefaultEdge {
        private final SitemapNode v1;
        private final SitemapNode v2;
        private final CorrespondentActions label;
        private final ArrayList<Conversation> convs;


        public SitemapEdge(SitemapNode v1, SitemapNode v2, CorrespondentActions label, ArrayList<Conversation> convs) {
            this.v1 = v1;
            this.v2 = v2;
            this.label = label;
            this.convs = convs;
        }

        public SitemapNode getV1() {
            return v1;
        }

        public SitemapNode getV2() {
            return v2;
        }

        @Override
        public String toString() {
            return "\n{" +
                    v1.getNodeID().getId() +
                    " -> " + v2.getNodeID().getId() +
                    ", http=" + label.getHttpActions() +
                    ", DOM=" + label.getDomActions() +
                    '}';
        }
    }

    private final DirectedMultigraph<SitemapNode, SitemapEdge> actionDepGraph =
            new DirectedMultigraph<SitemapNode, SitemapEdge>(SitemapEdge.class);

    private final EntityID ctxID;

    public Sitemap(EntityID ctxID) {     
        this.ctxID = ctxID;
    }

    public boolean addEdge(SitemapNode from, SitemapNode to, CorrespondentActions act, ArrayList<Conversation> convs){
        actionDepGraph.addVertex(from);
        actionDepGraph.addVertex(to);
        SitemapEdge edge = new SitemapEdge(from,to,act,convs);
        if(convs!=null){
            for(Conversation c : convs){
                to.addInbound(c);
            }
        }
        return actionDepGraph.addEdge(from,to,edge);
    }

    public EntityID getCtxID() {
        return ctxID;
    }

}
