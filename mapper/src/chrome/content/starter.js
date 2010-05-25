function openCapWin(){
	//Get window enumerator...
	var windowManager = Components.classes['@mozilla.org/appshell/window-mediator;1']
	.getService(Components.interfaces.nsIWindowMediator);
	var enumerator = windowManager.getEnumerator(null);
	var win = null
	// enumerate windows
	while(enumerator.hasMoreElements()){
		var wnd  = enumerator.getNext();
		if(wnd.id == "capturer"){
			win = wnd;
		}
	}
	if(win==null){
		var win = window.open("chrome://webappmapper/content/wizard.xul","Capturer main window", "chrome,width=400,height=300");
	}
	win.focus();
}
