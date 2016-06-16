Global = {
	outline : '',
	operations : Array(),
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
		saveFile : 'packageExplorer?operation=updateFile',
		addProject : 'packageExplorer?operation=createProject',

		uploadFile : 'packageExplorer?operation=uploadFile',
		downloadProject : 'packageExplorer?operation=downloadProject',
		addFile : 'packageExplorer?operation=createFile',
		renameFile : 'packageExplorer?operation=renameFile',
		moveFile : 'packageExplorer?operation=moveFile',
		copyFile : 'packageExplorer?operation=copyFile',
		addPackage : 'packageExplorer?operation=createPackage',
		removeFile : 'packageExplorer?operation=deleteFile',
		removePackage : 'packageExplorer?operation=deletePackage',
		removeProject : 'packageExplorer?operation=removeProject',
		logout : 'packageExplorer?operation=logout',

		importFromBAE : 'deploy?operation=importFromBAE',
		importFromSAE : 'deploy?operation=importFromSAE',
		deploy : 'deploy?operation=deploy',
		run : 'deploy?operation=run',
		log : 'deploy?operation=log',
		build : 'deploy?operation=build',
		debug : 'deploy?operation=debug',
		stopdebug : 'deploy?operation=stopdebug',
		singleRun : 'deploy?operation=singleRun',

		buildws : 'ws://123.57.2.1:3000/building',
		editws : 'ws://123.57.2.1:3001/file/',
		authws : 'ws://123.57.2.1:3001/auth/',
		// editws: 'ws://127.0.0.1:8080/rwsocket/file/',
		// authws: 'ws://127.0.0.1:8080/rwsocket/auth/',

		share : 'share?operation=genShareUrl',
		saveApp : 'share?operation=saveApp',
		addDeveloper : 'share?operation=addDeveloper',
		delDeveloper : 'share?operation=delDeveloper',

		getDevelopers : 'rwfileauth?operation=getDevelopers',
		grant : 'rwfileauth?operation=grant',
		revoke : 'rwfileauth?operation=revoke',

		findInProject : 'fileSearch?operation=findInProject',

		preferences : "window?operation=ace_setting",

		declaration : "http://123.57.32.46:8080/pop2016-completion/declaration",

		completion : "http://123.57.2.1:7000/completion",
		refreshOutline : "http://123.57.2.1:7000/outline",
		// completion :
		// "http://182.92.236.173:8080/pop2016-completion/completion",
		// refreshOutline :
		// "http://182.92.236.173:8080/pop2016-completion/outline",

		findbugs : "http://123.57.2.1:2001/findbugs",
		checkstyle : "http://123.57.2.1:2002/checkstyle",

		// TenxCloud:
		tenxCloud : "tenxcloud?",
		// IaaS
		deployToIaaS : "packageExplorer?operation=deployToIaaS",

		startTerminal : "http://123.57.2.1:9621/startTerminal",

		saveUserData : 'monitor?operation=saveData',
		setSaveData : 'monitor?operation=setSaveData'
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

	onSaveFile : function() {
		if (Global.ideViewer.codePanel.getActiveTab() != null) {
			var url = Global.url.saveFile;
			var path = Global.ideViewer.codePanel.getActiveTab().path;
			if (Global.isSaveUserData) {
				var time = new Date().getTime();
				Global.pushUserData('{"data":{},"path":"' + path + '","time":"'
						+ time + '","operation":"save"}');
			}
			var param = {
				path : path,
				code : Global.ideViewer.codePanel.getActiveTab().editor
						.getSession().getValue(),
				appname : appname,
				apptype : apptype,
				ownername : ownername
			};
			function success(data) {
				if (data == "success") {
					if (!Global.ideViewer.codePanel.getActiveTab().isSaved) {
						var tab = Global.ideViewer.codePanel.getActiveTab();
						tab.isSaved = true;
						$("#codearea").tabs('update', {
							tab : tab.editTab,
							type : 'header',
							options : {
								title : tab.tabName
							}
						});
						consolePanel.append('info', 'Success', 'File ' + path
								+ ' saved');
						tab.refreshOutline();
					}
				} else {
					consolePanel.append('info', 'Error', data);
				}

			}
			consolePanel.append('info', 'Processing', 'Saving changes...');
			if (write == "true") {
				$.post(url, param, success);
			} else {
				consolePanel.append('info', 'Success',
						'This project is read only for you')
			}

		} else {
			if (Global.ideViewer.codePanel == null) {
				// alert("failed");
			}
		}
	},

	saveFileTab : function(tab) {
		var url = Global.url.saveFile;
		var path = tab.path;
		var param = {
			path : path,
			code : tab.editor.getSession().getValue(),
			appname : appname,
			apptype : apptype,
			ownername : ownername
		};
		function success(data) {
			if (data == "success") {
				if (!tab.isSaved) {
					tab.isSaved = true;
					$("#codearea").tabs('update', {
						tab : tab.editTab,
						type : 'header',
						options : {
							title : tab.tabName
						}
					});
					consolePanel.append('info', 'Success', 'File ' + path
							+ ' saved');
					tab.refreshOutline();
				}
			} else {
				consolePanel.append('info', 'Error', data);
			}
		}
		consolePanel.append('info', 'Processing', 'Saving changes...');
		if (write == "true") {
			$.post(url, param, success);
		} else {
			consolePanel.append('info', 'Success',
					'This project is read only for you')
		}
	},

	onSaveAll : function() {
		var tabs = codePanel.openTabs;
		for (i in tabs) {
			if (!tabs[i].isSaved) {
				Global.saveFileTab(tabs[i]);
			}
		}
	},

	onUndo : function() {
		var tab = Global.ideViewer.codePanel.getActiveTab();
		tab.editor.undo();
	},

	onRedo : function() {
		var tab = Global.ideViewer.codePanel.getActiveTab();
		tab.editor.redo();
	},

	getCurrentTab : function() {
		return Global.ideViewer.codePanel.getActiveTab();
	},

	onRunProject : function() {
		Global.onSaveAll();
		var url = Global.url.run;
		var param = {
			appid : appid,
			user : username,
		};
		if (apptype == "javaweb") {
			param["mode"] = "compile";
		}
		var success = function(data, status) {
			console.log(data);
			var d = JSON.parse(data);
			if (apptype == "cpp" || apptype == "javaweb") {
				consolePanel.wsurl = "ws://" + d.domain + ':' + d.wsport;
			} else {
				consolePanel.weburl = "http://" + d.domain + ':' + d.port;
			}
			if (d.code != 0) {
				consolePanel.append('info', 'Error', d.msg);
			} else {
				if (apptype == "cpp" || apptype == "javaweb") {
					// 需要编译的项目，连接websocket，如果之前存在连接，则断开重连；
					if (consolePanel.ws != null) {
						consolePanel.ws.close();
						consolePanel.ws = null;
						stdStream.connect_times = 0;
					}
					stdStream.open();
					$("#consolepanel").tabs('select', "console");
				} else {
					consolePanel.append('info', 'Success',
							'Project is running at <a href=http://' + d.domain
									+ ':' + d.port + ' target="_blank">'
									+ 'http://' + d.domain + ':' + d.port
									+ '</a>');
				}
				terminal.dockerid = d.dockerid;
				terminal.port = d.port;
				$("#consolepanel").tabs('enableTab', 'terminal');
			}
		}
		consolePanel.append('info', 'Processing',
				'Project is ready for running...');
		$.get(url, param, success);
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

	onAddPackage : function() {
		Global.explorerPanel.onAddPackage();
	},

	onAddFile : function() {
		Global.explorerPanel.onAddFile();
	},

	onRenameFile : function() {
		Global.explorerPanel.onRename();
	},

	onRemove : function(project, path, type, name) {
		var url = "";
		if (type == 'project') {
			url = Global.url.removeProject;
		} else if (type == 'package') {
			url = Global.url.removePackage;
		} else {
			url = Global.url.removeFile;
		}
		var param = {
			appname : appname,
			apptype : apptype,
			ownername : ownername,
			path : path
		};
		var success = function(data) {
			if (data == "success") {
				if (type != 'project') {
					explorerPanel.refreshTree();
					consolePanel.append('info', 'Success', name
							+ ' already removed');
					if (Global.isOpenableFileType(type)) {
						for (tab in codePanel.openTabs) {
							if (name == tab.tabName
									|| name + '*' == tab.tabName)
								codePanel.openTabs.pop(tab);
							var codearea = $("#codearea");
							codearea.tabs('close', name);
						}
					}
				} else {
					window.location.reload();
				}
			} else {
				consolePanel.append('info', 'Error', data);
			}
		};
		$.messager.confirm('Delete', 'Confirm delete ' + path + '?',
				function(r) {
					if (r) {
						consolePanel.append('info', 'Processing', 'Remove '
								+ name);
						$.post(url, param, success);
					}
				});
	},

	onShare : function() {
		consolePanel.append('info', 'Processing', 'Generate share url')
		$
				.post(
						Global.url.share,
						{
							appname : appname
						},
						function(data, status) {
							consolePanel
									.append(
											'info',
											'Success',
											'share url: <span>'
													+ window.location.host
													+ data
													+ '</span>, click <a href="#" class="copy">here</a> to copy to clipboard');
							$(".copy").zclip({
								path : "js/jquery.zclip/ZeroClipboard.swf",
								copy : function() {
									return $(this).prev().text();
								}
							});
						});
	},

	onDebug : function() {
		// set breakpoints -> start
		// ##TEST##
		// var breakpoints = '[{"debugtest.DebugTest": "13"},
		// {"debugtest.DebugTest": "15"}]';
		breakpointsJson = "[";
		var tabs = Global.ideViewer.codePanel.getCodeTabs();
		for ( var id in tabs) {
			var tab = tabs[id];
			var file = tab.path;
			if (file.search(".java") != -1) {
				var className = file.substr(14);
				className = className.substring(0, className.search(".java"))
						.replace(/\//, ".");
				var breakpoints = tab.editor.getSession().getBreakpoints();
				for ( var i = 0; i < breakpoints.length; i++) {
					if (breakpoints[i]) {
						breakpointsJson += '{"' + className + '":'
								+ (parseInt(i) + 1) + '},';
					}
				}
			}
		}
		breakpointsJson += "]";

		if (breakpointsJson == '[]') {
			jQuery.messager.alert('Warning',
					'<h3>Please set your breakpoints first !</h3>');
			return;
		}
		// set breakpoints -> end

		// var watchExpressions = "[a,b,c]";
		var watchExpressions = "[]";

		$("#TerminateButton").linkbutton('enable').bind('click',
				Global.onTerminate);
		$("#ResumeButton").linkbutton('enable').bind('click', Global.onResume);
		$("#RunToLineButton").linkbutton('enable').bind('click',
				Global.onRunToLine);
		$("#StepOverButton").linkbutton('enable').bind('click',
				Global.onStepOver);
		$("#StepIntoButton").linkbutton('enable').bind('click',
				Global.onStepInto);
		$("#DebugButton").linkbutton('disable');
		// dwr.engine.setActiveReverseAjax(true);
		// dwr.engine.setNotifyServerOnPageUnload(true);

		consolePanel.append('info', 'Debug',
				'Connecting to the remote Tomcat, please wait for a moment...');

		// DwrDebugIO.debugCmd('start', appname,
		// '{"breakpoints":'+breakpointsJson+',
		// "watchExpressions":'+watchExpressions+'}', appid);
		var reqDebugUrl = Global.url.debug;
		var param = {
			appid : appid,
			appname : appname
		};
		$.post(reqDebugUrl, param, function(data) {
			var obj = JSON.parse(data);
			if (obj.code == 0) {
				var args = '{"breakpoints":' + breakpointsJson
						+ ', "watchExpressions":' + watchExpressions + '}';
				debugDockerId = obj.dockerid;
				// connect to debug websocket
				var wsAddr = 'ws://' + obj.domain + ':' + obj.port + '/debug/'
						+ ownername + '/' + appname;
				console.log(wsAddr);
				debugws = new WebSocket(wsAddr);
				debugws.onerror = function(event) {
					consolePanel.append('info', 'Debug',
							'Project debug connection error.');
				};
				debugws.onopen = function(event) {
					console.log("Debug connection opened.");
					debugws.send('start##' + args);
				};
				debugws.onmessage = function(event) {
					var args = event.data.split("##");
					if (args[0] == "JDBStartOver") {
						var runUrl = 'http://' + obj.domain + ':'
								+ obj.debugport + '/';
						jQuery.messager.alert("DebugInfo",
								"Please visit here to start debug: <a href=\""
										+ runUrl + "\" target=\"_blank\">"
										+ runUrl + "</a>");
					} else if (args[0] == "receiveDebugMessages") {
						consolePanel.append('info', 'Debug', args[1]);
					} else if (args[0] == "receiveAlertMsg") {
						jQuery.messager.alert(args[1], args[2]);
					} else if (args[0] == "contToNoSuspend") {
						Global.ideViewer.codePanel.getActiveTab()
								.removeMarker();
						$("#ResumeButton").linkbutton('disable');
						$("#StepOverButton").linkbutton('disable');
						$("#StepIntoButton").linkbutton('disable');
					} else if (args[0] == "terminate") {
						Global.ideViewer.codePanel.getActiveTab()
								.removeMarker();
						$("#DebugButton").linkbutton('enable');
						$("#StepOverButton").linkbutton('disable');
						$("#StepIntoButton").linkbutton('disable');
						$("#ResumeButton").linkbutton('disable');
						$("#TerminateButton").linkbutton('disable');
						Global.isDebuging = false;
						Global.isRunning = false;
					} else if (args[0] == "currentStopLine") {
						var project = args[1];
						var path = args[2];
						var line = args[3];
						var array = path.split('/');
						var title = array[array.length - 1];
						Global.ideViewer.onFileSelect(title, path, project,
								function() {
									Global.ideViewer.codePanel.getActiveTab()
											.highlightDebugLine(line);
								});
						$("#TerminateButton").linkbutton('enable');
						$("#ResumeButton").linkbutton('enable');
						$("#StepOverButton").linkbutton('enable');
						$("#StepIntoButton").linkbutton('enable');
					}
				};
				debugws.onclose = function(event) {
					consolePanel.append('info', 'Debug',
							'Debug connection closed.');
				}
			} else {
				consolePanel.append('info', 'Debug',
						'Debug connection requset failure. ErrorCode = '
								+ obj.code);
			}
		});

	},
	onStepInto : function() {
		console.log("step into");
		debugws.send('step');
	},
	onStepOver : function() {
		console.log("step over");
		debugws.send('next');
	},
	onResume : function() {
		console.log("resume");
		debugws.send('resume');
	},
	onTerminate : function() {
		console.log("terminate");
		debugws.send('stop');
		// request to clean dockerid
		var reqStopDebugUrl = Global.url.stopdebug;
		var param = {
			dockerid : debugDockerId
		};
		$.post(reqStopDebugUrl, param, function(data) {

		});

		$("#TerminateButton").linkbutton('disable');
		$("#ResumeButton").linkbutton('disable');
		$("#RunToLineButton").linkbutton('disable');
		$("#StepOverButton").linkbutton('disable');
		$("#StepIntoButton").linkbutton('disable');
		$("#DebugButton").linkbutton('enable');
		consolePanel.append('info', 'Debug', 'Debug process terminated');
	},

	getCurrentProject : function() {
		return Global.ideViewer.codePanel.getActiveTab().project;
	},

	getCurrentTab : function() {
		return Global.ideViewer.codePanel.getActiveTab();
	},

	breakpointInSourceFile : function(project, path, row, isAdd, session) {
		if (Global.isDebuging) {
			if (isAdd) {
				DwrDebugIO.runningCmd('addBreakpoint', project, 'src' + path
						+ ':' + (parseInt(row) + 1), {
					callback : function(str) {
						if (str == "true") {
							session.setBreakpoint(row);
						} else {
							alert('Please set breakpoints.');
						}
					}
				});
			} else {
				DwrDebugIO.runningCmd('removeBreakpoint', project, 'src' + path
						+ ':' + (parseInt(row) + 1), {
					callback : function(str) {
						if (str == "true") {
							session.clearBreakpoint(row);
						} else {
							alert('Please set breakpoints.');
						}
					}
				});
			}
		} else {
			if (isAdd)
				session.setBreakpoint(row);
			else
				session.clearBreakpoint(row);
		}
	},

	onDeployToIaaS : function() {
		var url = Global.url.deployToIaaS;

		var param = {
			usernameIaaS : "123",
			passwordIaaS : "456",
			appname : appname,
			ownername : ownername,
			apptype : apptype
		};

		var success = function(data) {
			consolePanel.append('info', 'Success', 'Project uploaded');
		}

		$.post(url, param, success);
	},

	// for TenxCloud
	tenxCloudGetStatus : function() {
		// alert("start get status");
		url = Global.url.tenxCloud;
		param = {
			operation : "getstatus"
		};
		var success = function() {
			alert("return");
		};
		$.post(url, param, success);
	},

	tenxCloudInitUser : function() {
		url = Global.url.tenxCloud;
		param = {
			operation : "inituser",
			appname : appname
		};
		var success = function(data) {
			alert(data);
		};
		$.post(url, param, success);
	},

	tenxCloudCreateService : function() {
		url = Global.url.tenxCloud;
		param = {
			operation : "createservice",
			appname : appname,
			apptype : apptype

		};
		var success = function(data) {
			alert(data);
		};
		$.post(url, param, success);
	},

	tenxCloudGetServiceInfo : function() {
		url = Global.url.tenxCloud;
		param = {
			operation : "getserviceinfo"
		};
		var success = function(data) {
			alert(data);
		};
		$.post(url, param, success);
	},

	tenxCloudStartService : function() {
		url = Global.url.tenxCloud;
		param = {
			operation : "startservice",
			appname : appname,
			apptype : apptype,
			ownername : ownername
		};
		var success = function(data) {
			alert(data);
		};
		$.post(url, param, success);
	},
	tenxCloudStopService : function() {
		url = Global.url.tenxCloud;
		param = {
			operation : "stopservice",
			appname : appname,
			apptype : apptype,
			ownername : ownername
		};
		var success = function(data) {
			alert(data);
		};
		$.post(url, param, success);
	},
	tenxCloudUpload : function() {
		url = Global.url.tenxCloud;
		param = {
			operation : "upload",
			appname : appname,
			apptype : apptype,
			ownername : ownername
		};
		var success = function(data) {
			alert(data);
		};
		$.post(url, param, success);
	},

	pushUserData : function(message) {
		if (savedata) {
			Global.operations.push(message);

			var array = Global.operations;
			var json = "[";
			for ( var i = 0; i < array.length; i++) {
				json += array[i] + ',';
			}
			json = json.substring(0, json.length - 1);
			json += "]";
			Global.operations = Array();
			url = Global.url.saveUserData;
			param = {
				username : username,
				appname : appname,
				json : json
			};
			success = function(data) {

			};
			$.post(url, param, success);
		}
	},

	saveData : function() {
		url = Global.url.setSaveData;
		param = {
			username : username,
			appname : appname
		};
		success = function(data) {
			savedata = data;
			var saveDataMenu = $("#saveData");
			var status = "Off";
			if (savedata == "true")
				status = "On";
			saveDataMenu.html('<div class="menu-text">Save Data: ' + status
					+ ' (Click to change)</ div>');
		};
		$.post(url, param, success);
	}
};
