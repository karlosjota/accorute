//******************************************************************************
// ------ Apycom.com DHTML Tabs Data -------------------------------------------
//******************************************************************************
var bblankImage      = "img/blank.gif";
var bmenuWidth       = 250;
var bmenuHeight      = 0;
var bmenuOrientation = 0;

var bmenuBackColor   = "";
var bmenuBorderWidth = 0;
var bmenuBorderColor = "";
var bmenuBorderStyle = "";
var bmenuBackImage   = "";

var bbeforeItemSpace = 0;
var bafterItemSpace  = 0;

var bbeforeItemImage = ["img/tab01_before_n.gif","img/tab01_before_o.gif","img/tab01_before_s.gif"];
//var bbeforeItemImage = ["",""]
var bbeforeItemImageW = 5;
var bbeforeItemImageH = 18;

var bafterItemImage  = ["img/tab01_after_n.gif","img/tab01_after_o.gif","img/tab01_after_s.gif"];
//var bafterItemImage = ["",""]
var bafterItemImageW = 5;
var bafterItemImageH = 18;

var babsolute = 0;
var bleft     = 120;
var btop      = 120;

var bfloatable       = 1;
var bfloatIterations = 6;

var bfontStyle       = ["normal 8pt Tahoma","",""];
var bfontColor       = ["#000000","","#000000"];
var bfontDecoration  = ["","",""];

var bitemBorderWidth = 0;
var bitemBorderColor = ["","", ""];
var bitemBorderStyle = ["","",""];

var bitemBackColor = ["#ffffff","#FFEEB9","#F9BC00"];
var bitemBackImage = ["img/tab01_back_n.gif","img/tab01_back_o.gif","img/tab01_back_s.gif"];
var bitemAlign     = "center";
var bitemCursor    = "default";

var bitemSpacing = 0;
var bitemPadding = 0;
var browSpace    = 0;

var biconAlign  = "left";
var biconWidth  = 16;
var biconHeight = 16;

var bseparatorWidth = 7;
var bselectedItem   = 2;

var bstyles =
[
  ["biconWidth=50","biconHeight=20","bitemBackImage=img/tab01_back_n2.gif,img/tab01_back_o2.gif,img/tab01_back_s.gif","bbeforeItemImage=img/tab01_before_n2.gif,img/tab01_before_o2.gif,img/tab01_before_s.gif","bafterItemImage=img/tab01_after_n2.gif,img/tab01_after_o2.gif,img/tab01_after_s.gif"],
];

var bmenuItems =
[
  ["-"],
  ["Subitem 1"],
  ["Subitem 2"],
  ["-"],
  ["$Subitem 3",,,,,,"0"],
  ["Subitem 4" ,,,,,, "0"],
  ["Subitem 5" ,,,,,, "0"],
];

apy_tabsInit();
