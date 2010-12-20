// level scope settins structure
var MENU_TPL = [
// root level configuration (level 0)
{
	// item sizes
	'width': 160,
	'height': 20,
	// absolute position of the menu on the page (in pixels)
	// with centered content use Tigra Menu PRO or Tigra Menu GOLD
	'block_top': 150,
	'block_left': 400,
	// offsets between items of the same level (in pixels)
	'top': 21,
	'left': 10,
	// time delay before menu is hidden after cursor left the menu (in milliseconds)
	'hide_delay': 200,
	// submenu expand delay after the rollover of the parent (negative value: expand on click)
	'expd_delay': -1,
	// names of the CSS classes for the menu elements in different states
	// tag: [normal, hover, mousedown]
	'css' : {
		'inner' : 'minner',
		'outer' : ['moout', 'moover']
	}
},
// sub-menus configuration (level 1)
// any omitted parameters are inherited from parent level
{
	'width': 140,
	// position of the submenu relative to top left corner of the parent item
	'block_top': 16,
	'block_left': -120
},
// sub-sub-menus configuration (level 2)
{
	'block_left': 130
}
// the depth of the menu is not limited
// make sure there is no comma after the last element
];

