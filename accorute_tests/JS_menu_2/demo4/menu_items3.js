// items structure
// each item is the array of one or more properties:
// [text, link, settings, subitems ...]
// this sample structure demonstrates the use of HTML inside the menu items as well as wrapper functions

var MENU_ITEMS3 = [
	['<span style="color:yellow;font-weight:bold">Simple Inline HTML</span>&nbsp;<a href="http://www.softcomplex.com/news.xml"><img src="img/rss.gif" alt="Subscribe to News Feed" border="0" align="baseline" /></a>'],
	['Tables, Forms', null, null,
		[pupup('<form><table class="tableForm"><tr><th colspan="2">Login Form</th></tr><tr><td>Username:</td><td><input type="Text" /></td></tr><tr><td>Password:</td><td><input type="Password" /></td></tr><tr><th colspan="2" align="right"><input type="Submit" value="Login" /></th></tr></table></form>')]
	],
	['Even iFrames', null, null,
		[pupup('<iframe src="http://www.softcomplex.com" style="width:240px; height:150px;"></iframe>'), 'http://www.softcomplex.com'],
	]
];


//	This simple function is a wrapper. It puts html around provided text.
//	You can write your own wrappers for higher efficiency and better code maintanability
function pupup (text) {
	return '<table border=0 cellpadding=0 cellspacing=0><tr><td><img border=0 src="img/01.gif" width=9 height=11></td><td background="img/02.gif"><img border=0 src="img/pixel.gif" width=261 height=11></td><td colspan=2 rowspan=2 valign="top"><img border=0 src="img/03.gif" width=89 height=167></td><td><img border=0 src="img/pixel.gif" width=1 height=11></td></tr><tr><td rowspan=2 background="img/04.gif"><img border=0 src="img/pixel.gif" width=9 height=100></td><td rowspan="2" bgcolor="#339933" valign="top" style="text-align:center;padding:10px;">'
		+ text + '</td><td><img border=0 src="img/pixel.gif" width=1 height=156></td></tr><tr><td background="img/05.gif"><img border=0 src="img/pixel.gif" width=13 height=20></td><td rowspan="2"><img border=0 src="img/pixel.gif" width=76 height=20></td><td><img border=0 src="img/pixel.gif" width=1 height=20></td></tr><tr><td><img border=0 src="img/06.gif" width=9 height=17></td><td background="img/07.gif"><img border=0 src="img/pixel.gif" width=1 height=17></td><td><img border=0 src="img/08.gif" width=13 height=17></td><td><img border=0 src="img/pixel.gif" width=1 height=17></td></tr></table>';
}
