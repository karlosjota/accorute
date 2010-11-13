var isHorizontal=1;

var pressedItem = 0;

var blankImage="img/blank.gif";
var fontStyle="normal 8pt Verdana";
var fontColor=["#000000","#FF0000"];
var fontDecoration=["none","none"];

var itemBackColor=["#dddddd","#ffffff"];
var itemBorderWidth=0;
var itemAlign="left";
var itemBorderColor=["#6655ff","#665500"];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];

var menuBackImage="";
var menuBackColor="#dddddd";
var menuBorderColor="#000000";
var menuBorderStyle="solid";
var menuBorderWidth=0;
var transparency=80;
var transition=24;
var transDuration=500;
var shadowColor="#999999";
var shadowLen=4;
var menuWidth="";

var itemCursor="hand";
var itemTarget="_blank";
var statusString="text";
var subMenuAlign = "left";
var iconTopWidth  = 16;
var iconTopHeight = 16;
var iconWidth=16;
var iconHeight=16;
var arrowImageMain=["img/arrow_d.gif","img/arrow_d2.gif"];
var arrowImageSub=["img/arrow_r.gif","img/arrow_r2.gif"];
var arrowWidth=7;
var arrowHeight=7;
var itemSpacing=1;
var itemPadding=3;

var separatorImage="img/separ1.gif";
var separatorWidth="100%";
var separatorHeight="5";
var separatorAlignment="center";

var separatorVImage="img/separv1.gif";
var separatorVWidth="5";
var separatorVHeight="16";

var moveCursor = "move";
var movable = 0;
var absolutePos = 0;
var posX = 20;
var posY = 100;

var floatable=1;
var floatIterations=5;

var menuItems = 
[
    ["Home","testlink00126.html","img/new1-05.gif","img/new1-05.gif","Home Tip"],
    ["-"],
    ["Our Products","testlink00127.html","img/new1-08.gif","img/new1-08.gif","Our Products Tip"],
    ["|Product 1","testlink00128.html","img/b011.gif","img/b01.gif","Product 1 Tip"],
    ["|Product 2","","img/b011.gif","img/b01.gif","Product 2 Tip","_self",],
    ["||Docs","testlink00129.html","img/b061.gif","img/b06.gif"],
    ["||How to Setup","testlink00130.html","img/b061.gif","img/b06.gif"],
    ["|||Parameters","testlink00131.html","img/b021.gif","img/b02.gif"],
    ["|||-"],
    ["|||Tutorial","testlink00132.html","img/b021.gif","img/b02.gif"],
    ["||More Info","testlink00133.html","img/b061.gif","img/b06.gif"],
    ["|Product 3","","img/b011.gif","img/b01.gif","Product 3 Tip"],
    ["|Product 4","testlink00134.html","img/b011.gif","img/b01.gif","Product 4 Tip"],
    ["|-"],
    ["|Product 5","testlink00135.html","img/b011.gif","img/b01.gif","Product 5 Tip"],
    ["|Product 6","testlink00136.html","img/b011.gif","img/b01.gif","Product 6 Tip"],
    ["-"],
    ["More Info","testlink00137.html","img/new4-0985.gif"],
    ["|You can place <b>any HTML code</b><br> to item, for example <u>image</u>:<br><img src=img/logo.gif>","testlink00138.html"],
    ["Javascript calls","","img/new4-038.gif"],
    ["|Click to call<br>message box","javascript:alert('Hello world!')","img/b09.gif","img/b092.gif"],
    ["|Click to call<br>confirmation<br>dialog","javascript:confirm('Do you want to confirm?')","img/b09.gif","img/b092.gif"],
    ["|Open 'Testlink'<br>page in 680x600<br>window","javascript:open('testlink00139.html','_blank','scrollbars,width=680,height=600')","img/b09.gif","img/b092.gif"],
    ["Contact Us","mailto:support@apycom.com","img/new4-098.gif","img/new4-098.gif","Contact Us Tip"]
];

apy_init();
