package su.msu.cs.lvk.accorute.gui;

import com.truchsess.util.TreeCursor;
import com.truchsess.util.TreeWalker;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

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
        private final boolean update_;
        private final DefaultTreeModel model_;

        public DefaultMutableTreeNode getRoot() {
            return root_;
        }

        DefaultMutableTreeNode root_;
        
        public TreeModelWalker() {
            current_ = root_ = null;
            update_ = false;
            model_ = null;
        }
        public TreeModelWalker(DefaultMutableTreeNode oldRoot, DefaultTreeModel model) {
            current_ = null;
            root_ = oldRoot;     
            update_ = true;
            model_ = model;
        }
        private DefaultMutableTreeNode searchChild(DefaultMutableTreeNode node, Object userObject){
            for(int i = 0; i < node.getChildCount(); i++){
                DefaultMutableTreeNode n = (DefaultMutableTreeNode)node.getChildAt(i); 
                if(userObject == n.getUserObject() ){
                    return n;
                }
            }
            return null;
        }
        public void doDown(TreeCursor treeCursor) throws TreeWalker.AbortProcessingException {
            Object userObject = treeCursor.getElement();
            if(current_ == null){
                if(root_ == null || update_ && root_.getUserObject() != userObject){
                    root_ = new DefaultMutableTreeNode(userObject);
                }
                current_ = root_;
            }else{
                DefaultMutableTreeNode child = null;
                if(update_){
                    child = searchChild(current_, userObject);
                    model_.nodeChanged(current_);
                }
                if(child == null){
                    child = new DefaultMutableTreeNode(userObject);
                    current_.add(child);
                    if(model_ != null){
                        int cindex [] = {current_.getChildCount() -1};
                        if(current_.getChildCount() == 1){
                            model_.reload(current_);
                        }
                        model_.nodesWereInserted(current_, cindex);
                    }
                }
                current_ = child;
                model_.nodeChanged(child);
            }
        }

        public void doUp(TreeCursor treeCursor) throws TreeWalker.AbortProcessingException {
            current_ = (DefaultMutableTreeNode) current_.getParent();
        }

        public void doNext(TreeCursor treeCursor) throws TreeWalker.AbortProcessingException {
            Object userObject = treeCursor.getElement();
            DefaultMutableTreeNode n = null;
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) current_.getParent();
            if(update_){
                n = searchChild(parent, userObject);
                model_.nodeChanged(n);
            }
            if(n == null){
                n = new DefaultMutableTreeNode(userObject);
                parent.add(n);
                if(model_ != null){
                    int cindex [] = {parent.getChildCount() -1};
                    model_.nodesWereInserted(parent, cindex);
                    model_.nodeChanged(parent);
                }
            }
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
    public static void rebuildModel(DefaultTreeModel model, final TaskManager taskManager_){
        TreeModelWalker walk = new TreeModelWalker((DefaultMutableTreeNode)model.getRoot(), model);
        try{
            taskManager_.walkTree(walk);
        }catch(TreeWalker.AbortProcessingException ex ){
        }
        DefaultMutableTreeNode newRoot = walk.getRoot();
        if(newRoot != model.getRoot()){
            model.setRoot(newRoot);
        }
    }


}
