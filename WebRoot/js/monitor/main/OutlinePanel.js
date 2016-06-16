//更改样式…………
outlinePanel = {		
    InitOutlinePanel : function() {
        $("#outlinepanel").attr({
            "region":'east',
            "title":'Outline',
            "collapsed":false,
            "split": true, 
        }).css({width:225});
        this.createTree();
        return this;
    }, 
    
    // treeView
    createTree : function() {
    	$('#outlinepanel').append($('<ul/>').attr({
    		'id':'outlineTreeView'
    	}));
    	this.treeView = $('#outlineTreeView');
    	//var treeData = [{name : "asdfasd"}];
    	//get tree json
    	//var url = Global.url.refreshExplorer;
    	//var success = function(data){
    	//	treeData = data;
    		//$('#consoleInput').val(data);
    	//};
    },
    
    refreshTree: function(data) {
    	var treeData = [];
    	//get tree json
    	var url = Global.url.refreshOutline;
    	var tab = Global.ideViewer.codePanel.getActiveTab();
    	var tabname = tab.tabName;
    	var success = function(data) {
			Global.outline = data;
			if (data == undefined) {
				$("#outlineTreeView").tree({
					data : []
				});
				return;
			}
			var session = tab.editor.session;
			var linecnt = session.getLength();
			var lines = session.getLines(0, linecnt);
			var charcnt = [];
			for ( var j in lines) {
				charcnt[j] = lines[j].length;
			}
			$.each(data.classes, function(k, json) {
				var outline = [];
				var classname = json.name;
				$.each(json.variables, function(i, item) {
					var visibility, isStatic = false, isFinal = false;
					$.each(item.modifiers, function(ii, iitem) {
						if (iitem == "static")
							isStatic = true;
						else if (iitem == "final")

							isFinal = true;
						else
							visibility = iitem;
					});
					var line = 0;
					var position = item.startPosition;
					for ( var j in charcnt) {
						position -= charcnt[j] + 1;
						if (position <= 0) {
							line = parseInt(j) + 1;
							break;
						}
					}
					var attributes = {
						type : "variable",
						line : line,
						visibility : visibility,
						isStatic : isStatic,
						isFinal : isFinal
					}
					var variable = {
						text : item.name
								+ " : "
								+ item.type.substring(item.type
										.lastIndexOf(".") + 1),
						iconCls : visibility + "_variable",
						attributes : attributes,
						leaf : true
					};
					outline.push(variable);
				});
				$.each(json.methods, function(i, item) {
					var isPub = true, isStatic = false, isFinal = false;
					$.each(item.modifiers, function(ii, iitem) {
						if (iitem == "static")
							isStatic = true;
						else if (iitem == "final")
							isFinal = true;
						else
							visibility = iitem;
					});
					var line = 0;
					var position = item.startPosition;
					for ( var j in charcnt) {
						position -= charcnt[j] + 1;
						if (position <= 0) {
							line = parseInt(j) + 1;
							break;
						}
					}
					var attributes = {
						type : "methods",
						line : line,
						visibility : visibility,
						isStatic : isStatic,
						isFinal : isFinal
					}
					var text = item.name + " (";
					$.each(item.parameters, function(ii, iitem) {
						var type = iitem.type.substring(iitem.type
								.lastIndexOf(".") + 1);
						text += type + " " + iitem.name;
						if (ii != item.parameters.length - 1)
							text += ", ";
					});
					text += ")";
					var return_value = item.return_value
							.substring(item.return_value.lastIndexOf(".") + 1);
					text += " : " + return_value;
					var method = {
						text : text,
						iconCls : visibility + "_function",
						attributes : attributes,
						leaf : true
					};
					outline.push(method);
				});
				var fathernode = {
					text : classname,
					iconCls : "class",
					leaf : false,
					children : outline,
				}
				treeData.push(fathernode);
			});
			$('#outlineTreeView').tree({
				data : treeData,
				onClick : function(node) {
					if (node.attributes && node.attributes.line)
						tab.goToLine(node.attributes.line);
				}
			});
		}
    	$.ajax({
    		url : url,
    		type: 'POST',
    		async :false,
    		data: {
    			appname : appname,
    			username : username,
    			path : tab.path
    		},
			dataType : "jsonp",
			jsonp : "jsonpCallback",
			success : success
		});
		/*
		$('#outlineTreeView').tree({
			data: data,
			loadFilter : function(data) {
				//alert("hwewrwe");
				//alert(data);
				var changeNode = function(node){
					ret = '{';
					ret += '"text":"' + node.name + '",';
					ret += '"iconCls":"' + node.iconCls + '",';
					ret += '"attributes":{'
							+ '"specifier":"' + node.specifier + '",'
							+ '"line":"' + node.line + '",'
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
						
					};
					ret += '}';
					return ret;
				};
				var retTree = "[";
				for (var i = 0, len = data.length; i < len - 1; i++){
					
					retTree += changeNode(data[i]) + ',';
				};
				retTree += changeNode(data[data.length-1]) + ']';
				//var retTree = changeNode(treeData);
				//$("#consoleOutput").val(retTree);
				return eval(retTree);
			},
			onClick : function(node) {	
					//alert(node.attributes.line);
					Global.ideViewer.codePanel.getActiveTab().goToLine(node.attributes.line);
			}
		});*/
	},

	removeTree : function() {
		$("#outlineTreeView").tree({
			data : []
		});
	},

	/**
	 * React to a ide attempting to be added
	 * @private
	 */
	onItemClick : function(view, record) {
		Global.ideViewer.codePanel.getActiveTab().goToLine(record.get('line'));
	},

	// Inherit docs
	onDestroy : function() {
		this.callParent(arguments);
		this.menu.destroy();
	}
};