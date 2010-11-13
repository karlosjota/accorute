//******************************************************************************
// ------ Apycom.com DHTML Tabs Data -------------------------------------------
//******************************************************************************
var bblankImage      = "img/blank.gif";
var bmenuWidth       = 400;
var bmenuHeight      = 0;
var bmenuOrientation = 0;

var bmenuBackColor   = "";
var bmenuBorderWidth = 0;
var bmenuBorderColor = "#FFFFFF";
var bmenuBorderStyle = "dotted";
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
var bfontColor       = ["#000000","#000000","#000000"];
var bfontDecoration  = ["none","none","none"];

var bitemBorderWidth = 0;
var bitemBorderColor = ["#ffffff","#ffffff", "#ffffff"];
var bitemBorderStyle = ["solid","solid","solid"];

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
var bselectedItem   = 5;

var btransition    = 24;
var btransDuration = 400;
var btransOptions  = "";

var bstyles =
[
  ["biconWidth=50","biconHeight=20","bitemBackImage=img/tab01_back_n2.gif,img/tab01_back_o2.gif,img/tab01_back_s.gif","bbeforeItemImage=img/tab01_before_n2.gif,img/tab01_before_o2.gif,img/tab01_before_s.gif","bafterItemImage=img/tab01_after_n2.gif,img/tab01_after_o2.gif,img/tab01_after_s.gif"],
];

var bmenuItems =
[
  ["-"],
  ["What is DHTML Tabs?",   "contentWhatIs",     ,,, "What is DHTML Tabs?"],
  ["Parameters",            "contentParameters", ,,, "DHTML Tabs Parameters Info",],
  ["How to Setup",          "contentSetup",      ,,, "Documentation"],
  ["-"],
  ["$DHTML Tabs Styles",     "contentStyles",    ,,,  "Tabs Styles", "0"],
  ["Purchase",               "contentPurchase",  ,,,  "Purchase DHTML Tabs", "0"],
  ["Apycom Products",        "contentProducts",  ,,,  "Others Apycom Products", "0"],
];

apy_tabsInit();
