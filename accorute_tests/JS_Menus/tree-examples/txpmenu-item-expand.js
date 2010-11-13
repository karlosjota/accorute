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

var titemBackColor   = ["#F5DCDC","#F5DCDC"];
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
var tXPTitleBackColor    = "#C94343";
var tXPTitleLeft    = "../index.html";
var tXPTitleLeftWidth = 4;
var tXPExpandBtn    = ["xpexpand1_red.gif","../index.html","../index.html","xpcollapse1_red.gif"];
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
    ["tfontStyle=bold 8pt Tahoma","titemBackColor=#265BCC,#265BCC","tfontColor=#FFFFFF,#FFBEBE", "tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","titemBackColor=#265BCC,#265BCC","tfontColor=#801E1E,#D47170", "tfontDecoration=none,none"],
    ["tfontStyle=bold 11px Tahoma","tfontColor=#0000FF,#5522FF"],
    ["titemBackColor=#CCBBBB,#FFFFFF", "tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","titemBackColor=#265BCC,#265BCC","tfontColor=#FFFFFF,#428EFF", "tfontDecoration=none,none"],
    ["tfontStyle=bold 12px Arial","tfontColor=#215DC6,#428EFF"],
];


var tXPStyles =
[
    ["tXPTitleBackColor=#F4D9D9", "tXPExpandBtn=xpexpand2_red.gif,xpexpand2_red.gif,xpcollapse2_red.gif,xpcollapse2_red.gif", "tXPTitleBackImg=xptitle2_red.gif"],
    ["tXPTitleBackColor=#265BCC", "tXPExpandBtn=xpexpand1.gif,xpexpand2.gif,xpcollapse1.gif,xpcollapse2.gif", "tXPTitleBackImg=xptitle.gif"]
];

var tmenuItems =
[
    ["+DHTML Tree Menu: XP Style", "", "../index.html","","", "XP Title Tip",,"0"],
    ["|Home", "testlink.htm", "../index.html", "../index.html", "", "Home Page Tip"],
    ["|+Product Info", "", "../index.html", "../index.html", "", "Product Info Tip"],
        ["||What's New", "testlink.htm", "../index.html", , , , , "2"],
        ["||Features", "testlink.htm", "../index.html", , , , , "2"],
        ["||Installation", "testlink.htm", "../index.html", , , , , "2"],
        ["||Functions", "testlink.htm", "../index.html", , , , , "2"],
        ["||Supported Browsers", "testlink.htm", "../index.html", , , , , "2"],
    ["|Samples", "", "../index.html", "../index.html", "", "Samples Tip"],
        ["||Sample 1", "testlink.htm", "iconarrred.gif"],
        ["||Sample 2", "testlink.htm", "iconarrred.gif"],
        ["||Sample 3", "testlink.htm", "iconarrred.gif"],
        ["||Sample 4", "testlink.htm", "iconarrred.gif"],
        ["||Sample 5", "testlink.htm", "iconarrred.gif"],
        ["||Sample 6", "testlink.htm", "iconarrred.gif"],
        ["||More Samples", "", "../index.html", "icon3_ro.gif"],
            ["|||New Sample 1", "testlink.htm", "iconarrred.gif"],
            ["|||New Sample 2", "testlink.htm", "iconarrred.gif"],
            ["|||New Sample 3", "testlink.htm", "iconarrred.gif"],
            ["|||New Sample 4", "testlink.htm", "iconarrred.gif"],
            ["|||New Sample 5", "testlink.htm", "iconarrred.gif"],
    ["|Purchase", "testlink.htm", "../index.html", "../index.html", "", "Purchase Tip"],
    ["|Support", "", "../index.html", "../index.html", "", "Support Tip"],
        ["||Write Us", "mailto:dhtml@dhtml-menu.com", "iconarrred.gif"],

    ["+Samples Gallery", "", "","","", "XP Title Tip",,"1","0"],
        ["|Samples Block 1", "", "../index.html", "../index.html", , , , "5"],
            ["||New Sample 1", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 2", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 3", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 4", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 5", "testlink.htm", "iconarrred.gif"],
        ["|Samples Block 2", "", "../index.html", "../index.html", , , , "5"],
            ["||New Sample 1", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 2", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 3", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 4", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 5", "testlink.htm", "iconarrred.gif"],
        ["|Samples Block 3", "", "../index.html", "../index.html", , , , "5"],
            ["||New Sample 1", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 2", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 3", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 4", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 5", "testlink.htm", "iconarrred.gif"],
            
    ["Samples Gallery 2", "", "../index.html","","", "XP Title Tip",,"4","1"],
        ["|Samples Block 1", "", "../index.html", "icon3_ro.gif"],
            ["||New Sample 1", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 2", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 3", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 4", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 5", "testlink.htm", "iconarrred.gif"],
        ["|Samples Block 2", "", "../index.html", "icon3_ro.gif"],
            ["||New Sample 1", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 2", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 3", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 4", "testlink.htm", "iconarrred.gif"],
            ["||New Sample 5", "testlink.htm", "iconarrred.gif"],
];



apy_tmenuInit();
