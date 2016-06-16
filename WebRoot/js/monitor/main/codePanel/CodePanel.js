codePanel = {

		openTabs : [],
		activeTab : null,

		//初始化，由ideviewer调用
		InitCodePanel : function() {
			var codepanel = $("#codepanel");
			codepanel.attr({
				"region":"center",
				"minwidth":300
			}).css({padding:0});
			
			
			
			//创建tabs
			codepanel.append($("<div/>").attr("id","codearea"));
			var codearea = $("#codearea");
			codearea.tabs({
				fit:true,
				border:false,
				onBeforeClose: function(title, index) {
					return codePanel.onTabClose(title, index);
				},
				onSelect: function(title, index) {
					var tab = $('#codearea').tabs('getTab', index);
					var path = tab.attr('path');
					//select the node in Explorer
					Global.explorerPanel.selectNodeByPath(path);
					var fullpath = tab.attr('fullpath');
					var codetab = codePanel.getTabByPath(fullpath);
					if (codetab != null)
						codetab.refreshOutline();
				},
				
				onContextMenu : function(e, title, index) {
					e.preventDefault();
		        		$("#rightMenu_tab").menu("show", {
							left: e.pageX,
							top: e.pageY
						});
		        	
				}
			});
			codePanel.codeArea = codearea;
			return codePanel;
		},

		//添加文件
		addFile : function(name, path, project, callbackFunction){
			//alert(path);
			//检查是否已打开
			var tab = codePanel.getTabByPath(project + "/" + path);
			//如果是，不作为，返回
			if (tab != null) {
				codePanel.setActiveTab(tab);
				if (callbackFunction && typeof(callbackFunction)=="function") {
					callbackFunction();
				}
				return;
			}
			//否则增加一个tab
			var tabid;
			if(codePanel.openTabs.length!=0){
				tabid = $(codePanel.openTabs[codePanel.openTabs.length-1]).attr("id")+1;
			}else{
				tabid = 0;
			}
			var newtab = new codeTab(tabid, name, path, project,null,callbackFunction);
			codePanel.activeTab = newtab;
			codePanel.openTabs.push(newtab);
		},

		setActiveTab : function(tab) {
			var id = codePanel.codeArea.tabs('getTabIndex', tab.editTab)
			codePanel.codeArea.tabs('select', id);
			tab.refreshOutline();
		},

		getActiveTab : function() {
			var tab = $('#codearea').tabs('getSelected');
			if (tab == null)
				return null;
			var fullpath = tab.attr('fullpath');
			return codePanel.getTabByPath(fullpath);
		},

		onCloseAllTabs : function() {
			var allTabs = $("#codearea").tabs('tabs');
			$.each(allTabs, function() {
				$("#codearea").tabs("close", 0);
			});
		},
		
		onTabClose : function(title, index) {
			var tab = $('#codearea').tabs('getTab', index);
			var fullpath = tab.attr('fullpath');
			var path = tab.attr('path');
			var i, itab;
			var _tab = codePanel.getTabByPath(fullpath);
			if(_tab.websocket!=null){
				_tab.websocket.close();
			}
			if (_tab.isSaved == false&&write=="true") {
				if (confirm(fullpath + '尚未保存，是否仍然关闭？') == false)
					return false;
				else {
					for (i = 0; i < codePanel.openTabs.length; i++) {
						itab = codePanel.openTabs[i];
						if  (itab.fullpath == fullpath) {
							codePanel.openTabs.splice(i, 1);
							break;
						}
					}
				}
			}
			var param = {
					path: path,
					appname: appname,
					apptype: apptype,
					ownername: ownername,
			};
			for (i = 0; i < codePanel.openTabs.length; i++) {
				itab = codePanel.openTabs[i];
				if  (itab.fullpath == fullpath) {
					codePanel.openTabs.splice(i, 1);
					break;
				}
			}
			if (codePanel.openTabs.length <= 0)
				outlinePanel.removeTree();
			return true;
		},

		// 这里的path要求以项目名起始
		getTabByPath: function(path) {
			for (i in codePanel.openTabs) {
				var tab = codePanel.openTabs[i];
				if (tab.fullpath == path) {
					return tab;
				}
			}

			return null;
		},

		getCodeTabs: function() {
			return codePanel.openTabs;
		}
};
