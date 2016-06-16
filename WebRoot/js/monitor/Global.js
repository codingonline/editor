Global = {
	outline : '',
	ideViewer : '',
	explorerPanel : '',
	consoleInputLength : 0,
	compileData : '',
	project : '',
	projectPath : '',
	copyPath : '',
	fileTypes : [ 'py', 'cpp', 'java', 'h', 'txt', 'dat', 'conf', 'html',
			'htm', 'js', 'css', 'xml', 'php', 'md', 'log', 'wsgi', 'yaml',
			'jsp', 'properties', 'gitignore' ],
	url : {
		refreshExplorer : 'packageExplorer?operation=Fs2Json&path=',
		loadFile : 'packageExplorer?operation=retrieveFile',
		closeFile : 'packageExplorer?operation=closeFile',
		logout : 'packageExplorer?operation=logout',
		refreshOutline : "http://123.57.2.1:7000/outline",
		startReplay : 'monitor?operation=startReplay',
	},

	isOpenableFileType : function(type) {
		for (i in Global.fileTypes) {
			if (type == Global.fileTypes[i])
				return true;
		}
		return false;
	},

	refreshExplorer : function() {
		this.explorerPanel.refreshTree();
	},

	onDownloadProject : function() {
		$("#downloadProjectForm").submit();
	},

	onOpenProject : function() {
		document.getElementById('openProjectWin').click();
	},

	getCurrentTab : function() {
		return Global.ideViewer.codePanel.getActiveTab();
	},

	onClearConsole : function() {
		var consoleInput = $("#consoleInput");
		consoleInput.val('');
		$("#consoleOutput").val('');
		$("#problemList").datagrid({
			data : []
		});
		Global.consoleInputLength = 0;
	},

	getCurrentProject : function() {
		return Global.ideViewer.codePanel.getActiveTab().project;
	},

	getCurrentTab : function() {
		return Global.ideViewer.codePanel.getActiveTab();
	},

	replayData : '',
	startindex : 0,
	replayProject : '',
	replayPath : '',

	onStartReplay : function() {
		var tab = Global.ideViewer.codePanel.getActiveTab();
		if (tab == null)
			return;
		Global.replayProject = appname;
		Global.replayPath = tab.path;
		var session = tab.editor.session;
		var slider = $('#ReplaySlider');
		Global.isPaused = false;
		if (Global.replayData == "") {
			Global.startindex = 0;
			param = {
				username : username,
				appname : appname,
				path : tab.path,
			};
			url = Global.url.startReplay;
			success = function(data) {
				var json = JSON.parse(data);
				if (json[0].length == 0) {
					alert('', "There is no monitor data.");
					return;
				}
				Global.replayData = json[0];
				session.setValue("");
				closetmp = new Array();
				var sTime = (new Date(Global.replayData[0].time)).getTime();
				var eTime = (new Date(
						Global.replayData[Global.replayData.length - 1].time))
						.getTime();
				slider.slider({
					'min' : sTime / 50,
					'max' : eTime / 50,
					'value' : sTime / 50
				});
				Global.replay(session, i, sTime);
			};
			$.post(url, param, success);
		} else {
			var sTime = (new Date(Global.replayData[Global.startindex].time))
					.getTime();
			Global.replay(session, Global.startindex, sTime);
			// console.log(Global.startindex);
		}
	},

	intervalId : '',
	replay : function(session, i, sTime) {
		var slider = $('#ReplaySlider');
		var isDetectCopy = true;
		Global.intervalId = setInterval(function() {
			while (slider.slider('getValue') == sTime / 50) {
				while (Global.replayData[i].operation == "save"
						|| Global.replayData[i].operation == "close") {
					isDetectCopy = false;
					if (Global.replayData[i].operation == "close") {
						closetmp[i] = session.getValue();
						if (i < Global.replayData.length - 1)
							session.setValue("");
					}
					i++;
					if (i == Global.replayData.length) {
						Global.onStopReplay();
						return;
					}
				}
				var o = Global.replayData[i].content;
				Global.dealData(session, o, isDetectCopy);
				i++;
				if (i == Global.replayData.length) {
					Global.onStopReplay();
					return;
				}
				Global.startindex = i;
				sTime = (new Date(Global.replayData[i].time)).getTime();
				isDetectCopy = true;
			}
			if (Global.isPaused)
				clearInterval(Global.intervalId);
			slider.slider('setValue', slider.slider('getValue') + 1);
		}, 50);
	},

	dealData : function(session, o, isDetectCopy) {
		var Range = ace.require('ace/range').Range;
		if (o.action == "insertText") {
			session.insert(o.range.start, o.text);
		} else if (o.action == "insertLines") {
			var linestart = o.range.start;
			session.insert(linestart, '\n');
			for (j in o.lines) {
				session.insert(linestart, o.lines[j]);
				linestart.row++;
				if (j < o.lines.length - 1)
					session.insert(linestart, '\n');
			}
		} else if (o.action == "removeText" || o.action == "removeLines") {
			var range = new Range(o.range.start.row, o.range.start.column,
					o.range.end.row, o.range.end.column);
			session.remove(range);
		}
	},

	closetmp : '',

	onChangeSlider : function(value, min) {
		if (Global.replayData == "")
			return;
		clearInterval(Global.intervalId);
		var tab = Global.ideViewer.codePanel.getActiveTab();
		if (tab == null)
			return;
		var session = tab.editor.session;
		var i = Global.startindex;
		var sTime = (new Date(Global.replayData[i].time)).getTime();
		var isDetectCopy = true;
		var lastTime = min * 50;
		if (i > 0)
			lastTime = (new Date(Global.replayData[i - 1].time)).getTime();
		while (value < lastTime / 50) {
			if (Global.replayData[i].operation == "close") {
				session.setValue(closetmp[i]);
			}
			var o = Global.replayData[i].content;
			var Range = ace.require('ace/range').Range;
			if (o.action == "removeText") {
				session.insert(o.range.start, o.text);
			} else if (o.action == "removeLines") {
				var linestart = o.range.start;
				session.insert(linestart, '\n');
				for (j in o.lines) {
					session.insert(linestart, o.lines[j]);
					linestart.row++;
					if (j < o.lines.length - 1)
						session.insert(linestart, '\n');
				}
			} else if (o.action == "insertText" || o.action == "insertLines") {
				var range = new Range(o.range.start.row, o.range.start.column,
						o.range.end.row, o.range.end.column);
				session.remove(range);
			}
			Global.startindex = i;
			i--;
			lastTime = (new Date(Global.replayData[i].time)).getTime();
		}
		while (value >= sTime / 50) {
			while (Global.replayData[i].operation == "save"
					|| Global.replayData[i].operation == "close") {
				isDetectCopy = false;
				if (Global.replayData[i].operation == "close") {
					closetmp[i] = session.getValue();
					if (i < Global.replayData.length - 1)
						session.setValue("");
				}
				i++;
				if (i == Global.replayData.length) {
					Global.onStopReplay();
					return;
				}
			}
			var o = Global.replayData[i].content;
			Global.dealData(session, o, isDetectCopy);
			i++;
			if (i == Global.replayData.length) {
				Global.onStopReplay();
				return;
			}
			Global.startindex = i;
			sTime = (new Date(Global.replayData[i].time)).getTime();
			isDetectCopy = true;
		}
		sTime = (new Date(Global.replayData[Global.startindex].time)).getTime();
		Global.replay(session, Global.startindex, sTime);
	},

	isPaused : false,

	onPauseReplay : function() {
		Global.isPaused = true;
	},

	onStopReplay : function() {
		clearInterval(Global.intervalId);
		var slider = $('#ReplaySlider');
		slider.slider('setValue', slider.slider('options').min);
		Global.replayData = '';
		Global.closetmp = '';
		Global.replayProject = '';
		Global.replayPath = '';
	},

};
