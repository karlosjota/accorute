package su.msu.cs.lvk.accorute.utils;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.SimpleScriptable;
import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;
import net.sourceforge.htmlunit.corejs.javascript.Context;
import net.sourceforge.htmlunit.corejs.javascript.Scriptable;
import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject;
import net.sourceforge.htmlunit.corejs.javascript.VMBridge;
import org.w3c.dom.NamedNodeMap;
import su.msu.cs.lvk.accorute.WebAppProperties;
import su.msu.cs.lvk.accorute.http.model.*;
import su.msu.cs.lvk.accorute.tasks.ResponseFetcher;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 13.11.2010
 * Time: 14:29:31
 * To change this template use File | Settings | File Templates.
 */
public class HtmlUnitUtils {

    protected static Logger logger = Logger.getLogger(HtmlUnitUtils.class.getName());
    public static Collection<String> getUserControllableFormFields(HtmlForm form){
        Set<String> controllableFieldNames = new HashSet<String>();
        //1. input
        for(HtmlElement i: form.getHtmlElementsByTagName("input")){
            HtmlInput input = (HtmlInput) i;
            final String type = input.getTypeAttribute();
            //1.1 type=text
            if(type.equalsIgnoreCase("text")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }
            //1.2 type=password
            else if(type.equalsIgnoreCase("password")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }
            //1.3 type=radio
            else if(type.equalsIgnoreCase("radio")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }
            //1.4 type=checkbox
            else if(type.equalsIgnoreCase("checkbox")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }
            /*
            else if(type.equalsIgnoreCase("hidden")){
                String name = input.getNameAttribute();
                controllableFieldNames.add(name);
            }*/
        }

        //selects
        for(HtmlElement i: form.getHtmlElementsByTagName("select")){
            HtmlSelect sel = (HtmlSelect) i;
            String name = sel.getNameAttribute();
            controllableFieldNames.add(name);
        }
        //textarea
        for(HtmlElement el: form.getHtmlElementsByTagName("textarea")){
            HtmlTextArea area = (HtmlTextArea) el;
            String name = area.getNameAttribute();
            controllableFieldNames.add(name);
        }
        return controllableFieldNames;
    }
    public static void cloneScriptableObjects(final DomNode node, final WebWindow window) throws NoSuchFieldException, IllegalAccessException{
        if(node instanceof HtmlInlineFrame)
            return;
        for(DomNode child: node.getChildren()){
            Field scriptObject_Field = DomNode.class.getDeclaredField("scriptObject_");
            scriptObject_Field.setAccessible(true);
            SimpleScriptable simpleScriptable = (SimpleScriptable) scriptObject_Field.get(child);
            if(simpleScriptable != null){
                SimpleScriptable newScriptable = simpleScriptable.clone();
                final WebWindow enclosingWindow = child.getPage().getEnclosingWindow();
                newScriptable.setParentScope((Scriptable) window.getScriptObject());
                newScriptable.setPrototype(simpleScriptable.getPrototype());
                newScriptable.setDomNode(child);
            }
            cloneScriptableObjects(child, window);
        }
    }
    private static void setField(Object o, Class theClass, String fieldName, Object fieldValue)throws NoSuchFieldException, IllegalAccessException{
        try{
            Field theField = theClass.getDeclaredField(fieldName);
            theField.setAccessible(true);
            theField.set(o, fieldValue);
        }catch(final NoSuchFieldException e){
            if(theClass.getSuperclass() != null){
                setField(o, theClass.getSuperclass(), fieldName,  fieldValue);
            }else{
                throw e;
            }

        }
    }
    private static Object getField(Object o, Class theClass,  String fieldName)throws NoSuchFieldException, IllegalAccessException{
        try{
            Field theField = theClass.getDeclaredField(fieldName);
            theField.setAccessible(true);
            return theField.get(o);

        }catch(final NoSuchFieldException e){
            if(theClass.getSuperclass() != null){
                return getField(o, theClass.getSuperclass(), fieldName);
            }else{
                throw e;
            }
        }
    }
    private static DomNode shallowCopy(DomNode original, HtmlPage enclosingPage, WebWindow enclosingWindow) throws NoSuchFieldException,InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException{
        DomNode node = original.cloneNode(false);

        //============================================
        //1.DomNode - applies to all elements
        //============================================
            //a. page_
            if(enclosingPage == null && !(original instanceof HtmlPage))
                throw new RuntimeException("enclosing page cannot be null except when cloning HtmlPage");
            setField(node, node.getClass(), "page_", enclosingPage);
            //b. scriptObject_
            SimpleScriptable originalScriptable = (SimpleScriptable) getField(original,original.getClass(), "scriptObject_");
            if(originalScriptable != null){
                SimpleScriptable newScriptable = originalScriptable.clone();
                newScriptable.setParentScope((Scriptable) enclosingWindow.getScriptObject());
                newScriptable.setPrototype(originalScriptable.getPrototype());
                setField(newScriptable, newScriptable.getClass(), "domNode_", node);
            }
        //============================================
        //2.HtmlPage
        //============================================
        if(node instanceof HtmlPage){
            HtmlPage page = (HtmlPage) node;
            //a. enclosingWindow
            page.setEnclosingWindow(enclosingWindow);
            //b. webClient_
            setField(page, page.getClass(), "webClient_", enclosingWindow.getWebClient());
            //c. lock_
            setField(page,  page.getClass(), "lock_", "");
            //d. attributeListeners_
            setField(page,  page.getClass(), "attributeListeners_", new ArrayList<HtmlAttributeChangeListener>());
        }
        //============================================
        //3.BaseFrame
        //============================================
        if(node instanceof BaseFrame){
            BaseFrame newFrame = (BaseFrame) node;  
            BaseFrame originalFrame = (BaseFrame) original;
            //a. Create new FrameWindow for this frame
            Class[] paramClasses = {BaseFrame.class};
            Constructor ctor = FrameWindow.class.getDeclaredConstructor(paramClasses);
            ctor.setAccessible(true);
            Object[] params = {newFrame};
            FrameWindow newFrameWindow = (FrameWindow)ctor.newInstance(params);
            //b. Set relationships between the frame and the window
            setField(newFrameWindow, newFrameWindow.getClass(), "frame_", newFrame);
            setField(node, node.getClass(), "enclosedWindow_", newFrameWindow);
            //c. copy the enclosed page into the FrameWindow
            //The deepCopy will also set relationships between page and window
            HtmlPage originalFramedPage = (HtmlPage) originalFrame.getEnclosedPage();
            HtmlPage newFramedPage = (HtmlPage) deepCopy(originalFramedPage, null, newFrameWindow);
        }

        return node;
    }
    private static void fixNodeAfterChildrenCreated(DomNode node, DomNode original, HtmlPage enclosingPage, WebWindow enclosingWindow) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        //============================================
        //1. HtmlPage
        if(node instanceof HtmlPage){
            HtmlPage page = (HtmlPage) node;
            //a. idMap_ and nameMap_
            Method addMappedElementMethod = HtmlPage.class.getDeclaredMethod("addMappedElement", HtmlElement.class, Boolean.TYPE);
            addMappedElementMethod.setAccessible(true);
            for(DomNode child: page.getChildNodes()){
                if(child instanceof HtmlElement){
                    Object args[] = {child, true};
                    addMappedElementMethod.invoke(page, args);
                }
            }
            //b. elementWithfocus_
            if(((HtmlPage)original).getFocusedElement()!=null){
                String focusPath = ((HtmlPage)original).getFocusedElement().getCanonicalXPath();
                page.setFocusedElement(page.<HtmlElement>getFirstByXPath(focusPath));
            }
        }
        //============================================
        //2.HtmlElement
        //============================================
        if(node instanceof HtmlElement){
            HtmlElement newElement = (HtmlElement) node;
            HtmlElement originalElement = (HtmlElement) original;
            HtmlForm originalOwningForm = (HtmlForm) getField(originalElement, originalElement.getClass(),  "owningForm_");
            if(originalOwningForm != null){
                String owningFormXPath = originalOwningForm.getCanonicalXPath();
                HtmlForm newOwningForm = enclosingPage.getFirstByXPath(owningFormXPath);
                setField(newElement, newElement.getClass(), "owningForm_", newOwningForm);
            }
        }
        //============================================
        //3.HtmlForm
        //============================================
        if(node instanceof HtmlForm){
            HtmlForm newForm = (HtmlForm) node;
            HtmlForm originalForm = (HtmlForm) original;   
            List<HtmlElement> originalLostChildren_ = (List<HtmlElement>)getField(originalForm,originalForm.getClass(), "lostChildren_");
            List<HtmlElement> newLostChildren_ = (List<HtmlElement>)getField(newForm, newForm.getClass(), "lostChildren_");
            newLostChildren_.clear();
            for(HtmlElement originalLostChild: originalLostChildren_){
                String xpath = originalLostChild.getCanonicalXPath();
                HtmlElement newLostChild = enclosingPage.getFirstByXPath(xpath);
                newLostChildren_.add(newLostChild);
            }
            
        }
    }
    private static DomNode deepCopy(DomNode original, DomNode parent, WebWindow newWindow) throws InstantiationException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        //1. Do a shallow copy
        DomNode node;
        if(parent == null){
            if(original instanceof HtmlPage){
                if(newWindow == null)
                    throw new RuntimeException("Unable to do a copy of a page without a window");
                node = shallowCopy(original, null, newWindow);
            }
            else{
                throw new RuntimeException("Unable to do a deep copy without parent");
            }
        }else{
            node = shallowCopy(original, (HtmlPage) parent.getPage(), newWindow);
        }
        //2. Append it to parent
        if(parent != null){
            Method basicAppendMethod = DomNode.class.getDeclaredMethod("basicAppend", DomNode.class);
            basicAppendMethod.setAccessible(true);
            Object params [] = { node };
            basicAppendMethod.invoke(parent, params);
        }
        //3. recursively copy children
        for(DomNode child: original.getChildren()){
            deepCopy(child, node, newWindow);
        }
        //4. Do fixes that are only available after the recursion finishes
        fixNodeAfterChildrenCreated(node, original, (HtmlPage) node.getPage(), node.getPage().getEnclosingWindow());
        //5. Set the page as the enclosed page for the window and set script object for the window
        if(node instanceof  HtmlPage){
            HtmlPage newPage = (HtmlPage) node;
            //a. Set relationships between the window and the page
            newWindow.setEnclosedPage(newPage);
            //ScriptObject for window
            SimpleScriptable originalScriptable = (SimpleScriptable) original.getPage().getEnclosingWindow().getScriptObject();
            if(originalScriptable != null){
                SimpleScriptable newScriptable = originalScriptable.clone();
                newScriptable.setParentScope((Scriptable) newWindow.getScriptObject());
                newScriptable.setPrototype(originalScriptable.getPrototype());
                setField(newScriptable, newScriptable.getClass(), "domNode_", newPage);
            }
        }
        return node;
    }
    public static HtmlPage clonePage(final HtmlPage other,final WebWindow window, final EntityID ctx){
        try{
            WebClient wc = window.getWebClient();
            HtmlPage p = (HtmlPage) deepCopy(other, null, window);
            return p;
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}

