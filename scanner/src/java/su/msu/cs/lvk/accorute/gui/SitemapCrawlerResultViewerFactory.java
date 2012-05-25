package su.msu.cs.lvk.accorute.gui;

import com.mxgraph.view.mxGraph;
import su.msu.cs.lvk.accorute.http.model.Sitemap;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.tasks.SitemapCrawler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 14.05.12
 * Time: 2:04
 * To change this template use File | Settings | File Templates.
 */
public class SitemapCrawlerResultViewerFactory implements TaskVisualiserFactory{
    public TaskVisualiser generate(Task t) {
        if(t instanceof  SitemapCrawler){
            return new SitemapCrawlerResultViewer((SitemapCrawler) t);
        }
        return null;
    }
    class SitemapCrawlerResultViewer implements TaskVisualiser{
        private JPanel panel_;
        private mxGraph graph_;

        SitemapCrawlerResultViewer(SitemapCrawler crawler) {
            panel_ = new JPanel();
            panel_.setLayout(new GridBagLayout());
            if(crawler.getResult() == null)
                return;
            final Sitemap sitemap = (Sitemap) crawler.getResult();
            /*
            DirectedMultigraph graph = sitemap.getActionDepGraph();
            graph_ = new mxGraph(){
                @Override
                public String convertValueToString(Object cell) {
                    Object o  = ((mxCell)cell).getValue();
                    if(o instanceof Sitemap.SitemapEdge){
                        Sitemap.SitemapEdge edge = (Sitemap.SitemapEdge) o;
                        return "";
                        
                    }
                    if(o instanceof Sitemap.SitemapNode){
                        Sitemap.SitemapNode node = (Sitemap.SitemapNode) o;
                        return "";
                    }
                    return super.convertValueToString(cell);    //To change body of overridden methods use File | Settings | File Templates.
                }
            };
            mxConstants.DEFAULT_FONTSIZE = 12;
            graph_.setHtmlLabels(true);
            graph_.setCellsBendable(true);
            graph_.setCellsCloneable(false);
            graph_.setCellsEditable(false);
            graph_.setCellsDisconnectable(false);
            graph_.setCellsResizable(false);
            graph_.setAllowDanglingEdges(false);
            graph_.setCellsMovable(true);
            graph_.setCellsDeletable(false);
            graph_.setConnectableEdges(false);
            graph_.setDropEnabled(false);
            graph_.setSplitEnabled(false);
            graph_.setCellsLocked(true);
            graph_.setEnabled(true);
            SitemapGraphModelBuilder.makeGraph(graph_, sitemap);
            mxGraphComponent graphComponent = new mxGraphComponent(graph_);
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = c.gridx = 0;
            c.weightx = 1;
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1.0;
            panel_.add(new JScrollPane(graphComponent),c);
            */
            new Thread(new Runnable() {
                public void run() {
                    try {
                        File temp = File.createTempFile("graph", ".dot");
                        sitemap.writeToFile(temp.getAbsolutePath(),"");
                        File temp2 = File.createTempFile("graph", ".png");
                        Runtime.getRuntime().exec("dot " + temp.getAbsolutePath() + " -Tpng -o " + temp2.getAbsolutePath()).waitFor();
                        //ImagePanel p = new ImagePanel();
                        //p.loadImage("/tmp/graph.png");
                        GridBagConstraints c = new GridBagConstraints();
                        c.gridy = c.gridx = 0;
                        c.weightx = 1;
                        c.fill = GridBagConstraints.BOTH;
                        c.weighty = 1.0;
                        panel_.add(new JScrollPane(new JLabel(new ImageIcon(temp2.getAbsolutePath()))),c);

                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }).run();

        }

        public Component getComponent() {
            return panel_;
        }
    }
}
