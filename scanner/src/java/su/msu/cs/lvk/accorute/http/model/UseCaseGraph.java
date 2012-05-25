package su.msu.cs.lvk.accorute.http.model;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.utils.CallbackContainer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 29.11.2010
 * Time: 1:13:36
 * To change this template use File | Settings | File Templates.
 */
public class UseCaseGraph extends CallbackContainer implements Comparator<UseCase> {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    public int compare(UseCase o1, UseCase o2) {
        return getCancelledByCount(o1) - getCancelledByCount(o2);
    }

    public void fromGraph(UseCaseGraph other){
        ucDepGraph.removeAllVertices(ucDepGraph.vertexSet());
        ucCancelGraph.removeAllVertices(ucCancelGraph.vertexSet());
        for(UseCase uc : other.getUseCases()){
            ucDepGraph.addVertex(uc);
            ucCancelGraph.addVertex(uc);
        }
        for(UseCase uc : other.getUseCases()){
            for(UseCase dep: other.getPrerequisites(uc)){
                ucDepGraph.addEdge(dep, uc);
            }
            for(UseCase canc: other.getCancelledBy(uc)){
                ucCancelGraph.addEdge(canc, uc);
            }
        }
        notifyCallbacks();
    }
    public static class graphEdge extends DefaultEdge {
        public UseCase getSource(){
            return (UseCase) super.getSource();
        }
        public UseCase getTarget(){
            return (UseCase) super.getTarget(); 
        }
        @Override
        public String toString(){
            return getSource() + " -> " + getTarget();   
        }
    }
    private static class DependencyRespectingIterator implements Iterator {
        private final UseCaseGraph ucGraph;
        private final PriorityQueue<UseCase> traversalOrder;
        private final Set<UseCase> alreadyTraversed = new HashSet<UseCase> ();
        private boolean wasTraversed(UseCase uc){
            return alreadyTraversed.contains(uc);
        }
        public DependencyRespectingIterator(UseCaseGraph g){
            ucGraph = g;
            traversalOrder = new PriorityQueue<UseCase>(g.getUCCount()>0?g.getUCCount():1, g);
            Iterator<UseCase> it = g.getUseCases().iterator();
            while (it.hasNext()){
                UseCase u = it.next();
                if(g.getPrerequisitesCount(u) == 0){
                    traversalOrder.add(u);
                }
            }
        }
        public boolean hasNext() {
            return ! traversalOrder.isEmpty();
        }

        public Object next() {
            UseCase top = traversalOrder.poll();
            alreadyTraversed.add(top);
            Iterator<UseCase> it = ucGraph.getUseCases().iterator();
            while (it.hasNext()){
                UseCase u = it.next();
                if(alreadyTraversed.contains(u))
                    continue;
                if(traversalOrder.contains(u))
                    continue;
                if(alreadyTraversed.containsAll(ucGraph.getPrerequisites(u)))
                    traversalOrder.add(u);
            }
            return top;
        }

        public void remove() {
            throw  new NotImplementedException("Removing elements from UC graph is prohibited!");
        }
    }
    public Iterator<UseCase> dependencyRespectingIterator(){
        return new DependencyRespectingIterator(this);
    }
    public Set<UseCase> getUseCases(){
        return ucDepGraph.vertexSet();
    }
    public int getUCCount(){
        return ucDepGraph.vertexSet().size();
    }
    public Set<UseCase> getPrerequisites(UseCase u){
        Set<graphEdge> dependent = ucDepGraph.incomingEdgesOf(u);
        Iterator<graphEdge> it = dependent.iterator();
        Set<UseCase> result = new HashSet<UseCase>();
        while (it.hasNext()){
            graphEdge e = it.next();
            result.add(e.getSource());
        }
        return result;
    }
    public int getPrerequisitesCount(UseCase u){
        return ucDepGraph.inDegreeOf(u);
    }
    public int getCancelledByCount(UseCase u){
        return ucCancelGraph.outDegreeOf(u);
    }
    public Set<UseCase> getCancelledBy(UseCase u){
        Set<graphEdge> dependent = ucCancelGraph.outgoingEdgesOf(u);
        Iterator<graphEdge> it = dependent.iterator();
        Set<UseCase> result = new HashSet<UseCase>();
        while (it.hasNext()){
            graphEdge e = it.next();
            result.add(e.getTarget());
        }
        return result;
    }
    private final DirectedMultigraph<UseCase, graphEdge> ucDepGraph =
            new DirectedMultigraph<UseCase, graphEdge>(graphEdge.class);
    private final DirectedMultigraph<UseCase, graphEdge> ucCancelGraph =
            new DirectedMultigraph<UseCase, graphEdge>(graphEdge.class);

    @Override
    public String toString() {
        return "UseCaseGraph{" +
                "\nDependencies: " + ucDepGraph.edgeSet() +
                "\nCancellations:" + ucCancelGraph.edgeSet() +
                "\n}";
    }

    public void writeToFile(String fname){
        try{
            FileWriter fstream = new FileWriter(fname);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("digraph usecases {\n");
            for(UseCase u : ucDepGraph.vertexSet()){
                out.write("\t"
                        + u.hashCode()
                        +"[shape = record, label=\"{"
                        + u.getHttpAct().getName()
                        +"|"
                        + u.getUserRole().getRoleName()
                        + "}\"];\n"
                );
            }
            for(graphEdge e:ucDepGraph.edgeSet()){
                int source = e.getSource().hashCode();
                int dest = e.getTarget().hashCode();
                out.write("\t"
                        + source
                        + "->"
                        + dest
                        +"[color=black, style = solid];\n"
                );
            }
            for(graphEdge e:ucCancelGraph.edgeSet()){
                int source = e.getSource().hashCode();
                int dest = e.getTarget().hashCode();
                out.write("\t"
                        + source
                        + "->"
                        + dest
                        +"[color=red, style = dotted];\n"
                );
            }
            out.write("}\n");
            out.close();
        }catch (IOException ex){
            logger.error("Error writing to file: ",ex);
        }
    }

    public boolean addUCIfNotPresent(UseCase u){
        if(getNodeByUC(u)!=null){
            return false;
        }
        addUC(u);
        return true;
    }
    public void addUC(UseCase uc){
        ucDepGraph.addVertex(uc);
        ucCancelGraph.addVertex(uc);
        notifyCallbacks();
    }
    private UseCase getNodeByUC(UseCase template){
        if(template == null){
            throw new IllegalArgumentException("Null template UC!!!");
        }
        UseCase res = null;
        Iterator it = ucDepGraph.vertexSet().iterator();
        while(it.hasNext()){
            UseCase uc = (UseCase)it.next();
            if(uc.getUserRole() != template.getUserRole())
                continue;
            if(WebAppProperties.getInstance().getAcEqDec().ActionEquals(uc.getHttpAct(),template.getHttpAct()))
                return uc;
        }
        return null;
    }
    public void addDependency(UseCase from, UseCase to){
        myAddEdge(from,to,ucDepGraph);
        notifyCallbacks();
    }
    public void addCancellation(UseCase from, UseCase to){
        myAddEdge(from,to,ucCancelGraph);
        notifyCallbacks();
    }
    private void myAddEdge(UseCase from, UseCase to,DirectedMultigraph d){
        UseCase fromNode = getNodeByUC(from);
        UseCase toNode = getNodeByUC(to);
        if(fromNode == null || toNode == null)
            throw new IllegalArgumentException("vertex is not present in the graph");
        d.addEdge(fromNode, toNode);
    }
}
