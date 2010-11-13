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
var menuWidth="0";
var absolutePos=1;
var posX=100;
var posY=80;
var floatable=0;
var floatIterations=0;
var movable=0;
var moveCursor="default";
var moveImage="";
var moveWidth=0;
var moveHeight=0;
var fontStyle="normal 7pt Verdana";
var fontColor=["#000000","#A0A0A0"];
var fontDecoration=["none","none"];
var itemBackColor=["",""];
var itemBorderWidth=0;
var itemAlign="left";
var itemBorderColor=["",""];
var itemBorderStyle=["",""];
var itemBackImage=["img/itemback1_n.gif","img/itemback1_o.gif"];
var itemSpacing=0;
var itemPadding=3;
var itemCursor="default";
var itemTarget="frm2";
var iconTopWidth=0;
var iconTopHeight=0;
var iconWidth=0;
var iconHeight=0;
var menuBackImage="";
var menuBackColor="";
var menuBorderColor="#B7B7B7 ";
var menuBorderStyle=["solid"];
var menuBorderWidth=1;
var subMenuAlign="";
var transparency=100;
var transition=8;
var transDuration=400;
var shadowColor="#858585";
var shadowLen=3;
var arrowImageMain=["",""];
var arrowImageSub=["img/arrow1_n.gif","img/arrow1_n.gif"];
var arrowWidth=6;
var arrowHeight=5;
var separatorImage="";
var separatorWidth="100%";
var separatorHeight="0";
var separatorAlignment="";
var separatorVImage="";
var separatorVWidth="0";
var separatorVHeight="100%";
var statusString="link";
var pressedItem=-2;

var itemStyles = [
   ["itemBackColor=#FFA466,#FFBF95",],
   ["itemBackColor=#FFFFFF,#FFFFFF",],
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
   ["||-","","","","",,"1",],
   ["||Purchase","http://www.apycom.com/order.html","","","",,"0",],
   ["||Download","http://www.apycom.com/download.html","","","",,,],
   ["|Web buttons","http://www.xp-web-buttons.com/","","","",,,],
   ["|DBF Viewer/Editor","http://www.dbfview.com/","","","",,,],
   ["|-","","","","",,"1",],
   ["|More Products","","","","",,,],
   ["||Product 1","testlink00399.html","","","",,,],
   ["|||Documentation","testlink00400.html","","","",,,],
   ["|||How to Setup","testlink00401.html","","","",,,],
   ["||Product 2","testlink00402.html","","","",,,],
   ["||Product 3","testlink00403.html","","","",,,],
   ["|||Documentation","testlink00404.html","","","",,,],
   ["|||How to Setup","testlink00405.html","","","",,,],
   ["||Product 4","testlink00406.html","","","",,,],
   ["Contacts","http://www.apycom.com/contact.html","","","",,,],
];

apy_initFrame("fset",0,1,1);
