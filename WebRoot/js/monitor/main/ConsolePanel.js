consolePanel = {

		con_tabs : [],
		ws : null,
		wsurl : null,
		weburl : null,

		// 初始化
		InitConsolePanel : function() {
			var thisPanel = $("#consolepanel");
			thisPanel.attr({
				"region" : 'south',
				"split" : true,
				"collapsible" : true,
			}).css({
				padding:0
			});
			thisPanel.tabs({
				border : false,
				onSelect: function(title){
					if(title=="console") {
						$("#console_input").find("input").focus();
					}
				}
			});
			thisPanel.tabs('add', {
				id : "info",
				title : "info"
			});
			thisPanel.tabs('add', {
				id : "log",
				title : "log"
			});
			thisPanel.tabs('add', {
				id : "console",
				title : "console"
			});
			thisPanel.tabs('add', {
				id : "terminal",
				title : "terminal"
			});

			thisPanel.tabs('add',{
				id:"consoleCallHierarchy",
				title:"Call Hierarchy",
			});

			thisPanel.tabs('add', {
				id : "consoleSearchResult",
				title : "Search Result",
			});

			$("#info").css({
				padding : 0
			});
			$("#info").append($("<div/>").attr({
				"id" : "info",
			}).addClass('console'));

			$("#log").css({
				padding : 0
			});
			$("#log").append($("<div/>").attr({
				"id" : "log",
			}).addClass('console'));

			$("#console").css({
				padding : 0,
			});
			$("#console").append($("<div/>").attr("id", "scroll_div")
					.addClass('console'));
			$("#scroll_div").append($("<div/>").attr({
				"id" : "console_output",
			}));
			$("#scroll_div").append($("<div/>").attr({
				"id" : "console_input"
			}));
			$("#console_input").append($("<div>").append($("<input/>").attr({
				"id" : "console_input_area"
			})));
			$("#console_input_area").keydown(stdStream.stdin);
			
			$("#terminal").css({
				padding : 0
			});

			$("#consoleSearchResult").append($("<ul/>").attr({
				"id" : "searchResultTree"
			}).css({
				// background-color:grey,
				width : 'auto',
				height : "auto",
				resize : "none",
				"background-color" : "rgb(230, 230, 230)"
			}));

			$("#consoleCallHierarchy").append($("<ul/>").attr({
				"id":"callHierarchyTree"
			}).css({
				//background-color:grey,
				width:'auto',
				height:"auto",
				resize:"none",
				"background-color":"rgb(230, 230, 230)"
			}));


			/*
			 * 搜索结果树结构： id: text: 文件路径 or 所在行号+内容 attributes: type: filename or line
			 * project: path: title: * line: 行号 * col: 列号 * pos: 文件中位置 children:[]
			 */


			$("#searchResultTree").tree(
					{
						data : [],
						onClick : function(node) {
							var project = node.attributes.project;
							var path = node.attributes.path;
							var title = node.attributes.title;
							var line;
							if (node.attributes.type != 'line') {

								Global.ideViewer.onFileSelect(title, path, project,
										function() {
									Global.ideViewer.codePanel
									.getActiveTab()
									.highlightDebugLine(1);
								});
							} else {
								line = node.attributes.line;
								// alert(line);
								Global.ideViewer.onFileSelect(title, path, project,
										function() {
									Global.ideViewer.codePanel
									.getActiveTab()
									.highlightDebugLine(line);
								});
							}
						}
					});

			$("#callHierarchyTree").tree({
				data :[],
				onClick : function(node) {
					var project = node.attributes.project;
					var path = node.attributes.path;
					var title = node.attributes.title;
					var line;
					if (node.attributes.type != 'line') {

						Global.ideViewer.onFileSelect(title, path, project, function(){

						});
					}
					else {
						line = node.attributes.line;
						//alert(line);
						Global.ideViewer.onFileSelect(title, path, project, function(){
							Global.ideViewer.codePanel.getActiveTab().addSelectionMarker(line-1,0,line,0);;
						});
					}
				}

			});

			thisPanel.tabs('select', 0);
			return thisPanel;
		},

		append : function(id, title, msg) {
			$("#consolepanel").tabs('select', id);
			if(id=="console") {this.appendConsole(title, msg); return; }
			var date = new Date();
			var div = $("#" + id).find("div").append(
					$("<pre/>").html(
							'[' + date.toLocaleTimeString() + '][' + title + '] '
							+ msg));
			div.scrollTop(div[0].scrollHeight);
		},

		appendConsole : function(title, msg){
			var date = new Date();
			if(title=="input"||title=="output"){
				$("#console_output").append(
						$("<pre/>").html(msg));
			}else{
				$("#console_output").append(
						$("<pre/>").html(
								'[' + date.toLocaleTimeString() + '][' + title + '] '
								+ msg));
			}
			$('#console_input_area').focus();
			var scroll_div = $("#scroll_div");
			scroll_div.scrollTop(scroll_div[0].scrollHeight);
		},

		showSearchResult : function(data) {
			this.clearSearchResult();
			$("#searchResultTree").tree('loadData', data);
			$("#consolepanel").tabs('select', 'Search Result');

		},

		showCallHierarchy : function(data) {
			this.clearCallHierarchy();
			$("#callHierarchyTree").tree('loadData', data);
			$("#consolepanel").tabs('select', 'Call Hierarchy');

		},

		clearCallHierarchy: function() {
			$("#callHierarchyList").datagrid("loadData",{total:0,rows:[]});  
		},

		clearSearchResult : function() {
			$("#searchResultList").datagrid("loadData", {
				total : 0,
				rows : []
			});
		}
}
