var blankImage = "img/blank.gif";

var isHorizontal = 1;
var menuWidth = "";

var absolutePos = 1;
var posX = 190;
var posY = 160;

var floatable = 1;
var floatIterations = 8;

var movable = 0;
var moveCursor = "move";
var moveImage  = "img/movepic2.gif";
var moveWidth  = 12;
var moveHeight = 24;

var fontStyle="normal 9pt Franklin Gothic Medium";
var fontColor=["#444444","#ffffff"];
var fontDecoration=["none","none"];

var itemBackColor=["#ffffff","#4792E6"];
var itemBorderWidth=0;
var itemAlign="left";
var itemBorderColor=["#6655ff","#665500"];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];
var itemSpacing=0;
var itemPadding=4;
var itemCursor="default";
var itemTarget="";

var iconTopWidth  = 16;
var iconTopHeight = 16;
var iconWidth  = 16;
var iconHeight = 16;

var menuBackImage="";
var menuBackColor="#ffffff";
var menuBorderColor="#AAAAAA";
var menuBorderStyle="solid";
var menuBorderWidth=1;
var subMenuAlign = "left";

var transparency=100;
var transition=3;
var transDuration=300;
var shadowColor="#777777";
var shadowLen=3;

var arrowImageMain=["img/arrowm1.gif","img/arrowm2.gif"];
var arrowImageSub=["img/arrow1.gif","img/arrow2.gif"];
var arrowWidth =9;
var arrowHeight=9;

var separatorImage="img/separ.gif";
var separatorWidth="100%";
var separatorHeight="3";
var separatorAlignment="right";

var separatorVImage="";
var separatorVWidth="5";
var separatorVHeight="16";

var statusString="Apycom DHTML Menu";

var menuItems = 
[
    ["Home","testlink00376.html","img/icon1.gif","img/icon2.gif","Home Tip"],
    ["Our Products","testlink00377.html","img/icon1.gif","img/icon2.gif","Our Products Tip"],
    ["|Product 1","testlink00378.html","img/icon1.gif","img/icon2.gif","Product 1 Tip"],
    ["|Product 2","testlink00379.html","img/icon1.gif","img/icon2.gif","Product 2 Tip"],
    ["||Info","testlink00380.html","img/icon1.gif","img/icon2.gif"],
    ["||Setup","testlink00381.html","img/icon1.gif","img/icon2.gif"],
    ["|||Parameters","testlink00382.html","img/icon1.gif","img/icon2.gif"],
    ["|||Tutorial","testlink00383.html","img/icon1.gif","img/icon2.gif"],
    ["||Help","testlink00384.html","img/icon1.gif","img/icon2.gif"],
    ["|Product 3","testlink00385.html","img/icon1.gif","img/icon2.gif","Product 3 Tip"],
    ["|Product 4","testlink00386.html","img/icon1.gif","img/icon2.gif","Product 4 Tip"],
    ["|-"],
    ["|Product 5","testlink00387.html","img/icon1.gif","img/icon2.gif","Product 5 Tip"],
    ["|Product 6","testlink00388.html","img/icon1.gif","img/icon2.gif","Product 6 Tip"],
    ["More Info","testlink00389.html","img/icon1.gif","img/icon2.gif"],
    ["|You can place <b>any HTML code</b><br> to item, for example <u>ordered list</u>:<br><ol><li>item<li>item<li>item</ol>","testlink00390.html"],
];

apy_init();
