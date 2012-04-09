package su.msu.cs.lvk.accorute.gui;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 09.04.12
 * Time: 1:21
 * To change this template use File | Settings | File Templates.
 */
public class LogWatchComponent extends AppenderSkeleton{
    private static final String ALL_THREAD_SELECT_ITEM = "All threads";
    private JTable table_;      
    private JPanel panel_;
    private JComboBox comboBox_;
    private DefaultTableModel dataModel_;
    private DefaultComboBoxModel comboBoxModel_;
    private final HashMap<String, Integer> threadNamesIndices_ = new HashMap<String, Integer>();
    TableRowSorter sorter;
    public void setFollowedThread(final String followedThread) {
        followedThread_ = followedThread;
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Object>(){
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                return followedThread_ == null || entry.getStringValue(2).equals(followedThread_);
            }
        });
        if(followedThread_ == null)
            followedThread_ =  ALL_THREAD_SELECT_ITEM;
        if(!threadNamesIndices_.containsKey(followedThread_)){
            threadNamesIndices_.put(followedThread_,comboBoxModel_.getSize());
            comboBoxModel_.addElement(followedThread_);
        }
        comboBox_.setSelectedIndex(threadNamesIndices_.get(followedThread_));
    }

    private String followedThread_ = null;
    private Component wrapper_;

    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    public boolean requiresLayout() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void append(LoggingEvent event){
        Date d = new Date(event.getTimeStamp());
        String data [] = {
                d.toString(),
                event.getLevel().toString(),
                event.getThreadName(),
                event.getLocationInformation().getFileName()+": " + event.getLocationInformation().getLineNumber(),
                event.getRenderedMessage()
        };
        if(!threadNamesIndices_.containsKey(event.getThreadName())){
            threadNamesIndices_.put(event.getThreadName(),comboBoxModel_.getSize());
            comboBoxModel_.addElement(event.getThreadName());
        }
        dataModel_.addRow(data);
    }

    public Component getComponent() {
        return panel_;
    }

    public LogWatchComponent(){
        super(true);
        final LogWatchComponent thisWatcher = this;
        String data[][] = {};
        String col[] = {"Time","Priority", "Thread", "File","Message"};
        dataModel_ = new DefaultTableModel(data, col);
        sorter = new TableRowSorter<DefaultTableModel>(dataModel_);
        table_ = new JTable(dataModel_);
        table_.setRowSorter(sorter);
        panel_ = new JPanel();
        panel_.setBorder(BorderFactory.createTitledBorder("Log messages"));
        comboBoxModel_ = new DefaultComboBoxModel();
        threadNamesIndices_.put(ALL_THREAD_SELECT_ITEM,comboBoxModel_.getSize());
        comboBoxModel_.addElement(ALL_THREAD_SELECT_ITEM);
        comboBox_ = new JComboBox(comboBoxModel_);
        comboBox_.setBorder(BorderFactory.createTitledBorder("Filter"));
        comboBox_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String threadName = (String)cb.getSelectedItem();
                if(!threadName.equals(ALL_THREAD_SELECT_ITEM))
                    thisWatcher.setFollowedThread(threadName);
                else
                    thisWatcher.setFollowedThread(null);
            }
        });
        panel_.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = c.gridx = 0;
        c.weightx = 1;
        c.weighty = 0;
        panel_.add(comboBox_,c);
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 1;
        c.weighty = 1.0;
        panel_.add(new JScrollPane(table_),c);
    }
}
