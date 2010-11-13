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
var tfontColor       = ["#3F3D3D","#7E7C7C"];
var tfontDecoration  = ["none","underline"];

var titemBackColor   = ["#F0F1F5","#F0F1F5"];
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
var tXPTitleBackColor    = "#57C34A";
var tXPTitleLeft    = "../index.html";
var tXPTitleLeftWidth = 4;
var tXPExpandBtn    = ["xpexpand1_green.gif","../index.html","../index.html","xpcollapse1_green.gif"];
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
    ["tfontStyle=bold 8pt Tahoma","tfontColor=#FFFFFF,#D2FCD5", "tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","tfontColor=#3F3D3D,#659669", "tfontDecoration=none,none"],
    ["tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","tfontColor=#444444,#5555FF"],
];

var tXPStyles =
[
    ["tXPTitleBackColor=#DEF3DB", "tXPExpandBtn=xpexpand2_green.gif,xpexpand2_green.gif,xpcollapse2_green.gif,xpcollapse2_green.gif", "tXPTitleBackImg=xptitle2_green.gif"]
];

var tmenuItems =
[
    ["+DHTML Tree Menu: XP Style", "", "../index.html","","", "XP Title Tip",,"0"],
    ["|Home", "testlink.htm", "../index.html", "../index.html", "", "Home Page Tip"],
    ["|Product Info", "", "../index.html", "../index.html", "", "Product Info Tip"],
        ["||What's New", "testlink.htm", "iconarrs.gif"],
        ["||Features", "testlink.htm", "iconarrs.gif"],
        ["||Installation", "testlink.htm", "iconarrs.gif"],
        ["||Functions", "testlink.htm", "iconarrs.gif"],
        ["||Supported Browsers", "testlink.htm", "iconarrs.gif"],
    ["|+Samples", "", "../index.html", "../index.html", "", "Samples Tip"],
        ["||Sample 1", "testlink.htm", "iconarrs.gif"],
        ["||#Sample 2", "testlink.htm", "iconarrs.gif"],
        ["||Sample 3", "testlink.htm", "iconarrs.gif"],
        ["||Sample 4", "testlink.htm", "iconarrs.gif"],
        ["||#Sample 5", "testlink.htm", "iconarrs.gif"],
        ["||Sample 6", "testlink.htm", "../index.html", "", "", "", "_"],
        ["||+More Samples", "", "../index.html", "icon3_so.gif"],
            ["|||New Sample 1", "testlink.htm", "iconarrs.gif"],
            ["|||New Sample 2", "testlink.htm", "../index.html", "", "", "", "_"],
            ["|||New Sample 3", "testlink.htm", "iconarrs.gif"],
            ["|||New Sample 4", "testlink.htm", "iconarrs.gif"],
            ["|||New Sample 5", "testlink.htm", "../index.html", "", "", "", "_"],
    ["|Purchase", "testlink.htm", "../index.html", "../index.html", "", "Purchase Tip"],
    ["|+Support", "", "../index.html", "../index.html", "", "Support Tip"],
        ["||Write Us", "mailto:dhtml@dhtml-menu.com", "iconarrs.gif"],



    ["Samples Gallery", "", "","","", "XP Title Tip",,"1","0"],
        ["|Samples Block 1", "", "../index.html", "icon3_so.gif"],
            ["||New Sample 1", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 2", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 3", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 4", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 5", "testlink.htm", "iconarrs.gif"],
        ["|Samples Block 2", "", "../index.html", "icon3_so.gif"],
            ["||New Sample 1", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 2", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 3", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 4", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 5", "testlink.htm", "iconarrs.gif"],
        ["|Samples Block 3", "", "../index.html", "icon3_so.gif"],
            ["||New Sample 1", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 2", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 3", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 4", "testlink.htm", "iconarrs.gif"],
            ["||New Sample 5", "testlink.htm", "iconarrs.gif"],
];



apy_tmenuInit();
