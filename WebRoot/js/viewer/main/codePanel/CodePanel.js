codePanel = {

		openTabs : [],
		activeTab : null,
		authws : null,

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

			// 创建右键 
			// 创建右键菜单
			var menuid = "rightMenu_code";
			$("#codearea").append($("<div/>").attr({
				'id':menuid,
				'style':"width:150px"
			}));
			$("#rightMenu_code").menu();
			$("#rightMenu_code").menu('appendItem', {
				text: 'open declaration',
				onclick: function(e) {
					declarcall.openDeclaration();
				}
			});
			$("#rightMenu_code").menu('appendItem', {
				text: 'open call hierarchy',
				onclick: function(e) {
					declarcall.callHierarchy();
				}
			});
			$("#rightMenu_code").hide();
			
			// tab 右键
			$("#codearea").append($("<div/>").attr({
				'id' : 'rightMenu_tab',
				'style':"width:150px"
			}));
			$("#rightMenu_tab").menu();
			$("#rightMenu_tab").menu('appendItem', {
				text: 'Close All Tabs',
				onclick: function(e) {
					codePanel.onCloseAllTabs();
				}
			});
			$("#rightMenu_tab").hide();
			
			
			codePanel.openGlobalSocket();
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
			codePanel.setRWEditor(path);
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

		setRWEditor: function(path){
			var tab = null;
			for(i in codePanel.openTabs){
				if(codePanel.openTabs[i].path==path){
					tab = codePanel.openTabs[i];
				}
			}
			if(tab!=null){
				$.get(Global.url.getDevelopers, {
					filepath: '/'+tab.fullpath,
					apptype: apptype,
					ownername: ownername
				}, function(data){
					var devs = JSON.parse(data);
					tab.developers = devs;
					if(devs[0]!=username){
						tab.editor.setReadOnly(true);
					}else{
						tab.editor.setReadOnly(false);
					}
					if(devs.length>1&&tab.websocket==null){
						codePanel.openRWSocket(tab);
					}
				});
			}
		},

		openGlobalSocket: function(){
			var uri = Global.url.authws;
			codePanel.authws = new WebSocket(uri+username);
			codePanel.authws.onopen = function(){console.log("global ws open")}
			codePanel.authws.onerror = function(){console.log("global ws error")}
			codePanel.authws.onmessage = function(e){
				msg = JSON.parse(e.data);
				var tab = null;
				for(i in codePanel.openTabs){
					if("/"+codePanel.openTabs[i].fullpath==msg.content){
						tab = codePanel.openTabs[i];
						if(msg.code==203){
							tab.editor.setReadOnly(true);
						}else if(msg.code==303){
							tab.editor.setReadOnly(false);
						}
					}
				}
			}
		},

		openRWSocket: function(tab){
			if('WebSocket' in window){
				var uri = Global.url.editws+username +"?filepath="
				+ encodeURIComponent(tab.fullpath);
				console.log(uri);
				tab.websocket = new WebSocket(uri);
				tab.websocket.onerror = function(){
					console.log("error");
				};

				tab.websocket.onopen = function(event){
					console.log("open");
				}

				tab.websocket.onmessage = function(event){

					msg = JSON.parse(event.data);
					if(msg.code==201){
						consolePanel.append('info', "Info", msg.content+" opened "+tab.fullpath);
						var devs = JSON.parse(msg.content);
						if(tab.sender==null&&username==tab.developers[0]){
							tab.sender = setInterval(function(){
								var newString = tab.editor.getValue();
								if(tab.oldString!=newString&&tab.websocket!=null){
									console.log('send');
									tab.oldString = newString;
									var message = tab.editor.getValue();
									var msg = new Object();
									msg['content'] = message;
									msg['code'] = 102;
									msg['timestamp'] = Date.parse(new Date()).toString();
									tab.websocket.send(JSON.stringify(msg));
								}
							}, 1000);
						}
					}else if(msg.code==202){
						if(username!=tab.developers[0]){
							tab.editor.setValue(msg.content);
						}
					}

				}

				tab.websocket.onclose = function(){
					clearInterval(tab.sender);
					tab.sender=null;
					console.log("close");
				}
			}else{
				alert('Not support websocket')
			}
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
