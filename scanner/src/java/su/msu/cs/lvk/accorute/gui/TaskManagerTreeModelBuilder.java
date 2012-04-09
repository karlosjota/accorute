package su.msu.cs.lvk.accorute.gui;

import com.truchsess.util.TreeCursor;
import com.truchsess.util.TreeWalker;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 08.04.12
 * Time: 22:57
 * To change this template use File | Settings | File Templates.
 */
public class TaskManagerTreeModelBuilder {
    private TaskManagerTreeModelBuilder(){}
    private static class TreeModelWalker implements TreeWalker.Walk{
        DefaultMutableTreeNode current_;

        public DefaultMutableTreeNode getRoot() {
            return root_;
        }

        DefaultMutableTreeNode root_;
        public TreeModelWalker() {
            current_ = root_ = null;
        }

        public void doDown(TreeCursor treeCursor) throws TreeWalker.AbortProcessingException {
            if(root_ == null){
                root_ = new DefaultMutableTreeNode(treeCursor.getElement());
                current_ = root_;
            }else{
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(treeCursor.getElement());
                current_.add(child);
                current_ = child;
            }
        }

        public void doUp(TreeCursor treeCursor) throws TreeWalker.AbortProcessingException {
            current_ = (DefaultMutableTreeNode) current_.getParent();
        }

        public void doNext(TreeCursor treeCursor) throws TreeWalker.AbortProcessingException {
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(treeCursor.getElement());
            ((DefaultMutableTreeNode) current_.getParent()).add(n);
            current_ = n;
        }
    }
    public static DefaultTreeModel buildModel(final TaskManager taskManager_){
        TreeModelWalker walk = new TreeModelWalker();
        try{
            taskManager_.walkTree(walk);
        }catch(TreeWalker.AbortProcessingException ex ){
        }
        return new DefaultTreeModel(walk.getRoot());
    }


}
