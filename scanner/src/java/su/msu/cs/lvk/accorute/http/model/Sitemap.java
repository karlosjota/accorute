package su.msu.cs.lvk.accorute.http.model;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.04.2010
 * Time: 20:23:32
 * To change this template use File | Settings | File Templates.
 */
public class Sitemap {
    public class SitemapNode{
        public Set<Action> getActions() {
            return actions;
        }

        public void setActions(Collection<Action> actions) {
            this.actions.clear();
            addActions(actions);
        }
        public boolean addAction(Action action) {
            return this.actions.add(action);
        }
        public boolean addActions(Collection<Action> actions) {
            return this.actions.addAll(actions);
        }

        Set<Action> actions = new HashSet<Action>();
        boolean equals(SitemapNode other){
            return other.getActions().equals(actions);
        }
    }
    public class SitemapEdge extends DefaultEdge {
        private SitemapNode v1;
        private SitemapNode v2;
        private Action label;

        public SitemapEdge(SitemapNode v1, SitemapNode v2, Action label) {
            this.v1 = v1;
            this.v2 = v2;
            this.label = label;
        }

        public SitemapNode getV1() {
            return v1;
        }

        public SitemapNode getV2() {
            return v2;
        }
    }


    private Set<Action> actionSet;
    private DirectedMultigraph<SitemapNode, SitemapEdge> actionDepGraph =
            new DirectedMultigraph<SitemapNode, SitemapEdge>(SitemapEdge.class);
    private EntityID ctxID = EntityID.NOT_INITIALIZED;

    public Set<Action> getActionSet() {
        return actionSet;
    }

    public boolean addEdge(SitemapNode from, SitemapNode to, Action act){
        actionSet.add(act);
        boolean wasImpact = actionDepGraph.addVertex(from) || actionDepGraph.addVertex(to);
        SitemapEdge edge = new SitemapEdge(from,to,act);
        return wasImpact || actionDepGraph.addEdge(from,to,edge);
    }

    public EntityID getCtxID() {
        return ctxID;
    }

    public void setCtxID(EntityID ctxID) {
        this.ctxID = ctxID;
    }
}
