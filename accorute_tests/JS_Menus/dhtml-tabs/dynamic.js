var preloaded = 0;
var img_l, img_c, img_r;
var lnkColor="";

function apy_getObjectByID(id) {
  if (document.layers)          return document.layers[id];
  if (!document.getElementById) return document.all[id];
  return document.getElementById(id);
}

function apy_setPreloaded() {
  preloaded = true;
}

function apy_preload() {
  img_l = new Image();
  img_c = new Image();
  img_r = new Image();
  img_l.src = "img/btn_left_o.gif";
  img_c.src = "img/btn_back_o.gif";
  img_r.src = "img/btn_right_o.gif";
}

function apy_button(obj, state) {
  if (!preloaded) return;
  var btn_l = apy_getObjectByID(obj.id+'_l');
  var btn_c = apy_getObjectByID(obj.id+'_c');
  var btn_r = apy_getObjectByID(obj.id+'_r');
  if (state)
  {
    btn_l.src = img_l.src;
    btn_c.style.backgroundImage = "url(img/btn_back_o.gif)";
    btn_r.src = img_r.src;
    lnkColor = obj.style.color;
    obj.style.color = "#FFFFFF";
  }
  else
  {
    btn_l.src = "img/btn_left.gif";
    btn_c.style.backgroundImage = "url(img/btn_back.gif)";
    btn_r.src = "img/btn_right.gif";
    obj.style.color = lnkColor;
  }
}

apy_preload();
onload = apy_setPreloaded;
