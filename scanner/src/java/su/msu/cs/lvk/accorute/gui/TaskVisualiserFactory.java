package su.msu.cs.lvk.accorute.gui;

import su.msu.cs.lvk.accorute.taskmanager.Task;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 09.04.12
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public interface TaskVisualiserFactory {
    public interface TaskVisualiser {
        public Component getComponent();
    }
    public TaskVisualiser generate(Task t);
}
