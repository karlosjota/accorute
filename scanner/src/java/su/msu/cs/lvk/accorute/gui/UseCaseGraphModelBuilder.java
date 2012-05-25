package su.msu.cs.lvk.accorute.gui;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.UseCase;
import su.msu.cs.lvk.accorute.http.model.UseCaseGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 06.05.12
 * Time: 3:34
 * To change this template use File | Settings | File Templates.
 */
public class UseCaseGraphModelBuilder {
    public static void makeGraph(mxGraph ucGraph){
        ucGraph.getModel().beginUpdate();
        ((mxGraphModel) ucGraph.getModel()).clear();
        ucGraph.getModel().endUpdate();
        ucGraph.getModel().beginUpdate();
        try {
            ((mxGraphModel) ucGraph.getModel()).clear();
            UseCaseGraph g = WebAppProperties.getInstance().getUcGraph();
            Map<UseCase, Object> ucToNode = new HashMap<UseCase, Object>();
            List roots = new ArrayList();
            if(g != null && g.getUCCount()!=0){
                for(UseCase c : g.getUseCases()){
                    Object theNode = ucGraph.insertVertex(ucGraph.getDefaultParent(), null, c, 0,0, 250, 50);
                    ucToNode.put(c,theNode);
                    if(g.getPrerequisites(c).size() == 0){
                        roots.add(theNode);
                    }
                }
                for(UseCase c : g.getUseCases()){
                    Object theNode =  ucToNode.get(c);
                    for(UseCase pre : g.getPrerequisites(c)){
                        ucGraph.insertEdge(ucGraph.getDefaultParent(), null, "",theNode, ucToNode.get(pre));
                    }
                    for(UseCase canc : g.getCancelledBy(c)){
                        ucGraph.insertEdge(ucGraph.getDefaultParent(), null, "", theNode,ucToNode.get(canc),"strokeColor=red");
                    }
                }
                mxHierarchicalLayout layout = new mxHierarchicalLayout(ucGraph);
                layout.execute(ucGraph.getDefaultParent(), roots);
            }
        } finally {
            ucGraph.getModel().endUpdate();
        }
    }
}
