var cssStyle=1;
var cssClass="topMenu";
var topDY=-4;
var DX=0;
var blankImage="img/blank.gif";
var isHorizontal=1;
var menuWidth="100%";
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
var transparency=100;
var transition=24;
var transDuration=300;

var shadowColor="#AAAAAA";
var shadowLen=0;
var shadowTop=1;

var arrowImageMain=[];
var arrowImageSub=["img/arrow_r.gif","img/arrow_r.gif"];
var arrowWidth=7;
var arrowHeight=7;

var separatorImage="img/separ1.gif";
var separatorWidth="100%";
var separatorHeight="5";
var separatorAlignment="center";
var separatorVImage="img/separv1.gif";
var separatorVWidth="5";
var separatorVHeight=34;

var statusString="text";
var pressedItem=0;

var itemStyles = [
   ["CSS=topItemNormal,topItemOver", "itemWidth=160"],
   ["CSS=itemNormal,itemOver"],
   ["CSS=itemNormal,itemOver", "itemWidth=250"],
];

var menuStyles = [
   ["CSS=submenu"],
];

var menuItems = [
   ["<img src='img/blank.gif' width=10 height=29>",,,,,,],
   ["Home","testlink00002.html","img/new1-05.gif","img/new1-05.gif","Home Tip","_blank","0",],
   ["Our Products","testlink00003.html","img/new1-08.gif","img/new1-08.gif","Our Products Tip",,"0",],
     ["|Product1","testlink00004.html","img/new1-08.gif","img/new1-08.gif","Product Tip",,"1","0"],
     ["|Product2","testlink00005.html","img/new1-08.gif","img/new1-08.gif","Product Tip",,"1",],

       ["||Documentation","testlink00006.html","img/new1-08.gif","img/new1-08.gif","Product Tip",,"1","0"],
       ["||Support","testlink00007.html","img/new1-08.gif","img/new1-08.gif","Product Tip",,"1",],
       
     ["|Product3","testlink00008.html","img/new1-08.gif","img/new1-08.gif","Product Tip",,"1"],
     ["|Product4","testlink00009.html","img/new1-08.gif","img/new1-08.gif","Product Tip",,"1",],
     ["|Product5","testlink00010.html","img/new1-08.gif","img/new1-08.gif","Product Tip",,"1",],
   ["More Info","testlink00011.html","img/new4-0985.gif","","",,"0",],
   ["Javascript calls","","img/new4-038.gif","","",,"0",],
     ["|Click to call message box","javascript:alert('Hello world!')","img/b09.gif","img/b092.gif",,,"2","0"],
     ["|Click to call confirmation dialog","javascript:confirm('Do you want to confirm?')","img/b09.gif","img/b092.gif",,,"2"],
     ["|Open 'Testlink' page in 680x600 window","javascript:open('testlink00012.html','_blank','scrollbars,width=680,height=600')","img/b09.gif","img/b092.gif",,,"2"],
   ["Contact Us","mailto:support@apycom.com","img/new4-098.gif","img/new4-098.gif","Contact Us Tip",,"0",],
];

apy_init();
