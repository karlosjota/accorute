package su.msu.cs.lvk.accorute.gui;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;
import com.truchsess.util.TreeCursor;
import com.truchsess.util.TreeWalker;
import org.apache.commons.lang.StringUtils;
import su.msu.cs.lvk.accorute.RBAC.Role;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.UserContext;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;
import su.msu.cs.lvk.accorute.taskmanager.TaskManager;

import javax.swing.*;
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
public class RoleGraphModelBuilder {
    private RoleGraphModelBuilder(){}
    private static void walkTree(Role curRole, Object curNode, mxGraph roleGraph){
        enumUsers(curRole, curNode, roleGraph);
        int i = 0;
        for(Role childRole: curRole.getChildRoles()){
            Object childNode = roleGraph.insertVertex(roleGraph.getDefaultParent(), null, childRole, 0, 0, 150, 50,"fillColor=white;");
            roleGraph.insertEdge(roleGraph.getDefaultParent(), null, "", curNode, childNode);
            walkTree(childRole, childNode, roleGraph);
            i++;
        }
    }
    private static void enumUsers(Role curRole, Object curNode, mxGraph roleGraph){
        List<WebAppUser> users = WebAppProperties.getInstance().getUserService().getUsersByRole(curRole.getRoleName());
        for(WebAppUser u: users){
            List<UserContext> ctx = WebAppProperties.getInstance().getContextService().getContextsByUserID(u.getUserID());
            List<String> ctxList = new ArrayList<String>();
            for(UserContext c: ctx){
                ctxList.add(c.getContextID().toString());
            }
            Object userNode = roleGraph.insertVertex(roleGraph.getDefaultParent(), null, u, 0, 0, 200, 90);
            roleGraph.insertEdge(roleGraph.getDefaultParent(), null, "", curNode, userNode);
        }
    }
    public static void makeGraph(mxGraph roleGraph){
        roleGraph.getModel().beginUpdate();
        ((mxGraphModel) roleGraph.getModel()).clear();
        roleGraph.getModel().endUpdate();
        roleGraph.getModel().beginUpdate();
        try {
            ((mxGraphModel) roleGraph.getModel()).clear();
            List<Role> roles = WebAppProperties.getInstance().getRoles();
            if(roles != null && !roles.isEmpty()){
                Role rootRole = null;
                for(Role r : roles){
                    if(r.isRootRole())
                        rootRole = r;
                }
                Object rootNode = roleGraph.insertVertex(roleGraph.getDefaultParent(), null, rootRole, 0,0, 150, 50, "fillColor=white;");
                walkTree(rootRole, rootNode, roleGraph);
                mxHierarchicalLayout layout = new mxHierarchicalLayout(roleGraph);
                List roots = new ArrayList();
                roots.add(rootNode);
                layout.execute(roleGraph.getDefaultParent(), roots);
            }
        } finally {
            roleGraph.getModel().endUpdate();
        }
    }


}
