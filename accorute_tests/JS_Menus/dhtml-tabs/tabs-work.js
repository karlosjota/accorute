//******************************************************************************
// ------ Apycom.com DHTML Tabs Data -------------------------------------------
//******************************************************************************
var bblankImage      = "img/blank.gif";
var bmenuWidth       = 0;
var bmenuHeight      = 0;
var bmenuOrientation = 0;

var bmenuBackColor   = "";
var bmenuBorderWidth = 0;
var bmenuBorderColor = "";
var bmenuBorderStyle = "";
var bmenuBackImage   = "";

var bbeforeItemSpace = 5;
var bafterItemSpace  = 5;

var bbeforeItemImage = ["img/style02_n_left.gif","img/style02_n_left.gif","img/style02_s_left.gif"];
//var bbeforeItemImage = ["",""]
var bbeforeItemImageW = 7;
var bbeforeItemImageH = 14;

var bafterItemImage  = ["img/style02_n_right.gif","img/style02_n_right.gif","img/style02_s_right.gif"];
//var bafterItemImage = ["",""]
var bafterItemImageW = 7;
var bafterItemImageH = 14;

var babsolute = 0;
var bleft     = 120;
var btop      = 120;

var bfloatable       = 1;
var bfloatIterations = 6;

var bfontStyle       = ["normal 10px Tahoma","",""];
var bfontColor       = ["#505050","","#505050"];
var bfontDecoration  = ["","",""];

var bitemBorderWidth = 0;
var bitemBorderColor = ["","", ""];
var bitemBorderStyle = ["","",""];

var bitemBackColor = ["#F1F1F1","#F1F1F1","#B2B2B2"];
var bitemBackImage = ["img/style02_n_back.gif","img/style02_n_back.gif","img/style02_s_back.gif"];
var bitemAlign     = "center valign=bottom";
var bitemCursor    = "default";

var bitemSpacing = 0;
var bitemPadding = 0;
var browSpace    = 0;

var biconAlign  = "left";
var biconWidth  = 16;
var biconHeight = 16;

var bseparatorWidth = 7;
var bselectedItem   = 0;

var btransition    = 12;
var btransDuration = 300;

var bstyles =
[
  ["biconWidth=50","biconHeight=20","bitemBackImage=img/tab01_back_n2.gif,img/tab01_back_o2.gif,img/tab01_back_s.gif","bbeforeItemImage=img/tab01_before_n2.gif,img/tab01_before_o2.gif,img/tab01_before_s.gif","bafterItemImage=img/tab01_after_n2.gif,img/tab01_after_o2.gif,img/tab01_after_s.gif"],
];

var bmenuItems =
[
  ["Login",    "contentLogin"],
  ["Register", "contentRegister"],
];

apy_tabsInit();
