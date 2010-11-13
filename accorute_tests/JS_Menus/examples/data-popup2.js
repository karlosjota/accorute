var popupMode=1;

var blankImage="img/blank.gif";
var isHorizontal=0;
var menuWidth=0;
var absolutePos=0;
var posX=20;
var posY=10;
var floatable=0;
var floatIterations=5;
var movable=0;
var moveCursor="move";
var moveImage="img/movepic4.gif";
var moveWidth=12;
var moveHeight=18;
var fontStyle="normal 8pt Tahoma";
var fontColor=["#3B3B3B","#FFFFFF"];
var fontDecoration=["none","none"];
var itemBackColor=["#FFFFEE","#4D74FD"];
var itemBorderWidth=0;
var itemAlign="left";
var itemBorderColor=["",""];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];
var itemSpacing=0;
var itemPadding=2;
var itemCursor="default";
var itemTarget="";
var iconTopWidth=16;
var iconTopHeight=16;
var iconWidth=16;
var iconHeight=16;
var menuBackImage="";
var menuBackColor="";
var menuBorderColor="#BFBFBF #737373 #4D4D4D #AAAAAA ";
var menuBorderStyle=["solid"];
var menuBorderWidth=1;
var subMenuAlign="left";
var transparency=80;
var transition=10;
var transDuration=300;
var shadowColor="#C7C7C7";
var shadowLen=3;
var arrowImageMain=["img/arrow_r.gif","img/arrow_r.gif"];
var arrowImageSub=["img/arrow_r.gif","img/arrow_r.gif"];
var arrowWidth=7;
var arrowHeight=7;

var separatorImage="img/separ1.gif";
var separatorWidth="100%";
var separatorHeight="5";
var separatorAlignment="center";

var separatorVImage="img/separ1.gif";
var separatorVWidth="100&";
var separatorVHeight="5";
var statusString="text";
var pressedItem=-2;

var menuItems = [
   ["Open Link","testlink00074.html"],
   ["Open Link in New Window","testlink00075.html","","","","_blank"],
   ["More Info","testlink00076.html"],
     ["|Help","testlink00077.html"],
     ["|About","testlink00078.html"],
];

apy_init();
