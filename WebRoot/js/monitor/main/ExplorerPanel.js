explorerPanel = {

    InitExplorerPanel : function() {
    	
    	// 创建exploere本体
        $("#explorerpanel").attr({
            region:'west',
            split:true, 
            collapsed:false,
            animate : false,
            title:'Explorer',
        });
        $("#explorerpanel").css({width:225});
        
        
        // 创建右键菜单
        var menuid = "rightMenu";
        $("#explorerpanel").append($("<div/>").attr({
        	'id':menuid,
        	'style':"width:170px"
        }));
        $("#rightMenu").menu();
        
        $("#rightMenu").menu('appendItem', {
        	text: 'New File',
        	onclick: function() {
        		//alert('delete');
        		explorerPanel.onAddFile();
        	}
        });
        $("#rightMenu").menu('appendItem', {
        	text: 'New Folder',
        	onclick: function() {
        		var node = $('#treeView').tree('getSelected');
        		explorerPanel.onAddPackage();
        	}
        });
        $("#rightMenu").menu('appendItem', {
        	text: 'Copy',
        	onclick: function() {
        		var node = $('#treeView').tree('getSelected');
        		Global.copyPath = node.attributes.path;
        	}
        });
        $("#rightMenu").menu('appendItem', {
        	text: 'Paste',
        	onclick: function() {
        		var node = $('#treeView').tree('getSelected');
        		var dstPath = node.attributes.path;
        		if(Global.copyPath) {
        			var url = Global.url.copyFile;
        			var param = {
        					ownername: ownername,
        					appname: appname,
        	    			apptype: apptype,
        					src: Global.copyPath,
        					dst: dstPath
        				};
        			var success = function(data){
    					explorerPanel.refreshTree();
                		if(data=='success'){
                			var infostr = 'New file ';
                			var arr = param.src.split('/');
                			var srcfilename = null;
                			if(arr[arr.length-1])
                				srcfilename = arr[arr.length-1];
                			else {
                				srcfilename = arr[arr.length-2];
                				infostr = 'New directory ';
                			}
                			var p = param.dst.lastIndexOf('/');
                			var dstdirname = param.dst.substring(0, p+1);
                			consolePanel.append('info', 'Success', infostr + dstdirname + srcfilename + ' pasted');
                		}else{
                			consolePanel.append('info', 'Error', data);
                		}
    				};
    				consolePanel.append('info', 'Processing', 'Copying file...');
    				$.post(url, param, success);
        		}
        	}
        });
        $("#rightMenu").menu('appendItem', {
        	text: 'Rename',
        	onclick: function() {
        		explorerPanel.onRename();
        	}
        });
        
        $("#rightMenu").menu('appendItem', {
        	text: 'Delete',
        	onclick: function() {
        		explorerPanel.onRemove();
        	}
        });
        $("#rightMenu").menu('appendItem', {
        	text: 'Run as Java Application',
        	onclick: function() {
        		var node = $('#treeView').tree('getSelected');
        		var type = node.attributes.type;
        		var path = node.attributes.path;
        		if(type == 'java') {
        			var url = Global.url.loadFile;
        			var param = {
        					path: path,
        					appname: appname,
        	    			apptype: apptype,
        	    			ownername: ownername,
        			};
        			$.post(url, param, function(data) {
        				if( data.indexOf("public static void main") != -1 ) {
        					
        					
        					
        				} else {
        					jQuery.messager.alert('Failure', 'Sorry, this file does not contain a MAIN method !');
        				}
        			});
        		} else {
        			jQuery.messager.alert('Failure', 'Sorry, only java file can be run as a Java application !');
        		}
        	}
        });
        $('#rightMenu').menu('appendItem', {
        	text: 'Authorization',
        	onclick: rwFileAuth.onRWAuthDialog
        })
        
        $("#rightMenu").hide();
 
        this.createTree();
        var rootNode = this.treeView.tree('getRoot');
        this.treeView.tree('collapseAll', rootNode.target);
        this.treeView.tree('expand', rootNode.target);
        return this;
    },
    
    
    createTree : function() {
    	$('#explorerpanel').append($('<ul/>').attr({
    		'id':'treeView'
    	}));
    	var treeData = null;
    	//get tree json
    	var url = Global.url.refreshExplorer;
    	$.ajax({
    		url : url,
    		type: 'POST',
    		async :false,
    		data: {
    			appname: appname,
    			apptype: apptype,
    			ownername: ownername
    		},
    		success : function(data){
        		treeData = eval(data);
        		//$('#consoleInput').val(data);
        	}
    	});
    	
    	if (treeData == null) {
    		//alert("fetch file failed");
    	}
    	else {
    		$('#treeView').tree({
    			data : treeData,
    			loadFilter : function(data) {
    				var changeNode = function(node){
    					ret = "";
    					if(node!=undefined){
    						ret = '{';
        					ret += '"text":"' + node.name + '",';
        					ret += '"iconCls":"' + node.iconCls + '",';
        					ret += '"attributes":{'
        						+ '"project":"' + node.project + '",'
        						+ '"path":"' + node.path + '",'
        						+ '"type":"' + node.type + '",';
        					if(node.leaf) {
        						ret += '"leaf":true,';
        					}
        					ret += '},';
        					if(node.children != null){
        						ret += '"children":[';
        						for (c in node.children) {
        							ret += changeNode(node.children[c]) + ',';
        						}
        						ret += ']';
        					}
        					ret += '}';
    					}	
    					return ret;
    				};
    				var retTree = "[";
    				for (var i = 0, len = data.length; i < len - 1; i++){
    					retTree += changeNode(data[i]) + ',';
    				};
    				retTree += changeNode(data[data.length-1]) + ']';
    				return eval(retTree);
    			},
    			onClick : function(node) {
    				//alert(node.target);
    				if (Global.isOpenableFileType( node.attributes.type )) {
    					Global.project = node.attributes.project;
    					//alert(node.attributes.project + " " + node.attributes.path);
    					codePanel.addFile(node.text, node.attributes.path, node.attributes.project, null);
    				}
    			},
    			// 处理右键
    			onContextMenu : function(e, node) {
    				e.preventDefault();
    				$("#treeView").tree('select', node.target);
    				$("#rightMenu").menu("show", {
    					left: e.pageX,
    					top: e.pageY
    				})
    			}
    		});
    	}
    	this.treeView = $('#treeView');
    },
    
    refreshTree : function() {
    	var treeData = null;
    	//get tree json
    	var url = Global.url.refreshExplorer;
    	var success = function(data){
    		treeData = data;
    		//$('#consoleInput').val(data);
    	};
    	$.ajax({
    		url : url,
    		type: 'POST',
    		async :false,
    		data: {
    			appname: appname,
    			apptype: apptype,
    			ownername: ownername
    		},
    		success : function(data){
        		treeData = eval(data);
        	}
    	});
    	if (treeData == null) {

    	}
    	
    	$('#treeView').tree('loadData', treeData);
    	
    },

    onAddPackage: function() {
    	var explorerPanel = this;
    	var active = this.treeView.tree('getSelected');
    	var path = "";
    	if(active!=null){
    		var path = active.attributes.path;
    		if (active.attributes.leaf == true) {
        		var slash = path.lastIndexOf('/');
        		path = path.substring(0, slash+1);
        	}
        	if (active.attributes.type == 'project') {
        		path = '/';
        	}
    	}
    	
    	$.messager.prompt('New folder', 'Name', function(r){
    		if(r) {
    			var url = Global.url.addPackage;
    			var param = {
    					path : path+r,
    	    			appname: appname,
    	    			apptype: apptype,
    	    			ownername: ownername
    				};
    			var success = function(data){
					explorerPanel.refreshTree();
            		if(data=='success'){
            			consolePanel.append('info', 'Success', 'Creating new folder '+r);
            		}else{
            			consolePanel.append('info', 'Error', data);
            		}
				};
				consolePanel.append('info', 'Processing', 'Creating new folder...');
				$.post(url, param, success);
    		}
    	});	
    },

    onAddFile: function() {
    	var explorerPanel = this;
    	var active = this.treeView.tree('getSelected');
    	var path = "";
    	if(active!=null){
    		var path = active.attributes.path;
    		if (active.attributes.leaf == true) {
        		var slash = path.lastIndexOf('/');
        		path = path.substring(0, slash+1);
        	}
        	if (active.attributes.type == 'project') {
        		path = '/';
        	}
    	}

    	$.messager.prompt('New file', 'Name', function(r){
    		if(r) {
    			r = r.replace(/\s+/g, "");
    			var url = Global.url.addFile;
    			var param = {
    					path : path+r,
    	    			appname: appname,
    	    			apptype: apptype,
    	    			ownername: ownername
    				};
    			var success = function(data){
					explorerPanel.refreshTree();
            		if(data=='success'){
            			consolePanel.append('info', 'Success', 'Creating new file '+r);
            		}else{
            			consolePanel.append('info', 'Error', data);
            		}
				};
				consolePanel.append('info', 'Processing', 'Creating new file '+r);
				$.post(url, param, success);
    			
    		}
    	});	
 
    },

    onRename: function() {
    	var node = explorerPanel.treeView.tree('getSelected');
    	var type = node.attributes.type;
    	var path = node.attributes.path;
    	var project = node.attributes.project;
    	$.messager.prompt('Rename', 'Name', function(r){
    		if(r) {
    			var url = Global.url.renameFile;
    			var param = {
    					path : path,
    	    			appname: appname,
    	    			apptype: apptype,
    					name : r,
    					ownername: ownername
    				};
    			var success = function(data){
					explorerPanel.refreshTree();
            		if(data=='success'){
            			consolePanel.append('info', 'Success', 'Rename '+node.text+' to '+r);
            		}else{
            			consolePanel.append('info', 'Error', data);
            		}
				};
				consolePanel.append('info', 'Processing', 'Rename '+node.text+' to '+r);
				$.post(url, param, success);
    			
    		}
    	});	
    },

    onRemove: function() {
    	var node = explorerPanel.treeView.tree('getSelected');
    	var type = node.attributes.type;
    	var path = node.attributes.path;
    	var project = appname;
    	var name = node.text;
    	Global.onRemove(project, path, type, name);
    },
    
    onCommit: function() {
    	Global.onCommit();
    },
    
    currentPath: function(){
    	var explorerPanel = this;
    	var active = this.treeView.tree('getSelected');
    	var path = "";
    	if(active!=null){
    		var path = active.attributes.path;
    		if (active.attributes.leaf == true) {
        		var slash = path.lastIndexOf('/');
        		path = path.substring(0, slash+1);
        	}
        	if (active.attributes.type == 'project') {
        		path = '/';
        	}
    	}
    	return path;
    },
    
    search: function(node, path) {
    	if(node.attributes.path == path)
    		return node;
    	for(var i in node.children) {
    		var ret = this.search(node.children[i], path);
    		if(ret)
    			return ret;
    	}
    	return null;
    },
    
    //select node in Explorer linked with editor
    selectNodeByPath: function(path, project) {
    	var rootNode = $("#treeView").tree('getRoot');
    	var tobeSelected = this.search(rootNode, path);
    	if(tobeSelected) {
    		if(tobeSelected.target){
    			$("#treeView").tree('expandTo', tobeSelected.target);
        		$("#treeView").tree('select', tobeSelected.target);
        		$("#treeView").tree('scrollTo', tobeSelected.target);
    		}
    	}
    },
    
}