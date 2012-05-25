package su.msu.cs.lvk.accorute.gui;

import su.msu.cs.lvk.accorute.http.model.Conversation;
import su.msu.cs.lvk.accorute.taskmanager.Task;
import su.msu.cs.lvk.accorute.tasks.ResponseFetcher;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ngo
 * Date: 09.04.12
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class ResponseFetcherResultViewerFactory implements TaskVisualiserFactory {
    public TaskVisualiser generate(Task t) {
        if(t instanceof ResponseFetcher){
            return new ResponseFetcherResultViewer((ResponseFetcher)t);
        }else{
            return null;
        }
    }
    class ResponseFetcherResultViewer implements TaskVisualiser {
        private final Component theComponent;
        private JTextArea requestArea_, responseArea_;
        ResponseFetcherResultViewer(ResponseFetcher fetcher) {
            Conversation conversation = (Conversation) fetcher.getResult();
            if(conversation != null){
                requestArea_ = new JTextArea(conversation.getRequest().toString());
                requestArea_.setEditable(false);
                requestArea_.setLineWrap(true);
                requestArea_.setBorder(BorderFactory.createTitledBorder("Request"));
                responseArea_ = new JTextArea(conversation.getResponse().toString());
                responseArea_.setEditable(false);
                responseArea_.setLineWrap(true);
                responseArea_.setBorder(BorderFactory.createTitledBorder("Response"));
                theComponent = new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        new JScrollPane(requestArea_), new JScrollPane(responseArea_)
                );
            }else{
                theComponent = new JPanel();
            }
        }

        public Component getComponent() {
            return theComponent;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
