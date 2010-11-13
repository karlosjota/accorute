var isHorizontal=1;

var blankImage="img/blank.gif";
var fontStyle="normal 8pt Tahoma";
var fontColor=["#000000","#000000"];
var fontDecoration=["none","none"];

var itemBackColor=["#FFFFFF","#bbccee"];
var itemBorderWidth=1;
var itemAlign="left";
var itemBorderColor=["#FFFFFF","#316AC5"];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];

var subMenuAlign = "left";

var menuBackImage="";
var menuBackColor="#ECE9D8";
var menuBorderColor="#AAAAAA";
var menuBorderStyle="solid";
var menuBorderWidth=0;
var transparency=100;
var transition=24;
var transDuration=300;
var shadowColor="#CCCCCC";
var shadowLen=4;
var menuWidth="";

var iconTopWidth  = 16;
var iconTopHeight = 16;
var iconWidth  = 16;
var iconHeight = 16;

var itemCursor="default";
var itemTarget="";
var statusString="";
var arrowImageMain=[];
var arrowImageSub=["img/xparrow1.gif","img/xparrow1.gif"];
var arrowWidth =9;
var arrowHeight=9;
var itemSpacing=1;
var itemPadding=3;

var separatorImage="img/xpsepar.gif";
var separatorWidth="100%";
var separatorHeight="3";
var separatorAlignment="right";

var separatorVImage="";
var separatorVWidth="5";
var separatorVHeight="16";

var moveCursor = "move";
var moveImage  = "";
var moveWidth      = 12;
var moveHeight     = 24;

var movable = 0;
var absolutePos = 0;
var posX = 20;
var posY  = 120;

var floatable=0;
var floatIterations=5;

var itemStyles =
[
  ["itemBackColor=#ECE9D8,#bbccee","itemBorderColor=#ECE9D8,#316AC5"]
];

var menuStyles =
[
  ["menuBorderWidth=1","menuBackColor=#ffffff"]
];

var menuItems = 
[
    ["Home","testlink00302.html","","","Home Tip",,"0"],
    ["Our Products","testlink00303.html","","","Our Products Tip",,"0"],
    ["|Product 1","testlink00304.html","img/xpicon1.gif","img/xpicon1.gif","Product 1 Tip",,,"0"],
    ["|Product 2","testlink00305.html","img/xpicon2.gif","img/xpicon2.gif","Product 2 Tip"],
    ["||Desc.","testlink00306.html","img/xpicon3.gif","img/xpicon3.gif",,,,"0"],
    ["||Setup","testlink00307.html","img/xpicon4.gif","img/xpicon4.gif"],
    ["|||Parameters","testlink00308.html","img/xpicon1.gif","img/xpicon1.gif",,,,"0"],
    ["|||Tutorial","testlink00309.html","img/xpicon2.gif","img/xpicon2.gif"],
    ["||Help","testlink00310.html","img/xpicon5.gif","img/xpicon5.gif"],
    ["|Product 3","testlink00311.html","img/xpicon5.gif","img/xpicon5.gif","Product 3 Tip"],
    ["|Product 4","testlink00312.html","img/xpicon2.gif","img/xpicon2.gif","Product 4 Tip"],
    ["|-"],
    ["|Product 5","testlink00313.html","img/xpicon3.gif","img/xpicon3.gif","Product 5 Tip"],
    ["|Product 6","testlink00314.html","img/xpicon4.gif","img/xpicon4.gif","Product 6 Tip"],
    ["More Info","testlink00315.html","","",,,"0"],
    ["|You can place <b>any HTML code</b><br> to item, for example <u>image</u>:<br><img src=img/logo.gif>","testlink00316.html",,,,,,"0"],
    ["Javascript calls","","","",,,"0"],
    ["|Click to call<br>message box","javascript:alert('Hello world!')","","",,,,"0"],
    ["|Click to call<br>confirmation<br>dialog","javascript:confirm('Do you want to confirm?')"],
    ["|Open 'Testlink'<br>page in 680x600<br>window","javascript:open('testlink00317.html','_blank','scrollbars,width=680,height=600')"],
    ["Contact Us","mailto:support@apycom.com","","","Contact Us Tip",,"0"]
];

apy_init();
