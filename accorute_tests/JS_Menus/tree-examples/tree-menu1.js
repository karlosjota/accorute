// Lines
var tpoints = 1;
var tpointsImage = "../index.html";
var tpointsVImage = "../index.html";
var tpointsCImage = "../index.html";


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

var tfloatable       = 1;
var tfloatIterations = 10;

var tmoveable        = 0;
var tmoveImage       = "";
var tmoveImageHeight = 12;

var tfontStyle       = "normal 8pt Tahoma";
var tfontColor       = ["#3F3D3D","#7E7C7C"];
var tfontDecoration  = ["none","underline"];

var titemBackColor   = ["",""];
var titemAlign       = "left";
var titemBackImage   = ["",""];
var titemCursor      = "pointer";
var titemHeight      = 26;
var titemTarget      = "_blank";

var ticonWidth       = 21;
var ticonHeight      = 15;
var ticonAlign       = "left";

var tmenuBackImage   = "../index.html";
var tmenuBackColor   = "#E0E0E0";
var tmenuBorderColor = "#FFFFFF";
var tmenuBorderStyle = "solid";
var tmenuBorderWidth = 2;

var texpandBtn       =["expandbtn2.gif","../index.html","collapsebtn2.gif"];
var texpandBtnW      = 9;
var texpandBtnH      = 9;
var texpandBtnAlign  = "left"

// XP-Style Parameters
var tXPStyle = 0;
var tXPIterations = 10;                  // expand/collapse speed
var tXPTitleTopBackColor = "";
var tXPTitleBackColor    = "#AFB1C3";
var tXPTitleLeft    = "../index.html";
var tXPTitleLeftWidth = 4;
var tXPExpandBtn    = ["xpexpand1_s.gif","../index.html","../index.html","xpcollapse1_s.gif"];
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
   ["tfontDecoration=none,none"]
];

var tmenuItems =
[
    ["Home", "testlink.htm", "../index.html", "../index.html", "", "Home Page Tip"],

    ["<img src='img/sep.gif' width=113 height=1>"],

    ["+Product Info", "", "../index.html", "../index.html", "", "Product Info Tip"],
        ["|What's New", "testlink.htm", ""],
        ["|Features", "testlink.htm", ""],
        ["|Installation", "testlink.htm", ""],
        ["|Functions", "testlink.htm", ""],
        ["|Supported Browsers", "testlink.htm", ""],
    ["Samples", "", "../index.html", "../index.html", "", "Samples Tip"],
        ["|Sample 1", "testlink.htm", ""],
        ["|Sample 2", "testlink.htm", ""],
        ["|Sample 3", "testlink.htm", ""],
        ["|Sample 4", "testlink.htm", ""],
        ["|Sample 5", "testlink.htm", ""],
        ["|Sample 6", "testlink.htm", ""],
        ["|More Samples", "", "../index.html", "icon3_to.gif"],
            ["||New Sample 1", "testlink.htm", ""],
            ["||New Sample 2", "testlink.htm", ""],
            ["||New Sample 3", "testlink.htm", ""],
            ["||New Sample 4", "testlink.htm", ""],
            ["||New Sample 5", "testlink.htm", ""],
    ["Purchase", "testlink.htm", "../index.html", "../index.html", "", "Purchase Tip"],
    ["+Support", "", "../index.html", "../index.html", "", "Support Tip"],
        ["|Index &nbsp;<select style='width:120px;height:17px;font:normal 10px Tahoma,Arial;'><option>Section 1<option>Section 2<option>Section 3</select>", "", "", "", "", "", "", "0"],
        ["|Search <input type=text style='width:80px;height:17px;font:normal 10px Tahoma,Arial;'>&nbsp;&nbsp;<input type=button value='Go' style='width:30px;height:17px;font:normal 10px Tahoma,Arial;'>", "", "", "", "", "", "", "0"],
        ["|Write Us", "mailto:dhtml@dhtml-menu.com", ""],

    ["<img src='img/sep.gif' width=113 height=1>"],

    ["Samples Gallery", "", "../index.html", "../index.html", "", "Samples Gallery Tip"],
        ["|+Samples Block 1", "", "../index.html", "icon3_to.gif"],
            ["||New Sample 1", "testlink.htm", ""],
            ["||New Sample 2", "testlink.htm", ""],
            ["||New Sample 3", "testlink.htm", ""],
            ["||New Sample 4", "testlink.htm", ""],
            ["||New Sample 5", "testlink.htm", ""],
        ["|Samples Block 2", "", "../index.html", "icon3_to.gif"],
            ["||New Sample 1", "testlink.htm", ""],
            ["||New Sample 2", "testlink.htm", ""],
            ["||New Sample 3", "testlink.htm", ""],
            ["||New Sample 4", "testlink.htm", ""],
            ["||New Sample 5", "testlink.htm", ""],
        ["|Samples Block 3", "", "../index.html", "icon3_to.gif"],
            ["||New Sample 1", "testlink.htm", ""],
            ["||New Sample 2", "testlink.htm", ""],
            ["||New Sample 3", "testlink.htm", ""],
            ["||New Sample 4", "testlink.htm", ""],
            ["||New Sample 5", "testlink.htm", ""],
];



apy_tmenuInit();
