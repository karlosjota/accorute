//Apycom.com DHTML-Tuner StyleNames
//ItemStyles
//Purchase Style
//Separator Style
//Style Three
//End of ItemStyles
//SmStyles
//Submenu Style 2
//End of SmStyles
//End of StyleNames

var blankImage="img/blank.gif";
var isHorizontal=0;
var menuWidth=0;
var absolutePos=1;
var posX=185;
var posY=180;
var floatable=1;
var floatIterations=0;
var movable=1;
var moveCursor="move";
var moveImage="img/movepic.gif";
var moveWidth=10;
var moveHeight=20;
var fontStyle="normal 8pt Verdana";
var fontColor=["#FFFFFF","#FFFFFF"];
var fontDecoration=["none","none"];
var itemBackColor=["#FF0F0F","#970000"];
var itemBorderWidth=2;
var itemAlign="left";
var itemBorderColor=["#FF0909","#970000"];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];
var itemSpacing=0;
var itemPadding=2;
var itemCursor="default";
var itemTarget="_blank";
var iconTopWidth=0;
var iconTopHeight=0;
var iconWidth=0;
var iconHeight=0;
var menuBackImage="";
var menuBackColor="#FF2C06";
var menuBorderColor="#FF9191 #FF7837 #E10000 #952D00 ";
var menuBorderStyle=["outset"];
var menuBorderWidth=3;
var subMenuAlign="";
var transparency=100;
var transition=5;
var transDuration=400;
var shadowColor="#B6B6B6";
var shadowLen=3;
var arrowImageMain=["",""];
var arrowImageSub=["img/arrow1_o.gif","img/arrow1_o.gif"];
var arrowWidth=6;
var arrowHeight=5;
var separatorImage="img/sep.gif";
var separatorWidth="100%";
var separatorHeight="1";
var separatorAlignment="";
var separatorVImage="";
var separatorVWidth="0";
var separatorVHeight="100%";
var statusString="link";
var pressedItem=1;
var pathPrefix="";
var topDX=0;
var topDY=0;
var DX=-5;
var DY=0;
var saveNavigationPath=0;

var itemStyles = [
   ["fontStyle=bold 8pt Tahoma","itemBackColor=#FF7777,#A40000","itemBorderColor=#FF7777,#A40000",],
   ["itemBackColor=#FF2C06,#FF2C06",],
   ["fontColor=#000000,#FFFFFF","itemBackColor=#FFE737,#B76900","itemBorderColor=#FFE737,#B76900",],
];

var menuStyles = [
   ["menuBorderColor=#B76900",],
];

var menuItems = [
   ["Apycom Home","http://www.apycom.com/","","","",,,],
   ["DHTML Menu","","","","",,,],
   ["|Home","../index.html","","","",,,],
   ["||Examples","../examples/dhtml-menu-ex1.html","","","",,,],
   ["||How to Setup","../indexex.html#set","","","",,,],
   ["||Parameters","../params.html","","","",,,],
   ["|Purchase","../order.html","","","",,"0",],
   ["|Download","http://www.apycom.com/download.html","","","",,,],
   ["Our Products","","","","",,,],
   ["|Java menus","","","","",,,],
   ["||Drop Down Menus","http://www.apycom.com/xp-drop-down-menu/","","","",,,],
   ["||Animated Menus","http://www.apycom.com/animated-buttons/","","","",,,],
   ["||Website Buttons","http://www.apycom.com/website-buttons/","","","",,,],
   ["||Navigation Tabs","http://www.apycom.com/navigation-bar-tabs/","","","",,,],
   ["||Live Examples","http://www.apycom.com/indexex.html","","","",,,],
   ["||Sreenshots","http://www.apycom.com/shots/xpdd.html","","","",,,],
   ["||-","","","","",,"1",],
   ["||Purchase","http://www.apycom.com/order.html","","","",,"0",],
   ["||Download","http://www.apycom.com/download.html","","","",,,],
   ["|Web buttons","http://www.xp-web-buttons.com/","","","",,,],
   ["|DBF Viewer/Editor","http://www.dbfview.com/","","","",,,],
   ["|-","","","","",,"1",],
   ["|More Products","","","","",,"2",],
   ["||Product 1","testlink00559.html","","","",,"2","0"],
   ["|||Documentation","testlink00560.html","","","",,"2",],
   ["|||How to Setup","testlink00561.html","","","",,"2",],
   ["||Product 2","testlink00562.html","","","",,"2",],
   ["||Product 3","testlink00563.html","","","",,"2",],
   ["|||Documentation","testlink00564.html","","","",,"2",],
   ["|||How to Setup","testlink00565.html","","","",,"2",],
   ["||Product 4","testlink00566.html","","","",,"2",],
   ["Contacts","http://www.apycom.com/contact.html","","","",,,],
];

apy_init();
