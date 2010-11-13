//Apycom.com DHTML-Tuner StyleNames
//ItemStyles
//Purchase Style
//Separator Style
//Style Three
//End of ItemStyles
//SmStyles
//More Submenu
//End of SmStyles
//End of StyleNames

var blankImage="img/blank.gif";
var isHorizontal=1;
var menuWidth="0";
var absolutePos=0;
var posX=0;
var posY=0;
var floatable=0;
var floatIterations=0;
var movable=0;
var moveCursor="default";
var moveImage="";
var moveWidth=0;
var moveHeight=0;
var fontStyle="normal 8pt Tahoma";
var fontColor=["#000000","#FFFFFF"];
var fontDecoration=["none","none"];
var itemBackColor=["#FBFF62","#FF8B22"];
var itemBorderWidth=0;
var itemAlign="left";
var itemBorderColor=["",""];
var itemBorderStyle=["",""];
var itemBackImage=["",""];
var itemSpacing=0;
var itemPadding=4;
var itemCursor="hand";
var itemTarget="frm2";
var iconTopWidth=0;
var iconTopHeight=0;
var iconWidth=0;
var iconHeight=0;
var menuBackImage="";
var menuBackColor="#FFFFFF";
var menuBorderColor="#000000 ";
var menuBorderStyle=["solid"];
var menuBorderWidth=1;
var subMenuAlign="";
var transparency=100;
var transition=10;
var transDuration=400;
var shadowColor="#B6B6B6";
var shadowLen=3;
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
var pressedItem=1;

var itemStyles = [
   ["fontStyle=bold 8pt Tahoma","itemBackColor=#FFD900,#FF812D",],
   ["itemBackColor=#000000,#3F3F3F","itemBorderColor=#FFFFFF,#FFFFFF",],
   ["itemBackColor=#BAFF17,#FF962D",],
];

var menuStyles = [
   ["menuBorderColor=#25AD12",],
];

var menuItems = [
   ["Apycom Home","http://www.apycom.com/","","","",,,],
   ["DHTML Menu","","","","",,,],
   ["|Home","../index.html","","","",,,],
   ["||Examples","dhtml-menu-ex1.html","","","",,,],
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
   ["||-","","","","",],
   ["||Purchase","http://www.apycom.com/order.html","","","",,"0",],
   ["||Download","http://www.apycom.com/download.html","","","",,,],
   ["|Web buttons","http://www.xp-web-buttons.com/","","","",,,],
   ["|DBF Viewer/Editor","http://www.dbfview.com/","","","",,,],
   ["|-","","","","",],
   ["|More Products","","","","",,"2",],
   ["||Product 1","testlink00391.html","","","",,"2","0"],
   ["|||Documentation","testlink00392.html","","","",,"2","0"],
   ["|||How to Setup","testlink00393.html","","","",,"2",],
   ["||Product 2","testlink00394.html","","","",,"2",],
   ["||Product 3","testlink00395.html","","","",,"2",],
   ["|||Documentation","testlink00396.html","","","",,"2","0"],
   ["|||How to Setup","testlink00397.html","","","",,"2",],
   ["||Product 4","testlink00398.html","","","",,"2",],
   ["Contacts","http://www.apycom.com/contact.html","","","",,,],
];

apy_initFrame("fset",0,1,0);
