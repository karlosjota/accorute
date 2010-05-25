function dumpLN(txt){
    dump(txt);
    dump("\n");
}

var Capturer = function(){
}

Capturer.prototype = {
    listening: false,
    opened_tabs: new Array(),
    sessions: new Array(),
    clicks_count: 0,
    submits_count:0,
    pageload_count:0,

    dumpCurSession: function(){
        if(this.sessions.length  > 0){
            dumpLN(JSON.stringify(this.sessions[this.sessions.length - 1], null, "  "));
        }
    },

    newSession :function(sname, user){
        this.clicks_count = 0;
        this.pageload_count = 0;
        this.submits_count = 0;
        this.register();
        this.listening = true;
        var now = new Date();
        this.sessions[this.sessions.length] = new Array();
        var cur_sess = this.sessions[this.sessions.length - 1];
        cur_sess[cur_sess.length] = {
            type: "SessionCreated",
            name : sname,
            user: user,
            time: now.getTime()
        };
        cur_sess[cur_sess.length] = {type: "UCCreated", time: now.getTime()};

        },
    finishSession: function(){
        this.unregister();
        var cur_sess = this.sessions[this.sessions.length - 1];
        var now = new Date();
        cur_sess[cur_sess.length] = {type: "UCEnded", time: now.getTime()};
        cur_sess[cur_sess.length] = {
            type: "SessionFinished",
            time: now.getTime(),
            clicks : this.clicks_count,
            submits: this.submits_count,
            pages: this.pageload_count
	    };
        this.dumpCurSession();
        return {clicks : this.clicks_count, submits: this.submits_count, pages: this.pageload_count };
    },

    register : function(){
        dumpLN("Capturer::register\n");
        var observerService = Components.classes["@mozilla.org/observer-service;1"]
        .getService(Components.interfaces.nsIObserverService);
        observerService.addObserver(this, "xul-window-visible", false);
        observerService.addObserver(this, "xul-window-destroyed", false);
        this.observe();
    },
    observe : function(subject, topic, data) {
        var windowManager = Components.classes['@mozilla.org/appshell/window-mediator;1']
        .getService(Components.interfaces.nsIWindowMediator);
        var enumerator = windowManager.getEnumerator("navigator:browser");
        this.opened_tabs = new Array();
        while(enumerator.hasMoreElements()){
            var wnd  = enumerator.getNext();
            wnd.addEventListener("DOMContentLoaded", this, true);
            var tabs = wnd.gBrowser.browsers;
            for( var i = 0; i< tabs.length; i++){
                var doc = tabs[i];
                this.opened_tabs[this.opened_tabs.length] = doc;
                //doc.addEventListener("DOMContentLoaded", this, true);
            }
        }
        for(var k = 0; k< this.opened_tabs.length; k++){
                var doc = this.opened_tabs[k].contentDocument;
                var links = doc.links;
                for(var i=0 ; i < links.length ; i++){
                links[i].addEventListener("click",this,  true);
            }
            var forms = doc.forms;
            for(var i=0 ; i < forms.length ; i++){
                forms[i].addEventListener("submit",this, true);
                var elems = forms[i].elements;
                for(var j=0;j<elems.length ;j++){
                    if(elems[j].type == "submit"){
                        elems[j].addEventListener("click",this,  true);
                    }
                }
            }
        }
    },
    unregister : function(){
        this.listening = false;
        dumpLN("Capturer::unregister\n");
        var observerService = Components.classes["@mozilla.org/observer-service;1"]
        .getService(Components.interfaces.nsIObserverService);
        observerService.removeObserver(this, "xul-window-visible");
        observerService.removeObserver(this, "xul-window-destroyed");
    },
    handleEvent:function(evt) {
        if(! this.listening )
            return;
        var now = new Date();
        var cur_sess = this.sessions[this.sessions.length - 1];
        if(evt.altKey && evt.ctrlKey){;
            cur_sess[cur_sess.length] = {type: "UCEnded", time: now.getTime()};
            dumpLN("New UC started!");
            cur_sess[cur_sess.length] = {type: "UCStarted", time: now.getTime()};
        }
        now = new Date();
        cur_sess[cur_sess.length]  = {time: now.getTime()};
        var event_record = cur_sess[cur_sess.length - 1];
        if(evt.type == "DOMContentLoaded"){
            this.pageload_count ++;
            event_record.type = "pageLoaded";
            event_record.tabs = new Array();
            this.observe();
            for(var i = 0; i< this.opened_tabs.length; i++){
                wnd = this.opened_tabs[i];
                event_record.tabs[i] = { location : wnd.contentDocument.location.href};
                event_record.tabs[i].frames = new Array();
                var frames = wnd.contentWindow.frames;
                for(var j=0; j< frames.length; j++){
                    event_record.tabs[i].frames[j] = { location: frames[j].document.location.href };
                }
            }
            var doc = evt.originalTarget; // doc is a document that triggered "onload" event
            event_record.document = { location: doc.location.href };
        } else if(evt.type == "click") {
            var link = evt.currentTarget;
            if(link.tagName != "A"){                
                return;
            }
            this.clicks_count ++;
            event_record.type = "linkClicked";
            event_record.href = link.href;
            event_record.cookie = link.ownerDocument.cookie;
            // what is the number of the document that produced the click?
            /*var rootdoc = link.ownerDocument;
            for(var i = 0; i< this.opened_tabs.length && event_record.rootdoc == null; i++){
                wnd = this.opened_tabs[i];
                if(wnd.contentDocument == rootdoc){
                    event_record.rootdoc = { tab: i, frame: -1 };
                    break;
                }
                var frames = wnd.contentWindow.frames;
                for(var j=0; j< frames.length; j++){
                    if(frames[j].document == rootdoc){
                    event_record.rootdoc = { tab: i, frame: j };
                    break;
                    }
                }
            }*/
            event_record.document = event_record.document = { location: link.ownerDocument.location.href };
        } else if(evt.type == "submit") {
            this.submits_count ++;
            event_record.type = "formSubmitted";
            var form = evt.currentTarget;
            event_record.action = form.action;
            event_record.cookie = form.ownerDocument.cookie;
            event_record.method = form.method;
            event_record.elements = new Array();
            for(var i = 0; i< form.length; i++){
            event_record.elements[i] = {
                name:form.elements[i].name,
                value: form.elements[i].value,
                type: form.elements[i].type,
                checked: form.elements[i].checked
                };
            }
            /*var rootdoc = form.ownerDocument;
            for(var i = 0; i< this.opened_tabs.length && event_record.rootdoc == null; i++){
                wnd = this.opened_tabs[i];
                if(wnd.contentDocument == rootdoc){
                    event_record.rootdoc = { tab: i, frame: -1 };
                    break;
                }
                var frames = wnd.contentWindow.frames;
                for(var j=0; j< frames.length; j++){
                    if(frames[j].document == rootdoc){
                    event_record.rootdoc = { tab: i, frame: j };
                    break;
                    }
                }
            }
            */
            event_record.document = event_record.document = { location: form.ownerDocument.location.href };            
        }
    }
}

var capturer =  new Capturer();
