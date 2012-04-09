package su.msu.cs.lvk.accorute.gui;

import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.tasks.HtmlPageParser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 09.04.12
 * Time: 12:24
 * To change this template use File | Settings | File Templates.
 */
public class HtmlPageParserResultViewerFactory implements TaskVisualiserFactory {
    public TaskVisualiser generate(Task t) {
        if(! (t instanceof HtmlPageParser))
            return null;
        return new HtmlPageParserResultViewer((HtmlPageParser)t);  //To change body of implemented methods use File | Settings | File Templates.
    }

    class HtmlPageParserResultViewer implements TaskVisualiser {
        private JTable table_;
        private JPanel panel_;
        private DefaultTableModel dataModel_;
        public Component getComponent() {
            return panel_;
        }

        HtmlPageParserResultViewer(HtmlPageParser parser) {
            panel_ = new JPanel();
            panel_.setBorder(BorderFactory.createTitledBorder("Actions found on page " + parser.getPage().getUrl()));
            panel_.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = c.gridx = 0;
            c.weightx = 1;
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1.0;

            String data[][] = {};
            String col[] = {"Dom actions","Http Action"};
            dataModel_ = new DefaultTableModel(data, col);
            table_ = new JTable(dataModel_);
            Object result = parser.getResult();
            if(result != null){
                ArrayList<HtmlPageParser.ResultItem> items = (ArrayList<HtmlPageParser.ResultItem>) result;
                for(HtmlPageParser.ResultItem item : items){
                    String row [] = {
                        item.getDomActionChain().toString(),
                        item.getAction().toString()
                    };
                    dataModel_.addRow(row);
                }
            }
            panel_.add(new JScrollPane(table_),c);
        }
    }
}
