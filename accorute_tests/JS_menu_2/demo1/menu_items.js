// items structure
// each item is the array of one or more properties:
// [text, link, settings, subitems ...]
// use the builder to export errors free structure if you experience problems with the syntax

var MENU_ITEMS = [
    ['Menu Compatibility', null, null,
     ['Supported Browsers', null, null,
      ['Win32 Browsers', null, null, 
       ['Internet Explorer 5+'],
       ['Netscape 6.0+'],
       ['Mozilla 0.9+'],
       ['AOL 5+'],
       ['Opera 5+'],
       ['Safari 3+'] // there must be no comma after the last element
      ],
      ['Mac OS Browsers', null, null,
       ['Internet Explorer 5+'],
       ['Netscape 6.0+'],
       ['Mozilla 0.9+'],
       ['AOL 5+'],
       ['Safari 1.0+']
      ],
      ['KDE (Linux, FreeBSD)', null, null,
       ['Netscape 6.0+'],
       ['Mozilla 0.9+']
      ]
     ],
     // this is how custom javascript code can be called from the item
     // note how apostrophes are escaped inside the string, i.e. 'Don't' must be 'Don\'t'
     ['Unsupported Browsers', 'javascript:alert(\'hello world\')', null,
      ['Internet Explorer 4.x'],
      ['Netscape 4.x']
     ],
     ['Report test results', 'http://www.softcomplex.com/support.html']
    ],
    ['Docs & Info', null, null,
     ['Product Page', 'http://www.softcomplex.com/products/tigra_menu/', {'tw':'_blank'}],
     ['Documentation', 'http://www.softcomplex.com/products/tigra_menu/docs/'],
     ['Forums', 'http://www.softcomplex.com/forum/forumdisplay.php?fid=29'],
     ['TM Comparison Table', 'http://www.softcomplex.com/products/tigra_menu/docs/compare_menus.html']
    ],
    ['Contact', 'http://www.softcomplex.com/support.html'],
    ['Site', '1.html', null,
     ['2', '2.html'],
     ['3', '3.html']
    ]
    
];

