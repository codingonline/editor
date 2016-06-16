var PopCompleter = {
	getCompletions : function(editor, session, pos, prefix, callback) {
		var line = session.getLine(pos.row).substring (0, pos.column);
		var arr = line.split(" ");
		var chars;
		if (apptype == "javaweb")
			chars = ".";
		else if (apptype == "php")
			chars = "->";
		if (arr[arr.length - 1].lastIndexOf(chars) < 0) {
			if (apptype == "php") {
				var autoCompleteData = [];
				for (i in PHPMethods) {
					var method = PHPMethods[i];
					var completion = {
						meta : "function",
						caption : method,
						name : method,
						value : method,
						score : 4000 - i
					};
					autoCompleteData.push(completion);
				}
				return callback(null, autoCompleteData);
			}
			return callback(null, []);
		}

		var url = Global.url.completion;
		var path = Global.ideViewer.codePanel.getActiveTab().path;
		var lines = session.getLines(0, pos.row - 1);
		var cnt = 0;
		for ( var i in lines) {
			cnt += lines[i].length + 1;
		}
		cnt += pos.column;
		var param = {
			code : session.getValue(),
			position : cnt,
			path : path,
			appname : appname,
			apptype : apptype,
			username : username
		};

		function sortJson(x, y) {
			if (x.parent_cnt > y.parent_cnt)
				return 1;
			else if (x.parent_cnt < y.parent_cnt)
				return -1;
			else if (x.count < y.count)
				return 1;
			else if (x.count > y.count)
				return -1;
			else
				return (x.name > y.name) ? 1 : -1;
		}

		var success = function(data) {
			Global.completion = data;
			var json = data;
			var autoCompleteData = [];
			var score = 2000;
			json.variables.sort(sortJson);
			$.each(json.variables, function(i, item) {
				var meta = "variable";
				var caption, value;
				var isPub = true, isStatic = false, isFinal = false;
				$.each(item.modifiers, function(ii, iitem) {
					if (iitem == "private")
						isPub = false;
					else if (iitem == "static")
						isStatic = true;
					else if (iitem == "final")
						isFinal = true;
				});
				if (!isPub)
					return true;
				if (isStatic || isFinal) {
					meta += "(";
					if (isStatic)
						meta += "S";
					if (isStatic && isFinal)
						meta += ",";
					if (isFinal)
						meta += "F";
					meta += ")";
				}

				value = item.name;
				if (apptype == "javaweb") {
					caption = value;
					var type = item.type
							.substring(item.type.lastIndexOf(".") + 1);
					var classname = item.class.substring(item.class
							.lastIndexOf(".") + 1);
					caption += ":" + type + " -" + classname;
				} else if (apptype == "php") {
					caption = value + " -" + item.class;
				}

				var name = value;
				var completion = {
					meta : meta,
					caption : caption,
					name : name,
					value : value,
					score : score
				};
				autoCompleteData.push(completion);
				score--;
			});

			json.methods.sort(sortJson);
			$.each(json.methods, function(i, item) {
				var meta = "function";
				var completion;
				if (apptype == "javaweb") {
					if (item.return_value == null)
						return true;
				}
				var isPub = true, isStatic = false, isFinal = false;
				$.each(item.modifiers, function(ii, iitem) {
					if (iitem == "private")
						isPub = false;
					else if (iitem == "static")
						isStatic = true;
					else if (iitem == "final")
						isFinal = true;
				});
				if (!isPub)
					return true;
				if (isStatic || isFinal) {
					meta += "(";
					if (isStatic)
						meta += "S";
					if (isStatic && isFinal)
						meta += ",";
					if (isFinal)
						meta += "F";
					meta += ")";
				}
				if (apptype == "javaweb") {

					var value = item.name + "(";
					$.each(item.parameters, function(ii, iitem) {
						var type = iitem.type.substring(iitem.type
								.lastIndexOf(".") + 1);
						value += type + " " + iitem.name;
						if (ii != item.parameters.length - 1)
							value += ", ";
					});
					value += ")";

					var caption = value;
					var return_value = item.return_value
							.substring(item.return_value.lastIndexOf(".") + 1);
					var classname = item.class.substring(item.class
							.lastIndexOf(".") + 1);
					caption += ":" + return_value + " -" + classname;

					var name = value;
					completion = {
						meta : meta,
						caption : caption,
						name : name,
						value : value,
						comment : item.javadoc,
						common : item.common,
						class : item.class,
						count : item.count,
						score : score
					};
				} else if (apptype == "php") {
					var value = item.name + "(";
					$.each(item.formalParameters, function(ii, iitem) {
						value += iitem.name;
						if (ii != item.formalParameters.length - 1)
							value += ", ";
					});
					value += ")";
					var name = value;
					var caption = value + " -" + item.class;
					completion = {
						meta : meta,
						caption : caption,
						name : name,
						value : value,
						comment : item.comment,
						score : score
					};
				}
				autoCompleteData.push(completion);
				score--;
			});

			return callback(null, autoCompleteData);
		}

		$.ajax({
			type : 'POST',
			async : false,
			url : url,
			data : param,
			dataType : "jsonp",
			jsonp : "jsonpCallback",
			success : success
		});
	},

	showJavaDoc : function(event) {
		var editor = Global.ideViewer.codePanel.getActiveTab().editor;
		var popup = editor.completer.popup;
		if (event == "mousemove") {
			var row = popup.getHoveredRow();
		} else if (event == "setRow") {
			var row = popup.getRow();
		}
		var data = popup.getData(row);

		var popup_H = parseInt(popup.container.style.height);
		var popup_W = parseInt(popup.container.style.width);
		var popup_L = parseInt(popup.container.style.left);
		var popup_T = parseInt(popup.container.style.top);
		var height = 128;
		if (popup_H > height)
			height = popup_H;
		$('#commentWindow').window({
			width : 280,
			height : height,
			left : popup_L - 280,
			top : popup_T,
			title : 'Comment',
			modal : false,
			collapsible : false,
			minimizable : false,
			maximizable : false,
			closable : false,
			resizable : false,
			closed : true
		});
		if (data) {
			if (data.comment && data.comment != "") {
				$('#commentWindow').window('open');
				var comment = data.comment;
				comment = comment.substring(7, comment.length - 3);
				comment = comment.replace(new RegExp("\\*", "gm"), "<br />");
				$('#commentWindow').html(comment);
			} else {
				$('#commentWindow').window('close');
			}
		}
	},

	hideJavaDoc : function() {
		$('#commentWindow').window('close');
		var div = $("#console").find("div")[0];
		div.textContent = "";
	},

	recordUsage : function(data) {
		if (data.meta != "function" || !data.common)
			return;
		var param = {
			classname : data.class,
			method : data.name
		};
		$.ajax({
			type : 'POST',
			async : true,
			url : Global.url.completion,
			data : param
		});
	}
};
