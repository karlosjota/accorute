//Apycom.com DHTML-Tuner StyleNames
//ItemStyles
//Item Style 4
//End of ItemStyles
//SmStyles
//End of SmStyles
//End of StyleNames

var blankImage="img/blank.gif";
var isHorizontal=1;
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
var itemBackColor=["#DDDDDD","#BFBFBF"];
var itemBorderWidth=1;
var itemAlign="left";
var itemBorderColor=["#DDDDDD","#9A9A9A"];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];
var itemSpacing=2;
var itemPadding=3;
var itemCursor="default";
var itemTarget="_blank";
var iconTopWidth=16;
var iconTopHeight=16;
var iconWidth=16;
var iconHeight=16;
var menuBackImage="";
var menuBackColor="#DDDDDD";
var menuBorderColor="#C0C0C0 #969696 #828282 #D1D1D1 ";
var menuBorderStyle=["solid"];
var menuBorderWidth=1;
var subMenuAlign="left";

var transparency=80;
var transDuration=300;
var transition=27;
var transOptions="gradientSize=0.75,wipestyle=1,motion=reverse";

var shadowColor="#000000";
var shadowLen=0;
var arrowImageMain=["img/arrow_r.gif","img/arrow_r.gif"];
var arrowImageSub=["img/arrow_r.gif","img/arrow_r.gif"];
var arrowWidth=7;
var arrowHeight=7;
var separatorImage="img/separ1.gif";
var separatorWidth="100%";
var separatorHeight="5";
var separatorAlignment="center";
var separatorVImage="img/separv1.gif";
var separatorVWidth="5";
var separatorVHeight=16;
var statusString="text";
var pressedItem=1;


var menuItems = [
   ["-","","","","",,,],
   ["Home","testlink00024.html","img/new1-05.gif","img/new1-05.gif",,"_blank",,],
   ["-","","","","",,,],
   ["Our Products","testlink00025.html","img/new1-08.gif","img/new1-08.gif",,,,],
   ["|Product 1","testlink00026.html","img/b011.gif","img/b01.gif",,,,],
   ["|Product 2","testlink00027.html","img/b011.gif","img/b01.gif",,,,],
   ["||Description","testlink00028.html","img/b061.gif","img/b06.gif","",,,],
   ["||Setup","testlink00029.html","img/b061.gif","img/b06.gif","",,,],
   ["|||Parameters","testlink00030.html","img/b021.gif","img/b02.gif","",,,],
   ["|||Tutorial","testlink00031.html","img/b021.gif","img/b02.gif","",,,],
   ["||Help","testlink00032.html","img/b061.gif","img/b06.gif","",,,],
   ["|Product 3","testlink00033.html","img/b011.gif","img/b01.gif",,,,],
   ["|Product 4","testlink00034.html","img/b011.gif","img/b01.gif",,,,],
   ["|Product 5","testlink00035.html","img/b011.gif","img/b01.gif",,,,],
   ["||Description","testlink00036.html","img/b061.gif","img/b06.gif","",,,],
   ["||How to Setup","testlink00037.html","img/b061.gif","img/b06.gif","",,,],
   ["|||Parameters","testlink00038.html","img/b021.gif","img/b02.gif","",,,],
   ["|||Tutorial","testlink00039.html","img/b021.gif","img/b02.gif","",,,],
   ["||Help","testlink00040.html","img/b061.gif","img/b06.gif","",,,],
   ["|Product 6","testlink00041.html","img/b011.gif","img/b01.gif",,,,],
   ["-","","","","",],
   ["More Info","testlink00042.html","img/new4-0985.gif","","",,,],
   ["|You can place <b>any HTML code</b><br> to item, for example <u>image</u>:<br><img src=img/logo.gif>","testlink00043.html","","","",,,],
   ["-","","","","",,,],
   ["Javascript calls","","img/new4-038.gif","","",,,],
   ["|Click to call message box","javascript:alert('Hello world!')","img/b09.gif","img/b092.gif","",,,],
   ["|Click to callconfirmation dialog","javascript:confirm('Do you want to confirm?')","img/b09.gif","img/b092.gif","",,,],
   ["|Open 'Testlink' page in 680x600<br>window","javascript:open('testlink00044.html','_blank','scrollbars,width=680,height=600')","img/b09.gif","img/b092.gif","",,,],
   ["-","","","","",,,],
   ["Contact Us","mailto:support@apycom.com","img/new4-098.gif","img/new4-098.gif",,,,],
   ["-","","","",""],
];

apy_init();
