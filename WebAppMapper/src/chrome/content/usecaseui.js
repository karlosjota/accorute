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
    var cell = document.createElement("listcell");
	cell.setAttribute("label",document.getElementById("userRoleSelect").selectedItem.label);
	row.appendChild(cell);
    list.appendChild(row);
}

function enumerateUsers(){
    document.getElementById("theWizard").canRewind = false;
    var list =  document.getElementById("userList");
    var menu = document.getElementById("userSelect");
    populateMenuFromList(menu,list);
}

function finishUC(){
	document.getElementById("start-stop-button").onclick = startUC;
	document.getElementById("start-stop-button").label =
		"Start";
	document.getElementById("sessName").disabled = false;
	document.getElementById("userSelect").disabled = false;
	document.getElementById("theWizard").canAdvance = true;
	var list = document.getElementById("SessList");
	var item = list.getItemAtIndex(list.getRowCount() - 1);
	var actions = capturer.finishSession();
	var cell = item.childNodes[1];
	cell.setAttribute("label",actions.clicks);
	cell = item.childNodes[2];
	cell.setAttribute("label",actions.submits);
	cell = item.childNodes[3];
	cell.setAttribute("label",actions.pages);
}

function startUC(){
	document.getElementById("start-stop-button").onclick = finishUC;
	document.getElementById("start-stop-button").label = "Finish";
	document.getElementById("theWizard").canAdvance = false;
    var list = document.getElementById("sessList");
    var deps = "";
    var dependencies = new Array();
    for(var i=0;i<list.itemCount;i++){
        var item = list.getItemAtIndex(i)
        if(item.selected){
            var UCname = item.childNodes[0].getAttribute("label");
            var uname  = item.childNodes[1].getAttribute("label");
            deps = deps + "( " + UCname +" , " + uname + " ) ";
            dependencies[dependencies.length] = {usecase: UCname, user: uname};
        }
    }
    var row = document.createElement('listitem');
    cell = document.createElement("listcell");
	cell.setAttribute("label",document.getElementById("sessName").value);
	row.appendChild(cell);
	cell = document.createElement("listcell");
	cell.setAttribute("label",document.getElementById("userSelect").selectedItem.label);
	row.appendChild(cell);
	cell = document.createElement("listcell");
	cell.setAttribute("label",deps);
	row.appendChild(cell);
    list.appendChild(row);
    document.getElementById("sessName").disabled = true;
	document.getElementById("userSelect").disabled = true;
	capturer.newSession(document.getElementById("sessName").value,
            document.getElementById("userSelect").selectedItem.label,
            dependencies);
}

function dumpSettingsToFile(file){
    var settings = {
        roles: new Array(),
        users: new Array(),
        trace: capturer.sessions
    };
    var list = document.getElementById("userList");
    for(var i=0;i<list.itemCount;i++){
        var name = list.getItemAtIndex(i).childNodes[0].getAttribute("label");
        var role = list.getItemAtIndex(i).childNodes[1].getAttribute("label");
        settings.users[settings.users.length] = {
            name: name,
            role: role
        }
    }
    var list = document.getElementById("roleList");
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