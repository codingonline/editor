openServiceUiApi= {

		
		
		init: function(){
		
		},
		
		appendConsoleInfo: function(title,info){
			consolePanel.append('info', title, info);
		},
		
		appendToolBarButton: function(serviceName, buttonName, func){
			var toolbar = $('#toolbar');
			var buttonId = serviceName + "_" + buttonName;
			var i=0;
			while($("#"+ buttonId).length!=0){
			    buttonId = serviceName + "_" + buttonName+ i.toString();
			    i = i + 1;
			}
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": buttonId
			}).html(buttonName).bind("click",func));
			toolbar.append(" ");
		
		},
		
		appendToolBarMenu: function(serviceName, menuName, items){
			/*
			var toolbar = $('#toolbar');
			var menuId = serviceName + "_" + menuName;
			var i=0;
			while($('#'+ menuId).length!=0){
			    menuId = serviceName + "_" + menuName+ i;
			    i = i + 1;
			}
	
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": menuId
			}).html(menuName));
			toolbar.append($("<div/>").attr("id", menuId+"Menu"));
			var MB = $("#"+menuId);
			var Menu = $("#"+menuId+"Menu");
			var i = 0;
			
			for(i=0; i<items.length ;i++){
				Menu.append(items[i]);
			}
			
			MB.menubutton({
				menu: "#"+menuId+"Menu"
			});
			*/
			var menuId = serviceName + "_" + menuName;
			var i=0;
			while($('#'+ menuId).length!=0){
			    menuId = serviceName + "_" + menuName+ i;
			    i = i + 1;
			}
	
			var toolMenu = $('#ToolsMenu');
			var newTool = $("<div/>").append($("<span/>").attr("id", menuId).html(menuName));
			var i = 0;
			var div = $("<div/>");
			for(i=0; i<items.length ;i++){
				div.append(items[i]);
			}
			newTool.append(div);
			toolMenu.append(newTool);
			
			
			
		},
		
		createNewMenuItem: function(serviceName, itemName, func){
			var itemId = serviceName + "_" + itemName;
			var i=0;
			
			while($("#"+ itemId).length!=0){
			    itemId = serviceName + "_" + itemName+ i.toString();
			    i = i + 1;
			}
			var item= $("<div/>").attr("id", itemId ).html(itemName).bind('click',
					func);
			
			return item;
			
		},
		
		highlightArea: function(row1,column1,row2,column2){
			Global.ideViewer.codePanel.getActiveTab().addSelectionMarker(row1,column1,row2,column2);
		},
		
		highlightLine : function(line) {
			var Range = ace.require("ace/range").Range;
			Global.ideViewer.codePanel.getActiveTab().editor.getSession().addMarker(new Range(line-1, 0, line-1, 2000), "debug_highlight", "line", true);
		},
		

}