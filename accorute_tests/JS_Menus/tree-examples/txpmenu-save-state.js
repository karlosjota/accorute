var tsaveState = 1;

var tpressedFontColor = "#AA0000";
var tpathPrefix_img = "img/index.html";

var tlevelDX = 20;
var ttoggleMode = 1;

var texpanded = 0;
var tcloseExpanded   = 0;
var tcloseExpandedXP = 0;

var tblankImage      = "img/blank.gif";
var tmenuWidth       = 230;
var tmenuHeight      = 0;

var tabsolute        = 1;
var tleft            = 20;
var ttop             = 80;

var tfloatable       = 0;
var tfloatIterations = 10;

var tmoveable        = 0;
var tmoveImage       = "img/movepic.gif";
var tmoveImageHeight = 12;

var tfontStyle       = "normal 8pt Tahoma";
var tfontColor       = ["#854E15","#BF772C"];
var tfontDecoration  = ["none","underline"];

var titemBackColor   = ["#F9E8D7","#F9E8D7"];
var titemAlign       = "left";
var titemBackImage   = ["",""];
var titemCursor      = "pointer";
var titemHeight      = 22;
var titemTarget      = "_blank";

var ticonWidth       = 21;
var ticonHeight      = 15;
var ticonAlign       = "left";

var tmenuBackImage   = "";
var tmenuBackColor   = "";
var tmenuBorderColor = "#FFFFFF";
var tmenuBorderStyle = "solid";
var tmenuBorderWidth = 0;

var texpandBtn       =["expandbtn2.gif","../index.html","collapsebtn2.gif"];
var texpandBtnW      = 9;
var texpandBtnH      = 9;
var texpandBtnAlign  = "left"

var tpoints       = 0;
var tpointsImage  = "";
var tpointsVImage = "";
var tpointsCImage = "";

// XP-Style Parameters
var tXPStyle = 1;
var tXPIterations = 10;                  // expand/collapse speed
var tXPTitleTopBackColor = "";
var tXPTitleBackColor    = "#E18A2C";
var tXPTitleLeft    = "../index.html";
var tXPTitleLeftWidth = 4;
var tXPExpandBtn    = ["xpexpand1_orange.gif","../index.html","../index.html","xpcollapse1_orange.gif"];
var tXPTitleBackImg = "../index.html";

var tXPBtnWidth  = 25;
var tXPBtnHeight = 25;

var tXPIconWidth  = 31;
var tXPIconHeight = 32;

var tXPFilter=1;

var tXPBorderWidth = 1;
var tXPBorderColor = '#FFFFFF';



var tstyles =
[
    ["tfontStyle=bold 8pt Tahoma","titemBackColor=#265BCC,#265BCC","tfontColor=#FFFFFF,#FFDFBD", "tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","titemBackColor=#265BCC,#265BCC","tfontColor=#854E15,#AD7235", "tfontDecoration=none,none"],
    ["tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","tfontColor=#444444,#5555FF"],
];

var tXPStyles =
[
    ["tXPTitleBackColor=#F9E7D5", "tXPExpandBtn=xpexpand2_orange.gif,xpexpand2_orange.gif,xpcollapse2_orange.gif,xpcollapse2_orange.gif", "tXPTitleBackImg=xptitle2_orange.gif"]
];

var tmenuItems =
[
    ["+DHTML Tree Menu: XP Style", "", "../index.html","","", "XP Title Tip",,"0"],
    ["|Home", "testlink.htm", "../index.html", "../index.html", "", "Home Page Tip"],
    ["|Product Info", "", "../index.html", "../index.html", "", "Product Info Tip"],
        ["||What's New", "testlink.htm", "iconarror.gif"],
        ["||Features", "testlink.htm", "iconarror.gif"],
        ["||Installation", "testlink.htm", "iconarror.gif"],
        ["||Functions", "testlink.htm", "iconarror.gif"],
        ["||Supported Browsers", "testlink.htm", "iconarror.gif"],
    ["|Samples", "", "../index.html", "../index.html", "", "Samples Tip"],
        ["||Sample 1", "testlink.htm", "iconarror.gif"],
        ["||Sample 2", "testlink.htm", "iconarror.gif"],
        ["||Sample 3", "testlink.htm", "iconarror.gif"],
        ["||Sample 4", "testlink.htm", "iconarror.gif"],
        ["||Sample 5", "testlink.htm", "iconarror.gif"],
        ["||Sample 6", "testlink.htm", "iconarror.gif"],
        ["||More Samples", "", "../index.html", "icon3_oro.gif"],
            ["|||New Sample 1", "testlink.htm", "iconarror.gif"],
            ["|||New Sample 2", "testlink.htm", "iconarror.gif"],
            ["|||New Sample 3", "testlink.htm", "iconarror.gif"],
            ["|||New Sample 4", "testlink.htm", "iconarror.gif"],
            ["|||New Sample 5", "testlink.htm", "iconarror.gif"],
    ["|Purchase", "testlink.htm", "../index.html", "../index.html", "", "Purchase Tip"],
    ["|Support", "", "../index.html", "../index.html", "", "Support Tip"],
        ["||Write Us", "mailto:dhtml@dhtml-menu.com", "iconarror.gif"],

    ["+Samples Gallery", "", "","","", "XP Title Tip",,"1","0"],
        ["|Samples Block 1", "", "../index.html", "icon3_oro.gif"],
            ["||New Sample 1", "testlink.htm", "iconarror.gif"],
            ["||New Sample 2", "testlink.htm", "iconarror.gif"],
            ["||New Sample 3", "testlink.htm", "iconarror.gif"],
            ["||New Sample 4", "testlink.htm", "iconarror.gif"],
            ["||New Sample 5", "testlink.htm", "iconarror.gif"],
        ["|Samples Block 2", "", "../index.html", "icon3_oro.gif"],
            ["||New Sample 1", "testlink.htm", "iconarror.gif"],
            ["||New Sample 2", "testlink.htm", "iconarror.gif"],
            ["||New Sample 3", "testlink.htm", "iconarror.gif"],
            ["||New Sample 4", "testlink.htm", "iconarror.gif"],
            ["||New Sample 5", "testlink.htm", "iconarror.gif"],
        ["|Samples Block 3", "", "../index.html", "icon3_oro.gif"],
            ["||New Sample 1", "testlink.htm", "iconarror.gif"],
            ["||New Sample 2", "testlink.htm", "iconarror.gif"],
            ["||New Sample 3", "testlink.htm", "iconarror.gif"],
            ["||New Sample 4", "testlink.htm", "iconarror.gif"],
            ["||New Sample 5", "testlink.htm", "iconarror.gif"],
];



apy_tmenuInit();
