// level scope settins structure
var MENU_TPL = [
// root level configuration (level 0)
{
	// item sizes
	'height': 25,
	'width': 140,
	// absolute position of the menu on the page (in pixels)
	// with centered content use Tigra Menu PRO or Tigra Menu GOLD
	'block_top': 15,
	'block_left': 20,
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
		'outer' : ['m0l0oout', 'm0l0oover'],
		'inner' : ['m0l0iout', 'm0l0iover']
	}	
},
// sub-menus configuration (level 1)
// any omitted parameters are inherited from parent level
{
	'width': 120,
	// position of the submenu relative to top left corner of the parent item
	'block_left': 50,
	'css' : {
		'outer' : ['m0l1oout', 'm0l1oover'],
		'inner' : ['m0l1iout', 'm0l1iover']
	}
}
];