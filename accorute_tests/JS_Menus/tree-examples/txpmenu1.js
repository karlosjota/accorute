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
var ttop             = 80

var tfloatable       = 0;
var tfloatIterations = 10;

var tmoveable        = 0;
var tmoveImage       = "img/movepic.gif";
var tmoveImageHeight = 12;

var tfontStyle       = "normal 8pt Tahoma";
var tfontColor       = ["#215DC6","#428EFF"];
var tfontDecoration  = ["none","underline"];

var titemBackColor   = ["#D6DFF7","#D6DFF7"];
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
var tXPTitleBackColor    = "#265BCC";
var tXPExpandBtn    = ["xpexpand1.gif","../index.html","../index.html","xpcollapse2.gif"];
var tXPTitleBackImg = "../index.html";

var tXPTitleLeft      = "../index.html";
var tXPTitleLeftWidth = 4;

var tXPBtnWidth  = 25;
var tXPBtnHeight = 25;

var tXPIconWidth  = 31;
var tXPIconHeight = 32;

var tXPFilter=1;

var tXPBorderWidth = 1;
var tXPBorderColor = '#FFFFFF';



var tstyles =
[
    ["tfontStyle=bold 8pt Tahoma","titemBackColor=#265BCC,#265BCC","tfontColor=#FFFFFF,#428EFF", "tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","titemBackColor=#265BCC,#265BCC","tfontColor=#215DC6,#428EFF", "tfontDecoration=none,none"],
    ["tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","tfontColor=#444444,#5555FF"],
];

var tXPStyles =
[
    ["tXPTitleBackColor=#D0DAF8", "tXPExpandBtn=xpexpand3.gif,xpexpand4.gif,xpcollapse3.gif,xpcollapse4.gif", "tXPTitleBackImg=xptitle2.gif"]
];

var tmenuItems =
[
    ["+DHTML Tree Menu: XP Style", "", "../index.html","","", "XP Title Tip",,"0"],
    ["|Home", "testlink.htm", "../index.html", "../index.html", "", "Home Page Tip"],
    ["|+Product Info", "", "../index.html", "../index.html", "", "Product Info Tip"],
        ["||What's New", "testlink.htm", "iconarr.gif"],
        ["||Features", "testlink.htm", "iconarr.gif"],
        ["||Installation", "testlink.htm", "iconarr.gif"],
        ["||Functions", "testlink.htm", "iconarr.gif"],
        ["||Supported Browsers", "testlink.htm", "iconarr.gif"],
    ["|Samples", "", "../index.html", "../index.html", "", "Samples Tip"],
        ["||Sample 1", "testlink.htm", "iconarr.gif"],
        ["||Sample 2", "testlink.htm", "iconarr.gif"],
        ["||Sample 3", "testlink.htm", "iconarr.gif"],
        ["||Sample 4", "testlink.htm", "iconarr.gif"],
        ["||Sample 5", "testlink.htm", "iconarr.gif"],
        ["||Sample 6", "testlink.htm", "iconarr.gif"],
        ["||More Samples", "", "../index.html", "icon3o.gif"],
            ["|||New Sample 1", "testlink.htm", "iconarr.gif"],
            ["|||New Sample 2", "testlink.htm", "iconarr.gif"],
            ["|||New Sample 3", "testlink.htm", "iconarr.gif"],
            ["|||New Sample 4", "testlink.htm", "iconarr.gif"],
            ["|||New Sample 5", "testlink.htm", "iconarr.gif"],
    ["|Purchase", "testlink.htm", "../index.html", "../index.html", "", "Purchase Tip"],
    ["|Support", "", "../index.html", "../index.html", "", "Support Tip"],
        ["||Write Us", "mailto:dhtml@dhtml-menu.com", "iconarr.gif"],

    ["Samples Gallery", "", "","","", "XP Title Tip",,"1","0"],
        ["|Samples Block 1", "", "../index.html", "icon3o.gif"],
            ["||New Sample 1", "testlink.htm", "iconarr.gif"],
            ["||New Sample 2", "testlink.htm", "iconarr.gif"],
            ["||New Sample 3", "testlink.htm", "iconarr.gif"],
            ["||New Sample 4", "testlink.htm", "iconarr.gif"],
            ["||New Sample 5", "testlink.htm", "iconarr.gif"],
        ["|Samples Block 2", "", "../index.html", "icon3o.gif"],
            ["||New Sample 1", "testlink.htm", "iconarr.gif"],
            ["||New Sample 2", "testlink.htm", "iconarr.gif"],
            ["||New Sample 3", "testlink.htm", "iconarr.gif"],
            ["||New Sample 4", "testlink.htm", "iconarr.gif"],
            ["||New Sample 5", "testlink.htm", "iconarr.gif"],
        ["|Samples Block 3", "", "../index.html", "icon3o.gif"],
            ["||New Sample 1", "testlink.htm", "iconarr.gif"],
            ["||New Sample 2", "testlink.htm", "iconarr.gif"],
            ["||New Sample 3", "testlink.htm", "iconarr.gif"],
            ["||New Sample 4", "testlink.htm", "iconarr.gif"],
            ["||New Sample 5", "testlink.htm", "iconarr.gif"],
];



apy_tmenuInit();
