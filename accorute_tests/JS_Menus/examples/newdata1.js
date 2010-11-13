//Apycom.com DHTML-Tuner StyleNames
//ItemStyles
//Bold Style
//Separator Style
//End of ItemStyles
//SmStyles
//End of SmStyles
//End of StyleNames

var blankImage="img/blank.gif";
var isHorizontal=1;
var menuWidth="250";
var absolutePos=1;
var posX=10;
var posY=110;
var floatable=1;
var floatIterations=0;
var movable=0;
var moveCursor="default";
var moveImage="";
var moveWidth=0;
var moveHeight=0;
var fontStyle="normal 7pt Verdana";
var fontColor=["#000000","#FFFFFF"];
var fontDecoration=["none","none"];
var itemBackColor=["#FFFFFF","#1286CB"];
var itemBorderWidth=1;
var itemAlign="left";
var itemBorderColor=["#E4E4E4","#000000"];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];
var itemSpacing=1;
var itemPadding=1;
var itemCursor="default";
var itemTarget="_blank";
var iconTopWidth=0;
var iconTopHeight=0;
var iconWidth=0;
var iconHeight=0;
var menuBackImage="";
var menuBackColor="#FFFFFF";
var menuBorderColor="#828282 ";
var menuBorderStyle=["solid"];
var menuBorderWidth=1;
var subMenuAlign="";
var transparency=100;
var transition=24;
var transDuration=150;
var shadowColor="#8D8D8D";
var shadowLen=2;
var arrowImageMain=["",""];
var arrowImageSub=["img/arrow1_n.gif","img/arrow1_o.gif"];
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
var pressedItem=-2;

var itemStyles = [
   ["fontStyle=bold 7pt Verdana",],
   ["itemBackColor=#FFFFFF,#FFFFFF","itemBorderColor=#FFFFFF,#FFFFFF",],
];

var menuItems = [
   ["Apycom Home","http://www.apycom.com/","","","",,,],
   ["DHTML Menu","../index.html","","","",,,],
   ["|Home","../index.html","","","",,,],
   ["||Examples","dhtml-menu-ex1.html","","","",,,],
   ["||How to Setup","../indexex.html#set","","","",,,],
   ["||Parameters","../params.html","","","",,,],
   ["|Purchase","../order.html","","","",,"0",],
   ["|Download","http://www.apycom.com/download.html","","","",,,],
   ["Our products","","","","",,,],
   ["|Java Menus","","","","",,,],
   ["||Drop Down Menus","http://www.apycom.com/xp-drop-down-menu/","","","",,,],
   ["||Animated Menus","http://www.apycom.com/animated-buttons/","","","",,,],
   ["||Website Buttons","http://www.apycom.com/website-buttons/","","","",,,],
   ["||Navigation Tabs","http://www.apycom.com/navigation-bar-tabs/","","","",,,],
   ["||Live Examples","http://www.apycom.com/indexex.html","","","",,,],
   ["||Sreenshots","http://www.apycom.com/shots/xpdd.html","","","",,,],
   ["||-","","","","",,1],
   ["||Purchase","http://www.apycom.com/order.html","","","",,"0",],
   ["||Download","http://www.apycom.com/download.html","","","",,,],
   ["|Web Buttons","http://www.xp-web-buttons.com/","","","",,,],
   ["|DBF Viewer/Editor","http://www.dbfview.com/","","","",,,],
   ["|-","","","","",,1],
   ["|More Products","","","","",,,],
   ["||Product 1","testlink00443.html","","","",,,],
   ["|||Documentation","testlink00444.html","","","",,,],
   ["|||How to Setup","testlink00445.html","","","",,,],
   ["||Product 2","testlink00446.html","","","",,,],
   ["||Product 3","testlink00447.html","","","",,,],
   ["|||Documentation","testlink00448.html","","","",,,],
   ["|||How to Setup","testlink00449.html","","","",,,],
   ["||Product 4","testlink00450.html","","","",,,],
   ["Contacts","http://www.apycom.com/contact.html","","","",,,],
];

apy_init();
