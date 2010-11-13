var isHorizontal=0;

var iconTopWidth  = 16;
var iconTopHeight = 16;
var subMenuAlign = "left";

var blankImage="img/blank.gif";
var fontStyle="normal 8pt Arial";
var fontColor=["#000000","#ffffff"];
var fontDecoration=["none","none"];

var itemBackColor=["#cccccc","#000080"];
var itemBorderWidth=0;
var itemAlign="left";
var itemBorderColor=["#c0e0FF","#80A0FF"];
var itemBorderStyle=["solid","solid"];
var itemBackImage=["",""];

var menuBackImage="";
var menuBackColor="#cccccc";
var menuBorderColor="#eeeeee #000000 #000000 #eeeeee";
var menuBorderStyle="solid";
var menuBorderWidth=1;
var transparency=100;
var transition=0;
var transDuration=0;
var shadowColor="";
var shadowLen=0;
var menuWidth="0%";  // NEW (NN% or NNpx. Default - 0px)

var itemTarget="";
var statusString="";
var iconWidth=16;
var iconHeight=16;
var arrowImageMain=["img/arrow_r.gif","img/arrow_rw.gif"]; // NEW
var arrowImageSub=["img/arrow_r.gif","img/arrow_rw.gif"]; // NEW
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
var separatorVHeight="100%";

var movable=1;
var moveImage="img/movepic.gif";
var moveWidth      = 12;
var moveHeight     = 18;

var absolutePos=1;
var posX=30;
var posY=110;

var floatable=1;
var floatIterations=5;

var itemCursor = "hand";
var itemTarget = "_self";
var moveCursor = "move"; 

var menuItems =
[
    ["Home","testlink00196.html","img/home.gif"],
    ["Our Products","testlink00197.html","img/progs.gif"],
    ["|Product 1","testlink00198.html","img/folders.gif","img/folders.gif","Product 1 Tip"],
    ["|Product 2","testlink00199.html","img/folders.gif","img/folders.gif","Product 2 Tip"],
    ["||Description","testlink00200.html","img/edit.gif","img/edit.gif"],
    ["||Setup","testlink00201.html","img/edit.gif","img/edit.gif"],
    ["|||Parameters","testlink00202.html","img/favor.gif","img/favor.gif"],
    ["|||Tutorial","testlink00203.html","img/favor.gif","img/favor.gif"],
    ["||Help","testlink00204.html","img/edit.gif","img/edit.gif"],
    ["|Product 3","testlink00205.html","img/folders.gif","img/folders.gif","Product 3 Tip"],
    ["|-"],
    ["|Product 4","testlink00206.html","img/folders.gif","img/folders.gif","Product 4 Tip"],
    ["|Product 5","testlink00207.html","img/folders.gif","img/folders.gif","Product 5 Tip"],
    ["||Description","testlink00208.html","img/edit.gif","img/edit.gif"],
    ["||Setup","testlink00209.html","img/edit.gif","img/edit.gif"],
    ["|||Parameters","testlink00210.html","img/favor.gif","img/favor.gif"],
    ["|||-"],
    ["|||Tutorial","testlink00211.html","img/favor.gif","img/favor.gif"],
    ["||Help","testlink00212.html","img/edit.gif","img/edit.gif"],
    ["|Product 6","testlink00213.html","img/folders.gif","img/folders.gif","Product 6 Tip"],
    ["More Info","testlink00214.html","img/setup.gif"],
    ["|You can place <b>any HTML code</b><br> to item, for example <u>image</u>:<br><img src=img/logo.gif>","testlink00215.html"],
    ["Javascript calls","","img/run.gif"],
    ["|Click to call<br>message box","javascript:alert('Hello world!')","img/refresh.gif"],
    ["|Click to call<br>confirmation<br>dialog","javascript:confirm('Do you want to confirm?')","img/refresh.gif"],
    ["|Open 'Testlink'<br>page in 680x600<br>window","javascript:open('testlink00216.html','_blank','scrollbars,width=680,height=600')","img/refresh.gif"],
    ["Contact Us","mailto:support@apycom.com","img/mail.gif"]
];

apy_init();
