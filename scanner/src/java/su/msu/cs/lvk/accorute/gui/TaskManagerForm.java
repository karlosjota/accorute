package su.msu.cs.lvk.accorute.gui;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.RBAC.SimpleRBACRole;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.constants.ActionParameterLocation;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.tasks.*;
import su.msu.cs.lvk.accorute.utils.Callback0;
import su.msu.cs.lvk.accorute.utils.Callback3;
import su.msu.cs.lvk.accorute.utils.Callback4;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 08.04.12
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class TaskManagerForm{
    private JTree taskTree;
    private JPanel taskManagerPanel;
    private JTabbedPane tabPane;
    private JButton taskManagerButton;
    private JPanel taskOverviewPane;
    private JButton refreshButton;
    private JTextField entityIdName;
    private JTextField entityIdValue;
    private JButton entityIdAddButton;
    private JTable entityIdTable;
    private JTable dynamicTokenTable;
    private JTextField dynamicTokenName;
    private JComboBox dynamicTokenLocation;
    private JButton addDynamicToken;
    private JTextField scopeIncludeUrls;
    private JTextField scopeExcludeUrls;
    private JTextField dynamicTokenValue;
    private JButton urlPatternChangeButton;
    private JTextField roleName;
    private JButton roleAddButton;
    private JPanel roleGraphPanel;
    private JComboBox roleSelector;
    private JComboBox roleForUserSelector;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton addUserButton;
    private JPanel logPanel;
    private JPanel taskDetailPanel;
    private JPanel proxyPanel;
    private JTable requestTable;
    private JTabbedPane tabbedPane1;
    private JPanel rawRequestPanel;
    private JPanel decompositedRequestPanel;
    private JTextField ucNameField;
    private JComboBox usColeSelector;
    private JButton addUCButton;
    private JComboBox ucSelectorDepFrom;
    private JComboBox ucSelectorDepTo;
    private JButton addDepButton;
    private JComboBox ucSelectorCancelFrom;
    private JComboBox ucSelectorCancelTo;
    private JButton addCancButton;
    private JPanel ucGraphPanel;
    private JTextPane rawRequestTextPane;
    private JComboBox requestSelector;
    private JPanel useCasesTab;
    private JTable requestDecompositionTable;
    private JTable ucDetailTable;
    private JTable stChActionsTable;
    private JTable stChActionDetails;
    private JComboBox contextSelector;
    private JSpinner formIndex;
    private JTextField submitXpath;
    private JButton authChangeButton;
    private JTextField loginURL;
    private JButton changeMainPage;
    private JTextField mainPageUrl;
    private JButton changeButton1;
    private LogWatchComponent logger_;
    private DefaultMutableTreeNode currentShownResult = null;
    mxGraph roleGraph;
    mxGraph ucGraph;
    private final JFrame frame;

    private TaskManager taskManager_;
    final private ArrayList<TaskVisualiserFactory> taskResultViewerFactories = new ArrayList<TaskVisualiserFactory>();
    final private ArrayList<TaskVisualiserFactory> taskOverviewFactories = new ArrayList<TaskVisualiserFactory>();
    public void addTaskResultViewerFactory(TaskVisualiserFactory f){
        taskResultViewerFactories.add(f);
    }
    public void addtaskOverviewFactory(TaskVisualiserFactory f){
        taskOverviewFactories.add(f);
    }
    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }
    public void showTaskResults(DefaultMutableTreeNode node){
        currentShownResult = node;
        Task t = (Task) node.getUserObject();
        TaskVisualiserFactory.TaskVisualiser viewer = null;
        for(TaskVisualiserFactory factory : taskResultViewerFactories){
            //choose the first factory to be able to view results
            viewer = factory.generate(t);
            if(viewer != null)
                break;
        }
        taskDetailPanel.removeAll();
        if(viewer != null){
            taskDetailPanel.add(viewer.getComponent());
        }
    }
    public void showTaskLogs(DefaultMutableTreeNode node){
        Task t = (Task) node.getUserObject();
        logger_.setFollowedThread(t.getExecutorThreadName());
        tabPane.setSelectedComponent(logPanel);
    }
    public class TaskTreeMouseHandler extends MouseAdapter{
        private void myPopupEvent(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            final JTree tree = (JTree)e.getSource();
            final TreePath path = tree.getPathForLocation(x, y);
            if (path == null)
                return;

            tree.setSelectionPath(path);

            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

            JPopupMenu popup = new JPopupMenu();
            JMenuItem logs = new JMenuItem("show thread in logs");
            logs.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    showTaskLogs(node);
                }
            });
            popup.add(logs);
            JMenuItem results = new JMenuItem("view task results");
            results.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    showTaskResults(node);
                }
            });
            popup.add(results);
            if(node.getUserObject() instanceof HtmlElementActionPerformer){
                JMenuItem parser = new JMenuItem("queue HtmlPageParser run on resulting page");
                final HtmlElementActionPerformer theTask = ((HtmlElementActionPerformer) node.getUserObject());
                parser.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        taskManager_.addTask(new HtmlPageParser(
                                taskManager_,
                                (HtmlPage)theTask.getResult(),
                                theTask.getCtx(),
                                new Callback4<HtmlPage, ArrayList<DomAction>, HttpAction, Boolean>() {
                                    public void CallMeBack(HtmlPage param1, ArrayList<DomAction> param2, HttpAction param3, Boolean param4) {
                                        //do nothing
                                    }
                                }
                        ));
                        showTaskResults(node);
                    }
                });
                popup.add(parser);
            }else if(node.getUserObject() instanceof SitemapCrawler){
                JMenuItem export = new JMenuItem("export sitemap to an image");
                export.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Sitemap sitemap  = (Sitemap) ((SitemapCrawler)node.getUserObject()).getResult();
                                    File temp = File.createTempFile("graph", ".dot");
                                    sitemap.writeToFile(temp.getAbsolutePath(),"");
                                    //File temp2 = File.createTempFile("graph", ".png");
                                    //Runtime.getRuntime().exec("dot " + temp.getAbsolutePath() + " -Tpng -o " + temp2.getAbsolutePath()).waitFor();
                                    JOptionPane.showMessageDialog(frame, "Dot-file saved to " + temp.getAbsolutePath());
                                } catch (IOException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }/* catch (InterruptedException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                } */
                            }
                        }).start();
                    }
                });
                popup.add(export);
            }
            popup.show(tree, x, y);
        }
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) myPopupEvent(e);
            if(e.getClickCount() == 2){
                final JTree tree = (JTree)e.getSource();
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                showTaskResults((DefaultMutableTreeNode) selPath.getLastPathComponent());
            }
        }
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) myPopupEvent(e);
        }

    }
    public void refreshTaskTree(boolean full){

        if(full){
            DefaultTreeModel m = (DefaultTreeModel) taskTree.getModel();
            TreeNode[] nodes = m.getPathToRoot((DefaultMutableTreeNode)taskTree.getLastSelectedPathComponent());

            taskTree.setModel(TaskManagerTreeModelBuilder.buildModel(taskManager_));
            expandAll(taskTree, new TreePath(taskTree.getModel().getRoot()), true);
            if(nodes != null){
                ArrayList<TreeNode> treeNodes = new ArrayList<TreeNode>();
                treeNodes.add((TreeNode)taskTree.getModel().getRoot());
                for(int i = 0; i< nodes.length-1; i++){
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes[i];
                    treeNodes.add(treeNodes.get(i).getChildAt(node.getIndex(nodes[i+1])));
                }
                taskTree.setSelectionPath(new TreePath(treeNodes.toArray()));
            }
        }else{
            TaskManagerTreeModelBuilder.rebuildModel((DefaultTreeModel)  taskTree.getModel(), taskManager_);
        }

        switch (taskManager_.getStatus()) {
            case NOT_STARTED:
                taskManagerButton.setText("Start");
                break;
            case RUNNING:
                taskManagerButton.setText("Pause");
                break;
            case PAUSED:
                taskManagerButton.setText("Resume");
                break;
        }

    }
    public TaskManagerForm(final TaskManager taskManager) {
        taskManager_ = taskManager;
        final TaskManagerForm theForm = this;
        tabPane.addChangeListener(new ChangeListener() {
            // This method is called whenever the selected tab changes
            public void stateChanged(ChangeEvent evt) {
                JTabbedPane pane = (JTabbedPane)evt.getSource();
                // Get current tab
                if(pane.getSelectedIndex() == 2){
                    if(currentShownResult != null){
                        theForm.showTaskResults(currentShownResult);
                    }
                }
            }
        });
        frame = new JFrame("TaskManagerForm");
        frame.setContentPane(this.taskManagerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        refreshContexts();
        taskTree.setToggleClickCount(0);
        taskTree.setCellRenderer(new TaskTreeCellRenderer());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refreshTaskTree(true);
                taskManagerButton.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        taskManager_.toggle();
                    }
                });
            }
        });
        taskTree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        taskManager_.addTaskTreeChangeListener(new Callback0() {
            public void CallMeBack() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        refreshTaskTree(false);
                    }
                });
            }
        });
        taskDetailPanel.setLayout(new GridLayout(1, 1));
        taskTree.addMouseListener(new TaskTreeMouseHandler());
        taskTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) taskTree.getLastSelectedPathComponent();
                if (node == null) return;
                Object nodeInfo = node.getUserObject();
                if (nodeInfo instanceof Task) {
                    Task t = (Task) nodeInfo;
                    TaskVisualiserFactory.TaskVisualiser vis = null;
                    for (TaskVisualiserFactory f : taskOverviewFactories) {
                        vis = f.generate(t);
                        if (vis != null)
                            break;
                    }
                    if (vis != null) {
                        taskOverviewPane.removeAll();
                        taskOverviewPane.setLayout(new GridLayout(1, 1));
                        taskOverviewPane.add(vis.getComponent());
                        taskOverviewPane.revalidate();
                    }
                }
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTaskTree(true);
            }
        });

        entityIdTable.setModel(new AbstractTableModel() {
            {
                Callback0 theCallback = new Callback0() {
                    public void CallMeBack() {
                        ((AbstractTableModel) entityIdTable.getModel()).fireTableDataChanged();
                    }
                };
                WebAppProperties.getInstance().getIdParamNameRegexList().addCallback(theCallback);
                WebAppProperties.getInstance().getIdParamValueRegexList().addCallback(theCallback);
            }
            public int getRowCount() {
                return WebAppProperties.getInstance().getIdParamNameRegexList().size();
            }

            public int getColumnCount() {
                return 2;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex >= WebAppProperties.getInstance().getIdParamNameRegexList().size())
                    return "";
                switch (columnIndex) {
                    case 0:
                        return WebAppProperties.getInstance().getIdParamNameRegexList().get(rowIndex);
                    case 1:
                        return WebAppProperties.getInstance().getIdParamValueRegexList().get(rowIndex);
                    default:
                        return "";
                }
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Pattern.class;
                    case 1:
                        return Pattern.class;
                    default:
                        return String.class;
                }
            }
            @Override
            public String getColumnName(int column) {
                switch (column) {
                    case 0:
                        return "Name regex";
                    case 1:
                        return "Value regex";
                    default:
                        return "";
                }
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (rowIndex <= WebAppProperties.getInstance().getIdParamNameRegexList().size()) {
                    switch (columnIndex) {
                        case 0:
                            try{
                                WebAppProperties.getInstance().getIdParamNameRegexList().set(rowIndex, Pattern.compile((String) aValue));
                            }catch (PatternSyntaxException ex ){
                                theForm.displayError("Invalid regex for id parameter name");
                            }
                            return;
                        case 1:
                            try{
                                WebAppProperties.getInstance().getIdParamValueRegexList().set(rowIndex, Pattern.compile((String) aValue));
                            }catch (PatternSyntaxException ex ){
                                theForm.displayError("Invalid regex for id parameter value");
                            }
                            return;
                        default:
                            return;
                    }
                }
                super.setValueAt(aValue, rowIndex, columnIndex);
            }
        });
        TableColumn col = entityIdTable.getColumnModel().getColumn(0);
        col.setCellEditor(new DefaultCellEditor(new JTextField()));
        col = entityIdTable.getColumnModel().getColumn(1);
        col.setCellEditor(new DefaultCellEditor(new JTextField()));
        dynamicTokenTable.setModel(new AbstractTableModel() {
            {
                WebAppProperties.getInstance().getDynamicTokens().addCallback(new Callback0() {
                    public void CallMeBack() {
                        ((AbstractTableModel) dynamicTokenTable.getModel()).fireTableDataChanged();
                    }
                });
            }

            public int getRowCount() {
                return WebAppProperties.getInstance().getDynamicTokens().size();
            }

            public int getColumnCount() {
                return 3;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex >= WebAppProperties.getInstance().getDynamicTokens().size())
                    return "";
                switch (columnIndex) {
                    case 0:
                        return WebAppProperties.getInstance().getDynamicTokens().get(rowIndex).getName();
                    case 1:
                        return WebAppProperties.getInstance().getDynamicTokens().get(rowIndex).getValue();
                    case 2:
                        return WebAppProperties.getInstance().getDynamicTokens().get(rowIndex).getLocation();
                    default:
                        return "";
                }
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return ActionParameterLocation.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public String getColumnName(int column) {
                switch (column) {
                    case 0:
                        return "Name regex";
                    case 1:
                        return "Value regex";
                    case 2:
                        return "Location";
                    default:
                        return "";
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (rowIndex <= WebAppProperties.getInstance().getDynamicTokens().size()) {
                    ActionParameter oldParam = WebAppProperties.getInstance().getDynamicTokens().get(rowIndex);
                    ActionParameter newParam = new ActionParameter(
                            oldParam.getName(),
                            oldParam.getValue(),
                            oldParam.getLocation(),
                            oldParam.getMeaning(),
                            oldParam.getDatatype(),
                            oldParam.getRole()
                    );
                    switch (columnIndex) {
                        case 0:
                            try{
                                Pattern.compile((String)aValue);
                                newParam = new ActionParameter(
                                        (String) aValue,
                                        oldParam.getValue(),
                                        oldParam.getLocation(),
                                        oldParam.getMeaning(),
                                        oldParam.getDatatype(),
                                        oldParam.getRole()
                                );
                            }catch (PatternSyntaxException ex ){
                                theForm.displayError("Invalid regex for dynamic token name");
                            }
                            break;
                        case 1:
                            try{
                                Pattern.compile((String)aValue);
                                newParam = new ActionParameter(
                                        oldParam.getName(),
                                        (String) aValue,
                                        oldParam.getLocation(),
                                        oldParam.getMeaning(),
                                        oldParam.getDatatype(),
                                        oldParam.getRole()
                                );
                            }catch (PatternSyntaxException ex ){
                                theForm.displayError("Invalid regex for dynamic token value");
                            }
                            break;
                        case 2:
                            newParam = new ActionParameter(
                                    oldParam.getName(),
                                    oldParam.getValue(),
                                    (ActionParameterLocation) aValue,
                                    oldParam.getMeaning(),
                                    oldParam.getDatatype(),
                                    oldParam.getRole()
                            );
                            break;
                    }
                    WebAppProperties.getInstance().getDynamicTokens().set(rowIndex, newParam);
                }
                super.setValueAt(aValue, rowIndex, columnIndex);
            }
        });
        TableColumn column = dynamicTokenTable.getColumnModel().getColumn(2);
        Object [] locations = ActionParameterLocation.values();
        column.setCellEditor(new DefaultCellEditor(new JComboBox(new DefaultComboBoxModel(locations))));
        dynamicTokenLocation.setModel(new DefaultComboBoxModel(locations));
        addDynamicToken.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = dynamicTokenName.getText();
                String value = dynamicTokenValue.getText();
                ActionParameterLocation location = (ActionParameterLocation) dynamicTokenLocation.getSelectedItem();
                try{
                    Pattern.compile(name);
                }catch (PatternSyntaxException ex ){
                    theForm.displayError("Invalid regex for dynamic token name");
                    return;
                }
                try{
                    Pattern.compile(value);
                }catch (PatternSyntaxException ex ){
                    theForm.displayError("Invalid regex for dynamic token value");
                    return;
                }
                WebAppProperties.getInstance().addDynamicToken(name, value, location);
            }
        });
        entityIdAddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Pattern name, value;
                try{
                    name = Pattern.compile(entityIdName.getText());
                }catch (PatternSyntaxException ex ){
                    theForm.displayError("Invalid regex for entity id name");
                    return;
                }
                try{
                    value = Pattern.compile(entityIdValue.getText());
                }catch (PatternSyntaxException ex ){
                    theForm.displayError("Invalid regex for entity id value");
                    return;
                }
                WebAppProperties.getInstance().getIdParamNameRegexList().add(name);
                WebAppProperties.getInstance().getIdParamValueRegexList().add(value);
            }
        });
        final Callback0 scopeCallback = new Callback0() {
            public void CallMeBack() {
                refreshScope();
            }
        };
        refreshScope();
        WebAppProperties.getInstance().getUrlIncludeScopeNotifier().addCallback(scopeCallback);
        WebAppProperties.getInstance().getUrlIncludeScopeNotifier().addCallback(scopeCallback);
        WebAppProperties.getInstance().getMainPageNotifier().addCallback(scopeCallback);
        urlPatternChangeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    WebAppProperties.getInstance().setUrlExcludeScope(Pattern.compile(scopeExcludeUrls.getText()));
                } catch (PatternSyntaxException ex) {
                    displayError("Invalid URL exclude scope regex");
                }
                try {
                    WebAppProperties.getInstance().setUrlIncludeScope(Pattern.compile(scopeIncludeUrls.getText()));
                } catch (PatternSyntaxException ex) {
                    displayError("Invalid URL include scope regex");
                }
            }
        });
        roleGraph = new mxGraph(){
            @Override
            public String convertValueToString(Object cell) {
                Object o  = ((mxCell)cell).getValue();
                if(o instanceof Role){
                    return ((Role)o).getRoleName();
                }
                if(o instanceof WebAppUser){
                    WebAppUser u = (WebAppUser) o;
                    String res = "";
                    for(Map.Entry<String,String> entry: u.getStaticCredentials().entrySet()){
                        res +=entry.getKey()+":<b>" + entry.getValue()+"</b><br>";
                    }
                    return res;
                }
                return super.convertValueToString(cell);    //To change body of overridden methods use File | Settings | File Templates.
            }
        };
        mxConstants.DEFAULT_FONTSIZE = 12;
        roleGraph.setHtmlLabels(true);
        roleGraph.setCellsBendable(false);
        roleGraph.setCellsCloneable(false);
        roleGraph.setCellsEditable(false);
        roleGraph.setCellsDisconnectable(false);
        roleGraph.setCellsResizable(false);
        roleGraph.setCellsDisconnectable(false);
        roleGraph.setAllowDanglingEdges(false);
        roleGraph.setCellsMovable(false);
        roleGraph.setCellsDeletable(false);
        roleGraph.setConnectableEdges(false);
        roleGraph.setDropEnabled(false);
        roleGraph.setSplitEnabled(false);
        roleGraph.setCellsLocked(true);
        roleGraph.setEnabled(false);
        refreshRolesAndUsers();
        WebAppProperties.getInstance().getRoles().addCallback(new Callback0() {
            public void CallMeBack() {
                refreshRolesAndUsers();
            }
        });
        mxGraphComponent graphComponent = new mxGraphComponent(roleGraph);

        roleGraphPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = gridBagConstraints.weighty = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        roleGraphPanel.add(new JScrollPane(graphComponent), gridBagConstraints);
        roleAddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newRoleName = roleName.getText();
                for (Role r : WebAppProperties.getInstance().getRoles()) {
                    if (r.getRoleName().equals(newRoleName))
                        return;
                }
                Role parent = (Role) roleSelector.getSelectedItem();
                Role newRole;
                if (parent == null) {
                    newRole = SimpleRBACRole.createRootRole(newRoleName);
                } else {
                    newRole = parent.addChildRole(newRoleName);
                }
                WebAppProperties.getInstance().getRoles().add(newRole);
            }
        });
        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String uname = usernameField.getText();
                String passw = passwordField.getText();
                Role theRole = (Role) roleForUserSelector.getSelectedItem();
                WebAppUser user = new WebAppUser();
                user.getStaticCredentials().put("log", uname);
                user.getStaticCredentials().put("password", passw);
                user.setRole(theRole);
                WebAppProperties.getInstance().getUserService().addOrModifyUser(user);
                refreshRolesAndUsers();
            }
        });
        requestSelector.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null && value instanceof Conversation) {
                    Conversation conv = (Conversation) value;
                    String url = conv.getRequest().getURL().toString();
                    if (url.length() > 60)
                        url = url.substring(0, 30) + "<...>" + url.substring(url.length() - 31, url.length() - 1);
                    setText(
                            "#" + conv.getId().toString() +
                                    " " + conv.getRequest().getMethod() + " "
                                    + url
                    );
                }
                return this;
            }
        });
        contextSelector.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ((AbstractTableModel) requestTable.getModel()).fireTableDataChanged();
            }
        });
        requestTable.setModel(new AbstractTableModel() {
            {
                WebAppProperties.getInstance().getConversationService().addCallback(new Callback0() {
                    public void CallMeBack() {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                ((AbstractTableModel) requestTable.getModel()).fireTableDataChanged();
                                EntityID contextId = ((UserContext)contextSelector.getSelectedItem()).getContextID();
                                Collection<Conversation> convs = WebAppProperties.getInstance().getConversationService().getContextConversations(contextId);
                                if(convs != null){
                                    int i = requestSelector.getSelectedIndex();
                                    requestSelector.setModel(new DefaultComboBoxModel(convs.toArray()));
                                    requestSelector.setSelectedIndex(i);
                                }
                            }
                        });
                    }
                });
            }

            String columns[] = {
                    "ID","Actor", "Method", "Url", "Cookies"
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            public int getRowCount() {
                if(contextSelector.getSelectedItem() == null)
                    return 0;
                EntityID contextID = ((UserContext)contextSelector.getSelectedItem()).getContextID();
                Collection<Conversation> convs = WebAppProperties.getInstance().getConversationService().getContextConversations(contextID);
                if (convs == null)
                    return 0;
                return WebAppProperties.getInstance().getConversationService().getContextConversations(contextID).size();
            }

            public int getColumnCount() {
                return columns.length;
            }

            @Override
            public String getColumnName(int column) {
                return columns[column];
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                EntityID userID = ((UserContext)contextSelector.getSelectedItem()).getUserID();
                WebAppUser u = WebAppProperties.getInstance().getUserService().getUserByID(userID);

                EntityID contextID = ((UserContext)contextSelector.getSelectedItem()).getContextID();
                List<Conversation> convs = WebAppProperties.getInstance().getConversationService().getContextConversations(contextID);
                if (convs == null || rowIndex >= convs.size())
                    return "";
                Conversation conv = convs.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return conv.getId();
                    case 1:
                        if(u != null && u.getStaticCredentials().containsKey("log")){
                            return u.getStaticCredentials().get("log") + "{" + contextID.toString() + "}";
                        }else{
                            return "{" + contextID.toString() + "}";
                        }
                    case 2:
                        return conv.getRequest().getMethod();
                    case 3:
                        return conv.getRequest().getURL();
                    case 4:
                        return conv.getRequest().getHeader("Cookie");
                }
                return "";
            }
        });
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;
                int num = ((ListSelectionModel) e.getSource()).getMinSelectionIndex();
                EntityID contextID = ((UserContext)contextSelector.getSelectedItem()).getContextID();
                rawRequestTextPane.setText(WebAppProperties.getInstance().getConversationService().getContextConversations(contextID).get(num).getRequest().toString());
                ((AbstractTableModel) requestDecompositionTable.getModel()).fireTableDataChanged();
            }
        });
        requestTable.addMouseListener(new MouseAdapter() {
            private void myPopupEvent(MouseEvent e) {
                if (e.getSource() != requestTable)
                    return;
                int x = e.getX();
                int y = e.getY();
                requestTable.getComponentAt(x, y);
                final int selected = requestTable.rowAtPoint(e.getPoint());
                requestTable.getSelectionModel().setSelectionInterval(selected, selected);
                JPopupMenu popup = new JPopupMenu();
                JMenuItem ucSelect = new JMenuItem("select in useCase tab");
                ucSelect.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        requestSelector.setSelectedIndex(selected);
                        tabPane.setSelectedComponent(useCasesTab);
                    }
                });
                popup.add(ucSelect);
                JMenuItem makeStateChanging = new JMenuItem("mark as state-changing");
                makeStateChanging.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        EntityID proxyContext = ((UserContext)contextSelector.getSelectedItem()).getContextID();
                        Request r = WebAppProperties.getInstance().getConversationService().getContextConversations(proxyContext).get(selected).getRequest();
                        //TODO: ask for action name
                        HttpAction act = new HttpAction("", WebAppProperties.getInstance().getRcd().decompose(r.genWebRequest()));
                        WebAppProperties.getInstance().addStateChangingAction(act);
                    }
                });
                popup.add(makeStateChanging);
                popup.show(requestTable, x, y);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) myPopupEvent(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) myPopupEvent(e);
            }
        });
        ucGraph = new mxGraph(){
            @Override
            public String convertValueToString(Object cell) {
                Object o  = ((mxCell)cell).getValue();
                if(o instanceof UseCase){
                    return "<b>"+((UseCase)o).getHttpAct().getName()+"</b> by "+ ((UseCase)o).getUserRole().getRoleName();
                }
                return super.convertValueToString(cell);    //To change body of overridden methods use File | Settings | File Templates.
            }
        };
        ucGraph.setHtmlLabels(true);
        ucGraph.setCellsBendable(false);
        ucGraph.setCellsCloneable(false);
        ucGraph.setCellsEditable(false);
        ucGraph.setCellsDisconnectable(false);
        ucGraph.setCellsResizable(false);
        ucGraph.setCellsDisconnectable(false);
        ucGraph.setAllowDanglingEdges(false);
        ucGraph.setCellsMovable(false);
        ucGraph.setCellsDeletable(false);
        ucGraph.setConnectableEdges(false);
        ucGraph.setDropEnabled(false);
        ucGraph.setSplitEnabled(false);
        ucGraph.setCellsLocked(true);
        ucGraph.setEnabled(false);
        refreshUseCases();
        WebAppProperties.getInstance().getUcGraph().addCallback(new Callback0() {
            public void CallMeBack() {
                refreshUseCases();
            }
        });
        mxGraphComponent ucGraphComponent = new mxGraphComponent(ucGraph);
        ucGraphPanel.setLayout(new GridBagLayout());
        ucGraphPanel.add(new JScrollPane(ucGraphComponent), gridBagConstraints);
        addUCButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String actionName = ucNameField.getText();
                Role role = (Role) usColeSelector.getSelectedItem();
                Conversation conv = (Conversation) requestSelector.getSelectedItem();
                List<ActionParameter> params = WebAppProperties.getInstance().getRcd().decompose(conv.getRequest().genWebRequest());
                HttpAction act = new HttpAction(actionName, params);
                WebAppProperties.getInstance().getUcGraph().addUC(new UseCase(role, act));
                WebAppProperties.getInstance().addStateChangingAction(act);
            }
        });
        addDepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UseCase from = (UseCase) ucSelectorDepFrom.getSelectedItem();
                UseCase to = (UseCase) ucSelectorDepTo.getSelectedItem();
                WebAppProperties.getInstance().getUcGraph().addDependency(from, to);
            }
        });
        addCancButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UseCase from = (UseCase) ucSelectorCancelFrom.getSelectedItem();
                UseCase to = (UseCase) ucSelectorCancelTo.getSelectedItem();
                WebAppProperties.getInstance().getUcGraph().addCancellation(from, to);
            }
        });
        requestDecompositionTable.setModel(new AbstractTableModel() {

            Request currentRequest;
            List<ActionParameter> currentParams;

            public int getRowCount() {
                int num = requestTable.getSelectionModel().getMinSelectionIndex();
                if (num == -1)
                    return 0;
                EntityID proxyContext = ((UserContext)contextSelector.getSelectedItem()).getContextID();
                Request r = WebAppProperties.getInstance().getConversationService().getContextConversations(proxyContext).get(num).getRequest();
                if (currentRequest != r) {
                    currentRequest = r;
                    currentParams = WebAppProperties.getInstance().getRcd().decompose(r.genWebRequest());
                }
                return currentParams.size();
            }
            private String[] columns = {
                    "name", "value", "location", "meaning", "datatype"
            };
            public int getColumnCount() {
                return columns.length;
            }

            @Override
            public String getColumnName(int column) {
                return columns[column];
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                int num = requestTable.getSelectionModel().getMinSelectionIndex();
                EntityID proxyContext = ((UserContext)contextSelector.getSelectedItem()).getContextID();;
                Request r = WebAppProperties.getInstance().getConversationService().getContextConversations(proxyContext).get(num).getRequest();
                if (currentRequest != r) {
                    currentRequest = r;
                    currentParams = WebAppProperties.getInstance().getRcd().decompose(r.genWebRequest());
                }
                ActionParameter param = currentParams.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return param.getName();
                    case 1:
                        return param.getValue();
                    case 2:
                        return param.getLocation();
                    case 3:
                        return param.getMeaning();
                    case 4:
                        return param.getDatatype();
                }
                return "";
            }
        });
        ucDetailTable.setModel(new AbstractTableModel() {
            private String [] columns = {
                    "name", "value", "location", "meaning", "datatype"
            };
            public int getRowCount() {
                Object sel = ucGraph.getSelectionCell();
                if(sel == null)
                    return 0;
                mxCell selected = ((mxCell)sel);
                if(!selected.isVertex())
                    return 0;
                UseCase uc = (UseCase) selected.getValue();
                return  uc.getHttpAct().getActionParameters().size();
            }
            public int getColumnCount() {
                return columns.length;
            }

            @Override
            public String getColumnName(int column) {
                return columns[column];
            }
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object sel = ucGraph.getSelectionCell();
                if(sel == null)
                    return 0;
                mxCell selected = ((mxCell)sel);
                if(!selected.isVertex())
                    return 0;
                UseCase uc = (UseCase) selected.getValue();
                ActionParameter param = uc.getHttpAct().getActionParameters().get(rowIndex);
                switch (columnIndex){
                    case 0:
                        return param.getName();
                    case 1:
                        return param.getValue();
                    case 2:
                        return param.getLocation();
                    case 3:
                        return param.getMeaning();
                    case 4:
                        return param.getDatatype();
                }
                return "";
            }
        });
        ucGraph.getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
            public void invoke(Object o, mxEventObject mxEventObject) {
                ((AbstractTableModel) ucDetailTable.getModel()).fireTableDataChanged();
            }
        });
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem saveConf = new JMenuItem("Save configuration");
        JMenuItem loadConf = new JMenuItem("Load configuration");
        fileMenu.add(loadConf);
        fileMenu.add(saveConf);
        saveConf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    final Task tsk = new ConfigurationSaver(taskManager, file);
                    tsk.registerCallback(new Callback0() {
                        public void CallMeBack() {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if(tsk.isSuccessful()){
                                        JOptionPane.showMessageDialog(frame, "Configuration saving finished normally");
                                    }else {
                                        theForm.displayError("Configuration saving finished with errors, check the logs");
                                    }
                                }
                            });
                        }
                    });
                    taskManager.addTask(tsk);
                }
            }
        });
        loadConf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    final Task tsk = new ConfigurationLoader(taskManager, file);
                    tsk.registerCallback(new Callback0() {
                        public void CallMeBack() {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (tsk.isSuccessful()) {
                                        JOptionPane.showMessageDialog(frame, "Configuration loading finished normally");
                                    } else {
                                        theForm.displayError("Configuration loading finished with errors, check the logs");
                                    }
                                }
                            });
                        }
                    });
                    taskManager.addTask(tsk);
                }
            }
        });
        JMenu analysisMenu = new JMenu("Analysis");

        JMenuItem checkConf = new JMenuItem("Check configuration");
        checkConf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    if(WebAppProperties.getInstance().checkSettings()){
                        JOptionPane.showMessageDialog(frame, "Configuration is OK!");
                    }
                }catch (IllegalStateException ex){
                    theForm.displayError("Configuration incomplete: " + ex.getMessage());
                }
            }
        });
        analysisMenu.add(checkConf);
        JMenuItem run = new JMenuItem("Run accorute");
        run.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                taskManager.addTask(new FullAnalysisTask(taskManager));
            }
        });
        analysisMenu.add(run);
        JMenuItem loadPage = new JMenuItem("Load a page");
        loadPage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<UserContext> contexts = new ArrayList<UserContext>();
                for(Role r: WebAppProperties.getInstance().getRoles()){
                    for(WebAppUser u:WebAppProperties.getInstance().getUserService().getUsersByRole(r.getRoleName())){
                        contexts.addAll(WebAppProperties.getInstance().getContextService().getContextsByUserID(u.getUserID()));
                    }
                }
                if(contexts.size() == 0){
                    displayError("You must create at least one context!");
                    return;
                }
                Object choice = JOptionPane.showInputDialog(
                        frame,
                        "Choose the context",
                        "Choose the context",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        contexts.toArray(),
                        contexts.get(0)
                );
                if(choice == null){
                    return;
                }
                UserContext ctx = (UserContext) choice;
                choice = JOptionPane.showInputDialog(
                        frame,
                        "Enter the page URL",
                        "Enter the page URL",
                        JOptionPane.PLAIN_MESSAGE
                );
                if(choice == null){
                    return;
                }
                String url = (String) choice;
                URL theUrl;
                try{
                    theUrl = new URL(url);
                } catch (MalformedURLException e1) {
                    displayError("Invalid URL!");
                    return;
                }
                taskManager.addTask(new HtmlElementActionPerformer(
                        taskManager,
                        theUrl,
                        ctx.getContextID(),
                        new Callback3<ArrayList<Conversation>, ArrayList<HttpAction>, HtmlPage>() {
                            public void CallMeBack(ArrayList<Conversation> param1, ArrayList<HttpAction> param2, HtmlPage param3) {
                                //nothing
                            }
                        }));
            }
        });
        analysisMenu.add(loadPage);
        JMenuItem createCtx = new JMenuItem("Create a fresh context and log in");
        createCtx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<WebAppUser> users = new ArrayList<WebAppUser>();
                for(Role r: WebAppProperties.getInstance().getRoles()){
                    users.addAll(WebAppProperties.getInstance().getUserService().getUsersByRole(r.getRoleName()));
                }
                if(users.size() == 0){
                    displayError("You must create at least one user!");
                }
                Object choice = JOptionPane.showInputDialog(
                        frame,
                        "Choose user",
                        "Choose user",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        users.toArray(),
                        users.get(0)
                );
                if(choice == null)
                    return;
                WebAppUser theUser = (WebAppUser) choice;
                UserContext context = new UserContext();
                context.setUserID(theUser.getUserID());
                WebAppProperties.getInstance().getContextService().addOrUpdateContext(context);
                JOptionPane.showMessageDialog(frame, "You created context {"+context.getContextID()+"}");
                if(!theUser.getUserRole().getRoleName().equals("public")){
                    final Task loginTask = WebAppProperties.getInstance().getAuthTaskFactory().genContextedTask(context.getContextID(), taskManager);
                    taskManager.addTask(loginTask);
                }
            }
        });
        analysisMenu.add(createCtx);
        menuBar.add(analysisMenu);
        frame.setJMenuBar(menuBar);
        WebAppProperties.getInstance().getStateChangingHttpActions().addCallback(new Callback0() {
            public void CallMeBack() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ((AbstractTableModel) stChActionsTable.getModel()).fireTableDataChanged();
                    }
                });
            }
        });
        stChActionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stChActionsTable.setModel(new AbstractTableModel() {
            private String[] columns = {
                    "#", "name", "method", "URL"
            };

            public int getColumnCount() {
                return columns.length;
            }

            @Override
            public String getColumnName(int column) {
                return columns[column];
            }

            public int getRowCount() {
                return WebAppProperties.getInstance().getStateChangingHttpActions().size();
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                HttpAction act = WebAppProperties.getInstance().getStateChangingHttpActions().get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return rowIndex;
                    case 1:
                        return act.getName();
                    case 2:
                        List<ActionParameter> params = act.getActionParameters();
                        boolean post = false;
                        for (ActionParameter p : params) {
                            if (p.getLocation().equals(ActionParameterLocation.BODY)) {
                                post = true;
                            }
                        }
                        return post ? "POST" : "GET";
                    case 3:
                        return WebAppProperties.getInstance().getRcd().getURL(act.getActionParameters());
                }
                return "";
            }
        });
        stChActionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ((AbstractTableModel) stChActionDetails.getModel()).fireTableDataChanged();
            }
        });
        stChActionDetails.setModel(new AbstractTableModel() {
            private String[] columns = {
                    "name", "value", "location", "meaning", "datatype"
            };

            public int getRowCount() {
                int index = stChActionsTable.getSelectionModel().getMinSelectionIndex();
                if (index == -1)
                    return 0;
                HttpAction act = WebAppProperties.getInstance().getStateChangingHttpActions().get(index);
                return act.getActionParameters().size();
            }

            public int getColumnCount() {
                return columns.length;
            }

            @Override
            public String getColumnName(int column) {
                return columns[column];
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                int index = stChActionsTable.getSelectionModel().getMinSelectionIndex();
                HttpAction act = WebAppProperties.getInstance().getStateChangingHttpActions().get(index);
                ActionParameter param = act.getActionParameters().get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return param.getName();
                    case 1:
                        return param.getValue();
                    case 2:
                        return param.getLocation();
                    case 3:
                        return param.getMeaning();
                    case 4:
                        return param.getDatatype();
                }
                return "";
            }
        });
        WebAppProperties.getInstance().getContextService().addCallback(new Callback0() {
            public void CallMeBack() {
                refreshContexts();
            }
        });
        contextSelector.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ((AbstractTableModel) requestTable.getModel()).fireTableDataChanged();
                ((AbstractTableModel) requestDecompositionTable.getModel()).fireTableDataChanged();
            }
        });
        refreshAuthSettings();
        WebAppProperties.getInstance().getAuthTaskFactoryNotifier().addCallback(new Callback0() {
            public void CallMeBack() {
                refreshAuthSettings();
            }
        });

        authChangeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    URL url = new URL(loginURL.getText());
                    int f_idx = (Integer)formIndex.getValue();
                    if(f_idx <0 ){
                        theForm.displayError("Form index cannot be negative");
                        return;
                    }
                    String submit_xpath = submitXpath.getText();
                    WebAppProperties.getInstance().setAuthTaskFactory(new FormBasedAuthTaskFactory(url, f_idx, submit_xpath));
                } catch (MalformedURLException e1) {
                    theForm.displayError("Malformed login URL!");
                }
            }
        });

        changeMainPage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    WebAppProperties.getInstance().setMainPage(new URL(mainPageUrl.getText()));
                } catch (MalformedURLException e1) {
                    theForm.displayError("Main page URL is malformed!");
                }
            }
        });
    }

    private void refreshScope() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scopeIncludeUrls.setText(WebAppProperties.getInstance().getUrlIncludeScope().toString());
                scopeExcludeUrls.setText(WebAppProperties.getInstance().getUrlExcludeScope().toString());
                mainPageUrl.setText(WebAppProperties.getInstance().getMainPage().toString());
            }
        });
    }
    public void refreshAuthSettings(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ContextedTaskFactory fact = WebAppProperties.getInstance().getAuthTaskFactory();
                if(fact instanceof FormBasedAuthTaskFactory){
                    FormBasedAuthTaskFactory f = (FormBasedAuthTaskFactory) fact;
                    loginURL.setText(f.getUrl().toString());
                    formIndex.setValue(f.getFormIndex());
                    submitXpath.setText(f.getSubmitXPath());
                }
            }
        });
    }
    public void refreshContexts(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                List<UserContext> contexts = new ArrayList<UserContext>();
                for(Role r: WebAppProperties.getInstance().getRoles()){
                    for(WebAppUser u:WebAppProperties.getInstance().getUserService().getUsersByRole(r.getRoleName())){
                        contexts.addAll(WebAppProperties.getInstance().getContextService().getContextsByUserID(u.getUserID()));
                    }
                }
                UserContext proxyContext = new UserContext();
                proxyContext.setContextID(WebAppProperties.getInstance().getProxyContext());
                contexts.add(proxyContext);
                int i = contextSelector.getSelectedIndex();
                if(i == -1)
                    i = 0;
                contextSelector.setModel(new DefaultComboBoxModel(contexts.toArray()));
                contextSelector.setSelectedIndex(i);
            }
        });
    }
    public void refreshUseCases(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UseCaseGraphModelBuilder.makeGraph(ucGraph);
                Object [] ucases = WebAppProperties.getInstance().getUcGraph().getUseCases().toArray();
                int i;
                i = ucSelectorDepFrom.getSelectedIndex(); ucSelectorDepFrom.setModel(new DefaultComboBoxModel(ucases));ucSelectorDepFrom.setSelectedIndex(i);
                i = ucSelectorDepTo.getSelectedIndex(); ucSelectorDepTo.setModel(new DefaultComboBoxModel(ucases));ucSelectorDepTo.setSelectedIndex(i);
                i = ucSelectorCancelFrom.getSelectedIndex(); ucSelectorCancelFrom.setModel(new DefaultComboBoxModel(ucases));ucSelectorCancelFrom.setSelectedIndex(i);
                i = ucSelectorCancelTo.getSelectedIndex(); ucSelectorCancelTo.setModel(new DefaultComboBoxModel(ucases));ucSelectorCancelTo.setSelectedIndex(i);
            }
        });
    }
    public void refreshRolesAndUsers(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RoleGraphModelBuilder.makeGraph(roleGraph);
                Object [] roles = WebAppProperties.getInstance().getRoles().toArray();
                int i;
                i = roleSelector.getSelectedIndex();
                if(i == -1 && roles.length != 0)
                    i = 0;
                roleSelector.setModel(new DefaultComboBoxModel(roles));roleSelector.setSelectedIndex(i);
                i = roleForUserSelector.getSelectedIndex();
                if(i == -1 && roles.length != 0)
                    i = 0;
                roleForUserSelector.setModel(new DefaultComboBoxModel(roles));roleForUserSelector.setSelectedIndex(i);
                i = usColeSelector.getSelectedIndex();
                if(i == -1 && roles.length != 0)
                    i = 0;
                usColeSelector.setModel(new DefaultComboBoxModel(roles));usColeSelector.setSelectedIndex(i);
            }
        });
    }
    public void displayError(final String what){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(frame, what, "Error",JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    public void addLogAppender(LogWatchComponent logger){
        logger_ = logger;
        logPanel.setLayout(new GridLayout(1, 1));
        logPanel.add(logger_.getComponent());
    }
}