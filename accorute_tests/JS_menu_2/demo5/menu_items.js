// items structure
// each item is the array of one or more properties:
// [text, link, settings, subitems ...]
// use the builder to export errors free structure if you experience problems with the syntax

var MENU_ITEMS = [
	['SoftComplex','http://www.softcomplex.com/', {'tw' : 'content'},
		['Services','http://www.softcomplex.com/services.html', {'tw' : 'content'}],
		['Download','http://www.softcomplex.com/download.html', {'tw' : 'content'}],
		['Order','http://www.softcomplex.com/order.html', {'tw' : 'content'}],
		['Support','http://www.softcomplex.com/support.html', {'tw' : 'content'}]
	],
	['Special Targets', null, null,
		['New Window','http://www.javascript-menu.com/', {'tw' : '_blank'}],
		['Parent Window','http://www.javascript-menu.com/', {'tw' : '_parent'}],
		['Same Frame','http://www.javascript-menu.com/', {'tw' : '_self'}]
	],
	['Another Item', null, null,
		['Level 1 Item 0','http://www.softcomplex.com/products/tigra_menu/', {'tw' : 'content'}],
		['Level 1 Item 1','http://www.softcomplex.com/products/tigra_calendar/', {'tw' : 'content'}],
		['Level 1 Item 2','http://www.softcomplex.com/products/tigra_scroller/', {'tw' : 'content'}],
		['Level 1 Item 3','http://www.softcomplex.com/products/tigra_form_validator/', {'tw' : 'content'}]
	],
	['Home','content.html', {'tw' : 'content'}]
];