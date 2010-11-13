var tpressedFontColor = "#AA0000";
var tpathPrefix_img = "img/index.html";

var tlevelDX = 20;
var ttoggleMode = 1;

var texpanded = 0;
var tcloseExpanded   = 1;
var tcloseExpandedXP = 1;

var tblankImage      = "img/blank.gif";
var tmenuWidth       = 230;
var tmenuHeight      = 0;

var tabsolute        = 1;
var tleft            = 20;
var ttop             = 80;

var tfloatable       = 1;
var tfloatIterations = 10;

var tmoveable        = 0;
var tmoveImage       = "img/movepic.gif";
var tmoveImageHeight = 12;

var tfontStyle       = "normal 8pt Tahoma";
var tfontColor       = ["#3F3D3D","#7E7C7C"];
var tfontDecoration  = ["none","underline"];

var titemBackColor   = ["#F6F6EC","#F6F6EC"];
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
var tXPTitleBackColor    = "#94A664";
var tXPTitleLeft    = "../index.html";
var tXPTitleLeftWidth = 4;
var tXPExpandBtn    = ["xpexpand1_o.gif","../index.html","../index.html","xpcollapse1_o.gif"];
var tXPTitleBackImg = "../index.html";

var tXPBtnWidth  = 25;
var tXPBtnHeight = 23;

var tXPIconWidth  = 31;
var tXPIconHeight = 32;

var tXPFilter=1;

var tXPBorderWidth = 1;
var tXPBorderColor = '#FFFFFF';


var tstyles =
[
    ["tfontStyle=bold 8pt Tahoma","tfontColor=#FFFFFF,#E0E7B8", "tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","tfontColor=#56662D,#72921D", "tfontDecoration=none,none"],
    ["tfontDecoration=none,none"],
    ["tfontStyle=bold 8pt Tahoma","tfontColor=#444444,#5555FF"],
];

var tXPStyles =
[
    ["tXPTitleBackColor=#E2E9BC", "tXPExpandBtn=xpexpand3_o.gif,xpexpand4_o.gif,xpcollapse3_o.gif,xpcollapse4_o.gif", "tXPTitleBackImg=xptitle2_o.gif"]
];

var tmenuItems =
[
    ["+DHTML Tree Menu: XP Style", "", "../index.html","","", "XP Title Tip",,"0"],
    ["|Home", "testlink.htm", "../index.html", "../index.html", "", "Home Page Tip"],
    ["|Product Info", "", "../index.html", "../index.html", "", "Product Info Tip"],
        ["||What's New", "testlink.htm", "iconarro.gif"],
        ["||Features", "testlink.htm", "iconarro.gif"],
        ["||Installation", "testlink.htm", "iconarro.gif"],
        ["||Functions", "testlink.htm", "iconarro.gif"],
        ["||Supported Browsers", "testlink.htm", "iconarro.gif"],
    ["|+Samples", "", "../index.html", "../index.html", "", "Samples Tip"],
        ["||Sample 1", "testlink.htm", "iconarro.gif"],
        ["||Sample 2", "testlink.htm", "iconarro.gif"],
        ["||Sample 3", "testlink.htm", "iconarro.gif"],
        ["||Sample 4", "testlink.htm", "iconarro.gif"],
        ["||Sample 5", "testlink.htm", "iconarro.gif"],
        ["||Sample 6", "testlink.htm", "iconarro.gif"],
        ["||More Samples", "", "../index.html", "icon3_oo.gif"],
            ["|||New Sample 1", "testlink.htm", "iconarro.gif"],
            ["|||New Sample 2", "testlink.htm", "iconarro.gif"],
            ["|||New Sample 3", "testlink.htm", "iconarro.gif"],
            ["|||New Sample 4", "testlink.htm", "iconarro.gif"],
            ["|||New Sample 5", "testlink.htm", "iconarro.gif"],
    ["|Purchase", "testlink.htm", "../index.html", "../index.html", "", "Purchase Tip"],
    ["|Support", "", "../index.html", "../index.html", "", "Support Tip"],
        ["||Write Us", "mailto:dhtml@dhtml-menu.com", "iconarro.gif"],

    ["Samples Gallery", "", "","","", "XP Title Tip",,"1","0"],
        ["|Samples Block 1", "", "../index.html", "icon3_oo.gif"],
            ["||New Sample 1", "testlink.htm", "iconarro.gif"],
            ["||New Sample 2", "testlink.htm", "iconarro.gif"],
            ["||New Sample 3", "testlink.htm", "iconarro.gif"],
            ["||New Sample 4", "testlink.htm", "iconarro.gif"],
            ["||New Sample 5", "testlink.htm", "iconarro.gif"],
        ["|Samples Block 2", "", "../index.html", "icon3_oo.gif"],
            ["||New Sample 1", "testlink.htm", "iconarro.gif"],
            ["||New Sample 2", "testlink.htm", "iconarro.gif"],
            ["||New Sample 3", "testlink.htm", "iconarro.gif"],
            ["||New Sample 4", "testlink.htm", "iconarro.gif"],
            ["||New Sample 5", "testlink.htm", "iconarro.gif"],
        ["|Samples Block 3", "", "../index.html", "icon3_oo.gif"],
            ["||New Sample 1", "testlink.htm", "iconarro.gif"],
            ["||New Sample 2", "testlink.htm", "iconarro.gif"],
            ["||New Sample 3", "testlink.htm", "iconarro.gif"],
            ["||New Sample 4", "testlink.htm", "iconarro.gif"],
            ["||New Sample 5", "testlink.htm", "iconarro.gif"],

    ["Samples Gallery 2", "", "","","", "XP Title Tip",,"1","0"],
        ["|Samples Block 1", "", "../index.html", "icon3_oo.gif"],
            ["||New Sample 1", "testlink.htm", "iconarro.gif"],
            ["||New Sample 2", "testlink.htm", "iconarro.gif"],
            ["||New Sample 3", "testlink.htm", "iconarro.gif"],
            ["||New Sample 4", "testlink.htm", "iconarro.gif"],
            ["||New Sample 5", "testlink.htm", "iconarro.gif"],
        ["|Samples Block 2", "", "../index.html", "icon3_oo.gif"],
            ["||New Sample 1", "testlink.htm", "iconarro.gif"],
            ["||New Sample 2", "testlink.htm", "iconarro.gif"],
            ["||New Sample 3", "testlink.htm", "iconarro.gif"],
            ["||New Sample 4", "testlink.htm", "iconarro.gif"],
            ["||New Sample 5", "testlink.htm", "iconarro.gif"],
        ["|Samples Block 3", "", "../index.html", "icon3_oo.gif"],
            ["||New Sample 1", "testlink.htm", "iconarro.gif"],
            ["||New Sample 2", "testlink.htm", "iconarro.gif"],
            ["||New Sample 3", "testlink.htm", "iconarro.gif"],
            ["||New Sample 4", "testlink.htm", "iconarro.gif"],
            ["||New Sample 5", "testlink.htm", "iconarro.gif"],

];



apy_tmenuInit();
