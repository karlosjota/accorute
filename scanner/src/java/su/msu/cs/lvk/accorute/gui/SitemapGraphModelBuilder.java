package su.msu.cs.lvk.accorute.gui;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;
import org.jgrapht.graph.DirectedMultigraph;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.Sitemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 14.05.12
 * Time: 2:16
 * To change this template use File | Settings | File Templates.
 */
public class SitemapGraphModelBuilder {
    public static void makeGraph(mxGraph sitemapGraph, Sitemap sitemap){
        sitemapGraph.getModel().beginUpdate();
        ((mxGraphModel) sitemapGraph.getModel()).clear();
        sitemapGraph.getModel().endUpdate();
        sitemapGraph.getModel().beginUpdate();
        try {
            ((mxGraphModel) sitemapGraph.getModel()).clear();
            DirectedMultigraph<Sitemap.SitemapNode, Sitemap.SitemapEdge> theGraph = sitemap.getActionDepGraph();
            Map<Sitemap.SitemapNode, Object> userObjectsForNodes = new HashMap<Sitemap.SitemapNode, Object>();
            for(Sitemap.SitemapNode node: theGraph.vertexSet()){
                String style = "fillColor=black;";
                if(node == sitemap.getEntryNode()){
                    style = "fillColor=blue;";
                }else if(node == sitemap.getExitNode()){
                    style = "fillColor=green;";
                }else if(node == sitemap.getExitNode()){
                    style = "fillColor=red;";
                }

                Object userObject = sitemapGraph.insertVertex(sitemapGraph.getDefaultParent(), null, node, 0, 0, 10,10, style);
                userObjectsForNodes.put(node, userObject);
            }
            Map<String, Object> labelNodeUserObjects = new HashMap<String, Object>();
            for(Sitemap.SitemapEdge edge: theGraph.edgeSet()){
                Object from = userObjectsForNodes.get(edge.getV1());
                Object to = userObjectsForNodes.get(edge.getV2());
                String lbl = edge.getLabel().getAsJGraphLabel();
                Object userObject;
                if(labelNodeUserObjects.containsKey(lbl)){
                    userObject = labelNodeUserObjects.get(lbl);
                }else{
                    String [] lines = lbl.split("\n");
                    int longest = 0;
                    for(int i = 0; i < lines.length ; i++){
                        if(lines[i].length() > longest)
                            longest = lines[i].length();
                    }
                    userObject = sitemapGraph.insertVertex(sitemapGraph.getDefaultParent(), null, lbl, 0, 0, longest * 7, lines.length*20);
                    labelNodeUserObjects.put(lbl, userObject);
                }
                sitemapGraph.insertEdge(sitemapGraph.getDefaultParent(), null, edge, from, userObject);
                sitemapGraph.insertEdge(sitemapGraph.getDefaultParent(), null, edge, userObject, to);
            }

            mxOrganicLayout layout = new mxOrganicLayout(sitemapGraph);
            //layout.setEdgeDistanceCostFactor(10000);
            //layout.setEdgeCrossingCostFactor(10000);
            //layout.setOptimizeEdgeCrossing(true);
            //layout.setOptimizeEdgeDistance(true);
            layout.execute(sitemapGraph.getDefaultParent());
            /*mxHierarchicalLayout layout = new mxHierarchicalLayout(sitemapGraph);
            List roots = new ArrayList();
            roots.add(userObjectsForNodes.get(sitemap.getEntryNode()));
            layout.execute(sitemapGraph.getDefaultParent(), roots);
            */
            /*
            mxPartitionLayout layout = new mxPartitionLayout(sitemapGraph, false);
            layout.execute(sitemapGraph.getDefaultParent());
            */
            /*
            mxFastOrganicLayout layout = new mxFastOrganicLayout(sitemapGraph);
            layout.execute(sitemapGraph.getDefaultParent());
            */
        } finally {
            sitemapGraph.getModel().endUpdate();
        }
    }
}
