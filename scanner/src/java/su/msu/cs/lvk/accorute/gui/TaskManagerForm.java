package su.msu.cs.lvk.accorute.gui;

import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;
import su.msu.cs.lvk.accorute.utils.Callback0;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;

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
    private JTree roleTree;
    private JButton taskManagerButton;
    private JPanel taskOverviewPane;
    private JButton refreshButton;
    private LogWatchComponent logger_;

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
    void showTaskResults(DefaultMutableTreeNode node){
        Task t = (Task) node.getUserObject();
        TaskVisualiserFactory.TaskVisualiser viewer = null;
        for(TaskVisualiserFactory factory : taskResultViewerFactories){
            //choose the first factory to be able to view results
            viewer = factory.generate(t);
            if(viewer != null)
                break;
        }
        ((JPanel) tabPane.getComponentAt(2)).removeAll();
        if(viewer != null){
            ((JPanel) tabPane.getComponentAt(2)).add(viewer.getComponent());
            tabPane.setSelectedIndex(2);
        }
    }
    void showTaskLogs(DefaultMutableTreeNode node){
        Task t = (Task) node.getUserObject();
        logger_.setFollowedThread(t.getExecutorThreadName());
        tabPane.setSelectedIndex(1);
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
    public void refreshRoleTree(){
        roleTree.setModel(RoleTreeModelBuilder.buildModel());
        expandAll(roleTree, new TreePath(roleTree.getModel().getRoot()), true);
    }
    public TaskManagerForm(final TaskManager taskManager) {
        taskManager_ = taskManager;
        frame = new JFrame("TaskManagerForm");
        frame.setContentPane(this.taskManagerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        taskTree.setToggleClickCount(0);
        taskTree.setCellRenderer(new TaskTreeCellRenderer());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refreshTaskTree(true);
                refreshRoleTree();
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
        ((JPanel) tabPane.getComponentAt(2)).setLayout(new GridLayout(1, 1));
        taskTree.addMouseListener(new TaskTreeMouseHandler());
        taskTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) taskTree.getLastSelectedPathComponent();
                if(node == null) return;
                Object nodeInfo = node.getUserObject();
                if(nodeInfo instanceof Task){
                    Task t = (Task) nodeInfo;
                    TaskVisualiserFactory.TaskVisualiser vis = null;
                    for(TaskVisualiserFactory f: taskOverviewFactories){
                        vis = f.generate(t);
                        if(vis != null)
                            break;
                    }
                    if(vis != null){
                        taskOverviewPane.removeAll();
                        taskOverviewPane.setLayout(new GridLayout(1,1));
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
    }
    public void addLogAppender(LogWatchComponent logger){
        logger_ = logger;
        ((JPanel) tabPane.getComponentAt(1)).setLayout(new GridLayout(1, 1));
        ((JPanel) tabPane.getComponentAt(1)).add(logger_.getComponent());
    }
}
