//Apycom.com DHTML-Tuner StyleNames
//ItemStyles
//Purchase Style
//Separator Style
//End of ItemStyles
//SmStyles
//End of SmStyles
//End of StyleNames

var blankImage="img/blank.gif";
var isHorizontal=0;
var menuWidth="128";
var absolutePos=0;
var posX=30;
var posY=20;
var floatable=0;
var floatIterations=0;
var movable=0;
var moveCursor="default";
var moveImage="";
var moveWidth=0;
var moveHeight=0;
var fontStyle="bold 8pt Verdana";
var fontColor=["#C97901","#FFFFFF"];
var fontDecoration=["none","none"];
var itemBackColor=["",""];
var itemBorderWidth=0;
var itemAlign="center";
var itemBorderColor=["",""];
var itemBorderStyle=["",""];
var itemBackImage=["img/itemback2_n.gif","img/itemback2_o.gif"];
var itemSpacing=0;
var itemPadding=7;
var itemCursor="hand";
var itemTarget="_blank";
var iconTopWidth=0;
var iconTopHeight=0;
var iconWidth=0;
var iconHeight=0;
var menuBackImage="";
var menuBackColor="";
var menuBorderColor="#B7B7B7 ";
var menuBorderStyle=["solid"];
var menuBorderWidth=0;
var subMenuAlign="";
var transparency=100;
var transition=24;
var transDuration=400;
var shadowColor="#858585";
var shadowLen=0;
var arrowImageMain=["",""];
var arrowImageSub=["",""];
var arrowWidth=6;
var arrowHeight=5;
var separatorImage="";
var separatorWidth="100%";
var separatorHeight="0";
var separatorAlignment="left";
var separatorVImage="";
var separatorVWidth="0";
var separatorVHeight="100%";
var statusString="link";
var pressedItem=1;


var itemStyles = [
   ["itemBackColor=#FFA466,#FFBF95",],
   ["itemBackColor=#FFFFFF,#FFFFFF",],
];

var menuItems = [
   ["Apycom Home","http://www.apycom.com/","","","",,,],
   ["DHTML Menu","","","","",,,],
   ["|Home","../index.html","","","",,,],
   ["||Examples<img src='img/blank.gif' height=1 width=50>","dhtml-menu-ex1.html","","","",,,],
   ["||How to Setup","../indexex.html#set","","","",,,],
   ["||Parameters","../params.html","","","",,,],
   ["|Purchase","../order.html","","","",,"0",],
   ["|Download<img src='img/blank.gif' height=1 width=53>","http://www.apycom.com/download.html","","","",,,],
   ["Our Products","","","","",,,],
   ["|Java menus<img src='img/blank.gif' height=1 width=39>","","","","",,,],
   ["||Drop Down Menus","http://www.apycom.com/xp-drop-down-menu/","","","",,,],
   ["||Animated Menus","http://www.apycom.com/animated-buttons/","","","",,,],
   ["||Website Buttons","http://www.apycom.com/website-buttons/","","","",,,],
   ["||Navigation Tabs","http://www.apycom.com/navigation-bar-tabs/","","","",,,],
   ["||Live Examples","http://www.apycom.com/indexex.html","","","",,,],
   ["||Sreenshots","http://www.apycom.com/shots/xpdd.html","","","",,,],
   ["||Purchase","http://www.apycom.com/order.html","","","",,"0",],
   ["||Download","http://www.apycom.com/download.html","","","",,,],
   ["|Web buttons","http://www.xp-web-buttons.com/","","","",,,],
   ["|DBF View/Edit","http://www.dbfview.com/","","","",,,],
   ["|More Products","","","","",,,],
   ["||Product 1<img src='img/blank.gif' height=1 width=55>","testlink00491.html","","","",,,],
   ["|||Documentation<img src='img/blank.gif' height=1 width=15>","testlink00492.html","","","",,,],
   ["|||How to Setup","testlink00493.html","","","",,,],
   ["||Product 2","testlink00494.html","","","",,,],
   ["||Product 3","testlink00495.html","","","",,,],
   ["|||Documentation<img src='img/blank.gif' height=1 width=15>","testlink00496.html","","","",,,],
   ["|||How to Setup","testlink00497.html","","","",,,],
   ["||Product 4","testlink00498.html","","","",,,],
   ["Contacts","http://www.apycom.com/contact.html","","","",,,],
];

apy_init();
