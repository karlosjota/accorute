var isHorizontal=0;

var iconTopWidth  = 16;
var iconTopHeight = 16;
var subMenuAlign = "left";
var moveImage  = "img/movepic4.gif";
var moveWidth      = 12;
var moveHeight      = 18;

var blankImage="img/blank.gif";
var fontStyle="normal 8pt Tahoma";
var fontColor=["#000000","#0000ff"];
var fontDecoration=["none","underline"];

var itemBackColor=["#E2EEFF","#ffffff"];
var itemBorderWidth=1;
var itemAlign="left";
var itemBorderColor=["#c0e0FF","#80A0FF"];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];

var menuBackImage="";
var menuBackColor="#ffffff";
var menuBorderColor="#000000";
var menuBorderStyle="solid";
var menuBorderWidth=0;
var transparency=100;
var transition=10;
var transDuration=300;
var shadowColor="#ffffff";
var shadowLen=0;
var menuWidth="";  // NEW (NN% or NNpx. Default - 0px)

var statusString="text";
var iconWidth=16;
var iconHeight=16;
var arrowImageMain=["img/arrow_r.gif","img/arrow_r.gif"];
var arrowImageSub=["img/arrow_r.gif","img/arrow_r.gif"]; // NEW
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

var movable=0;
var absolutePos=1;
var posX=50;
var posY=162;

var itemCursor = "hand";
var itemTarget = "_self";
var moveCursor = "move";

var floatable=1;
var floatIterations=4;

var menuItems =
[
    ["Home","testlink00334.html","img/new1-05.gif","img/new1-05.gif","Home Tip","_blank"],
    ["Our Products","testlink00335.html","img/new1-08.gif","img/new1-08.gif","Our Products Tip"],
    ["|Product 1","testlink00336.html","img/b011.gif","img/b01.gif","Product 1 Tip"],
    ["|Product 2","testlink00337.html","img/b011.gif","img/b01.gif","Product 2 Tip"],
    ["||Description","testlink00338.html","img/b061.gif","img/b06.gif"],
    ["||Setup","testlink00339.html","img/b061.gif","img/b06.gif"],
    ["|||Parameters","testlink00340.html","img/b021.gif","img/b02.gif"],
    ["|||Tutorial","testlink00341.html","img/b021.gif","img/b02.gif"],
    ["||Help","testlink00342.html","img/b061.gif","img/b06.gif"],
    ["|Product 3","testlink00343.html","img/b011.gif","img/b01.gif","Product 3 Tip"],
    ["|Product 4","testlink00344.html","img/b011.gif","img/b01.gif","Product 4 Tip"],
    ["|Product 5","testlink00345.html","img/b011.gif","img/b01.gif","Product 5 Tip"],
    ["||Description","testlink00346.html","img/b061.gif","img/b06.gif"],
    ["||How to Setup","testlink00347.html","img/b061.gif","img/b06.gif"],
    ["|||Parameters","testlink00348.html","img/b021.gif","img/b02.gif"],
    ["|||Tutorial","testlink00349.html","img/b021.gif","img/b02.gif"],
    ["||Help","testlink00350.html","img/b061.gif","img/b06.gif"],
    ["|Product 6","testlink00351.html","img/b011.gif","img/b01.gif","Product 6 Tip"],
    ["More Info","testlink00352.html","img/new4-0985.gif"],
    ["|You can place <b>any HTML code</b><br> to item, for example <u>image</u>:<br><img src=img/logo.gif>","testlink00353.html"],
    ["Javascript calls","","img/new4-038.gif"],
    ["|Click to call<br>message box","javascript:alert('Hello world!')","img/b09.gif","img/b092.gif"],
    ["|Click to call<br>confirmation<br>dialog","javascript:confirm('Do you want to confirm?')","img/b09.gif","img/b092.gif"],
    ["|Open 'Testlink'<br>page in 680x600<br>window","javascript:open('testlink00354.html','_blank','scrollbars,width=680,height=600')","img/b09.gif","img/b092.gif"],
    ["Contact Us","mailto:support@apycom.com","img/new4-098.gif","img/new4-098.gif","Contact Us Tip"]
];

//
apy_init();
