//Apycom.com DHTML-Tuner StyleNames
//ItemStyles
//DHTML Style
//Products Style
//Separator Style
//Contacts Style
//End of ItemStyles
//SmStyles
//End of SmStyles
//End of StyleNames

var blankImage="img/blank.gif";
var isHorizontal=1;
var menuWidth="0";
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
var fontStyle="normal 8pt Verdana";
var fontColor=["#FFFFFF","#FFFFFF"];
var fontDecoration=["none","none"];
var itemBackColor=["#FF7979","#EF6161"];
var itemBorderWidth=0;
var itemAlign="left";
var itemBorderColor=["#FFFFFF","#FFFFFF"];
var itemBorderStyle=["ridge","ridge"];
var itemBackImage=["",""];
var itemSpacing=0;
var itemPadding=5;
var itemCursor="default";
var itemTarget="_blank";
var iconTopWidth=0;
var iconTopHeight=0;
var iconWidth=0;
var iconHeight=0;
var menuBackImage="";
var menuBackColor="#FFFFFF";
var menuBorderColor="#FFFFFF #FFFFFF #FFFFFF #FFFFFF ";
var menuBorderStyle=["ridge"];
var menuBorderWidth=1;
var subMenuAlign="";
var transparency=100;
var transition=23;
var transDuration=400;
var shadowColor="#FFBBBB";
var shadowLen=3;
var arrowImageMain=["",""];
var arrowImageSub=["img/arrow2_n.gif","img/arrow2_n.gif"];
var arrowWidth=7;
var arrowHeight=7;
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
   ["itemBackColor=#CE9DE1,#B978D1",],
   ["itemBackColor=#84DA7A,#48D641",],
   ["itemBackColor=#84DA7A,#84DA7A","itemBorderColor=#84DA7A,#84DA7A","itemBorderWidth=1","itemBorderStyle=dashed,dashed",],
   ["itemBackColor=#5EA6E1,#507ECB",],
];

var menuItems = [
   ["Apycom Home","http://www.apycom.com/","","","",,,],
   ["DHTML Menu","","","","",,"0",],
   ["|Home","../index.html","","","",,"0",],
   ["||Examples","dhtml-menu-ex1.html","","","",,"0",],
   ["||How to Setup","../indexex.html#set","","","",,"0",],
   ["||Parameters","../params.html","","","",,"0",],
   ["|Purchase","../order.html","","","",,"0",],
   ["|Download","http://www.apycom.com/download.html","","","",,"0",],
   ["Our Products","","","","",,"1",],
   ["|Java menus","","","","",,"1",],
   ["||Drop Down Menus","http://www.apycom.com/xp-drop-down-menu/","","","",,"1",],
   ["||Animated Menus","http://www.apycom.com/animated-buttons/","","","",,"1",],
   ["||Website Buttons","http://www.apycom.com/website-buttons/","","","",,"1",],
   ["||Navigation Tabs","http://www.apycom.com/navigation-bar-tabs/","","","",,"1",],
   ["||Live Examples","http://www.apycom.com/indexex.html","","","",,"1",],
   ["||Sreenshots","http://www.apycom.com/shots/xpdd.html","","","",,"1",],
   ["||-","","","","",,"2",],
   ["||Purchase","http://www.apycom.com/order.html","","","",,"1",],
   ["||Download","http://www.apycom.com/download.html","","","",,"1",],
   ["|Web buttons","http://www.xp-web-buttons.com/","","","",,"1",],
   ["|DBF Viewer/Editor","http://www.dbfview.com/","","","",,"1",],
   ["|-","","","","",,"2",],
   ["|More Products","","","","",,"1",],
   ["||Product 1","testlink00507.html","","","",,"1",],
   ["|||Documentation","testlink00508.html","","","",,"1",],
   ["|||How to Setup","testlink00509.html","","","",,"1",],
   ["||Product 2","testlink00510.html","","","",,"1",],
   ["||Product 3","testlink00511.html","","","",,"1",],
   ["|||Documentation","testlink00512.html","","","",,"1",],
   ["|||How to Setup","testlink00513.html","","","",,"1",],
   ["||Product 4","testlink00514.html","","","",,"1",],
   ["Contacts","http://www.apycom.com/contact.html","","","",,"3",],
];

apy_init();
