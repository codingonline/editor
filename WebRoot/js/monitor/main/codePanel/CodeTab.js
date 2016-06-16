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
			
			var modelist = ace.require("ace/ext/modelist");
	        var filePath = codePanel.getActiveTab().path;
	        var mode = modelist.getModeForPath(filePath).mode;
	        editor.session.setMode(mode);
			
			editor.getSession().setValue(data);
			editor.setOption("enableEmmet", true);
			thisTab.editor = editor;
			
			thisTab.refreshOutline();
			
			editor.setReadOnly(true);
				
			
			// completer
			editor.setOptions({
				enableBasicAutocompletion: false,
		        enableSnippets: false
//		        enableLiveAutocompletion: true
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
	
	this.InitCodeTab();
}
