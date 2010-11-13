//******************************************************************************
// ------ Apycom.com DHTML Tabs Data -------------------------------------------
//******************************************************************************
var bblankImage      = "img/blank.gif";
var bmenuWidth       = 0;
var bmenuHeight      = 22;
var bmenuOrientation = 0;

var bmenuBackColor   = "";
var bmenuBorderWidth = 0;
var bmenuBorderColor = "";
var bmenuBorderStyle = "";
var bmenuBackImage   = "";

var bbeforeItemSpace = 0;
var bafterItemSpace  = 0;

var bbeforeItemImage = ["",""]
var bbeforeItemImageW = 13;
var bbeforeItemImageH = 22;

var bafterItemImage  = [];
//var bafterItemImage = ["",""]
var bafterItemImageW = 13;
var bafterItemImageH = 22;

var babsolute = 0;
var bleft     = 120;
var btop      = 120;

var bfloatable       = 1;
var bfloatIterations = 6;

var bfontStyle       = ["bold 8pt Tahoma"];
var bfontColor       = ["#ffffff","","#ffffff"];
var bfontDecoration  = ["","",""];

var bitemBorderWidth = 0;
var bitemBorderColor = ["","", ""];
var bitemBorderStyle = ["","",""];

var bitemBackColor = ["#24B327","#83D83D","#B79A15"];
var bitemBackImage = ["img/style01_n_back.gif","img/style01_o_back.gif","img/style01_s_back.gif",];
var bitemAlign     = "center";
var bitemCursor    = "default";
var bitemTarget    = "_blank";

var bitemSpacing = 0;
var bitemPadding = 0;
var browSpace    = 0;

var biconAlign  = "left";
var biconWidth  = 16;
var biconHeight = 16;

var bseparatorWidth = 7;
var bselectedItem   = 0;
var bselectedSmItem = -1;

var btransition    = 24;
var btransDuration = 400;

//---------------------
var bsmHeight      = 16;
var bsmBackColor   = "#AA0000";
var bsmItemAlign   = "center";
var bsmBorderColor = "#AA0000";
var bsmBorderStyle = "";
var bsmBorderWidth = 0;
var bsmBorderBottomDraw = 1;
var bsmItemSpacing = 0;
var bsmItemPadding = 0;

// bitemBackImageSpec syntax:
// bitemBackImageSpec=normal-normal,normal-over,normal-selected,over-normal,over-selected,selected-normal,selected-over
var back_nn="img/style01_nn_center.gif,";
var back_no="img/style01_no_center.gif,";
var back_ns="img/style01_ns_center.gif,";
var back_on="img/style01_on_center.gif,";
var back_os="img/style01_os_center.gif,";
var back_sn="img/style01_sn_center.gif,";
var back_so="img/style01_so_center.gif";
var backSpec = back_nn+back_no+back_ns+back_on+back_os+back_sn+back_so;

var bstyles =
[
  ["bitemWidth=22","bitemBackImageSpec="+backSpec],
  ["bbeforeItemImage=img/style01_n_left.gif,img/style01_o_left.gif,img/style01_s_left.gif"],
  ["bafterItemImage=img/style01_n_right.gif,img/style01_o_right.gif,img/style01_s_right.gif"],
];

var bmenuItems =
[
  ["Style Name",,,,,,,"1"],
  ["-",,,,,,,"0"],
  ["Style Description"],
  ["-",,,,,,,"0"],
  ["Style Variations"],
  ["-",,,,,,,"0"],
  ["Empty",,,,,,,"2"],
];

apy_tabsInit();
