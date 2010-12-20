// level scope settins structure
var MENU_TPL3 = [
// root level configuration (level 0)
{
	// item sizes
	'height': 25,
	'width': 150,
	// absolute position of the menu on the page (in pixels)
	// with centered content use Tigra Menu PRO or Tigra Menu GOLD
	'block_top': 100,
	'block_left': 470,
	// offsets between items of the same level (in pixels)
	'top': 24,
	'left': 0,
	// time delay before menu is hidden after cursor left the menu (in milliseconds)
	'hide_delay': 200,
	// submenu expand delay after the rollover of the parent 
	'expd_delay': 200,
	// names of the CSS classes for the menu elements in different states
	// tag: [normal, hover, mousedown]
	'css' : {
		'inner' : 'minner',
		'outer' : ['moout', 'moover', 'modown']
	}
},
// sub-menus configuration (level 1)
// any omitted parameters are inherited from parent level
{
	'height': 280,
	'width': 363,
	// position of the submenu relative to top left corner of the parent item
	'block_top': 10,
	'block_left': -363,
	'css' : {
		'inner' : '',
		'outer' : ''
	}
}
// the depth of the menu is not limited
// make sure there is no comma after the last element
];
