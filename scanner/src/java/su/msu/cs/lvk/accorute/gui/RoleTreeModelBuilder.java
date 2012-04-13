package su.msu.cs.lvk.accorute.gui;

import com.truchsess.util.TreeCursor;
import com.truchsess.util.TreeWalker;
import org.apache.commons.lang.StringUtils;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.UserContext;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 08.04.12
 * Time: 22:57
 * To change this template use File | Settings | File Templates.
 */
public class RoleTreeModelBuilder {
    private RoleTreeModelBuilder(){}
    private static void walkTree(DefaultMutableTreeNode curNode, Role curRole){
        enumUsers(curNode, curRole);
        for(Role childRole: curRole.getChildRoles()){
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("ROLE " + childRole.getRoleName());
            curNode.add(childNode);
            walkTree(childNode, childRole);
        }
    }
    private static void enumUsers(DefaultMutableTreeNode curNode, Role curRole){
        List<WebAppUser> users = WebAppProperties.getInstance().getUserService().getUsersByRole(curRole.getRoleName());
        for(WebAppUser u: users){
            List<UserContext> ctx = WebAppProperties.getInstance().getContextService().getContextsByUserID(u.getUserID());
            List<String> ctxList = new ArrayList<String>();
            for(UserContext c: ctx){
                ctxList.add(c.getContextID().toString());
            }
            curNode.add(new DefaultMutableTreeNode("USER "  + u.getStaticCredentials() + " [" + StringUtils.join(ctxList,", ") + "]"));
        }
    }
    public static DefaultTreeModel buildModel(){
        List<Role> roles = WebAppProperties.getInstance().getRoles();
        if(roles == null || roles.isEmpty())
            return new DefaultTreeModel(new DefaultMutableTreeNode("Empty"));
        Role rootRole = null;
        for(Role r : roles){
            if(r.isRootRole())
                rootRole = r;
        }
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROLE " + rootRole.getRoleName());
        walkTree(rootNode,rootRole);
        return new DefaultTreeModel(rootNode);
    }


}
