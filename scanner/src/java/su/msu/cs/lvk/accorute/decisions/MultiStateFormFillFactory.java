package su.msu.cs.lvk.accorute.decisions;

import com.gargoylesoftware.htmlunit.html.*;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.EntityID;
import su.msu.cs.lvk.accorute.http.model.WebAppUser;

import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 18.11.2010
 * Time: 15:48:42
 * To change this template use File | Settings | File Templates.
 */
public class MultiStateFormFillFactory implements FormFillerFactory {
    public static int numInvokations = 0;
    public String defaultInputText = null;
    public String defaultTextAreaText = null;
    public boolean cycleSelects = false;
    public boolean cycleRadioButtons = false;
    public boolean cycleCheckboxes = false;

    public MultiStateFormFillFactory(
            String defaultInputText,
            String defaultTextAreaText,
            boolean cycleSelects,
            boolean cycleRadioButtons,
            boolean cycleCheckboxes)
    {
        this.defaultInputText = defaultInputText;
        this.defaultTextAreaText = defaultTextAreaText;
        this.cycleSelects = cycleSelects;
        this.cycleRadioButtons = cycleRadioButtons;
        this.cycleCheckboxes = cycleCheckboxes;
    }

    public FormFiller generate(HtmlForm f, EntityID ctx) {
        numInvokations++;
        return new SimpleFormFill(
                ctx,
                f,
                cycleCheckboxes,
                cycleRadioButtons,
                cycleSelects,
                defaultTextAreaText,
                defaultInputText
        );
    }

    /**
     * This interfaces denotes rewindable iterators with side effects
     * The iterator has the finite number of states, which can be cycled through
     * using next().
     * These states may represent:
     * 1. One form element with multiple states(e.g. checkbox, option of multichoice select)
     * 2. A group of interdependent elements, e.g. the group of radiobuttons
     */
    public interface RewindingFormStateIterator {
        /**
         * Switches back to the first state
         */
        void rewind();
        boolean hasNext();

        /**
         * Switches the form to the next state
         */
        void next();
    }
    public class RadioButtonFormStateIter implements  RewindingFormStateIterator{
        List<HtmlRadioButtonInput> radios;
        int current;
        public RadioButtonFormStateIter(List<HtmlRadioButtonInput> r){
            radios = r;
            if(radios.size() == 0)
                throw new RuntimeException();
            rewind();
        }

        public void rewind() {
            radios.get(0).setChecked(true);
            for(int i=1; i<radios.size();i++)
                radios.get(i).setChecked(false);
            current = 1;
        }

        public boolean hasNext() {
            return current < radios.size();
        }

        public void next() {
            if(current>=radios.size())
                throw new NoSuchElementException();
            radios.get(current).setChecked(true);
            for(int i=0;i<current;i++){
                radios.get(i).setChecked(false);
            }
            for(int i = current + 1; i< radios.size(); i++){
                radios.get(i).setChecked(false);
            }
            current ++;
        }
    }
    public class SingleOptionFormStateIter implements RewindingFormStateIterator {
        HtmlOption opt;
        public SingleOptionFormStateIter(HtmlOption op){
            opt = op;
            rewind();
        }
        public void rewind() {
            opt.setSelected(false);
        }

        public boolean hasNext() {
            return !opt.isSelected();
        }

        public void next() {
            opt.setSelected(!opt.isSelected());
        }
    }
    public class SelectFormStateIter implements RewindingFormStateIterator {
        HtmlSelect sel;
        int current;
        public SelectFormStateIter(HtmlSelect s) {
            sel = s;
            if(s.isMultipleSelectEnabled())
                throw new IllegalArgumentException("Use SingleOptionFormStateIter for each option of multiselect");
            if(s.getOptionSize() == 0)
                throw new IllegalArgumentException("Zero options - nothing to select");
            rewind();
        }

        public void rewind() {
            sel.getOption(0).setSelected(true);
            for(int i=1; i<sel.getOptionSize();i++)
                sel.getOption(i).setSelected(false);
            current = 1;
        }

        public boolean hasNext() {
            return current < sel.getOptionSize();
        }

        public void next() {
            if(current>=sel.getOptionSize())
                throw new NoSuchElementException();
            sel.getOption(current).setSelected(true);
            for(int i=0;i<current;i++){
                sel.getOption(i).setSelected(false);
            }
            for(int i = current + 1; i< sel.getOptionSize(); i++){
                sel.getOption(i).setSelected(false);
            }
            current ++;
        }
    }
    public class CheckBoxFormStateIter implements RewindingFormStateIterator {
        HtmlCheckBoxInput box;

        public CheckBoxFormStateIter(HtmlCheckBoxInput box) {
            this.box = box;
            rewind();
        }

        public void rewind() {
            box.setChecked(false);
        }

        public boolean hasNext() {
            return !box.isChecked();
        }

        public void next() {
            box.setChecked(!box.isChecked() );
        }
    }

    public class SimpleFormFill implements  FormFiller{
        public String defaultInputText;
        public String defaultTextAreaText;
        public boolean cycleSelects;
        public boolean cycleRadioButtons;
        public boolean cycleCheckboxes;

        private final List<HtmlElement> submittable = new ArrayList<HtmlElement>();
        private HtmlForm form;
        private EntityID ctx;
        private final List<RewindingFormStateIterator> currentState = new ArrayList<RewindingFormStateIterator>();
        private Iterator<HtmlElement> submitIter;
        private HtmlElement currentSubmit;

        public SimpleFormFill(EntityID ctx,
                              HtmlForm form,
                              boolean cycleCheckboxes,
                              boolean cycleRadioButtons,
                              boolean cycleSelects,
                              String defaultTextAreaText,
                              String defaultInputText)
        {
            this.ctx = ctx;
            this.form = form;
            this.cycleCheckboxes = cycleCheckboxes;
            this.cycleRadioButtons = cycleRadioButtons;
            this.cycleSelects = cycleSelects;
            this.defaultTextAreaText = defaultTextAreaText;
            this.defaultInputText = defaultInputText;
            EntityID uid = WebAppProperties.getInstance().getContextService().getContextByID(ctx).getUserID();
            WebAppUser user = WebAppProperties.getInstance().getUserService().getUserByID(uid);
            Map<String , ArrayList<HtmlRadioButtonInput>> radioGroups = new HashMap<String , ArrayList<HtmlRadioButtonInput>>();
            //1. input
            for(HtmlElement i: form.getHtmlElementsByTagName("input")){
                HtmlInput input = (HtmlInput) i;
                final String type = input.getTypeAttribute();
                //1.1 type=text
                if(type.equalsIgnoreCase("text")){
                    String name = input.getNameAttribute();
                    if(user.getStaticCredentials().containsKey(name)){
                        input.setValueAttribute(user.getStaticCredentials().get(name));
                    }else{
                        boolean gotPartial = false;
                        for (Map.Entry<String,String> e : user.getStaticCredentials().entrySet()){
                            String key = e.getKey();
                            if(name.toLowerCase().contains(key.toLowerCase())){
                                gotPartial = true;
                                input.setValueAttribute(e.getValue());
                                break;
                            }
                        }
                        if(!gotPartial){
                            if(name.toLowerCase().contains("mail")){
                                input.setValueAttribute("test@example.org");//TODO: terrible cludge!
                            }else if(defaultInputText!=null){
                                input.setValueAttribute(defaultInputText);
                            }
                        }
                    }
                }
                //1.2 type=password
                else if(type.equalsIgnoreCase("password")){
                    String name = input.getNameAttribute();
                    if(user.getStaticCredentials().containsKey(name)){
                        input.setValueAttribute(user.getStaticCredentials().get(name));
                    }else if(user.getStaticCredentials().get("password") != null){
                        input.setValueAttribute(user.getStaticCredentials().get("password"));
                    }
                }
                //1.3 type=radio
                else if(type.equalsIgnoreCase("radio")){
                    String name = input.getNameAttribute();
                    if(cycleRadioButtons){
                        HtmlRadioButtonInput radio = (HtmlRadioButtonInput) input;
                        if(radioGroups.containsKey(name)){
                            radioGroups.get(name).add(radio);
                        }else{
                            ArrayList<HtmlRadioButtonInput> ar = new ArrayList<HtmlRadioButtonInput>();
                            ar.add(radio);
                            radioGroups.put(name,ar);
                        }
                    }
                }
                //1.4 type=checkbox
                else if(type.equalsIgnoreCase("checkbox")){
                    String name = input.getNameAttribute();
                    if(cycleCheckboxes){
                        currentState.add(
                                new CheckBoxFormStateIter((HtmlCheckBoxInput)input)
                        );
                    }
                }
                //1.5 submit
                else if(type.equalsIgnoreCase("submit")){
                    submittable.add(input);
                }
                //image
                else if(type.equalsIgnoreCase("image")){
                    submittable.add(input);
                }
                //button
                else if(type.equalsIgnoreCase("button")){
                    submittable.add(input);
                }
            }
            //radiobuttons...
            if(cycleRadioButtons){
                for(ArrayList<HtmlRadioButtonInput> array: radioGroups.values()){
                    currentState.add(
                            new RadioButtonFormStateIter(array)
                    );
                }
            }
            //selects

            for(HtmlElement i: form.getHtmlElementsByTagName("select")){
                HtmlSelect sel = (HtmlSelect) i;
                String name = sel.getNameAttribute();
                if(cycleSelects){
                    if(sel.isMultipleSelectEnabled()){
                        for(HtmlOption opt:sel.getOptions()){
                            currentState.add(
                                    new SingleOptionFormStateIter(opt)
                            );
                        }
                    }else{
                        currentState.add(
                                new SelectFormStateIter(sel)
                        );
                    }
                }
            }
            //textarea
            for(HtmlElement el: form.getHtmlElementsByTagName("textarea")){
                HtmlTextArea area = (HtmlTextArea) el;
                String name = area.getNameAttribute();
                if(user.getStaticCredentials().containsKey(name)){
                    area.setText(user.getStaticCredentials().get(name));
                }else if(defaultTextAreaText != null){
                    area.setText(defaultTextAreaText);
                }
            }
            //another way of specifying buttons
            for(HtmlElement el: form.getHtmlElementsByTagName("button")){
                submittable.add(el);
            }
            submitIter = submittable.iterator();
        }
        public boolean hasNext() {
            for(RewindingFormStateIterator it : currentState){
                if(it.hasNext())
                    return true;
            }
            return submitIter.hasNext();
        }

        public HtmlElement next() {
            if(currentSubmit == null){
                currentSubmit = submitIter.next();
                return currentSubmit;
            }
            RewindingFormStateIterator lowest = null;
            //search for the first iterator that can be next'ed...
            for (RewindingFormStateIterator iter: currentState) {
                if(iter.hasNext()){
                    lowest = iter;
                    break;
                }
            }
            //if all of them are exhausted, switch to the next submit element we have
            if(lowest == null){
                //if this is the last submitter, throw
                if(!submitIter.hasNext())
                    throw new NoSuchElementException();
                //get next submitter...
                currentSubmit = submitIter.next();
                // rewind all the iterators
                for (RewindingFormStateIterator iter: currentState) {
                    iter.rewind();
                }
                return currentSubmit;
            }
            //we definitely have a non-exhausted iterator!
            boolean carry;
            Iterator<RewindingFormStateIterator> it = currentState.iterator();
            // while we encounter exhausted iterators, rewind them
            do{
                carry = false;
                RewindingFormStateIterator rewIter = it.next();
                if(!rewIter.hasNext()){
                    rewIter.rewind();
                    carry = true;
                }else{
                    rewIter.next();
                }
            }while(carry);
            return currentSubmit;
        }

    }
}
