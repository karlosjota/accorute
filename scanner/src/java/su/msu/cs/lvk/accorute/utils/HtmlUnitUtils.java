package su.msu.cs.lvk.accorute.utils;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.gargoylesoftware.htmlunit.javascript.NamedNodeMap;
import com.gargoylesoftware.htmlunit.javascript.SimpleScriptable;
import com.gargoylesoftware.htmlunit.javascript.host.*;
import com.gargoylesoftware.htmlunit.javascript.host.History;
import com.gargoylesoftware.htmlunit.javascript.host.Node;
import com.gargoylesoftware.htmlunit.javascript.host.css.CSSRuleList;
import com.gargoylesoftware.htmlunit.javascript.host.css.CSSStyleSheet;
import com.gargoylesoftware.htmlunit.javascript.host.css.ComputedCSSStyleDeclaration;
import com.gargoylesoftware.htmlunit.javascript.host.css.StyleSheetList;
import com.gargoylesoftware.htmlunit.javascript.host.html.*;
import net.sourceforge.htmlunit.corejs.javascript.*;
import net.sourceforge.htmlunit.corejs.javascript.regexp.NativeRegExp;
import org.apache.commons.lang.NotImplementedException;
import org.w3c.dom.css.CSSImportRule;
import su.msu.cs.lvk.accorute.http.model.*;

import java.lang.reflect.*;
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
    private static DomNode shallowCopyDomBeforeRecursion(DomNode original, HtmlPage enclosingPage, WebWindow enclosingWindow, HashMap<Object, Object> originalsToClonesMap) throws NoSuchFieldException,InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException{
        if(wasAlreadyCloned(original, originalsToClonesMap)){
            return (DomNode)getAlreadyCloned(original, originalsToClonesMap);
        }
        DomNode node = original.cloneNode(false);
        originalsToClonesMap.put(original, node);

        //============================================
        //1.DomNode - applies to all elements
        //============================================
            //a. page_
            if(enclosingPage == null && !(original instanceof HtmlPage))
                throw new RuntimeException("enclosing page cannot be null except when cloning HtmlPage");
            setField(node, node.getClass(), "page_", enclosingPage);
            //b. scriptObject_ - now just copyed verbatim, will be set later
            SimpleScriptable originalScriptable = (SimpleScriptable) getField(original,original.getClass(), "scriptObject_");
            setField(node, node.getClass(), "scriptObject_", originalScriptable);
        //TODO: DomElement
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
            FrameWindow newFrameWindow = null;
            if(wasAlreadyCloned(originalFrame.getEnclosedWindow(), originalsToClonesMap)){
                newFrameWindow = (FrameWindow) getAlreadyCloned(originalFrame.getEnclosedWindow(), originalsToClonesMap);
            }else{
                Class[] paramClasses = {BaseFrame.class};
                Constructor ctor = FrameWindow.class.getDeclaredConstructor(paramClasses);
                ctor.setAccessible(true);
                Object[] params = {newFrame};
                newFrameWindow = (FrameWindow)ctor.newInstance(params);
                originalsToClonesMap.put(originalFrame.getEnclosedWindow(), newFrameWindow);
            }
            //b. Set relationships between the frame and the window
            setField(newFrameWindow, newFrameWindow.getClass(), "frame_", newFrame);
            setField(node, node.getClass(), "enclosedWindow_", newFrameWindow);
            //c. copy the enclosed page into the FrameWindow
            //The deepCopyDom will also set relationships between page and window
            HtmlPage originalFramedPage = (HtmlPage) originalFrame.getEnclosedPage();
            HtmlPage newFramedPage = (HtmlPage) deepCopyDom(originalFramedPage, null, newFrameWindow, originalsToClonesMap);
        }

        return node;
    }
    private static void shallowCopyDomAfterRecursion(DomNode node, DomNode original, HtmlPage enclosingPage, WebWindow enclosingWindow, HashMap<Object, Object> originalsToClonesMap) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
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
                if(wasAlreadyCloned(((HtmlPage) original).getFocusedElement(), originalsToClonesMap)){
                    page.setFocusedElement((HtmlElement)getAlreadyCloned(((HtmlPage) original).getFocusedElement(), originalsToClonesMap));
                }else{
                    String focusPath = ((HtmlPage)original).getFocusedElement().getCanonicalXPath();
                    page.setFocusedElement(page.<HtmlElement>getFirstByXPath(focusPath));
                    logger.error("Focused element not recorded as cloned???");
                }
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
                HtmlForm newOwningForm = null;
                if(wasAlreadyCloned(originalOwningForm, originalsToClonesMap)) {
                    newOwningForm = (HtmlForm)getAlreadyCloned(originalOwningForm, originalsToClonesMap);
                }else{
                    String owningFormXPath = originalOwningForm.getCanonicalXPath();
                    newOwningForm = enclosingPage.getFirstByXPath(owningFormXPath);
                    logger.error("Owning form not recorded as cloned???");
                }
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
                HtmlElement newLostChild = null;
                if(wasAlreadyCloned(originalLostChild, originalsToClonesMap)){
                    newLostChild = (HtmlElement) getAlreadyCloned(originalLostChild, originalsToClonesMap);
                }else{
                    String xpath = originalLostChild.getCanonicalXPath();
                    newLostChild = enclosingPage.getFirstByXPath(xpath);
                    logger.error("Lost child not recorded as cloned???");
                }
                newLostChildren_.add(newLostChild);
            }
            
        }
    }
    private static DomNode deepCopyDom(DomNode original, DomNode parent, WebWindow newWindow, HashMap<Object, Object> originalsToClonesMap) throws InstantiationException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        //1. Do a shallow copy of a DOM Node
        DomNode node;
        if(parent == null){
            if(original instanceof HtmlPage){
                if(newWindow == null)
                    throw new RuntimeException("Unable to do a copy of a page without a window");
                node = shallowCopyDomBeforeRecursion(original, null, newWindow, originalsToClonesMap);
            }
            else{
                node = shallowCopyDomBeforeRecursion(original, (HtmlPage)newWindow.getEnclosedPage(), newWindow, originalsToClonesMap);
                logger.warn("Cloning with null parent...");
            }
        }else{
            node = shallowCopyDomBeforeRecursion(original, (HtmlPage) parent.getPage(), newWindow, originalsToClonesMap);
        }
        //2. Append node to its parent
        if(parent != null){
            Method basicAppendMethod = DomNode.class.getDeclaredMethod("basicAppend", DomNode.class);
            basicAppendMethod.setAccessible(true);
            Object params [] = { node };
            basicAppendMethod.invoke(parent, params);
        }
        //3. recursively clone children
        for(DomNode child: original.getChildren()){
            deepCopyDom(child, node, newWindow, originalsToClonesMap);
        }
        //4. Do fixes that are only available after the recursion finishes
        shallowCopyDomAfterRecursion(node, original, (HtmlPage) node.getPage(), node.getPage().getEnclosingWindow(), originalsToClonesMap);
        //5. Set the page as the enclosed page for the window
        if(node instanceof  HtmlPage){
            HtmlPage newPage = (HtmlPage) node;
            //a. Set relationships between the window and the page
            newWindow.setEnclosedPage(newPage);
        }
        return node;
    }
    private static Object cloneObjectOrDie(Object original, HashMap<Object, Object> originalsToClonesMap, ContextFactory contextFactory)throws InstantiationException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        if(wasAlreadyCloned(original, originalsToClonesMap))
            return getAlreadyCloned(original, originalsToClonesMap);
        if(original instanceof Scriptable){
            return deepCopyScriptable((Scriptable) original, originalsToClonesMap, contextFactory);
        }
        if(original instanceof LazilyLoadedCtor){
            LazilyLoadedCtor origCtor = (LazilyLoadedCtor) original;
            ScriptableObject origScope = (ScriptableObject) getField(origCtor, origCtor.getClass(), "scope");   
            ScriptableObject newScope = (ScriptableObject) deepCopyScriptable(origScope,originalsToClonesMap, contextFactory);
            String propertyName =  (String) getField(origCtor, origCtor.getClass(), "propertyName");        
            String className =  (String) getField(origCtor, origCtor.getClass(), "className");
            Boolean sealed = (Boolean) getField(origCtor, origCtor.getClass(), "sealed");   
            Boolean privileged = (Boolean) getField(origCtor, origCtor.getClass(), "privileged");
            Object origInitializedValue = getField(origCtor, origCtor.getClass(), "initializedValue");
            Integer state = (Integer) getField(origCtor, origCtor.getClass(), "state");
            SimpleScriptable dummy = new SimpleScriptable();
            LazilyLoadedCtor newCtor  = new LazilyLoadedCtor(dummy, propertyName, className, sealed);
            originalsToClonesMap.put(origCtor, newCtor);
            setField(newCtor, newCtor.getClass(), "scope",newScope);
            setField(newCtor, newCtor.getClass(),"privileged",privileged);
            Object newInitializedValue = cloneObjectOrDie(origInitializedValue, originalsToClonesMap, contextFactory);
            setField(newCtor, newCtor.getClass(),"initializedValue",newInitializedValue);
            setField(newCtor, newCtor.getClass(), "state", state);
            return newCtor;
        }
        if(original instanceof Number){
            Number res = null;
            if(original instanceof Double)
                res = new Double((Double)original);
            if(original instanceof Float)
                res = new Float((Float)original);
            if(original instanceof Integer)
                res = new Integer((Integer)original);
            if(original instanceof Long)
                res = new Long((Long)original);
            if(original instanceof Short)
                res = new Short((Short)original);
            if(original instanceof Byte)
                res = new Byte((Byte)original);
            originalsToClonesMap.put(original, res);
            return res;
        }
        if(original instanceof Boolean){
            Boolean newbool = new Boolean((Boolean)original);
            originalsToClonesMap.put(original, newbool);
            return newbool;
        }
        if(original instanceof String || original instanceof Undefined || original instanceof UniqueTag || original instanceof SecurityController || original instanceof IdFunctionCall){
            originalsToClonesMap.put(original,original);
            return original;
        }
        try{
            Class MemberBoxClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.MemberBox");
            Class InterpreterDataClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.InterpreterData");
            if(MemberBoxClass.isInstance(original) || InterpreterDataClass.isInstance(original)){
                originalsToClonesMap.put(original, original);
                return original;
            }
            
        }catch(ClassNotFoundException ex){
            throw new RuntimeException("The API changed!");
        }
        throw new RuntimeException("Alas, I couldn't clone this");
    }
    private static void copyIdScriptableObjectContents(IdScriptableObject originalScriptableObject, IdScriptableObject newScriptableObject, HashMap<Object, Object> originalsToClonesMap, ContextFactory contextFactory) throws InstantiationException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        copyScriptableObjectContents(originalScriptableObject, newScriptableObject, originalsToClonesMap, contextFactory);
        //a.prototypeValues
        try {
            Class PrototypeValuesClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.IdScriptableObject$PrototypeValues");
            Object origProtValues= getField(originalScriptableObject, originalScriptableObject.getClass(), "prototypeValues");
            if(origProtValues != null){
                //obj
                IdScriptableObject origObj = (IdScriptableObject) getField(origProtValues, origProtValues.getClass(), "obj");
                IdScriptableObject newObj  = (IdScriptableObject) deepCopyScriptable(origObj, originalsToClonesMap, contextFactory);
                //maxId
                Integer maxId = (Integer) getField(origProtValues, origProtValues.getClass(), "maxId");
                Constructor ctorPrototypeValuesClass = PrototypeValuesClass.getDeclaredConstructor(IdScriptableObject.class, Integer.TYPE);
                ctorPrototypeValuesClass.setAccessible(true);
                Object newProtValues = ctorPrototypeValuesClass.newInstance(newObj, maxId);
                // valueArray
                Object[] origValueArray = (Object[]) getField(origProtValues, origProtValues.getClass(), "valueArray");
                Object[] newValueArray = null;
                if (origValueArray != null) {
                    newValueArray = new Object[origValueArray.length];
                    for(int i = 0; i < newValueArray.length; i++){
                        newValueArray[i] = cloneObjectOrDie(origValueArray[i], originalsToClonesMap, contextFactory);
                    }
                    setField(newProtValues, newProtValues.getClass(), "valueArray",newValueArray );
                }
                //attributeArray
                short[] origAttributeArray = (short[]) getField(origProtValues, origProtValues.getClass(), "attributeArray");
                short[] newAttributeArray = null;
                if (origAttributeArray != null) {
                    newAttributeArray = origAttributeArray.clone();
                    setField(newProtValues, newProtValues.getClass(), "attributeArray",newAttributeArray );
                }
                //lastFoundId
                Integer lastFoundId = (Integer) getField(origProtValues, origProtValues.getClass(), "lastFoundId");
                setField(newProtValues, newProtValues.getClass(), "lastFoundId", lastFoundId);
                //constructorId
                Integer constructorId = (Integer) getField(origProtValues, origProtValues.getClass(), "constructorId");
                setField(newProtValues, newProtValues.getClass(), "constructorId", constructorId);
                //constructor
                IdFunctionObject origConstructor = (IdFunctionObject) getField(origProtValues, origProtValues.getClass(), "constructor");
                IdFunctionObject newConstructor = (IdFunctionObject) deepCopyScriptable(origConstructor, originalsToClonesMap, contextFactory);
                setField(newProtValues, newProtValues.getClass(), "constructor",newConstructor);
                //constructorAttrs
                Short constructorAttrs = (Short) getField(origProtValues, origProtValues.getClass(), "constructorAttrs");
                setField(newProtValues, newProtValues.getClass(), "constructorAttrs", constructorAttrs);
                setField(newScriptableObject, newScriptableObject.getClass(),"prototypeValues", newProtValues);
            }
        } catch (ClassNotFoundException e) {
            throw  new RuntimeException("Api changed");
        }
    }
    private static void copyScriptableObjectContents(ScriptableObject originalScriptableObject, ScriptableObject newScriptableObject, HashMap<Object, Object> originalsToClonesMap, ContextFactory contextFactory) throws InstantiationException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        //a. prototypeObject
        Scriptable newPrototype = deepCopyScriptable(originalScriptableObject.getPrototype(), originalsToClonesMap, contextFactory);
        newScriptableObject.setPrototype(newPrototype);
        //b. parentScopeObject
        Scriptable origParentScope = (Scriptable) getField(originalScriptableObject, originalScriptableObject.getClass(), "parentScopeObject");
        if(wasAlreadyCloned(originalScriptableObject.getParentScope(), originalsToClonesMap)){
            newScriptableObject.setParentScope((Scriptable)getAlreadyCloned(origParentScope, originalsToClonesMap));
        }else{
            if(originalScriptableObject instanceof Window){
                newScriptableObject.setParentScope(newScriptableObject);
            }else{
                ScriptableObject newParent = (ScriptableObject) deepCopyScriptable(origParentScope, originalsToClonesMap, contextFactory);
                setField(newScriptableObject, newScriptableObject.getClass(), "parentScopeObject", newParent);
            }
        }
        //c. count
        Integer count = (Integer) getField(originalScriptableObject, originalScriptableObject.getClass(), "count");
        setField(newScriptableObject, newScriptableObject.getClass(), "count", count);
        //d. slots
        Class SlotClass = ScriptableObject.class.getDeclaredClasses()[1];
        Class GetterSlotClass = ScriptableObject.class.getDeclaredClasses()[0];
        //d.1 clone Slots - first round
        Object [] originalSlots = (Object[]) getField(originalScriptableObject, originalScriptableObject.getClass(), "slots");
        Object newSlots = null;
        if(originalSlots != null){
            newSlots = Array.newInstance(SlotClass,originalSlots.length );
            for(int i = 0 ; i < originalSlots.length; i++){
                Object originalSlot = originalSlots[i];
                Object newSlot = null, prevNewSlot = null, firstNewSlot = null;
                while(originalSlot != null){
                    String name = (String) getField(originalSlot, originalSlot.getClass(), "name");
                    Integer indexOrHash = (Integer) getField(originalSlot, originalSlot.getClass(), "indexOrHash");
                    Short attributes = (Short) getField(originalSlot, originalSlot.getClass(), "attributes");
                    Boolean wasDeleted = (Boolean) getField(originalSlot, originalSlot.getClass(), "wasDeleted");
                    Object originalValue = getField(originalSlot, originalSlot.getClass(), "value");
                    Object newValue = null;
                    if(!wasDeleted && originalValue != null){
                        newValue = cloneObjectOrDie(originalValue, originalsToClonesMap, contextFactory);
                    }
                    if(GetterSlotClass.isInstance(originalSlot)){
                        // GetterSlot-specific actions
                        Constructor getterSlotCtor  = GetterSlotClass.getDeclaredConstructor(String.class, Integer.TYPE, Integer.TYPE);
                        getterSlotCtor.setAccessible(true);
                        newSlot = getterSlotCtor.newInstance(name, indexOrHash, attributes);
                        Object originalGetter = getField(originalSlot, GetterSlotClass, "getter");
                        Object originalSetter = getField(originalSlot, GetterSlotClass, "setter");
                        Object newGetter = null, newSetter = null;
                        if(originalGetter != null){
                            newGetter = cloneObjectOrDie(originalGetter, originalsToClonesMap, contextFactory);
                        }
                        if(originalSetter != null){
                            newSetter = cloneObjectOrDie(originalSetter, originalsToClonesMap, contextFactory);
                        }
                        setField(newSlot, newSlot.getClass(), "getter", newGetter);
                        setField(newSlot, newSlot.getClass(), "setter", newSetter);
                    }else{
                        //Slot-specific actions
                        Constructor slotCtor  = SlotClass.getDeclaredConstructor(String.class, Integer.TYPE, Integer.TYPE);
                        slotCtor.setAccessible(true);
                        newSlot = slotCtor.newInstance(name, indexOrHash, attributes);
                    }
                    if(firstNewSlot == null){
                        firstNewSlot = newSlot;
                    }
                    //Common actions
                    setField(newSlot, newSlot.getClass(), "wasDeleted", wasDeleted);
                    if(!wasDeleted){
                        setField(newSlot, newSlot.getClass(), "value", newValue);
                    }
                    if(prevNewSlot != null){
                        setField(prevNewSlot, prevNewSlot.getClass(), "next", newSlot);
                    }
                    originalsToClonesMap.put(originalSlot, newSlot);
                    originalSlot = getField(originalSlot, originalSlot.getClass(), "next");
                    prevNewSlot = newSlot;
                }

                Array.set(newSlots,i, firstNewSlot);
            }
            //d.2 clone Slots - second round
            for(int i = 0 ; i < originalSlots.length; i++){
                Object originalSlot = originalSlots[i];
                Object newSlot = Array.get(newSlots, i);
                if(newSlot != null){
                    Object originalOrderedNext = getField(originalSlot, originalSlot.getClass(), "orderedNext");
                    Object newOrderedNext = null;
                    if (originalOrderedNext != null) {
                        Boolean wasDeleted = (Boolean) getField(originalOrderedNext, originalOrderedNext.getClass(), "wasDeleted");
                        if(!wasDeleted){
                            if(!wasAlreadyCloned(originalOrderedNext, originalsToClonesMap))
                                throw new RuntimeException("Cannot determine 'orderedNext' for slot!!!");
                            newOrderedNext = getAlreadyCloned(originalOrderedNext, originalsToClonesMap);
                        }
                    }
                    setField(newSlot, newSlot.getClass(), "orderedNext", newOrderedNext);
                }
            }
        }
        setField(newScriptableObject, newScriptableObject.getClass(), "slots", newSlots);
        //e. firstAdded and lastAdded
        Object  originalFirstAdded =  getField(originalScriptableObject, originalScriptableObject.getClass(), "firstAdded");
        if (originalFirstAdded != null) {
            Boolean wasDeleted = (Boolean) getField(originalFirstAdded, originalFirstAdded.getClass(), "wasDeleted");
            if(!wasDeleted){
                if(!wasAlreadyCloned(originalFirstAdded, originalsToClonesMap)){
                    throw new RuntimeException("Cannot determine firstAdded when cloning ScriptableObject");
                }
                setField(newScriptableObject, newScriptableObject.getClass(), "firstAdded", getAlreadyCloned(originalFirstAdded, originalsToClonesMap));
            }
        }
        Object  originalLastAdded =  getField(originalScriptableObject, originalScriptableObject.getClass(), "lastAdded");
        if (originalLastAdded != null) {
            Boolean wasDeleted = (Boolean) getField(originalLastAdded, originalLastAdded.getClass(), "wasDeleted");
            if(!wasDeleted){
                if( !wasAlreadyCloned(originalLastAdded, originalsToClonesMap)){
                    throw new RuntimeException("Cannot determine lastAdded when cloning ScriptableObject");
                }
                setField(newScriptableObject, newScriptableObject.getClass(), "lastAdded", getAlreadyCloned(originalLastAdded, originalsToClonesMap));
            }
        }
        //f. associatedValues 
        Map<Object, Object> originalAssociatedValues = (Map<Object, Object>) getField(originalScriptableObject, originalScriptableObject.getClass(), "associatedValues");
        Map<Object, Object> newAssociatedValues = null;
        if (originalAssociatedValues != null) {
            newAssociatedValues = new HashMap<Object, Object>();
            for(Map.Entry<Object, Object> originalEntry : originalAssociatedValues.entrySet()){
                Object origKey = originalEntry.getKey(), origValue = originalEntry.getValue();
                Object newKey = null, newValue = null;
                //key...
                if(origKey instanceof Scriptable){
                    newKey = deepCopyScriptable((Scriptable)origKey, originalsToClonesMap, contextFactory);
                }else if(origKey instanceof String){
                    newKey = origKey;
                }else{
                    throw new RuntimeException("AssociatedValues: key not a Scriptable or a String");
                }
                //value
                if(origValue instanceof Scriptable){
                    newValue = deepCopyScriptable((Scriptable)origValue, originalsToClonesMap, contextFactory);
                }else if(origKey instanceof String){
                    newValue = origValue;
                }else{
                    throw new RuntimeException("AssociatedValues: value not a Scriptable or a String");
                }
                newAssociatedValues.put(newKey, newValue);
            }
        }
        setField(newScriptableObject, newScriptableObject.getClass(), "associatedValues", newAssociatedValues);
    }
    private static EventListenersContainer deepCopyEventListenersContainer(EventListenersContainer origContainer, SimpleScriptable newNode, HashMap<Object, Object> originalsToClonesMap, ContextFactory contextFactory)throws InstantiationException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        if(wasAlreadyCloned(origContainer, originalsToClonesMap))
            return (EventListenersContainer) getAlreadyCloned(origContainer, originalsToClonesMap);
        if(origContainer == null)
            return null;
        //a.1. construct elc
        Constructor elcConstructor = EventListenersContainer.class.getDeclaredConstructor(SimpleScriptable.class);
        elcConstructor.setAccessible(true);
        EventListenersContainer newContainer = (EventListenersContainer) elcConstructor.newInstance(newNode);
        originalsToClonesMap.put(origContainer, newContainer);
        //a.2. clone eventHandlers_
        Class HandlersClass = EventListenersContainer.class.getDeclaredClasses()[0];
        Constructor ctor = HandlersClass.getDeclaredConstructor();
        ctor.setAccessible(true);
        Map origEventHandlers = (Map) getField(origContainer, origContainer.getClass(), "eventHandlers_");
        Map newEventHandlers = new HashMap();
        for(Object o : origEventHandlers.entrySet()){
            Map.Entry origEntry = (Map.Entry) o;
            String key = (String) origEntry.getKey();
            Object origHandlers = origEntry.getValue();
            Object newHandlers = ctor.newInstance();
            //a.2.1  capturingHandlers_
            List<Function> origCapturingHandlers = (List<Function>) getField(origHandlers, origHandlers.getClass(), "capturingHandlers_");
            List<Function> newCapturingHandlers = new ArrayList<Function>();
            for(Function origFunction: origCapturingHandlers){
                newCapturingHandlers.add((Function) deepCopyScriptable(origFunction, originalsToClonesMap, contextFactory));
            }
            setField(newHandlers,newHandlers.getClass(),"capturingHandlers_",newCapturingHandlers);
            //a.2.2  bubblingHandlers_
            List<Function> origBubblingHandlers = (List<Function>) getField(origHandlers, origHandlers.getClass(), "bubblingHandlers_");
            List<Function> newBubblingHandlers = new ArrayList<Function>();
            for(Function origFunction: origBubblingHandlers){
                newBubblingHandlers.add((Function) deepCopyScriptable(origFunction, originalsToClonesMap, contextFactory));
            }
            setField(newHandlers,newHandlers.getClass(),"bubblingHandlers_",newBubblingHandlers);
            //a.2.3  handler_
            Object origHandler =  getField(origHandlers, origHandlers.getClass(),"handler_");
            Scriptable newHandler = null;
            if(origHandler != null){
                if(! (origHandler instanceof Scriptable))
                    throw new RuntimeException("Handler not a scriptable??? WTF???");
                newHandler = deepCopyScriptable((Scriptable) origHandler, originalsToClonesMap, contextFactory);
            }
            setField(newHandlers,newHandlers.getClass(),"handler_",newHandler);
            newEventHandlers.put(key, newHandlers);
        }
        setField(newContainer, newContainer.getClass(), "eventHandlers_", newEventHandlers);
        return newContainer;
    }
    private static boolean wasAlreadyCloned(Object o, HashMap<Object, Object> originalsToClonesMap){
        return originalsToClonesMap.containsKey(o) ||originalsToClonesMap.containsValue(o);
    }
    private static Object getAlreadyCloned(Object o, HashMap<Object, Object> originalsToClonesMap){
        if(originalsToClonesMap.containsKey(o))
            return originalsToClonesMap.get(o);
        if(originalsToClonesMap.containsValue(o))
            return o;
        throw new RuntimeException("The object that should have been cloned is not cloned!");
    }
    private static Scriptable deepCopyScriptable(Scriptable originalScriptable, HashMap<Object, Object> originalsToClonesMap, ContextFactory contextFactory) throws InstantiationException, NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        if(wasAlreadyCloned(originalScriptable, originalsToClonesMap)){
            return (Scriptable)getAlreadyCloned(originalScriptable, originalsToClonesMap);
        }
        //================================================
        //I. SimpleScriptable and its descendants
        //================================================
        if(originalScriptable instanceof SimpleScriptable){
            //Basic stuff
            //a. Clone SimpleScriptable
            SimpleScriptable origSimpleScriptable = (SimpleScriptable) originalScriptable;
            SimpleScriptable newSimpleScriptable   = origSimpleScriptable.clone();
            originalsToClonesMap.put(originalScriptable, newSimpleScriptable);
            //b. copy ScriptableObject contents
            copyScriptableObjectContents(origSimpleScriptable, newSimpleScriptable,originalsToClonesMap, contextFactory);
            //c. set domNode_
            DomNode origDomNode = origSimpleScriptable.getDomNodeOrNull();
            if(origDomNode != null){
                if(wasAlreadyCloned(origDomNode, originalsToClonesMap)){
                    setField(newSimpleScriptable, newSimpleScriptable.getClass(), "domNode_", getAlreadyCloned(origDomNode, originalsToClonesMap));
                }else{
                    if(!wasAlreadyCloned(origDomNode.getParentNode(), originalsToClonesMap) || !wasAlreadyCloned(origDomNode.getPage().getEnclosingWindow(), originalsToClonesMap)){
                        throw new RuntimeException("Dome Node not cloned ???");
                    }
                    DomNode theParent = (DomNode) getAlreadyCloned(origDomNode.getParentNode(), originalsToClonesMap);
                    WebWindow theWindow = (WebWindow) getAlreadyCloned(origDomNode.getPage().getEnclosingWindow(),  originalsToClonesMap);
                    newSimpleScriptable.setDomNode(deepCopyDom(origDomNode, theParent, theWindow, originalsToClonesMap));
                }
            }else{
                setField(newSimpleScriptable, newSimpleScriptable.getClass(), "domNode_", null);
            }
            //=====================Node========================
            if(origSimpleScriptable instanceof Node){
                Node origNode = (Node) origSimpleScriptable;
                Node newNode = (Node) newSimpleScriptable;
                //a. eventListenersContainer_
                EventListenersContainer origContainer = (EventListenersContainer) getField(origNode, origNode.getClass(), "eventListenersContainer_");
                EventListenersContainer newContainer  = deepCopyEventListenersContainer(origContainer, newSimpleScriptable, originalsToClonesMap, contextFactory);
                setField(newNode, newNode.getClass(), "eventListenersContainer_", newContainer);

            }
            //=================NamedNodeMap====================
            if(origSimpleScriptable instanceof NamedNodeMap){
                NamedNodeMap origMap = (NamedNodeMap) origSimpleScriptable;
                NamedNodeMap newMap = (NamedNodeMap) newSimpleScriptable;
                Object origAttributes = getField(origMap, origMap.getClass(),"attributes_");
                if(!wasAlreadyCloned(origAttributes, originalsToClonesMap))
                    throw new RuntimeException("attributes from DomElement not properly cloned");
                setField(newMap,newMap.getClass(), "attributes_",getAlreadyCloned(origAttributes, originalsToClonesMap));
            }
            //=================HtmlCollection==================
            if(origSimpleScriptable instanceof HTMLCollection){
                //the elements will be recomputed
                setField(origSimpleScriptable, origSimpleScriptable.getClass(),"cachedElements_", null);
            }
            //==================CSSStyleSheet==================
            if(origSimpleScriptable instanceof CSSStyleSheet){
                CSSStyleSheet origSheet = (CSSStyleSheet) origSimpleScriptable;
                CSSStyleSheet newSheet = (CSSStyleSheet) newSimpleScriptable;
                //a. ownerNode_
                HTMLElement origOwnerNode = (HTMLElement) getField(origSheet, origSheet.getClass(), "ownerNode_");
                HTMLElement newOwnerNode = (HTMLElement) deepCopyScriptable(origOwnerNode, originalsToClonesMap, contextFactory);
                setField(newSheet, newSheet.getClass(), "ownerNode_", newOwnerNode);
                //b. wrapped_
                org.w3c.dom.css.CSSStyleSheet wrapped = (org.w3c.dom.css.CSSStyleSheet) getField(origSheet, origSheet.getClass(), "wrapped_");
                setField(newSheet, newSheet.getClass(), "wrapped_", wrapped);
                //c. imports_
                Map<org.w3c.dom.css.CSSImportRule, CSSStyleSheet> origImports = (Map<CSSImportRule, CSSStyleSheet>) getField(origSheet, origSheet.getClass(), "imports_");
                Map<org.w3c.dom.css.CSSImportRule, CSSStyleSheet> newImports = null;
                if (origImports != null) {
                    newImports = new HashMap<CSSImportRule, CSSStyleSheet>();
                    for(Map.Entry<CSSImportRule, CSSStyleSheet> origEntry : origImports.entrySet()){
                        CSSImportRule key = origEntry.getKey();
                        CSSStyleSheet origValue = origEntry.getValue();
                        CSSStyleSheet newValue = (CSSStyleSheet) deepCopyScriptable(origValue, originalsToClonesMap, contextFactory);
                        newImports.put(key, newValue);
                    }
                }
                setField(newSheet, newSheet.getClass(), "imports_", newImports);
                //d. cssRules_
                CSSRuleList origRuleList = (CSSRuleList) getField(origSheet, origSheet.getClass(), "cssRules_");
                CSSRuleList newRuleList = (CSSRuleList) deepCopyScriptable(origRuleList, originalsToClonesMap, contextFactory);
                setField(newSheet, newSheet.getClass(), "cssRules_", newRuleList);
            }
            //===================CSSRuleList===================
            if(origSimpleScriptable instanceof CSSRuleList){
                CSSRuleList origRuleList = (CSSRuleList) origSimpleScriptable;
                CSSRuleList newRuleList = (CSSRuleList) newSimpleScriptable;
                /*
                private final CSSStyleSheet stylesheet_;
                private final org.w3c.dom.css.CSSRuleList rules_;
                */
                //a. stylesheet_
                CSSStyleSheet origStyleSheet = (CSSStyleSheet) getField(origRuleList, origRuleList.getClass(), "stylesheet_");
                CSSStyleSheet newStyleSheet = (CSSStyleSheet) deepCopyScriptable(origStyleSheet, originalsToClonesMap, contextFactory);
                setField(newRuleList, newRuleList.getClass(), "stylesheet_", newStyleSheet);
                //b. rules_
                org.w3c.dom.css.CSSRuleList ruleList = (org.w3c.dom.css.CSSRuleList) getField(origRuleList, origRuleList.getClass(), "rules_");
                setField(newRuleList, newRuleList.getClass(), "rules_", ruleList);
            }
            //================StyleSheetList===================
            if(origSimpleScriptable instanceof StyleSheetList){
                StyleSheetList origStyleSheetList = (StyleSheetList) origSimpleScriptable;
                StyleSheetList newStyleSheetList = (StyleSheetList) newSimpleScriptable;
                HTMLCollection origCollection, newCollection;
                //a. nodes_
                origCollection = (HTMLCollection) getField(origStyleSheetList, origStyleSheetList.getClass(), "nodes_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newStyleSheetList, newStyleSheetList.getClass(), "nodes_", newCollection);
            }
            //==============NamespaceCollection===============
            if(origSimpleScriptable instanceof NamespaceCollection){
                NamespaceCollection origNSCol = (NamespaceCollection) origSimpleScriptable;
                NamespaceCollection newNSCol = (NamespaceCollection) newSimpleScriptable;
                //a. doc_
                HTMLDocument origDoc = (HTMLDocument) getField(origNSCol,origNSCol.getClass(), "doc_");
                HTMLDocument newDoc = (HTMLDocument) deepCopyScriptable(origDoc, originalsToClonesMap, contextFactory);
                setField(newNSCol, newNSCol.getClass(), "doc_", newDoc);
                //b. namespaces_
                List<Namespace> origNSList = (List<Namespace>) getField(origNSCol,origNSCol.getClass(), "namespaces_");
                List<Namespace> newNSList = new ArrayList<Namespace>();
                for(Namespace origNS: origNSList){
                    Namespace newNS = (Namespace) deepCopyScriptable(origNS, originalsToClonesMap, contextFactory);
                    newNSList.add(newNS);
                }
                setField(newNSCol, newNSCol.getClass(), "namespaces_", newNSList);
            }
            //==============HTMLOptionsCollection=============
            if(origSimpleScriptable instanceof HTMLOptionsCollection){
                HTMLOptionsCollection origCol = (HTMLOptionsCollection) origSimpleScriptable;
                HTMLOptionsCollection newCol = (HTMLOptionsCollection) newSimpleScriptable;
                HtmlSelect origSelect = (HtmlSelect) getField(origCol, origCol.getClass(), "htmlSelect_");
                if(!wasAlreadyCloned(origSelect, originalsToClonesMap)){
                    throw  new RuntimeException("Select not properly cloned!");
                }
                HtmlSelect newSelect  = (HtmlSelect) getAlreadyCloned(origSelect, originalsToClonesMap);
                setField(newCol, newCol.getClass(), "htmlSelect_", newSelect);
                
            }
            //====================BoxObject====================
            if(origSimpleScriptable instanceof BoxObject){
                BoxObject origBox = (BoxObject) origSimpleScriptable;
                BoxObject newBox = (BoxObject) newSimpleScriptable;
                //a. element_
                HTMLElement origElem = (HTMLElement) getField(origBox, origBox.getClass(), "element_");
                HTMLElement newElem = (HTMLElement) deepCopyScriptable(origElem, originalsToClonesMap, contextFactory);
                setField(newBox, newBox.getClass(), "element_", newElem);
            }
            //===================Document======================
            if(origSimpleScriptable instanceof Document){
                Document origDoc = (Document) origSimpleScriptable;
                Document newDoc = (Document) newSimpleScriptable;
                //a. window_
                Window origWin = (Window) getField(origDoc, origDoc.getClass(), "window_");
                if(!wasAlreadyCloned(origWin, originalsToClonesMap))
                    throw new RuntimeException("Window not cloned properly!!!");
                Window newWin = (Window) getAlreadyCloned(origWin, originalsToClonesMap);
                setField(newDoc, newDoc.getClass(), "window_", newWin);
                //b. implementation_
                setField(newDoc, newDoc.getClass(), "implementation_", null);
            }
            //===================Element=======================
            if(origSimpleScriptable instanceof Element){
                Element origEl = (Element) origSimpleScriptable;
                Element newEl = (Element) newSimpleScriptable;
                //a. attributes_
                NamedNodeMap origAttr = (NamedNodeMap) getField(origEl,origEl.getClass(), "attributes_");
                NamedNodeMap newAttr = (NamedNodeMap) deepCopyScriptable(origAttr, originalsToClonesMap, contextFactory);
                setField(newEl, newEl.getClass(), "attributes_", newAttr);
                //b. elementsByTagName_
                Map<String, HTMLCollection> origElByTagName = (Map<String, HTMLCollection>) getField(origEl, origEl.getClass(), "elementsByTagName_");
                Map<String, HTMLCollection> newElByTagName = null ;
                if (origElByTagName != null) {
                    newElByTagName = new HashMap<String, HTMLCollection>();
                    for(Map.Entry<String, HTMLCollection> origEntry: origElByTagName.entrySet()){
                        String key = origEntry.getKey();
                        HTMLCollection origVal = origEntry.getValue();
                        HTMLCollection newVal = (HTMLCollection) deepCopyScriptable(origVal, originalsToClonesMap, contextFactory);
                        newElByTagName.put(key, newVal);
                    }
                }
                setField(newEl, newEl.getClass(), "elementsByTagName_", newElByTagName);
            }
            //=================HtmlElement=====================
            if(origSimpleScriptable instanceof HTMLElement){
                HTMLElement origHtmlElement = (HTMLElement) origSimpleScriptable;
                HTMLElement newHtmlElement = (HTMLElement) newSimpleScriptable;
                //a. all_
                HTMLCollection origAll = (HTMLCollection) getField(origHtmlElement, origHtmlElement.getClass(), "all_");
                HTMLCollection newAll =  (HTMLCollection) deepCopyScriptable(origAll, originalsToClonesMap, contextFactory);
                setField(newHtmlElement, newHtmlElement.getClass(), "all_", newAll);
                //b. behaviors_
                Set<String> oldBehaviors = (Set<String>) getField(origHtmlElement, origHtmlElement.getClass(), "behaviors_");
                Set<String> newBehaviors = new  HashSet<String>();
                newBehaviors.addAll(oldBehaviors);
                setField(newHtmlElement, newHtmlElement.getClass(),"behaviors_", newBehaviors);
                //c. boxObject_
                BoxObject origBox = (BoxObject) getField(origHtmlElement, origHtmlElement.getClass(), "boxObject_");
                BoxObject newBox = (BoxObject) deepCopyScriptable(origBox, originalsToClonesMap, contextFactory);
                setField(newHtmlElement, newHtmlElement.getClass(), "boxObject_", newBox);
            }
            //================HTMLDocument=====================
            if(origSimpleScriptable instanceof HTMLDocument){
                HTMLDocument origHtmlDoc = (HTMLDocument) origSimpleScriptable;
                HTMLDocument newHtmlDoc  = (HTMLDocument) newSimpleScriptable;
                //a. activeElement_
                HTMLElement origActive = (HTMLElement) getField(origHtmlDoc,origHtmlDoc.getClass(), "activeElement_");
                HTMLElement newActive = (HTMLElement) deepCopyScriptable(origActive, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(),"activeElement_", newActive);
                HTMLCollection origCollection, newCollection;
                //b. all_
                origCollection = (HTMLCollection) getField(origHtmlDoc, origHtmlDoc.getClass(), "all_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(), "all_", newCollection);
                //c. forms_
                origCollection = (HTMLCollection) getField(origHtmlDoc, origHtmlDoc.getClass(), "forms_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(), "forms_", newCollection);
                //d. links_
                origCollection = (HTMLCollection) getField(origHtmlDoc, origHtmlDoc.getClass(), "links_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(), "links_", newCollection);
                //e. images_
                origCollection = (HTMLCollection) getField(origHtmlDoc, origHtmlDoc.getClass(), "images_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(), "images_", newCollection);
                //f. scripts_
                origCollection = (HTMLCollection) getField(origHtmlDoc, origHtmlDoc.getClass(), "scripts_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(), "scripts_", newCollection);
                //g. anchors_
                origCollection = (HTMLCollection) getField(origHtmlDoc, origHtmlDoc.getClass(), "anchors_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(), "anchors_", newCollection);
                //h. applets_
                origCollection = (HTMLCollection) getField(origHtmlDoc, origHtmlDoc.getClass(), "applets_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(), "applets_", newCollection);
                //i. styleSheets_
                StyleSheetList origList = (StyleSheetList) getField(origHtmlDoc, origHtmlDoc.getClass(), "styleSheets_");
                StyleSheetList newList = (StyleSheetList) deepCopyScriptable(origList, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(), "styleSheets_", newList);
                //j. namespaces
                NamespaceCollection origNsCol = (NamespaceCollection) getField(origHtmlDoc, origHtmlDoc.getClass(), "namespaces_");
                NamespaceCollection newNsCol = (NamespaceCollection) deepCopyScriptable(origNsCol, originalsToClonesMap, contextFactory);
                setField(newHtmlDoc, newHtmlDoc.getClass(), "namespaces_", newNsCol);
            }
            //================RowContainer=====================
            if(origSimpleScriptable instanceof RowContainer){
                RowContainer origRC = (RowContainer) origSimpleScriptable;
                RowContainer newRC = (RowContainer) newSimpleScriptable;
                //a. rows_
                HTMLCollection origRows = (HTMLCollection) getField(origRC, origRC.getClass(), "rows_");
                HTMLCollection newRows =  (HTMLCollection) deepCopyScriptable(origRows, originalsToClonesMap, contextFactory);
                setField(newRC, newRC.getClass(), "rows_", newRows);
            }
            //==============-HTMLFormElement===================
            if(origSimpleScriptable instanceof HTMLFormElement){
                HTMLFormElement origEl = (HTMLFormElement) origSimpleScriptable;
                HTMLFormElement newEl = (HTMLFormElement) newSimpleScriptable;
                //a. elements_
                HTMLCollection origElems = (HTMLCollection) getField(origEl, origEl.getClass(), "elements_");
                HTMLCollection newElems =  (HTMLCollection) deepCopyScriptable(origElems, originalsToClonesMap, contextFactory);
                setField(newEl, newEl.getClass(), "elements_", newElems);
            }
            //===============HTMLLinkElement===================
            if(origSimpleScriptable instanceof HTMLLinkElement){
                HTMLLinkElement origLink = (HTMLLinkElement) origSimpleScriptable;
                HTMLLinkElement newLink = (HTMLLinkElement) newSimpleScriptable;
                CSSStyleSheet origSheet = (CSSStyleSheet) getField(origLink, origLink.getClass(), "sheet_");
                CSSStyleSheet newSheet = (CSSStyleSheet) deepCopyScriptable(origSheet, originalsToClonesMap, contextFactory);
                setField(newLink, newLink.getClass(), "sheet_", newSheet);
            }
            //=================HTMLMapElement==================
            if(origSimpleScriptable instanceof HTMLMapElement){
                HTMLMapElement origMap = (HTMLMapElement) origSimpleScriptable;
                HTMLMapElement newMap  = (HTMLMapElement) newSimpleScriptable;
                HTMLCollection origAreas = (HTMLCollection) getField(origMap, origMap.getClass(), "areas_");
                HTMLCollection newAreas  = (HTMLCollection) deepCopyScriptable(origAreas, originalsToClonesMap, contextFactory);
                setField(newMap, newMap.getClass(), "areas_", newAreas);
            }
            //===============HTMLObjectElement==================
            if(origSimpleScriptable instanceof HTMLObjectElement){
                logger.warn("ActiveX is not working!");
            }
            //===============OfflineResourceList================
            if(origSimpleScriptable instanceof OfflineResourceList){
                OfflineResourceList origResourcesList = (OfflineResourceList) origSimpleScriptable;
                OfflineResourceList newResourcesList = (OfflineResourceList) newSimpleScriptable;
                Object origObj, newObj;
                //a. onchecking_
                origObj = getField(origResourcesList, origResourcesList.getClass(), "onchecking_");
                newObj = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                setField(newResourcesList, newResourcesList.getClass(), "onchecking_", newObj);
                //a. onerror_
                origObj = getField(origResourcesList, origResourcesList.getClass(), "onerror_");
                newObj = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                setField(newResourcesList, newResourcesList.getClass(), "onerror_", newObj);
                //b. onnoupdate_
                origObj = getField(origResourcesList, origResourcesList.getClass(), "onnoupdate_");
                newObj = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                setField(newResourcesList, newResourcesList.getClass(), "onnoupdate_", newObj);
                //c. ondownloading_
                origObj = getField(origResourcesList, origResourcesList.getClass(), "ondownloading_");
                newObj = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                setField(newResourcesList, newResourcesList.getClass(), "ondownloading_", newObj);
                //d. onprogress_
                origObj = getField(origResourcesList, origResourcesList.getClass(), "onprogress_");
                newObj = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                setField(newResourcesList, newResourcesList.getClass(), "onprogress_", newObj);
                //e. onupdateready_
                origObj = getField(origResourcesList, origResourcesList.getClass(), "onupdateready_");
                newObj = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                setField(newResourcesList, newResourcesList.getClass(), "onupdateready_", newObj);
                //f. oncached_
                origObj = getField(origResourcesList, origResourcesList.getClass(), "oncached_");
                newObj = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                setField(newResourcesList, newResourcesList.getClass(), "oncached_", newObj);
            }
            //===============HTMLStyleElement==================
            if(origSimpleScriptable instanceof HTMLStyleElement){
                HTMLStyleElement origStyle = (HTMLStyleElement) origSimpleScriptable;
                HTMLStyleElement newStyle = (HTMLStyleElement) newSimpleScriptable;
                CSSStyleSheet origSheet = (CSSStyleSheet) getField(origStyle, origStyle.getClass(), "sheet_");
                CSSStyleSheet newSheet = (CSSStyleSheet) deepCopyScriptable(origSheet, originalsToClonesMap, contextFactory);
                setField(newStyle, newStyle.getClass(), "sheet_", newSheet);
            }
            //===============HTMLSelectElement=================
            if(origSimpleScriptable instanceof HTMLSelectElement){
                HTMLSelectElement origSel = (HTMLSelectElement) origSimpleScriptable;
                HTMLSelectElement newSel = (HTMLSelectElement) newSimpleScriptable;
                HTMLOptionsCollection origOptionsArray = (HTMLOptionsCollection) getField(origSel, origSel.getClass(),"optionsArray_");
                HTMLOptionsCollection newOptionsArray  = (HTMLOptionsCollection) deepCopyScriptable(origOptionsArray, originalsToClonesMap, contextFactory);
                setField(newSel, newSel.getClass(),"optionsArray_", newOptionsArray);
            }
            //================HTMLTableElement=================
            if(origSimpleScriptable instanceof HTMLTableElement){
                HTMLTableElement origTableEl = (HTMLTableElement) origSimpleScriptable;
                HTMLTableElement newTableEl  = (HTMLTableElement) newSimpleScriptable;
                HTMLCollection origCollection, newCollection;
                //a. tbodies_
                origCollection = (HTMLCollection) getField(origTableEl, origTableEl.getClass(), "tBodies_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newTableEl, newTableEl.getClass(), "tBodies_", newCollection);
            }
            //==============HTMLTableRowElement=================
            if(origSimpleScriptable instanceof  HTMLTableRowElement){
                HTMLTableRowElement origRowEl = (HTMLTableRowElement) origSimpleScriptable;
                HTMLTableRowElement newRowEl  = (HTMLTableRowElement) newSimpleScriptable;
                HTMLCollection origCollection, newCollection;
                //a. cells_
                origCollection = (HTMLCollection) getField(origRowEl, origRowEl.getClass(), "cells_");
                newCollection =  (HTMLCollection) deepCopyScriptable(origCollection, originalsToClonesMap, contextFactory);
                setField(newRowEl, newRowEl.getClass(), "cells_", newCollection);
            }
            //======================Event=======================
            if(origSimpleScriptable instanceof Event){
                Event origEvent = (Event) origSimpleScriptable;
                Event newEvent  = (Event) newSimpleScriptable;
                Object origObj, newObj;
                //a. srcElement_
                origObj = getField(origEvent, origEvent.getClass(), "srcElement_");
                if(origObj != null){
                    if(!(origObj instanceof SimpleScriptable))
                        throw new RuntimeException("srcElement_ not SimpleScriptable - wtf???");
                    newObj = deepCopyScriptable((Scriptable) origObj, originalsToClonesMap, contextFactory);
                    setField(newEvent, newEvent.getClass(), "srcElement_", newObj);
                }
                //b.target_
                origObj = getField(origEvent, origEvent.getClass(), "target_");
                if (origObj != null) {
                    if(!(origObj instanceof SimpleScriptable))
                        throw new RuntimeException("target_ not SimpleScriptable - wtf???");
                    newObj = deepCopyScriptable((Scriptable) origObj, originalsToClonesMap, contextFactory);
                    setField(newEvent, newEvent.getClass(), "target_", newObj);
                }
                //c.currentTarget_
                origObj = getField(origEvent, origEvent.getClass(), "currentTarget_");
                if (origObj != null) {
                    if(!(origObj instanceof SimpleScriptable))
                        throw new RuntimeException("currentTarget_ not SimpleScriptable - wtf???");
                    newObj = deepCopyScriptable((Scriptable) origObj, originalsToClonesMap, contextFactory);
                    setField(newEvent, newEvent.getClass(), "currentTarget_", newObj);
                }
                //d.  keyCode_
                origObj = getField(origEvent, origEvent.getClass(), "keyCode_");
                if (origObj != null) {
                    if(!(origObj instanceof Integer))
                        throw new RuntimeException("keyCode_ not Integer - wtf???");
                    newObj = new Integer((Integer) origObj);
                    setField(newEvent, newEvent.getClass(), "keyCode_", newObj);
                }
                //e. returnValue_
                origObj = getField(origEvent, origEvent.getClass(), "returnValue_");
                if (origObj != null) {
                    if(!(origObj instanceof Boolean))
                        throw new RuntimeException("returnValue_ not Boolean - wtf???");
                    newObj = new Boolean((Boolean) origObj);
                    setField(newEvent, newEvent.getClass(), "returnValue_", newObj);
                }
            }
            //====================Location======================
            if(originalScriptable instanceof Location){
                Location origLocation = (Location) origSimpleScriptable;
                Location newLocation = (Location) newSimpleScriptable;
                Window origWin = (Window) getField(origLocation, origLocation.getClass(), "window_");
                Window newWin = (Window) deepCopyScriptable(origWin, originalsToClonesMap, contextFactory);
                setField(newLocation, newLocation.getClass(), "window_", newWin);
            }
            //====================Window========================
            if(origSimpleScriptable instanceof Window){
                Window origWindow = (Window) origSimpleScriptable;
                Window newWindow  = (Window) newSimpleScriptable;
                //a. document_
                Document origDoc = (Document) getField(origWindow, origWindow.getClass(), "document_");
                Document newDoc  = (Document) deepCopyScriptable(origDoc, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(), "document_", newDoc);
                //b. webWindow_
                Page newPage = (Page) newWindow.getDomNodeOrNull();
                WebWindow newWebWindow = newPage.getEnclosingWindow();
                setField(newWindow, newWindow.getClass(), "webWindow_", newWebWindow);
                //c. navigator
                Navigator origNav = (Navigator) getField(origWindow, origWindow.getClass(), "navigator_");
                Navigator newNav  = (Navigator) deepCopyScriptable(origNav, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(), "navigator_", newNav);
                //d. documentProxy_
                DocumentProxy newProxy = new DocumentProxy(newWindow.getWebWindow());
                setField(newWindow,newWindow.getClass(), "documentProxy_", newProxy);
                //e. screen_
                Screen origScreen = (Screen) getField(origWindow, origWindow.getClass(), "screen_");
                Screen newScreen = (Screen) deepCopyScriptable(origScreen, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(), "screen_", newScreen);
                //f. history_
                History origHist = (History) getField(origWindow, origWindow.getClass(), "history_");
                History newHist = (History) deepCopyScriptable(origHist, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(), "history_", newHist);
                //f. location_
                Location origLoc = (Location) getField(origWindow, origWindow.getClass(), "location_");
                Location newLoc = (Location) deepCopyScriptable(origLoc, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(), "location_", newLoc);
                //g. applicationCache_
                OfflineResourceList origApplicationCache = (OfflineResourceList) getField(origWindow,origWindow.getClass(), "applicationCache_");
                OfflineResourceList newApplicationCache  = (OfflineResourceList) deepCopyScriptable(origApplicationCache, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(),  "applicationCache_", newApplicationCache);
                //h. selection_
                Selection origSel = (Selection) getField(origWindow, origWindow.getClass(), "selection_");
                Selection newSel = (Selection) deepCopyScriptable(origSel, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(), "selection_", newSel);
                //i. currentEvent_
                Event origCurrentEvent = (Event) getField(origWindow, origWindow.getClass(), "currentEvent_");
                Event newCurrentEvent  = (Event) deepCopyScriptable(origCurrentEvent, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(),"currentEvent_", newCurrentEvent);
                //j. frames_
                HTMLCollection origFrames = (HTMLCollection) getField(origWindow, origWindow.getClass(), "frames_");
                HTMLCollection newFrames  = (HTMLCollection) deepCopyScriptable(origFrames, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(),"frames_", newFrames);
                //h. eventListenersContainer_
                EventListenersContainer origContainer = (EventListenersContainer) getField(origWindow, origWindow.getClass(), "eventListenersContainer_");
                EventListenersContainer newContainer  = deepCopyEventListenersContainer(origContainer, newSimpleScriptable, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(), "eventListenersContainer_", newContainer);
                //i. prototypes_
                Map<Class< ? extends SimpleScriptable>, Scriptable> origPrototypes = (Map<Class<? extends SimpleScriptable>, Scriptable>) getField(origWindow, origWindow.getClass(),"prototypes_"); 
                Map<Class< ? extends SimpleScriptable>, Scriptable> newPrototypes = new HashMap<Class<? extends SimpleScriptable>, Scriptable>();
                for(Map.Entry<Class< ? extends SimpleScriptable>, Scriptable> origEntry: origPrototypes.entrySet()){
                    Class<? extends SimpleScriptable> key = origEntry.getKey();
                    Scriptable origValue = origEntry.getValue();
                    Scriptable newValue = deepCopyScriptable(origValue, originalsToClonesMap, contextFactory);
                    newPrototypes.put(key, newValue);
                }
                setField(newWindow, newWindow.getClass(), "prototypes_", newPrototypes);
                //j. computedStyles_ TODO: properly clone computedStyles_
                WeakHashMap<Node, ComputedCSSStyleDeclaration> newCS = new WeakHashMap<Node, ComputedCSSStyleDeclaration>();
                setField(newWindow, newWindow.getClass(),  "computedStyles_", newCS);
                //k. controllers_    TODO: do something with controllers_
                //l. opener_
                Object origOpener = getField(origWindow, origWindow.getClass(), "opener_");
                Object newOpener = cloneObjectOrDie(origOpener, originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(),"opener_", newOpener );
                //m. top_
                Object origTop = getField(origWindow, origWindow.getClass(), "top_");
                Object newTop = cloneObjectOrDie(origTop,originalsToClonesMap, contextFactory);
                setField(newWindow, newWindow.getClass(),"top_", newTop );
            }

            return  newSimpleScriptable;
        }
        if (originalScriptable instanceof IdScriptableObject) {
            if(originalScriptable instanceof BaseFunction){
                Class InterpretedFunctionClass = null;
                Class InterpreterDataClass = null;
                Class NativeScriptClass = null;
                Class NativeRegExpCtorClass = null;
                
                try {
                    InterpretedFunctionClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.InterpretedFunction");
                    InterpreterDataClass =  Class.forName("net.sourceforge.htmlunit.corejs.javascript.InterpreterData");
                    NativeScriptClass =  Class.forName("net.sourceforge.htmlunit.corejs.javascript.NativeScript");
                    NativeRegExpCtorClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.regexp.NativeRegExpCtor");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                BaseFunction origFunction = (BaseFunction) originalScriptable;
                BaseFunction newFunction = null;
                if(origFunction instanceof FunctionObject){
                    //TODO: not sure if this is legal...
                    newFunction =  origFunction;
                    originalsToClonesMap.put(origFunction, newFunction);
                }else if(origFunction instanceof  EventHandler){
                    EventHandler origHandler = (EventHandler) origFunction;
                    DomNode origNode = (DomNode) getField(origHandler,origHandler.getClass(), "node_");
                    if(!wasAlreadyCloned(origNode, originalsToClonesMap)){
                        throw new RuntimeException("Node not properly cloned");
                    }
                    DomNode newNode = (DomNode) getAlreadyCloned(origNode, originalsToClonesMap);
                    String eventName_ = (String) getField(origHandler,origHandler.getClass(), "eventName_");
                    String jsSnippet_ = (String) getField(origHandler,origHandler.getClass(), "jsSnippet_");
                    //realFunction gets autogenerated
                    EventHandler newHandler = new EventHandler(newNode, eventName_, jsSnippet_);
                    originalsToClonesMap.put(origHandler, newHandler);
                    copyIdScriptableObjectContents((EventHandler)origFunction,(EventHandler) newHandler, originalsToClonesMap, contextFactory);
                    newFunction = newHandler;
                }else if(InterpretedFunctionClass.isInstance(origFunction)){
                    Object origInterpretedData = getField(origFunction, origFunction.getClass(), "idata");
                    final Object newInterpretedData = cloneObjectOrDie(origInterpretedData, originalsToClonesMap, contextFactory);
                    final Constructor ctor = InterpretedFunctionClass.getDeclaredConstructor(InterpreterDataClass, Object.class);
                    final ArrayList storage = new ArrayList();
                    ctor.setAccessible(true);
                    newFunction = (BaseFunction) contextFactory.call(new ContextAction() {
                        public Object run(Context cx) {
                            try {
                                return ctor.newInstance(newInterpretedData, null);
                            } catch (InstantiationException e) {
                                throw new RuntimeException(e);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    originalsToClonesMap.put(origFunction, newFunction);
                    copyIdScriptableObjectContents(origFunction, newFunction, originalsToClonesMap, contextFactory);
                    //securityController
                    Object origSecurityController = getField(origFunction, origFunction.getClass(),  "securityController");
                    Object newSecurityController = cloneObjectOrDie(origSecurityController, originalsToClonesMap, contextFactory);
                    setField(newFunction, newFunction.getClass(),"securityController", newSecurityController);
                    //securityDomain
                    Object origSecuritydomain = getField(origFunction, origFunction.getClass(), "securityDomain");  
                    Object newSecuritydomain = cloneObjectOrDie(origSecuritydomain, originalsToClonesMap, contextFactory); 
                    setField(newFunction, newFunction.getClass(),"securityDomain", newSecuritydomain);
                    //functionRegExps
                    Scriptable[] origFunctionRegExps = (Scriptable[]) getField(origFunction, origFunction.getClass(), "functionRegExps");
                    Scriptable[] newFunctionRegExps  = null;
                    if (origFunctionRegExps != null) {
                        newFunctionRegExps = new Scriptable[origFunctionRegExps.length];
                        for(int i = 0; i < origFunctionRegExps.length; i++){
                            newFunctionRegExps[i] = deepCopyScriptable(origFunctionRegExps[i], originalsToClonesMap, contextFactory);
                        }
                    }
                    setField(newFunction, newFunction.getClass(),"functionRegExps", newFunctionRegExps);
                }else if(origFunction instanceof IdFunctionObject){
                    //idcall
                    IdFunctionCall origCall = (IdFunctionCall) getField(origFunction, origFunction.getClass(), "idcall");
                    IdFunctionCall newCall  = (IdFunctionCall) cloneObjectOrDie(origCall, originalsToClonesMap, contextFactory);
                    //tag
                    Object origTag = getField(origFunction, origFunction.getClass(), "tag");
                    Object newTag  = cloneObjectOrDie(origCall, originalsToClonesMap, contextFactory);
                    //methodId
                    Integer id = (Integer) getField(origFunction, origFunction.getClass(), "methodId");
                    //arity
                    Integer arity  = (Integer) getField(origFunction, origFunction.getClass(), "arity");
                    newFunction = new IdFunctionObject(newCall, newTag, id, arity);
                    originalsToClonesMap.put(origFunction,newFunction);
                    copyIdScriptableObjectContents(origFunction, newFunction, originalsToClonesMap, contextFactory);
                    //useCallAsConstructor
                    Boolean useCallAsConstructor  = (Boolean) getField(origFunction, origFunction.getClass(), "useCallAsConstructor");
                    setField(newFunction, newFunction.getClass(), "useCallAsConstructor", useCallAsConstructor);
                    //functionName
                    String functionName = (String) getField(origFunction, origFunction.getClass(), "functionName");
                    setField(newFunction, newFunction.getClass(), "functionName", functionName);
                }else if(origFunction.getClass().getName().endsWith("BaseFunction")){
                    newFunction = new BaseFunction();
                    originalsToClonesMap.put(origFunction, newFunction);
                    copyIdScriptableObjectContents(origFunction, newFunction, originalsToClonesMap, contextFactory);
                }else if(NativeScriptClass.isInstance(origFunction)){
                    Script origScript = (Script) getField(origFunction, origFunction.getClass(), "script");
                    Script newScript = (Script) cloneObjectOrDie(origScript, originalsToClonesMap, contextFactory);
                    Constructor ctor = NativeScriptClass.getDeclaredConstructor(Script.class);
                    ctor.setAccessible(true);
                    newFunction = (BaseFunction) ctor.newInstance(newScript);
                    originalsToClonesMap.put(origFunction, newFunction);
                }else if(NativeRegExpCtorClass.isInstance(origFunction)){
                    Constructor ctor = NativeRegExpCtorClass.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    newFunction = (BaseFunction) ctor.newInstance();
                    originalsToClonesMap.put(origFunction, newFunction);
                    copyIdScriptableObjectContents(origFunction, newFunction, originalsToClonesMap, contextFactory);
                }else{
                    throw new NotImplementedException("Cloning this subtype of BaseFunction is not yet supported");
                }
                //prototypeProperty
                Object origPrototypeProperty = getField(origFunction, origFunction.getClass(), "prototypeProperty");
                Object newPrototypeProperty = cloneObjectOrDie(origPrototypeProperty, originalsToClonesMap, contextFactory);
                setField(newFunction, newFunction.getClass(), "prototypeProperty", newPrototypeProperty);
                //prototypePropertyAttributes
                Integer prototypePropertyAttributes = (Integer) getField(origFunction, origFunction.getClass(), "prototypePropertyAttributes");
                setField(newFunction, newFunction.getClass(), "prototypePropertyAttributes", prototypePropertyAttributes);
                return newFunction;
            }
            if(originalScriptable instanceof NativeObject){
                Constructor nativeObjectCtor = NativeObject.class.getConstructor();
                nativeObjectCtor.setAccessible(true);
                Class StopIterationClass = NativeIterator.class.getDeclaredClasses()[1];
                if(StopIterationClass.isInstance(originalScriptable)){
                    Object newStopIteration = nativeObjectCtor.newInstance();
                    originalsToClonesMap.put(originalScriptable, newStopIteration);
                    copyIdScriptableObjectContents((NativeObject) originalScriptable, (NativeObject)newStopIteration, originalsToClonesMap, contextFactory);
                    return (NativeObject) newStopIteration;
                }
                if(originalScriptable.getClass().getName().endsWith("NativeObject")){
                    NativeObject newNativeObject = new NativeObject();
                    originalsToClonesMap.put(originalScriptable, newNativeObject);
                    copyIdScriptableObjectContents((NativeObject) originalScriptable, newNativeObject, originalsToClonesMap, contextFactory);
                    return newNativeObject;
                }
            }
            if(originalScriptable instanceof  NativeArray){
                NativeArray origArray = (NativeArray) originalScriptable;
                Object [] origDense = (Object[]) getField(origArray, origArray.getClass(), "dense");
                Object [] newDense = null;
                if(origDense != null){
                    newDense = new Object[origDense.length];
                    for(int i = 0; i < origDense.length; i++){
                        newDense[i] = cloneObjectOrDie(origDense[i], originalsToClonesMap, contextFactory);
                    }
                }
                NativeArray newArray = new NativeArray(newDense);
                originalsToClonesMap.put(origArray, newArray);
                copyIdScriptableObjectContents(origArray, newArray, originalsToClonesMap, contextFactory);
                //denseOnly
                Boolean  denseOnly = (Boolean) getField(origArray, origArray.getClass(), "denseOnly");
                setField(newArray, newArray.getClass(), "denseOnly", denseOnly);
                //length
                Long length = (Long) getField(origArray, origArray.getClass(), "length");
                setField(newArray, newArray.getClass(), "length", length);
                return newArray;
            }   
            if(originalScriptable instanceof  NativeCall){
                NativeCall origCall = (NativeCall) originalScriptable;
                Constructor ctor = NativeCall.class.getDeclaredConstructor();
                ctor.setAccessible(true);
                NativeCall newCall = (NativeCall) ctor.newInstance();
                originalsToClonesMap.put(origCall, newCall);
                //function
                NativeFunction origFunction = (NativeFunction) getField(origCall, origCall.getClass(), "function");
                NativeFunction newFunction = (NativeFunction) deepCopyScriptable(origFunction, originalsToClonesMap, contextFactory);
                setField(newCall, newCall.getClass(), "function", newFunction);
                //originalArgs 
                Object[] origOriginalArgs = (Object[]) getField(origCall, origCall.getClass(), "originalArgs");
                Object[] newOriginalArgs = null;
                if(origOriginalArgs != null){
                    newOriginalArgs = new Object[origOriginalArgs.length];
                    for(int i = 0; i < origOriginalArgs.length; i++){
                        newOriginalArgs[i] = cloneObjectOrDie(origOriginalArgs[i], originalsToClonesMap, contextFactory);
                    }
                }
                setField(newCall, newCall.getClass(), "originalArgs", newOriginalArgs);
                //parentActivationCall
                NativeCall origParentActivationCall = (NativeCall) getField(origCall, origCall.getClass(), "parentActivationCall");
                NativeCall newParentActivationCall  = (NativeCall) deepCopyScriptable(origParentActivationCall, originalsToClonesMap, contextFactory);
                setField(newCall, newCall.getClass(),"parentActivationCall", newParentActivationCall);
                copyIdScriptableObjectContents(origCall, newCall, originalsToClonesMap, contextFactory);
                return newCall;
            } 
            if(originalScriptable instanceof  NativeIterator){
                NativeIterator origIterator = (NativeIterator) originalScriptable;
                Constructor ctor = NativeIterator.class.getDeclaredConstructor();
                ctor.setAccessible(true);
                NativeIterator newIterator = (NativeIterator) ctor.newInstance();
                originalsToClonesMap.put(origIterator, newIterator);
                copyIdScriptableObjectContents(origIterator, newIterator, originalsToClonesMap, contextFactory);
                //objectIterator
                Object origObjectIterator = getField(origIterator, origIterator.getClass(), "objectIterator");
                Object newObjectIterator  = cloneObjectOrDie(origObjectIterator, originalsToClonesMap, contextFactory);
                setField(newIterator, newIterator.getClass(),"objectIterator", newObjectIterator);
                return newIterator;
            }
            if(originalScriptable instanceof NativeRegExp){
                NativeRegExp origRE = (NativeRegExp) originalScriptable;
                Constructor ctor = NativeRegExp.class.getDeclaredConstructor();
                ctor.setAccessible(true);
                NativeRegExp newRE = (NativeRegExp) ctor.newInstance();
                originalsToClonesMap.put(origRE, newRE);
                //lastIndex 
                Double lastIndex = (Double) getField(origRE, origRE.getClass(), "lastIndex");
                setField(newRE, newRE.getClass(), "lastIndex", lastIndex);
                //re
                Object re =  getField(origRE, origRE.getClass(), "re");
                setField(newRE, newRE.getClass(), "re", re);
                copyIdScriptableObjectContents(origRE, newRE, originalsToClonesMap, contextFactory);
                return newRE;
            }
            try {
                //NativeJSON
                Class NativeJSONClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.NativeJSON");
                if(NativeJSONClass.isInstance(originalScriptable)){
                    Constructor ctor = NativeJSONClass.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    Object newNativeJSON = ctor.newInstance();
                    originalsToClonesMap.put(originalScriptable, newNativeJSON);
                    copyIdScriptableObjectContents((IdScriptableObject)originalScriptable, (IdScriptableObject) newNativeJSON, originalsToClonesMap, contextFactory);
                    return  (IdScriptableObject) newNativeJSON;
                }
                //NativeMath
                Class NativeMathClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.NativeMath");
                if(NativeMathClass.isInstance(originalScriptable)){
                    Constructor ctor = NativeMathClass.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    Object newNativeMath = ctor.newInstance();
                    originalsToClonesMap.put(originalScriptable, newNativeMath);
                    copyIdScriptableObjectContents((IdScriptableObject)originalScriptable, (IdScriptableObject) newNativeMath, originalsToClonesMap, contextFactory);
                    return  (IdScriptableObject) newNativeMath;
                }
                //NativeGenerator
                Class NativeGeneratorClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.NativeGenerator");
                if(NativeGeneratorClass.isInstance(originalScriptable)){
                    Constructor ctor = NativeGeneratorClass.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    Object newNativeGenerator = ctor.newInstance();
                    originalsToClonesMap.put(originalScriptable, newNativeGenerator);
                    copyIdScriptableObjectContents((IdScriptableObject)originalScriptable, (IdScriptableObject) newNativeGenerator, originalsToClonesMap, contextFactory);
                    return  (IdScriptableObject) newNativeGenerator;
                }
                //NativeBoolean
                Class NativeBooleanClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.NativeBoolean");
                if(NativeBooleanClass.isInstance(originalScriptable)){
                    Constructor ctor = NativeBooleanClass.getDeclaredConstructor(Boolean.TYPE);
                    ctor.setAccessible(true);
                    Boolean boolValue = (Boolean) getField(originalScriptable, originalScriptable.getClass(), "booleanValue");
                    Object newNativeBoolean = ctor.newInstance(boolValue);
                    originalsToClonesMap.put(originalScriptable, newNativeBoolean);
                    copyIdScriptableObjectContents((IdScriptableObject)originalScriptable, (IdScriptableObject) newNativeBoolean, originalsToClonesMap, contextFactory);
                    return (IdScriptableObject) newNativeBoolean;
                }
                //NativeError
                Class NativeErrorClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.NativeError");
                if(NativeErrorClass.isInstance(originalScriptable)){
                    Constructor ctor = NativeErrorClass.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    Object newNativeError = ctor.newInstance();
                    originalsToClonesMap.put(originalScriptable, newNativeError);
                    copyIdScriptableObjectContents((IdScriptableObject)originalScriptable, (IdScriptableObject) newNativeError, originalsToClonesMap, contextFactory);
                    //stackProvider
                    Object stackProvider = getField(originalScriptable, originalScriptable.getClass(), "stackProvider");
                    setField(newNativeError, newNativeError.getClass(), "stackProvider", stackProvider);
                    return (IdScriptableObject) newNativeError;
                }
                //NativeString
                Class NativeStringClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.NativeString");
                if(NativeStringClass.isInstance(originalScriptable)){
                    Constructor ctor = NativeStringClass.getDeclaredConstructor(String.class);
                    ctor.setAccessible(true);
                    String string = (String) getField(originalScriptable, originalScriptable.getClass(), "string");
                    Object newNativeString = ctor.newInstance(string);
                    originalsToClonesMap.put(originalScriptable, newNativeString);
                    copyIdScriptableObjectContents((IdScriptableObject)originalScriptable, (IdScriptableObject) newNativeString, originalsToClonesMap, contextFactory);
                    return (IdScriptableObject) newNativeString;
                }
                //NativeNumber
                Class NativeNumberClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.NativeNumber");
                if(NativeNumberClass.isInstance(originalScriptable)){
                    Constructor ctor = NativeNumberClass.getDeclaredConstructor(Double.TYPE);
                    ctor.setAccessible(true);
                    Double doubleValue = (Double) getField(originalScriptable, originalScriptable.getClass(), "doubleValue");
                    Object newNativeNumber = ctor.newInstance(doubleValue);
                    originalsToClonesMap.put(originalScriptable, newNativeNumber);
                    copyIdScriptableObjectContents((IdScriptableObject)originalScriptable, (IdScriptableObject) newNativeNumber, originalsToClonesMap, contextFactory);
                    return (IdScriptableObject) newNativeNumber;
                }
                //NativeDate
                Class NativeDateClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.NativeDate");
                if(NativeDateClass.isInstance(originalScriptable)){
                    Constructor ctor = NativeDateClass.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    Object newNativeDate = ctor.newInstance();
                    originalsToClonesMap.put(originalScriptable, newNativeDate);
                    copyIdScriptableObjectContents((IdScriptableObject)originalScriptable, (IdScriptableObject) newNativeDate, originalsToClonesMap, contextFactory);
                    Double date = (Double) getField(originalScriptable, originalScriptable.getClass(), "date");
                    setField(newNativeDate, newNativeDate.getClass(), "date", date);
                    return (IdScriptableObject) newNativeDate;
                }
                //Arguments
                Class ArgumentsClass = Class.forName("net.sourceforge.htmlunit.corejs.javascript.Arguments");
                if(ArgumentsClass.isInstance(originalScriptable)){
                    NativeCall origActivation = (NativeCall) getField(originalScriptable, originalScriptable.getClass(), "activation");
                    NativeCall newActivation  = (NativeCall) deepCopyScriptable(origActivation, originalsToClonesMap, contextFactory);
                    Constructor ctor = ArgumentsClass.getDeclaredConstructor(NativeCall.class);
                    ctor.setAccessible(true);
                    //call ctor with original Activator object because the new may be incomplete (being cloned)
                    Object newArguments = ctor.newInstance(origActivation);
                    originalsToClonesMap.put(originalScriptable, newArguments);
                    copyIdScriptableObjectContents((IdScriptableObject) originalScriptable, (IdScriptableObject) newArguments, originalsToClonesMap, contextFactory);
                    //activation
                    setField(newArguments, newArguments.getClass(), "activation", newActivation);
                    //objectCtor
                    BaseFunction origObjectCtor = (BaseFunction) getField(originalScriptable, originalScriptable.getClass(), "objectCtor");
                    BaseFunction newObjectCtor  = (BaseFunction) deepCopyScriptable(origObjectCtor, originalsToClonesMap, contextFactory);
                    setField(newArguments, newArguments.getClass(), "objectCtor", newObjectCtor);
                    //args
                    Object[] origArgs = (Object[]) getField(originalScriptable, originalScriptable.getClass(), "args");
                    Object[] newArgs  = null;
                    if(origArgs != null){
                        newArgs = new Object[origArgs.length];
                        for(int i = 0; i < origArgs.length; i++){
                            newArgs[i] = cloneObjectOrDie(origArgs[i], originalsToClonesMap, contextFactory);
                        }
                    }
                    setField(newArguments, newArguments.getClass(), "args", newArgs);
                    Object origObj, newObj;
                    //callerObj
                    origObj = getField(originalScriptable, originalScriptable.getClass(), "callerObj");
                    newObj  = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                    setField(newArguments, newArguments.getClass(), "callerObj", newObj);
                    //calleeObj
                    origObj = getField(originalScriptable, originalScriptable.getClass(), "calleeObj");
                    newObj  = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                    setField(newArguments, newArguments.getClass(), "calleeObj", newObj);
                    //lengthObj
                    origObj = getField(originalScriptable, originalScriptable.getClass(), "lengthObj");
                    newObj  = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                    setField(newArguments, newArguments.getClass(), "lengthObj", newObj);
                    //constructor
                    origObj = getField(originalScriptable, originalScriptable.getClass(), "constructor");
                    newObj  = cloneObjectOrDie(origObj, originalsToClonesMap, contextFactory);
                    setField(newArguments, newArguments.getClass(), "constructor", newObj);
                    return (IdScriptableObject) newArguments;
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        if(originalScriptable instanceof  NativeWith){
            NativeWith origWith = (NativeWith) originalScriptable;
            Constructor ctor = NativeWith.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            NativeWith newWith = (NativeWith) ctor.newInstance();
            originalsToClonesMap.put(origWith, newWith);
            //prototype
            Scriptable origPrototype = (Scriptable) getField(origWith, origWith.getClass(), "prototype");
            Scriptable newPrototype  = deepCopyScriptable(origPrototype, originalsToClonesMap, contextFactory);
            setField(newWith, newWith.getClass(),"prototype", newPrototype);
            //parent
            Scriptable origParent = (Scriptable) getField(origWith, origWith.getClass(), "parent");
            Scriptable newParent  = deepCopyScriptable(origParent, originalsToClonesMap, contextFactory);
            setField(newWith, newWith.getClass(),"parent", newParent);
            return newWith;
        }
        Class FallbackCallerClass = JavaScriptEngine.class.getDeclaredClasses()[0];
        if(FallbackCallerClass.isInstance(originalScriptable)){
            originalsToClonesMap.put(originalScriptable,originalScriptable);
            return originalScriptable;
        }
        if(originalScriptable instanceof Function){
            try {
                Function origFunction = (Function) originalScriptable;
                Function newFunction = null;
                Class FunctionWrapperClass = Class.forName("com.gargoylesoftware.htmlunit.javascript.FunctionWrapper");
                Class NativeFunctionToStringFunctionClass = Class.forName("com.gargoylesoftware.htmlunit.javascript.NativeFunctionToStringFunction");
                if(FunctionWrapperClass.isInstance(originalScriptable)){
                    Function origWrapped = (Function) getField(origFunction, origFunction.getClass(), "wrapped_");
                    Function newWrapped  = (Function) deepCopyScriptable(origWrapped, originalsToClonesMap, contextFactory);
                    if(NativeFunctionToStringFunctionClass.isInstance(originalScriptable)){
                        String separator = (String) getField(origFunction, origFunction.getClass(), "separator_");
                        Constructor ctor = NativeFunctionToStringFunctionClass.getDeclaredConstructor(Function.class, String.class);
                        ctor.setAccessible(true);
                        newFunction = (Function) ctor.newInstance(newWrapped, separator);
                        originalsToClonesMap.put(origFunction, newFunction);
                    }else{
                        throw new NotImplementedException("Cannot clone " + originalScriptable.getClass());
                    }
                }else if(origFunction instanceof Delegator){
                    Scriptable origObj = (Scriptable) getField(origFunction, origFunction.getClass(), "obj");
                    Scriptable newObj = deepCopyScriptable(origObj, originalsToClonesMap, contextFactory);
                    if(origFunction instanceof DocumentProxy){
                        WebWindow origWindow = (WebWindow) getField(origFunction, origFunction.getClass(), "webWindow_");
                        WebWindow newWin = (WebWindow) getAlreadyCloned(origWindow, originalsToClonesMap);
                        newFunction = new DocumentProxy(newWin);
                        originalsToClonesMap.put(origFunction, newFunction);
                    }else if(origFunction instanceof WindowProxy){
                        WebWindow origWindow = (WebWindow) getField(origFunction, origFunction.getClass(), "webWindow_");
                        WebWindow newWin = (WebWindow) getAlreadyCloned(origWindow, originalsToClonesMap);
                        newFunction = new WindowProxy(newWin);
                        originalsToClonesMap.put(origFunction, newFunction);
                    }else{
                        throw new NotImplementedException("Cannot clone " + originalScriptable.getClass());
                    }
                    setField(newFunction, newFunction.getClass(), "obj", newObj);
                }else{
                    throw new NotImplementedException("Cannot clone " + originalScriptable.getClass());    
                }
                return newFunction;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        throw new NotImplementedException("Cloning scriptables that are not SimpleScriptables is not yet supported");

    }
    private static void recursiveChangeScriptObject(DomNode node, HashMap<Object, Object> originalsToClonesMap, ContextFactory contextFactory) throws InvocationTargetException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InstantiationException {
        ScriptableObject theScriptableObject = (ScriptableObject) getField(node, node.getClass(), "scriptObject_");
        ScriptableObject newScriptObject = (ScriptableObject) deepCopyScriptable(theScriptableObject, originalsToClonesMap, contextFactory);
        node.setScriptObject(newScriptObject);
        for(DomNode child: node.getChildren()){
            recursiveChangeScriptObject(child, originalsToClonesMap, contextFactory);
        }
    }
    public static HtmlPage clonePage(final HtmlPage originalPage,final WebWindow window, final EntityID ctx){
        try{
            GregorianCalendar start = new GregorianCalendar();

            HashMap<Object, Object> originalsToClonesMap = new HashMap<Object, Object>();
            originalsToClonesMap.put(originalPage.getEnclosingWindow(), window);
            originalsToClonesMap.put(null, null);
            HtmlPage newPage = (HtmlPage) deepCopyDom(originalPage, null, window, originalsToClonesMap);
            Scriptable newWindowScriptable = deepCopyScriptable(
                    (Scriptable)originalPage.getEnclosingWindow().getScriptObject(),
                    originalsToClonesMap,
                    window.getWebClient().getJavaScriptEngine().getContextFactory()
            );
            window.setScriptObject(newWindowScriptable);
            recursiveChangeScriptObject(
                    newPage,
                    originalsToClonesMap,
                    window.getWebClient().getJavaScriptEngine().getContextFactory()
            );

            GregorianCalendar stop = new GregorianCalendar();
            logger.info("Clone page took " + (stop.getTimeInMillis() - start.getTimeInMillis()) / 1000.0);
            return newPage;
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}