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
var itemBackColor=["#FFFFFF","#4D74FD"];
var itemBorderWidth=0;
var itemAlign="left";
var itemBorderColor=["",""];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];
var itemSpacing=0;
var itemPadding=2;
var itemCursor="default";
var itemTarget="_blank";
var iconTopWidth=16;
var iconTopHeight=16;
var iconWidth=16;
var iconHeight=16;
var menuBackImage="";
var menuBackColor="#FFFFFF";
var menuBorderColor="#BFBFBF #737373 #4D4D4D #AAAAAA ";
var menuBorderStyle=["solid"];
var menuBorderWidth=1;
var subMenuAlign="left";
var transparency=80;
var transition=24;
var transDuration=300;

var shadowColor="#C7C7C7";
var shadowLen=3;
var shadowTop=0;

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
var separatorVHeight="100%";

var statusString="text";
var pressedItem=0;

var itemStyles = [
   ["itemWidth=120",],
   ["itemWidth=200",],
];

var menuItems = [
   ["Home","testlink00084.html","img/new1-05.gif","img/new1-05.gif","Home Tip","_blank","0",],
   ["Our Products","testlink00085.html","img/new1-08.gif","img/new1-08.gif","Our Products Tip",,"0",],
   ["|Product 1","testlink00086.html","img/b011.gif","img/b01.gif","Product 1 Tip",,"1",],
   ["|Product 2","testlink00087.html","img/b011.gif","img/b01.gif","Product 2 Tip",,,],
   ["||Description","testlink00088.html","img/b061.gif","img/b06.gif","",,,],
   ["||Setup","testlink00089.html","img/b061.gif","img/b06.gif","",,,],
   ["|||Parameters","testlink00090.html","img/b021.gif","img/b02.gif","",,,],
   ["|||Tutorial","testlink00091.html","img/b021.gif","img/b02.gif","",,,],
   ["||Help","testlink00092.html","img/b061.gif","img/b06.gif","",,,],
   ["|Product 3","testlink00093.html","img/b011.gif","img/b01.gif","Product 3 Tip",,,],
   ["|Product 4","testlink00094.html","img/b011.gif","img/b01.gif","Product 4 Tip",,,],
   ["|Product 5","testlink00095.html","img/b011.gif","img/b01.gif","Product 5 Tip",,,],
   ["||Description","testlink00096.html","img/b061.gif","img/b06.gif","",,,],
   ["||How to Setup","testlink00097.html","img/b061.gif","img/b06.gif","",,,],
   ["|||Parameters","testlink00098.html","img/b021.gif","img/b02.gif","",,,],
   ["|||Tutorial","testlink00099.html","img/b021.gif","img/b02.gif","",,,],
   ["||Help","testlink00100.html","img/b061.gif","img/b06.gif","",,,],
   ["|Product 6","testlink00101.html","img/b011.gif","img/b01.gif","Product 6 Tip",,,],
   ["-","","","","",,,],
   ["More Info","testlink00102.html","img/new4-0985.gif","","",,"0",],
   ["|You can place <b>any HTML code</b><br> to item, for example <u>image</u>:<br><img src=img/logo.gif>","testlink00103.html","","","",,,],
   ["Javascript calls","","img/new4-038.gif","","",,"0",],
   ["|Click to call message box","javascript:alert('Hello world!')","img/b09.gif","img/b092.gif","",,"1",],
   ["|Click to callconfirmation dialog","javascript:confirm('Do you want to confirm?')","img/b09.gif","img/b092.gif","",,"1",],
   ["|Open 'Testlink' page in 680x600<br>window","javascript:open('testlink00104.html','_blank','scrollbars,width=680,height=600')","img/b09.gif","img/b092.gif","",,"1",],
   ["-","","","","",,,],
   ["Contact Us","mailto:support@apycom.com","img/new4-098.gif","img/new4-098.gif","Contact Us Tip",,"0",],
];

apy_init();
