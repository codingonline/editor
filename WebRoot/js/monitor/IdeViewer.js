/**
加载整体
 **/
ideViewer = {
		topBar : '',
		explorerPanel : '',
		codePanel : '',
		consolePanel : '',
		outlinePanel : '',

		onFileSelect: function(title, path, project, callbackFunction) {
			this.codePanel.addFile(title, path, project, callbackFunction);
		},

		InitIdeViewer: function(){
			
			Global.ideViewer = this;
			
			// 初始化界面元素
			topBar.initMenuItems();

			// 渲染界面元素
			this.outlinePanel = outlinePanel.InitOutlinePanel();
			Global.outlinePanel = this.outlinePanel;
			this.codePanel = codePanel.InitCodePanel();
			//Global.codePanel = this.codePanel;
			this.explorerPanel = explorerPanel.InitExplorerPanel();
			Global.explorerPanel = this.explorerPanel;
			this.consolePanel = consolePanel.InitConsolePanel();
			this.topBar = topBar.InitTopBar();
			$("body").layout();

			// guest
			if(ownername=="guest"){
				consolePanel.append('info', "Warning", 
						"You are in the temporary workspace where you can edit and run apps. "
						+"But all you work won't be preserved after you leave or refresh the page. " 
						+"Click 'file->save project' to get a exact copy of this app permanently. ");
			}
			// delete app when leave the page, only guest
			window.onbeforeunload = function (event){   
				if(ownername=="guest"){
					$.post(Global.url.removeProject, {
						appname: appname,
						apptype: apptype,
						ownername: ownername
					});
				}
				var tabs = codePanel.openTabs;
				for(i in tabs) {
					if(!tabs[i].isSaved) {
						event.returnValue = "有文件尚未保存";
						break;
					}
				}
				var tabstr = "";
				for(i in tabs){
					tabstr += tabs[i].path.replace("&", "\&")+"&";
				}
			};

			$( window ).unload(function(event) {
				$.ajax( {
					async: false,
					data: {
						path: path,
						appname: appname,
						apptype: apptype,
						ownername: ownername,
					},
					error: function () {
						alert('Close notification error');
					},
					success: function ( data ) {
						alert('Successful close notification');
					},
					url: Global.url.logout
				} );
			});
		},
		
		preInit: function(){
			for(i in arguments){
				arguments[i].preInit();
			}
		},
		
		afterInit: function(){
			for(i in arguments){
				arguments[i].afterInit();
			}
		},

};