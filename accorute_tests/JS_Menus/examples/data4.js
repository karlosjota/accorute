var isHorizontal=1;

var blankImage="img/blank.gif";
var fontStyle="normal 9pt Franklin Gothic Medium";
var fontColor=["#444444","#ffffff"];
var fontDecoration=["none","none"];

var itemBackColor=["#ffffff","#4792E6"];
var itemBorderWidth=0;
var itemAlign="left";
var itemBorderColor=["#6655ff","#665500"];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];

var subMenuAlign = "left";

var menuBackImage="";
var menuBackColor="#ffffff";
var menuBorderColor="#AAAAAA";
var menuBorderStyle="solid";
var menuBorderWidth=1;
var transparency=100;
var transition=24;
var transDuration=300;
var shadowColor="#777777";
var shadowLen=3;
var menuWidth="";

var iconTopWidth  = 16;
var iconTopHeight = 16;
var iconWidth  = 16;
var iconHeight = 16;

var itemCursor="default";
var itemTarget="";
var statusString="Apycom DHTML Menu";
var arrowImageMain=["img/arrowm1.gif","img/arrowm2.gif"];
var arrowImageSub=["img/arrow1.gif","img/arrow2.gif"];
var arrowWidth =9;
var arrowHeight=9;
var itemSpacing=0;
var itemPadding=4;

var separatorImage="img/separ.gif";
var separatorWidth="100%";
var separatorHeight="3";
var separatorAlignment="right";

var separatorVImage="";
var separatorVWidth="5";
var separatorVHeight="16";

var moveCursor = "move";
var moveImage  = "img/movepic2.gif";
var moveWidth      = 12;
var moveHeight      = 24;

var movable = 1;
var absolutePos = 1;
var posX = 20;
var posY  = 120;

var floatable=0;
var floatIterations=5;

var itemStyles =
[
    ["fontStyle=bold 8pt Verdana",
     "fontColor=#4792E6,#ffffff",
     "itemBorderWidth=1",
     "itemBorderStyle=dotted,dotted",
     "itemBorderColor=#aaaaaa,#4792E6",
     "itemBackColor=#ffffff,#4792E6"]
];

var menuItems = 
[
    ["Home","testlink00238.html","img/icon1.gif","img/icon2.gif","Home Tip"],
    ["Our Products","testlink00239.html","img/icon1.gif","img/icon2.gif","Our Products Tip"],
    ["|Product 1","testlink00240.html","img/icon1.gif","img/icon2.gif","Product 1 Tip"],
    ["|Product 2","testlink00241.html","img/icon1.gif","img/icon2.gif","Product 2 Tip"],
    ["||Info","testlink00242.html","img/icon1.gif","img/icon2.gif"],
    ["||Setup","testlink00243.html","img/icon1.gif","img/icon2.gif"],
    ["|||Parameters","testlink00244.html","img/icon1.gif","img/icon2.gif"],
    ["|||Tutorial","testlink00245.html","img/icon1.gif","img/icon2.gif"],
    ["||Help","testlink00246.html","img/icon1.gif","img/icon2.gif"],
    ["|Product 3","testlink00247.html","img/icon1.gif","img/icon2.gif","Product 3 Tip"],
    ["|Product 4","testlink00248.html","img/icon1.gif","img/icon2.gif","Product 4 Tip"],
    ["|-"],
    ["|Product 5","testlink00249.html","img/icon1.gif","img/icon2.gif","Product 5 Tip",,"0"],
    ["|Product 6","testlink00250.html","img/icon1.gif","img/icon2.gif","Product 6 Tip"],
    ["More Info","testlink00251.html","img/icon1.gif","img/icon2.gif"],
    ["|You can place <b>any HTML code</b><br> to item, for example <u>ordered list</u>:<br><ol><li>item<li>item<li>item</ol>","testlink00252.html"],
    ["Javascript calls","","img/icon1.gif","img/icon2.gif"],
    ["|Click to call<br>message box","javascript:alert('Hello world!')","img/icon1.gif","img/icon2.gif"],
    ["|Click to call<br>confirmation<br>dialog","javascript:confirm('Do you want to confirm?')","img/icon1.gif","img/icon2.gif"],
    ["|Open 'Testlink'<br>page in 680x600<br>window","javascript:open('testlink00253.html','_blank','scrollbars,width=680,height=600')","img/icon1.gif","img/icon2.gif"],
    ["Contact Us","mailto:support@apycom.com","img/icon1.gif","img/icon2.gif","Contact Us Tip","_"]
];

apy_init();
