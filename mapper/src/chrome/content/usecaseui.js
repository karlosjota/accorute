function populateMenuFromList(menu, list){
    menu.removeAllItems();
    for(var i=0;i<list.itemCount;i++){
        var label = list.getItemAtIndex(i).childNodes[0].getAttribute("label");
        menu.appendItem(label,"");
    }
    menu.selectedItem = menu.getItemAtIndex(0);
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
    document.getElementById("theWizard").canRewind = false;
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
    document.getElementById("theWizard").canAdvance = true;
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

function startUC(){
    document.getElementById("start-stop-button").onclick = finishUC;
    document.getElementById("start-stop-button").label = "Finish";
    document.getElementById("theWizard").canAdvance = false;
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
    capturer.newSession(document.getElementById("sessName").value,
			document.getElementById("userSelect").selectedItem.label);
}

function dumpSettingsToFile(file){
    var settings = {
	scope: document.getElementById("webappScopeRegexp").value,
	sessTokenLocation: document.getElementById("webappSessTokenLocSelect").selectedItem.label,
	sessTokenName: document.getElementById("webappSessTokenName").value,
        roles: new Array(),
        users: new Array(),
	dependencies: new Array(),
	cancellations: new Array(),
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