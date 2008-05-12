var count = 1 ;
var DOMUtil = eXo.core.DOMUtil;
eXo.core.DragDrop2 = {
	obj : null,
	
	init : function(o, oRoot) {
		o.onmousedown = eXo.core.DragDrop2.start;
		
		o.root = oRoot && oRoot != null ? oRoot : o ;
		o.root.onmousedown = function() {
			this.style.zIndex = ++count ;
		}
		
		o.root.onDragStart = new Function();
		o.root.onDragEnd = new Function();
		o.root.onDrag = new Function();
	},
	
	start : function(e)	{
		var o = eXo.core.DragDrop2.obj = this;
		e = eXo.core.DragDrop2.fixE(e);
		var y = parseInt(DOMUtil.getStyle(o.root,"top"));
		var x = parseInt(DOMUtil.getStyle(o.root,"left"));
		o.lastMouseX = 		eXo.core.Browser.findMouseXInPage(e);
		o.lastMouseY = 		eXo.core.Browser.findMouseYInPage(e);
		o.root.onDragStart(x, y, o.lastMouseX, o.lastMouseY, e);
		document.onmousemove = eXo.core.DragDrop2.drag;
		document.onmouseup = eXo.core.DragDrop2.end;
		return false;
	},
	
	drag : function(e) {
		e = eXo.core.DragDrop2.fixE(e);
		var o = eXo.core.DragDrop2.obj;
		var ey = eXo.core.Browser.findMouseYInPage(e);
		var ex = eXo.core.Browser.findMouseXInPage(e);
		var y = parseInt(DOMUtil.getStyle(o.root, "top"));
		var x = parseInt(DOMUtil.getStyle(o.root, "left"));
		var nx, ny;

		nx = x + (ex - o.lastMouseX);
		ny = y + (ey - o.lastMouseY);
		
		eXo.core.DragDrop2.obj.root.style["left"] = nx + "px";
		eXo.core.DragDrop2.obj.root.style["top"] = ny + "px";
		eXo.core.DragDrop2.obj.lastMouseX = ex;
		eXo.core.DragDrop2.obj.lastMouseY = ey;
		
		eXo.core.DragDrop2.obj.root.onDrag(nx, ny, ex, ey, e);
		return false;
	},
	
	end : function(e) {
		e = eXo.core.DragDrop2.fixE(e);
		document.onmousemove = null;
		document.onmouseup = null;
		eXo.core.DragDrop2.obj.root.onDragEnd( parseInt(eXo.core.DragDrop2.obj.root.style["left"]), 
		parseInt(eXo.core.DragDrop2.obj.root.style["top"]), e.clientX, e.clientY);
		eXo.core.DragDrop2.obj = null;
	},
	
	fixE : function(e) {
		if (typeof e == 'undefined') e = window.event;
		if (typeof e.layerX == 'undefined') e.layerX = e.offsetX;
		if (typeof e.layerY == 'undefined') e.layerY = e.offsetY;
		return e;
	},
	
	isIn : function(x, y, component) {
	  var componentLeft = eXo.core.Browser.findPosX(component);
	  var componentRight = componentLeft + component.offsetWidth ;
	  var componentTop = eXo.core.Browser.findPosY(component) ;
	  var componentBottom = componentTop + component.offsetHeight ;
	  var isOver = false ;
	
			var uiWorkspaceContainer = document.getElementById("UIWorkspaceContainer") ;
			if ((uiWorkspaceContainer && uiWorkspaceContainer.style.display != "none") && eXo.core.Browser.isIE7()) {
				componentRight = componentRight - uiWorkspaceContainer.offsetWidth;
			}
			
	  if((componentLeft < x) && (x < componentRight)) {
	    if((componentTop < y) && (y < componentBottom)) {
	      isOver = true ;
	    }
	  }
	  return isOver ;
	} 
};