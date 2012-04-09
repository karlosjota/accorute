package su.msu.cs.lvk.accorute.gui;

import su.msu.cs.lvk.accorute.taskmanager.Task;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 09.04.12
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
public class GenericTaskVisualizerFactory implements TaskVisualiserFactory{
    class GenericTaskVisualiser implements  TaskVisualiser{
        private JPanel panel_;
        GenericTaskVisualiser(Task task) {
            panel_ = new JPanel();
            panel_.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.PAGE_START;
            c.gridy = c.gridx = 0;
            c.weightx = 1;
            panel_.add(new JLabel("Task :" + task.getClass().getSimpleName()), c);
            c.gridy++;
            panel_.add(new JLabel("Status :" + task.getStatus()), c);
            c.gridy++;
            panel_.add(new JLabel("Runner thread :" + task.getExecutorThreadName()), c);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            if(task.getStarted() != null){
                c.gridy++;
                panel_.add(new JLabel("Started :" + dateFormat.format(task.getStarted())), c);
            }
            if(task.getFinished() != null){
                c.gridy++;
                panel_.add(new JLabel("Finished :" + dateFormat.format(task.getFinished())), c);
            }
        }

        public Component getComponent() {
            return panel_;
        }
    }
    public TaskVisualiser generate(Task t) {
        return new GenericTaskVisualiser(t);
    }
}
