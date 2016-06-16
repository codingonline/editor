fileSearch = {
		// 0 : in files, 1 : in project
		range : 0,
		isReplace : false,
		
		afterInit : function() {
			var searchboxhtml = '\
				<table id=fileSearchTable>\
				<tr>\
				<td colspan="2"><input id="search_field" placeholder="Search for" spellcheck="false"></input></td>\
				</tr>\
				<tr>\
				<td  colspan="2"><input id="replace_field" placeholder="Replace with" spellcheck="false"></input></td>\
				</tr>\
				<tr>\
				<td><input type="radio" name="fileSearchRange" value="0" checked="true"> In open files </input></td>\
				<td><input type="radio" name="fileSearchRange" value="1">In project</input></td>\
				</tr>\
				<tr>\
				<td colspan="2"><input type="checkbox" id="fileSearchCase"> Case Sensitive </input></td>\
				</tr>\
				<!--\
				<tr>\
				<td colspan="2"><input type="checkbox" id="fileSearchRegexp"> Regular expressions </input></td>\
				<tr>\
				-->\
				<td colspan="2"><input type="checkbox" id="fileSearchReplace"> Replace </input></td>\
				</tr>\
				</table>'.replace(/>\s+/g, ">");


			var searchDialog = {
					id: 'fileSearch',
					title: "Search and Replace in Files",
					handlers: [fileSearch.ApplySearch],
					table: searchboxhtml
			};
			
			dialog.addDialog(searchDialog);
			
			
			
			$("input[name=fileSearchRange]").click(function() {
				fileSearch.range = $("input[name='fileSearchRange']:checked").val();
				if (fileSearch.range == 1) {
					$("input[id='fileSearchReplace']").attr('checked', false).attr('disabled', true);
				}
				if (fileSearch.range == 0) {
					$("input[id='fileSearchReplace']").attr('disabled', false);
				}
			});
			$("input[id='fileSearchReplace']").click(function() {
				fileSearch.isReplace = $("input[id='fileSearchReplace']").is(':checked');
			});
		},

		ApplySearch :  {
			text: 'Apply',
			handler: function() {
			fileSearch.isReplace = $("input[id='fileSearchReplace']").is(':checked');
			fileSearch.range = $("input[name='fileSearchRange']:checked").val();
			if (fileSearch.range == 0 && fileSearch.isReplace == false)
				fileSearch.FindAll();
			else if (fileSearch.isReplace == true)
				fileSearch.ReplaceAll();
			else if (fileSearch.range == 1) 
				fileSearch.FindInProject();
			},
		},
		
		FindAll : function() {
			var openTabs = codePanel.openTabs;
			consolePanel.clearSearchResult();
			
			var str = $("#search_field").val();
			var isCaseSens = $("input[id='fileSearchCase']").is(':checked');
			var isRegexp = $("input[id='fileSearchRegexp']").is(':checked');
			var option = "g";
			
			if (isCaseSens)	option += 'i';
			var patt = new RegExp(str, option);
			
			var findRes = [];
			for (i in openTabs) {
				var tab = openTabs[i];
				//var session = tab.editor.getSession();
				var editor = tab.editor;
				var content = editor.getValue();
				var lines = content.split('\n');

				//editor.removeSelectionMarker();
				var fileResult = {};
				var attributes = {};
				var children = [];
				if (lines.length > 0) {
					fileResult = {};
					fileResult.text = tab.fullpath;
					attributes.type = "filename";
					attributes.project = tab.project;
					attributes.path = tab.path;
					attributes.title = tab.tabName;
					fileResult.attributes = attributes;
					cnt = 0;
					for (j in lines) {
						cnt++;
						if (lines[j].indexOf(str) == -1)
							continue;
						var resultLine = {};
						resultLine.attributes = {};
						resultLine.attributes.type = "line";
						resultLine.attributes.project = tab.project;
						resultLine.attributes.path = tab.path;
						resultLine.attributes.title = tab.tabName;
						resultLine.attributes.line = cnt;
						//resultLine.attributes.content = editor.getSession().getLine(ranges[j].start.row);
						resultLine.text = "Line " + resultLine.attributes.line + " : " +  editor.getSession().getLine(cnt-1);
						children.push(resultLine);
						//consolePanel.appendSearchResult(tab.path, ranges[j].start.row+1, editor.getSession().getLine(ranges[j].start.row));
					}
					if (children.length > 0) {
						fileResult.children = children;
						findRes.push(fileResult);
					}
				}
			};
			//alert(JSON.stringify(findRes));
			consolePanel.append('console', "Search", "Success");
			consolePanel.showSearchResult(findRes);
			
		},
		
		ReplaceAll : function() {
			var openTabs = codePanel.openTabs;
			consolePanel.clearSearchResult();
			var str = $("#search_field").val();
			var replaceWith = $("#replace_field").val();
			var findRes = [];
			for (i in openTabs) {
				var tab = openTabs[i];
				var session = tab.editor.getSession();
				var editor = tab.editor;
				editor.findAll(str);
				editor.replaceAll(replaceWith);

			};
			//alert(findRes);
			consolePanel.append('console','Replace All', 'All "' + str + '" were replaced');

		},
		
		FindInProject : function() {
			var currentTab = Global.getCurrentTab();
			
			if (currentTab == null) {
				alert("Please choose a file");
				return;
			}
			consolePanel.clearSearchResult();
			var str = $("#search_field").val();
			var isCaseSens = $("input[id='fileSearchCase']").is(':checked');
			var isRegexp = $("input[id='fileSearchRegexp']").is(':checked');
			//alert(isCaseSens + " " + isRegexp);
			
			if (str == null || str == "")
				return;
			
			var url = Global.url.findInProject;
			var param = {
					key : str,
					appname: appname,
					apptype: apptype,
					ownername: ownername,
					isCaseSens: isCaseSens,
					isRegexp: isRegexp
			}
			var success = function(data) {
				//alert(data);
				var obj = eval('('+data+')');
				//alert(obj);
				
				consolePanel.append('console','Search', "Success");
				consolePanel.showSearchResult(eval('('+data+')'));
			}
			$.post(url, param, success);
		}
}

var SearchBox = function() {

	/*
	var searchDialog = $('<div/>').attr({
		'id' : 'SearchDialog',
	}).css({
		'margin-left':'auto',
		'margin-right':'auto'
	});
	var searchForm = $('<form/>').attr({
		'id' : 'SearchForm',
		'method' : 'post'
	}).css({
		'margin-left':'auto',
		'margin-right':'auto'
	});

	// 构造操作
	toolBar.append(searchDialog);
	searchDialog.append(searchForm);
	searchForm.html(searchboxhtml);
	searchDialog.dialog({
		title : 'Search and Replace',
		cache : false,
	});
	searchDialog.dialog('close');
	 */

	// 函数
	fileSearch.InitElement = function() {
		fileSearch.searchBox = $("#search_form");
		fileSearch.replaceBox = $("#replace_form");
		fileSearch.searchOptions = $("#search_options");
		fileSearch.regExpOption = $("#regexp");
		fileSearch.caseSensitiveOption = $("#casesensitive");
		fileSearch.wholeWordOption = $("#whileword");
		fileSearch.searchInput = $("#search_field");
		fileSearch.replaceInput = $("#replace_field");

		fileSearch.findInProjectBtn = $("#searchbtn_project");
		fileSearch.findAllBtn = $("#searchbtn_all");
		fileSearch.replaceBtn = $("#replacebtn_all");
	};
	fileSearch.ApplySearch = function() {
		return null;
	}
	fileSearch.Open = function() {
		searchDialog.dialog('open');
	};

	fileSearch.Close = function() {
		searchDialog.dialog('close');
	};
	fileSearch.FindAll = function() {
		var openTabs = codePanel.openTabs;
		consolePanel.clearSearchResult();
		var str = $("#search_field").val();
		var findRes = [];
		for (i in openTabs) {
			var tab = openTabs[i];
			var session = tab.editor.getSession();
			var editor = tab.editor;
			editor.findAll(str);
			var fileResult;
			var attributes = {};
			var children = [];
			var ranges = editor.getSelection().getAllRanges();
			if (ranges.length > 0) {
				fileResult = {};
				fileResult.text = tab.fullpath;
				attributes.type = "filename";
				attributes.project = tab.project;
				attributes.path = tab.path;
				attributes.title = tab.tabName;
				fileResult.attributes = attributes;

				for (j in ranges) {
					var resultLine = {};
					resultLine.attributes = {};
					resultLine.attributes.type = "line";
					resultLine.attributes.project = tab.project;
					resultLine.attributes.path = tab.path;
					resultLine.attributes.title = tab.tabName;
					resultLine.attributes.line = ranges[j].start.row+1;
					//resultLine.attributes.content = editor.getSession().getLine(ranges[j].start.row);
					resultLine.text = "Line " + resultLine.attributes.line + " : " +  editor.getSession().getLine(ranges[j].start.row);;
					children.push(resultLine);
					//consolePanel.appendSearchResult(tab.path, ranges[j].start.row+1, editor.getSession().getLine(ranges[j].start.row));
				}
				fileResult.children = children;

				findRes.push(fileResult);
			}
		};
		//alert(findRes);
		consolePanel.showSearchResult(findRes);
		consolePanel.appendInfo('Success','Find All');

	};

	fileSearch.ReplaceAll = function() {
		var openTabs = codePanel.openTabs;
		consolePanel.clearSearchResult();
		var str = $("#search_field").val();
		var replaceWith = $("#replace_field").val();
		var findRes = [];
		for (i in openTabs) {
			var tab = openTabs[i];
			var session = tab.editor.getSession();
			var editor = tab.editor;
			editor.findAll(str);
			editor.replaceAll(replaceWith);

		};
		//alert(findRes);
		consolePanel.appendInfo('Success','Replace All: All "' + str + '" were replaced');

	};

	fileSearch.FindInProject = function() {
		var currentTab = Global.getCurrentTab();
		if (currentTab == null) {
			alert("Please choose a file");
			return;
		}
		consolePanel.clearSearchResult();
		var str = $("#search_field").val();
		if (str == null || str == "")
			return;
		var url = Global.url.findInProject;
		var param = {
				key : str,
				appname: appname,
				apptype: apptype,
				ownername: ownername
		}
		var success = function(data) {
			//alert(data);
			var obj = eval('('+data+')');
			//alert(obj);
			consolePanel.showSearchResult(eval('('+data+')'));
			consolePanel.appendInfo('Success','Find In Project');
		}
		$.post(url, param, success);
	};

	fileSearch.BindEvent = function() {

		fileSearch.replaceBtn.bind('click', fileSearch.ReplaceAll);
		//fileSearch.findAllBtn.bind('click',fileSearch.FindAll);
		fileSearch.findAllBtn.bind('click',function(){
			alert($("#treeView").tree('getRoot'));
		});
		fileSearch.findInProjectBtn.bind('click',fileSearch.FindInProject);
	}


	fileSearch.InitElement();
	fileSearch.BindEvent();
}
