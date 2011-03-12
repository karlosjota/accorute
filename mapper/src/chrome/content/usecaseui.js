function populateMenuFromList(menu, list){
    menu.removeAllItems();
    for(var i=0;i<list.itemCount;i++){
        var label = list.getItemAtIndex(i).childNodes[0].getAttribute("label");
        menu.appendItem(label,"");
    }
    menu.selectedItem = menu.getItemAtIndex(0);
}

function clearListBox(list){
    while(list.itemCount != 0){
        list.removeItemAt(0);
    }
}

function addToken(){
    var list =  document.getElementById("tokenList");
    var row = document.createElement('listitem');
    var cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("webappSessTokenName").value);
    row.appendChild(cell);
    var cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("webappSessTokenLocSelect").selectedItem.label);
    row.appendChild(cell);
    list.appendChild(row);
}

function addRole(){
    var list =  document.getElementById("roleList");
    var row = document.createElement('listitem');
    var cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("roleName").value);
    row.appendChild(cell);
    var cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("parentRoleSelect").selectedItem.label);
    row.appendChild(cell);
    list.appendChild(row);
    var menu = document.getElementById("parentRoleSelect");
    populateMenuFromList(menu,list);
}

function enumerateRoles(){
    var list =  document.getElementById("roleList");
    var menu = document.getElementById("userRoleSelect");
    populateMenuFromList(menu,list);
}

function addUser(){
    var list =  document.getElementById("userList");
    var row = document.createElement('listitem');
    var cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("userName").value);
    row.appendChild(cell);
    cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("userRoleSelect").selectedItem.label);
    row.appendChild(cell);
    cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("userPassword").value);
    row.appendChild(cell);
    list.appendChild(row);
}

function enumerateUsers(){
    var list =  document.getElementById("userList");
    var menu = document.getElementById("userSelect");
    populateMenuFromList(menu,list);
}

function enumerateUCForDeps(){
    var list =  document.getElementById("sessList");
    var menu1 = document.getElementById("UCFromSelect");
    var menu2 = document.getElementById("UCToSelect");
    menu1.removeAllItems();
    menu2.removeAllItems();
    for(var i=0;i<list.itemCount;i++){
        var label = list.getItemAtIndex(i).childNodes[0].getAttribute("label") + " : " + list.getItemAtIndex(i).childNodes[1].getAttribute("label");
        menu1.appendItem(label,"");
        menu2.appendItem(label,"");
    }
    menu1.selectedItem = menu1.getItemAtIndex(0);
    menu2.selectedItem = menu2.getItemAtIndex(0);
}
function formDependency(){
    var list =  document.getElementById("depList");
    var row = document.createElement('listitem');
    var cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("UCFromSelect").selectedItem.label);
    row.appendChild(cell);
    cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("UCToSelect").selectedItem.label);
    row.appendChild(cell);
    list.appendChild(row);

}

function enumerateUCForCanc(){
    var list =  document.getElementById("sessList");
    var menu1 = document.getElementById("UCFromSelect2");
    var menu2 = document.getElementById("UCToSelect2");
    menu1.removeAllItems();
    menu2.removeAllItems();
    for(var i=0;i<list.itemCount;i++){
        var label = list.getItemAtIndex(i).childNodes[0].getAttribute("label") + " : " + list.getItemAtIndex(i).childNodes[1].getAttribute("label");
        menu1.appendItem(label,"");
        menu2.appendItem(label,"");
    }
    menu1.selectedItem = menu1.getItemAtIndex(0);
    menu2.selectedItem = menu2.getItemAtIndex(0);
}

function formCancellation(){
    var list =  document.getElementById("cancelList");
    var row = document.createElement('listitem');
    var cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("UCFromSelect2").selectedItem.label);
    row.appendChild(cell);
    cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("UCToSelect2").selectedItem.label);
    row.appendChild(cell);
    list.appendChild(row);
}

function finishUC(){
    document.getElementById("start-stop-button").onclick = startUC;
    document.getElementById("start-stop-button").label =
            "Start";
    document.getElementById("sessName").disabled = false;
    document.getElementById("userSelect").disabled = false;
    document.getElementById("saveToFileButton").disabled = false;
    document.getElementById("loadFromFileButton").disabled = false;
    capturer.finishSession();
    /*    var list = document.getElementById("SessList");
     var item = list.getItemAtIndex(list.getRowCount() - 1);
     var actions = capturer.finishSession();
     var cell = item.childNodes[1];
     cell.setAttribute("label",actions.clicks);
     cell = item.childNodes[2];
     cell.setAttribute("label",actions.submits);
     cell = item.childNodes[3];
     cell.setAttribute("label",actions.pages);*/
}
function selectionChange_tabs(){
    enumerateRoles();
    enumerateUsers();
    enumerateUCForDeps();
    enumerateUCForCanc();
}

function startUC(){
    document.getElementById("start-stop-button").onclick = finishUC;
    document.getElementById("start-stop-button").label = "Finish";
    var list = document.getElementById("sessList");
    var deps = "";
    for(var i=0;i<list.itemCount;i++){
        var item = list.getItemAtIndex(i)
        if(item.selected){
            var UCname = item.childNodes[0].getAttribute("label");
            var uname  = item.childNodes[1].getAttribute("label");
        }
    }
    var row = document.createElement('listitem');
    cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("sessName").value);
    row.appendChild(cell);
    cell = document.createElement("listcell");
    cell.setAttribute("label",document.getElementById("userSelect").selectedItem.label);
    row.appendChild(cell);
    list.appendChild(row);
    document.getElementById("sessName").disabled = true;
    document.getElementById("userSelect").disabled = true;
    document.getElementById("saveToFileButton").disabled = true;
    document.getElementById("loadFromFileButton").disabled = true;
    capturer.newSession(document.getElementById("sessName").value,
            document.getElementById("userSelect").selectedItem.label);
}

function dumpSettingsToFile(file){
    var settings = {
        scope: document.getElementById("webappScopeRegexp").value,
        roles: new Array(),
        users: new Array(),
        dependencies: new Array(),
        cancellations: new Array(),
        useCases: new Array(),
        dynamicTokens: new Array(),
        trace: capturer.sessions
    };
    var list = document.getElementById("depList");
    for(var i=0;i<list.itemCount;i++){
        var from = list.getItemAtIndex(i).childNodes[0].getAttribute("label");
        var to = list.getItemAtIndex(i).childNodes[1].getAttribute("label");
        settings.dependencies[settings.dependencies.length] = {
            from: from,
            to: to
        }
    }
    list = document.getElementById("cancelList");
    for(var i=0;i<list.itemCount;i++){
        var from = list.getItemAtIndex(i).childNodes[0].getAttribute("label");
        var to = list.getItemAtIndex(i).childNodes[1].getAttribute("label");
        settings.cancellations[settings.cancellations.length] = {
            from: from,
            to: to
        }
    }
    list = document.getElementById("userList");
    for(var i=0;i<list.itemCount;i++){
        var name = list.getItemAtIndex(i).childNodes[0].getAttribute("label");
        var role = list.getItemAtIndex(i).childNodes[1].getAttribute("label");
        var password = list.getItemAtIndex(i).childNodes[2].getAttribute("label");
        settings.users[settings.users.length] = {
            name: name,
            role: role,
            credentials :{
                login: name,
                password: password
            }
        }
    }
    list = document.getElementById("roleList");
    for(var i=0;i<list.itemCount;i++){
        var name = list.getItemAtIndex(i).childNodes[0].getAttribute("label");
        var parent = list.getItemAtIndex(i).childNodes[1].getAttribute("label");
        settings.roles[settings.roles.length] = {
            name: name,
            parent: parent
        }
    }
    list = document.getElementById("sessList");
    for(var i=0;i<list.itemCount;i++){
        var name = list.getItemAtIndex(i).childNodes[0].getAttribute("label");
        var user = list.getItemAtIndex(i).childNodes[1].getAttribute("label");
        settings.useCases[settings.useCases.length] = {
            name: name,
            user: user
        }
    }

    list = document.getElementById("tokenList");
    for(var i=0;i<list.itemCount;i++){
        var name = list.getItemAtIndex(i).childNodes[0].getAttribute("label");
        var location = list.getItemAtIndex(i).childNodes[1].getAttribute("label");
        settings.dynamicTokens[settings.dynamicTokens.length] = {
            name: name,
            location: location
        }
    }

    try {
        var foStream = Components.classes["@mozilla.org/network/file-output-stream;1"].
                createInstance(Components.interfaces.nsIFileOutputStream);
        foStream.init(file,0x02 | 0x08 | 0x10 , 0666, 0);
        var str = JSON.stringify(settings, null, "  ");
        str += "\n\n";
        foStream.write(str,str.length);
        foStream.close();
    } catch (ex) {
        alert(ex);
        return false;
    }
    return true;
}

function importSettingsFromFile(file){
    var settings;
    try {
        var data = "";
        var foStream = Components.classes["@mozilla.org/network/file-input-stream;1"].
                createInstance(Components.interfaces.nsIFileInputStream);
        var cstream = Components.classes["@mozilla.org/intl/converter-input-stream;1"].
              createInstance(Components.interfaces.nsIConverterInputStream);
        foStream.init(file,-1 , 0, 0);
        cstream.init(foStream, "UTF-8", 0, 0); // you can use another encoding here if you wish
        var str = {} ;
        var read = 0;
        do {
            read = cstream.readString(0xffffffff, str); // read as much as we can and put it in str.value
            data += str.value;
        } while (read != 0);
        cstream.close(); // this closes foStream
        settings = JSON.parse(data);
    } catch (ex) {
        alert(ex);
        return false;
    }
    capturer.sessions = settings.trace;
    //basic
    document.getElementById("webappScopeRegexp").value = settings.scope;

    capturer.listening = false;
    var list = document.getElementById("depList");
    clearListBox(list);
    for(var i=0;i<settings.dependencies.length;i++){
        var row = document.createElement('listitem');
        var cell = document.createElement("listcell");
        cell.setAttribute("label",settings.dependencies[i].from);
        row.appendChild(cell);
        cell = document.createElement("listcell");
        cell.setAttribute("label",settings.dependencies[i].to);
        row.appendChild(cell);
        list.appendChild(row);
    }
    list = document.getElementById("cancelList");
    clearListBox(list);
    for(var i=0;i<settings.cancellations.length;i++){
        var row = document.createElement('listitem');
        var cell = document.createElement("listcell");
        cell.setAttribute("label",settings.cancellations[i].from);
        row.appendChild(cell);
        cell = document.createElement("listcell");
        cell.setAttribute("label",settings.cancellations[i].to);
        row.appendChild(cell);
        list.appendChild(row);
    }
    list = document.getElementById("userList");
    clearListBox(list);
    for(var i=0;i<settings.users.length;i++){
        var row = document.createElement('listitem');
        var cell = document.createElement("listcell");
        cell.setAttribute("label",settings.users[i].name);
        row.appendChild(cell);
        cell = document.createElement("listcell");
        cell.setAttribute("label",settings.users[i].role);
        row.appendChild(cell);
        cell = document.createElement("listcell");
        cell.setAttribute("label",settings.users[i].credentials.password);
        row.appendChild(cell);
        list.appendChild(row);
    }
    list = document.getElementById("roleList");
    clearListBox(list);
    for(var i=0;i<settings.roles.length;i++){
        var row = document.createElement('listitem');
        var cell = document.createElement("listcell");
        cell.setAttribute("label",settings.roles[i].name);
        row.appendChild(cell);
        cell = document.createElement("listcell");
        cell.setAttribute("label",settings.roles[i].parent);
        row.appendChild(cell);
        list.appendChild(row);
    }
    list = document.getElementById("sessList");
    clearListBox(list);
    for(var i=0;i<settings.useCases.length;i++){
        var row = document.createElement('listitem');
        var cell = document.createElement("listcell");
        cell.setAttribute("label",settings.useCases[i].name);
        row.appendChild(cell);
        cell = document.createElement("listcell");
        cell.setAttribute("label",settings.useCases[i].user);
        row.appendChild(cell);
        list.appendChild(row);
    }

    list = document.getElementById("tokenList");
    clearListBox(list);
    for(var i=0;i<settings.dynamicTokens.length;i++){
        var row = document.createElement('listitem');
        var cell = document.createElement("listcell");
        cell.setAttribute("label",settings.dynamicTokens[i].name);
        row.appendChild(cell);
        cell = document.createElement("listcell");
        cell.setAttribute("label",settings.dynamicTokens[i].location);
        row.appendChild(cell);
        list.appendChild(row);
    }

    return true;
}

function saveToFile(){
    var nsIFilePicker = Components.interfaces.nsIFilePicker;
    var fp = Components.classes["@mozilla.org/filepicker;1"].createInstance(nsIFilePicker);
    fp.init(window, "Select a File", nsIFilePicker.modeSave);
    var res = fp.show();
    if (res != nsIFilePicker.returnCancel){
        var thefile = fp.file;
        if ( dumpSettingsToFile(thefile)){
            return true;
        }
        return false;
    }
    return false;
}

function loadFile(){
    var nsIFilePicker = Components.interfaces.nsIFilePicker;
    var fp = Components.classes["@mozilla.org/filepicker;1"].createInstance(nsIFilePicker);
    fp.init(window, "Select a File", nsIFilePicker.modeOpen);
    var res = fp.show();
    if (res != nsIFilePicker.returnCancel){
        var thefile = fp.file;
        if ( importSettingsFromFile(thefile)){
            return true;
        }
        return false;
    }
    return false;
}