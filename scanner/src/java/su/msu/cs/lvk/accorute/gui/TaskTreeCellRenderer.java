package su.msu.cs.lvk.accorute.gui;

import su.msu.cs.lvk.accorute.taskmanager.Task;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 10.04.12
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public class TaskTreeCellRenderer implements TreeCellRenderer{
    JPanel nodePanel;
    JLabel pictureLabel;
    JLabel taskClassLabel;
    JLabel infoLabel;
    DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    Color backgroundSelectionColor;
    Color backgroundNonSelectionColor;
    final private ImageIcon runningIcon, finishedIcon, blockedIcon, notStartedIcon, failedIcon;
    public TaskTreeCellRenderer() {
        nodePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 0;
        c.gridheight = 2;
        c.ipadx = 4;
        pictureLabel = new JLabel();
        nodePanel.add(pictureLabel,c);
        taskClassLabel = new JLabel(" ");
        taskClassLabel.setForeground(Color.blue);
        c.gridy = 0;
        c.ipadx = 0;
        c.gridx = 1;
        c.gridheight = 1;
        nodePanel.add(taskClassLabel, c);
        infoLabel = new JLabel(" ");
        infoLabel.setForeground(Color.black);
        c.gridy = 1;
        c.gridx = 1;
        c.gridheight = 1;
        nodePanel.add(infoLabel, c);
        backgroundSelectionColor = defaultRenderer
                .getBackgroundSelectionColor();
        backgroundNonSelectionColor = defaultRenderer
                .getBackgroundNonSelectionColor();
        runningIcon = new ImageIcon("src/resources/icons/running.gif");
        finishedIcon = new ImageIcon("src/resources/icons/finished.gif");
        blockedIcon = new ImageIcon("src/resources/icons/blocked.gif");
        notStartedIcon = new ImageIcon("src/resources/icons/not_started.gif");
        failedIcon = new ImageIcon("src/resources/icons/failed.gif");
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component returnValue = null;
        if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
            Object userObject = ((DefaultMutableTreeNode) value)
                    .getUserObject();
            if (userObject instanceof Task) {
                Task tsk = (Task) userObject;
                switch(tsk.getStatus()){
                    case BLOCKED:
                        pictureLabel.setIcon(blockedIcon); break;
                    case NOT_STARTED:
                        pictureLabel.setIcon(notStartedIcon); break;
                    case RUNNING:case SCHEDULED:
                        pictureLabel.setIcon(runningIcon); break;
                    case FINISHED:
                        if(tsk.isSuccessful()){
                            pictureLabel.setIcon(finishedIcon);
                        }else{
                            pictureLabel.setIcon(failedIcon);
                        }
                        break;

                }
                taskClassLabel.setText(tsk.getClass().getSimpleName());
                infoLabel.setText(tsk.toString());
                if (selected) {
                    nodePanel.setBackground(backgroundSelectionColor);
                } else {
                    nodePanel.setBackground(backgroundNonSelectionColor);
                }
                nodePanel.setEnabled(tree.isEnabled());
                returnValue = nodePanel;
            }
        }
        if (returnValue == null) {
            returnValue = defaultRenderer.getTreeCellRendererComponent(tree,
                    value, selected, expanded, leaf, row, hasFocus);
        }
        return returnValue;
    }
}
