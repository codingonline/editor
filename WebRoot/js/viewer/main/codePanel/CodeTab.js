function codeTab(id, name, path, project, node,callback) {
	this.id = id;
	this.path = path;
	this.fullpath = project+'/'+path;
	this.project = project;
	this.tabName = name;
	this.isSaved = true;
	this.marker = null;
	this.editTab = null;
	this.toolBar = null;
	this.selcetionrange = null;
	this.node = node;
	this.callbackFunction = callback;
	
	this.editor = null;
	this.developers = null;
	this.oldString = null;
	this.websocket = null;
	this.sender = null;
	
	//初始化函数
	this.InitCodeTab = function(){
		this.codearea = $("#codearea");
		var tabID = "tab_"+this.id;
		//加载tabs
		this.codearea.tabs('add',{
			id:tabID,
			title:this.tabName,
			closable:true,
			fit:true,
		});
		// 存储tab信息
		$("#"+tabID).attr({
			'project':this.project,
			'fullpath':this.fullpath,
			'path': this.path
		});
		this.editTab = $("#"+tabID);
		this.editTab[0].oncontextmenu = function(e){
        	//console.log(e.which);
			e.preventDefault();
        	if(e.which==3){
        		$("#rightMenu_code").menu("show", {
					left: e.pageX,
					top: e.pageY
				})
        	}
        }
		this.loadFile();
		
		return this;
	};
	
	this.goToLine = function(lineNumber) {
		this.editor.gotoLine(lineNumber);
	};

	this.loadFile = function(){
		var toolBar = Global.ideViewer.topBar;
		var tabID = "tab_"+this.id;
		var thisTab = this;
		var url = Global.url.loadFile;
		var param = {
				path: this.path,
    			appname: appname,
    			apptype: apptype,
    			ownername: ownername,
		};
		
		function success(data){
			var editor = ace.edit(tabID);
			ace.require('ace/ext/settings_menu').init(editor);
			editor.setTheme(preferences.theme);
			editor.setFontSize(preferences.font_size);
			//debug 标亮显示
			editor.on("guttermousedown", function(e){
				var target = e.domEvent.target;
				if (target.className.indexOf("ace_gutter-cell") == -1)
					return;
				if (!editor.isFocused())
					return;
				if (e.clientX > 25 + target.getBoundingClientRect().left)
					return;
				var row = e.getDocumentPosition().row;
				if( e.editor.session.getBreakpoints()[row] ) {
					Global.breakpointInSourceFile(thisTab.project, thisTab.path, row, false, e.editor.session);
				}
				else {
					Global.breakpointInSourceFile(thisTab.project, thisTab.path, row, true, e.editor.session);
				}
				e.stop();
			});
			
			//debug 自动换行
			editor.getSession().on('change',function(evt) {
				thisTab.documentChanged(evt, editor);
				if(savedata) {
					Global.operations.push('{"data":'+JSON.stringify(evt.data)+',"path":"'+path+'","time":'+new Date().getTime()+',"operation":"edit"}');
				}
			}); 
			count = 0;
			editor.getSession().selection.on('changeSelection', function(e) {
				count++;
			});
			
			var modelist = ace.require("ace/ext/modelist");
	        var filePath = codePanel.getActiveTab().path;
	        var mode = modelist.getModeForPath(filePath).mode;
	        editor.session.setMode(mode);
			
			editor.getSession().setValue(data);
			editor.setOption("enableEmmet", true);
			thisTab.editor = editor;
			
			thisTab.refreshOutline();
			
			editor.getSession().on('change', function() {
				if (thisTab.isSaved == true&&editor.getReadOnly()==false) {
					var newName = thisTab.tabName + '*';
					$("#codearea").tabs('update', {
						tab : thisTab.editTab,
						type : 'header',
						options: {
							title: newName
						}
						
					});
					thisTab.isSaved = false;
				}
			});
			
			// hotkeys
			var commands = editor.commands;
			commands.addCommand({
			    name: "save",
			    bindKey: {win: "Ctrl-S", mac: "Command-S"},
			    exec: function(arg) {
			        Global.onSaveFile();
			    }
			});
			
			commands.addCommand({
		        name: "showKeyboardShortcuts",
		        bindKey: {win: "Ctrl-Alt-h", mac: "Command-Alt-h"},
		        exec: function(editor) {
		            ace.config.loadModule("ace/ext/keybinding_menu", function(module) {
		                module.init(editor);
		                editor.showKeyboardShortcuts()
		            })
		        }
		    });
		    //editor.execCommand("showKeyboardShortcuts");
			commands.addCommands([{
				name: "showSettingsMenu",
				bindKey: {win: "Ctrl-q", mac: "Command-q"},
				exec: function(editor) {
					editor.showSettingsMenu();
				},
				readOnly: false
			}]);
			
			commands.addCommands([{
				name: "openFileSearch",
				bindKey: {win: "Ctrl-Shift-f", mac: "Ctrl-Shift-f"},
				exec: function(editor) {
					$("#fileSearchDialog").dialog("open");
					
				}
			}]);
			
			
			// completer
			editor.setOptions({
				enableBasicAutocompletion: true,
		        enableSnippets: true
//		        enableLiveAutocompletion: true
		    });
			var langTools = ace.require("ace/ext/language_tools");
			langTools.addCompleter(PopCompleter);
//			editor.completers = [completer];

			editor.selection.on('changeCursor',function(evt) {
				// check if there's "." to trigger completion
				//thisTab.removeSelectionMarker();
				var pos = editor.getCursorPosition();
				var line = editor.session.getLine(pos.row);
				var popup;
				if (editor.completer)
					popup = editor.completer.popup;
				if (apptype == "javaweb" && line.charAt(pos.column - 1) == "." && line.indexOf ("package") < 0 && line.indexOf ("import") < 0) {
					if (!popup || !popup.isOpen)
						editor.execCommand ("startAutocomplete");
				}
				else if (apptype == "php" && line.charAt(pos.column - 1) == ">" && line.charAt(pos.column - 2) == "-") {
					if (!popup || !popup.isOpen)
						editor.execCommand ("startAutocomplete");
				}
			});
			
			editor.selection.on('changeSelection',function(evt) {
				// check if there's "." to trigger completion
				thisTab.removeSelectionMarker();
			});
			
			
			this.editor = editor;
			if (thisTab.callbackFunction && typeof(thisTab.callbackFunction)=="function")
				thisTab.callbackFunction();
		};
		$.post(url, param, success);
		 
	};
	
	this.removeMarker = function() {
		if (this.marker){
			this.editor.getSession().removeMarker(this.marker);
			this.selcetionrange = null;
		}
	};
	
	this.highlightDebugLine = function(line) {
		var Range = ace.require("ace/range").Range;
		this.removeMarker();
		//this.editor.clearSelection();
    	this.marker = this.editor.getSession().addMarker(new Range(line-1, 0, line-1, 2000), "debug_highlight", "line", true);
    	this.goToLine(line);
	};
	
	this.addSelectionMarker = function(row1, column1, row2, column2) {
		var Range = ace.require("ace/range").Range;
		this.removeSelectionMarker();
		//this.editor.clearSelection();
		this.selectionrange = new Range(row1, column1, row2, column2);
    	this.editor.addSelectionMarker(this.selectionrange);
    	this.goToLine(row1+1);
	};
	this.removeSelectionMarker = function(){
		if(this.selectionrange != null){
			this.editor.removeSelectionMarker(this.selectionrange);
			this.selcetionrange =null;
			

		}
	}
	this.refreshOutline = function() {
		outlinePanel.refreshTree();
	},
	
	this.documentChanged = function(event, editor) {
		var delta = event.data;
		var range = delta.range;
		var len, firstRow, f1;
		if (delta.action == "insertText") {
			len = range.end.row - range.start.row;
			firstRow = range.start.column == 0? range.start.row: range.start.row+ 1;
		} else if (delta.action == "insertLines") {
			len = range.end.row - range.start.row;
			firstRow = range.start.row;
		} else if (delta.action == "removeText") {
			len = range.start.row- range.end.row;
			firstRow = range.start.row;
		} else if (delta.action == "removeLines") {
			len = range.start.row- range.end.row;
			firstRow = range.start.row;
		}
		var breakpoints = editor.session.getBreakpoints();
		var newBreakpoints = [];
		var changed = false;
		if (len > 0) {
			for( var index in breakpoints ) {
				var idx = parseInt(index);
				if( idx < firstRow ) {
					newBreakpoints.push(idx);
				} else {
					changed = true;
					newBreakpoints.push(idx+len);
				}
			}
		} else if (len < 0) {
			for( var index in breakpoints ) {
				var idx = parseInt(index);
				if( idx < firstRow ) {
					newBreakpoints.push(idx);
				} else if( (index < firstRow-len) && !newBreakpoints[firstRow]) {
					newBreakpoints.push(firstRow);
					changed = true;
				} else {
					newBreakpoints.push(len+idx);
					changed = true;
				}
			}
		}
		if( changed ) 
			editor.session.setBreakpoints(newBreakpoints);
	}, 
	
	this.InitCodeTab();
}
