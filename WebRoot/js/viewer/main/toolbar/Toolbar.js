topBar = {

		menuItems : [],
		customHandler : [],

		initMenuItems : function(){
			// Run Menu
			var runMB = $("<a/>").attr({
				"href": "#",
				"id": "RunMB"
			}).html("Run")
			var runMenu = $("<div/>").attr("id", "RunMenu");
			var runProject = $("<div/>").attr("id", "runProject").html("run project").bind('click', Global.onRunProject);
			runProject[0].dataset.options = "iconCls:'run'";
			runMenu.append(runProject);
			topBar.menuItems.push({MB: runMB, Menu: runMenu});

			// Window Menu
			var windowMB = $("<a/>").attr({
				"href": "#",
				"id": "WindowMB"
			}).html("Window");
			var windowMenu = $("<div/>").attr("id", "WindowMenu");
			var preferences =  $("<div/>").attr("id", "preferences").html("preferences").bind('click', function(){
				$("#preferencesDialog").dialog("open");
			});
			windowMenu.append(preferences);
			topBar.menuItems.push({MB: windowMB, Menu: windowMenu});
			
			// TenxCloud Menu for test
			/*var tenxcloudMB = $("<a/>").attr({
				"href": "#",
				"id": "TenxcloudMB"
			}).html("Tenxcloud");
			var tenxcloudMenu = $("<div/>").attr("id", "TenxcloudMenu");
			// Buttons in menu
			var getStatus = $("<div/>").attr("id", "getStatus").html("Get Status").bind('click', Global.tenxCloudGetStatus);
			
			var initUser = $("<div/>").attr("id", "initUser").html("Init User").bind('click', Global.tenxCloudInitUser);
			
			var getServiceInfo = $("<div/>").attr("id", "getServiceInfo").html("Get Service Info").bind('click', Global.tenxCloudGetServiceInfo);
			
			var createService =  $("<div/>").attr("id", "createService").html("Create Service").bind('click', Global.tenxCloudCreateService);
			var startService =  $("<div/>").attr("id", "startService").html("Start Service").bind('click', Global.tenxCloudStartService);
			var stopService =  $("<div/>").attr("id", "stopService").html("Stop Service").bind('click', Global.tenxCloudStopService);
			
			var upload = $("<div/>").attr("id", "upload").html("Upload").bind('click', Global.tenxCloudUpload);
			
			tenxcloudMenu.append(getStatus, initUser, getServiceInfo, createService, upload, startService, stopService);
			topBar.menuItems.push({MB: tenxcloudMB, Menu: tenxcloudMenu});*/
			
			// Git Menu
			/**
			 * 
			 */
			var gitMB = $("<a/>").attr({
				"href": "#",
				"id": "GitMB"
			}).html("Git");
			var gitMenu = $("<div/>").attr("id", "GitMenu");
			var gitInit = $("<div/>").attr("id", "gitInit").html("git init").bind('click', gitCmd.gitInit);
			var gitStatus = $("<div/>").attr("id", "gitStatus").html("git status").bind('click', gitCmd.gitStatus);
			var gitAdd = $("<div/>").attr("id", "gitAdd").html("git add").bind('click', gitCmd.gitAdd);
			var gitCommit = $("<div/>").attr("id", "gitCommit").html("git commit").bind('click', function(){
				$("#gitCommitDialog").dialog("open");
			});
			var gitpullPublic = $("<div/>").attr("id", "gitpullPublic").html("git pull from public");
			var gitpullPrivate = $("<div/>").attr("id", "gitpullPrivate").html("git pull from private");
			var gitpush = $("<div/>").attr("id", "gitPush").html("git push").bind('click', function(){
				$("#gitPushDialog").dialog("open");
			});
			var gitbranch = $("<div/>").attr("id", "gitBranch").html("git branch").bind('click', function(){
				gitCmd.gitBranchQuery();
				$("#gitBranchDialog").dialog("open");
			});
			gitMenu.append(gitInit, gitStatus, gitAdd, gitCommit, gitpullPublic, gitpullPrivate, gitpush,
					gitbranch);
			topBar.menuItems.push({MB: gitMB, Menu: gitMenu});
		},

		InitTopBar: function () {
			var toolbar = $('#toolbar');
			toolbar.attr({
				"region": "north",
				"noheader": true,
				"border": false
			}).css({
				"background-color": "rgb(230, 230, 230)"
			});	
			// File Menu
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "FileMB"
			}).html("File"));
			toolbar.append($("<div/>").attr("id", "FileMenu"));
			var fileMB = $("#FileMB");
			var fileMenu = $("#FileMenu");
			var importdiv = $("<div/>").append($("<span/>").attr("id", "importProject").html("import project..."));
			importdiv.append($("<div/>").append(
					$("<div/>").attr("id", "imfromBAE").html("from BAE").bind('click', function(){
						$('#importfromBAETable').find('input[name="unameBaidu"]').val(unamebaidu);
						$('#importfromBAETable').find('input[name="password"]').val(pwdbaidu);
						$("#importfromBAEDialog").dialog("open");
					}),
					$("<div/>").attr("id", "imfromSAE").html("from SAE").bind('click', function(){
						$('#importfromSAETable').find('input[name="unameSina"]').val(unamesina);
						$('#importfromSAETable').find('input[name="password"]').val(pwdsina);
						$("#importfromSAEDialog").dialog("open");
					})
			));
			var newdiv = $("<div/>").append($("<span/>").attr("id", "importProject").html("new..."));
			newdiv.append($("<div/>").append(
					$("<div/>").attr("id", "newProject").html("Project").bind('click', function(){
						$("#createProjectDialog").dialog("open");
					}),
					$("<div class='menu-sep'></div>"),
					$("<div/>").attr("id", "newFile").html("File").bind('click', Global.onAddFile),
					$("<div/>").attr("id", "newFolder").html("Folder").bind('click', Global.onAddPackage)
			));
			var opendiv = $("<div/>").attr("id", "openProject").html("open project<a id='openProjectWin' href='http://www.poprogramming.com/console' target='_blank'></a>").bind('click', Global.onOpenProject);
			var savediv = $("<div/>").attr("id", "saveProject").html("save project").bind('click', function(){
				$("#saveProjectDialog").dialog('open');
			});
			var exportZipdiv = $("<div/>").attr("id", "exportProjectZip").html("download project").bind('click', Global.onDownloadProject);
			var importZipdiv = $("<div/>").attr("id", "importZip").html("upload file").bind('click', function(){
				$("#importfromLocalDialog").dialog("open");
			});
			fileMenu.append(newdiv, opendiv, savediv, $("<div class='menu-sep'></div>"),
					exportZipdiv, importZipdiv
			);
			fileMB.menubutton({
				menu: "#FileMenu"
			});

			// Edit
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "EditMB"
			}).html("Edit"));
			toolbar.append($("<div/>").attr("id", "EditMenu"));
			var editMB = $("#EditMB");
			var editMenu = $("#EditMenu");
			var undo = $("<div/>").attr("id", "undo").html("undo").bind('click', Global.onUndo);
			var redo = $("<div/>").attr("id", "redo").html("redo").bind('click', Global.onRedo);
			var save = $("<div/>").attr("id", "save").html("save").bind('click', Global.onSaveFile);
			var saveall = $("<div/>").attr("id", "saveall").html("save all").bind('click', Global.onSaveAllFile);
			var searchInFile = $("<div/>").attr("id", "searchinfiles").html("search in files").bind('click', function(){
				$("#fileSearchDialog").dialog("open");
			});
			var renamediv = $("<div/>").attr("id", "rename").html("rename").bind('click', explorerPanel.onRename);
			var deletediv = $("<div/>").attr("id", "delete").html("delete").bind('click', explorerPanel.onRemove);
			undo[0].dataset.options = "iconCls: 'undo'";
			redo[0].dataset.options = "iconCls: 'redo'";
			save[0].dataset.options = "iconCls: 'save'";
			saveall[0].dataset.options = "iconCls: 'saveall'";
			deletediv[0].dataset.options = "iconCls: 'delete'";
			editMenu.append(undo, redo, $("<div class='menu-sep'></div>"),
					save, saveall, $("<div class='menu-sep'></div>"),
					searchInFile, $("<div class='menu-sep'></div>"),
					/*cutdiv, copydiv, pastediv,*/ renamediv, deletediv);
			editMB.menubutton({
				menu: "#EditMenu"
			});

//			// Run
//			toolbar.append($("<a/>").attr({
//			"href": "#",
//			"id": "RunMB"
//			}).html("Run"));
//			toolbar.append($("<div/>").attr("id", "RunMenu"));
//			var runMB = $("#RunMB");
//			var runMenu = $("#RunMenu");
//			var runProject = $("<div/>").attr("id", "runProject").html("run project").bind('click', Global.onRunProject);
//			runMenu.append(runProject);
//			runMB.menubutton({
//			menu: "#RunMenu"
//			});

			toolbar.append(this.menuItems[0].MB);
			toolbar.append(this.menuItems[0].Menu);
			this.menuItems[0].MB.menubutton({
				menu: "#"+this.menuItems[0].Menu[0].id
			});
			

			// Deploy
			/*toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "DeployMB"
			}).html("Deploy"));
			toolbar.append($("<div/>").attr("id", "DeployMenu"));
			var deployMB = $("#DeployMB");
			var deployMenu = $("#DeployMenu");
			var deployPaas = $("<div/>").attr("id", "deployPaas").html("Deploy to PaaS").bind('click', function(){
				if(paasname=='bae'){
					$('#deployProjectTable').find('input[name="uname"]').val(unamebaidu);
					$('#deployProjectTable').find('input[name="password"]').val(pwdbaidu);
				}else if(paasname=='sae'){
					$('#deployProjectTable').find('input[name="uname"]').val(unamesina);
					$('#deployProjectTable').find('input[name="password"]').val(pwdsina);
				}
				$("#deployProjectDialog").dialog("open");
			});
			var deployConfig = $("<div/>").attr("id", "deployConfig").html("Edit deploy Config");
			deployMenu.append(deployPaas, deployConfig);
			deployMB.menubutton({
				menu: "#DeployMenu"
			});*/


			// Git
			toolbar.append(this.menuItems[2].MB);
			toolbar.append(this.menuItems[2].Menu);
			this.menuItems[2].MB.menubutton({
				menu: "#"+this.menuItems[2].Menu[0].id
			});

			// Share
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "ShareMB"
			}).html("Share"));
			toolbar.append($("<div/>").attr("id", "ShareMenu"));
			var shareMB= $("#ShareMB");
			var shareMenu = $("#ShareMenu");
			var developers = $("<div/>").attr("id", "developers").html("developers").bind('click', function(){
				$('#developersDialog').dialog("open");
			});
			var genShareUrl = $("<div/>").attr("id", "genUrl").html("generate share url").bind('click', Global.onShare);
			shareMenu.append(developers,  $("<div class='menu-sep'></div>"),
					genShareUrl);
			shareMB.menubutton({
				menu: "#ShareMenu"
			});
			
			
			// Tools
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "ToolsMB"
			}).html("Tools"));
			//toolbar.append($("<div/>").attr("id", "ToolsMenu"));
			var ToolsMB= $("#ToolsMB");
			//var ToolsMenu = $("#ToolsMenu");
			/*
			var findbugs = $("<div/>").attr("id", "findbugs").html("findbugs").bind('click', function(){
				
				var url = Global.url.findbugs;
				var param = {
						filepath: "/data/repo/javaweb/"+ownername+"/"+appname+"/target/classes/"
			            
				};
				var success = function(data){
					consolePanel.append('info', 'FindBugs', data);
					
				};
				$.post(url, param, success);
			});
			var checkstyle = $("<div/>").attr("id", "checkstyle").html("checkstyle").bind('click', function(){
				
				var url = Global.url.checkstyle;
				var param = {
						filepath: "/data/repo/javaweb/"+ownername+"/"+appname+"/src/main/"
			            
				};
				var success = function(data){
					consolePanel.append('info', 'Checkstyle', data);
					
				};
				$.post(url, param, success);
			});
			var addPlugin = $("<div/>").attr("id", "addPlugin").html("install plugin").bind('click',
					function(){
				
				//dialog.addPlugin.flushTable();
				$('#addPluginDialog').dialog("open");
			});
			var delPlugin= $("<div/>").attr("id", "delPlugin").html("uninstall plugin").bind('click',
					function(){
				$('#delPluginDialog').dialog("open");
					
			});
			*/
			//ToolsMenu.append(findbugs,checkstyle,$("<div class='menu-sep'></div>"),addPlugin,delPlugin);
			ToolsMB.menubutton();
			
			/*
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "PluginMB"
			}).html("Plugin"));
			toolbar.append($("<div/>").attr("id", "PluginMenu"));
			var PluginMB = $("#PluginMB");
			var PluginMenu = $("#PluginMenu");
			var addPlugin = $("<div/>").attr("id", "addPlugin").html("install plugin").bind('click',
					function(){
				
				//dialog.addPlugin.flushTable();
				$('#addPluginDialog').dialog("open");
			});
			var delPlugin= $("<div/>").attr("id", "delPlugin").html("uninstall plugin").bind('click',
					function(){
				$('#delPluginDialog').dialog("open");
					
			});
			PluginMenu.append(addPlugin, delPlugin);
			PluginMB.menubutton({
				menu: "#PluginMenu"
			});
			*/
			// window
			toolbar.append(this.menuItems[1].MB);
			toolbar.append(this.menuItems[1].Menu);
			this.menuItems[1].MB.menubutton({
				menu: "#"+this.menuItems[1].Menu[0].id
			});
			
			

			// Help
			/*toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "HelpMB"
			}).html("Help"));
			toolbar.append($("<div/>").attr("id", "HelpMenu"));
			var helpMB = $("#HelpMB");
			var helpMenu = $("#HelpMenu");
			var documentdiv = $("<div/>").attr("id", "documentdiv").html("document");
			var problemsdiv = $("<div/>").attr("id", "problemsdiv").html("problems");
			var feedbackdiv = $("<div/>").attr("id", "feedbackdiv").html("feedback");
			helpMenu.append(documentdiv, problemsdiv, feedbackdiv);
			helpMB.menubutton({
				menu: "#HelpMenu"
			});*/
			
			
			toolbar.append('<span class="datagrid-btn-separator" style="vertical-align: middle; height: 15px;display:inline-block;float:none"></span>');
			// Deploy
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "DeployMB"
			}).html("Deploy"));
			toolbar.append($("<div/>").attr("id", "DeployMenu"));
			var deployMB = $("#DeployMB");
			var deployMenu = $("#DeployMenu");
			/*
			var deployPaas = $("<div/>").attr("id", "deployPaas").html("Deploy to PaaS").bind('click', function(){
				if(paasname=='bae'){
					$('#deployProjectTable').find('input[name="uname"]').val(unamebaidu);
					$('#deployProjectTable').find('input[name="password"]').val(pwdbaidu);
				}else if(paasname=='sae'){
					$('#deployProjectTable').find('input[name="uname"]').val(unamesina);
					$('#deployProjectTable').find('input[name="password"]').val(pwdsina);
				}
				$("#deployProjectDialog").dialog("open");
			});

			
			toolbar.append('<span class="datagrid-btn-separator" style="vertical-align: middle; height: 15px;display:inline-block;float:none"></span>');

			// Deploy
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "DeployMB"
			}).html("Deploy"));
			toolbar.append($("<div/>").attr("id", "DeployMenu"));
			var deployMB = $("#DeployMB");
			var deployMenu = $("#DeployMenu");
			/*
			 * Deploy to PaaS
			 * 
			var deployPaas = $("<div/>").attr("id", "deployPaas").html("Deploy to PaaS").bind('click', function(){
				if(paasname=='bae'){
					$('#deployProjectTable').find('input[name="uname"]').val(unamebaidu);
					$('#deployProjectTable').find('input[name="password"]').val(pwdbaidu);
				}else if(paasname=='sae'){
					$('#deployProjectTable').find('input[name="uname"]').val(unamesina);
					$('#deployProjectTable').find('input[name="password"]').val(pwdsina);
				}
				$("#deployProjectDialog").dialog("open");
			});
			var deployConfig = $("<div/>").attr("id", "deployConfig").html("Edit deploy Config");
			
			deployMenu.append(deployPaas, deployConfig);
			

			*/
			
			// Tenxcloud
			var deployToTenxcloud = $("<div/>").attr(
					"id", "deployToTenxcloud"	
				).html("Deploy to Tenxcloud").bind('click', tenxcloud.onDeploy);
			
			// IaaS
			var deployToIaaS = $('<div/>').attr({
				"id":"deployToIaaS"
			}).html("Deploy to IaaS").bind('click', function(){
				$("#deployToIaaSDialog").dialog("open");
			});
			
			deployMenu.append(deployToTenxcloud, deployToIaaS);
			
			
			
			deployMB.menubutton({
				menu: "#DeployMenu"
			});
			
			// monitor
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "MonitorMB"
			}).html("Monitor"));
			toolbar.append($("<div/>").attr("id", "MonitorMenu"));
			var monitorMB= $("#MonitorMB");
			var monitorMenu = $("#MonitorMenu");
			var status = "Off";
			if (savedata == "true") status = "On";
			var saveData = $("<div/>").attr("id", "saveData").html("Save Data: " + status + " (Click to change)").bind('click', Global.saveData);
			
			var openMonitor = $("<div/>").attr("id", "openMonitor").html("Open Monitor View").bind('click', function() {
				var location = window.location;
				location.href = "monitor.jsp" + location.search;
			});
			monitorMenu.append(saveData, $("<div class='menu-sep'></div>"),
					openMonitor);
			monitorMB.menubutton({
				menu: "#MonitorMenu"
			});

			// Menu end
			toolbar.append($("<br/>"));

			
			
			
			//添加工程
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "AddProject"
			}).linkbutton({plain: true, iconCls: 'explorerAddProject'}).bind('click', function(){
				$("#createProjectDialog").dialog("open");
			}));
			$("#AddProject").tooltip({
				position: "bottom",
				content: "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new project"
			});

			//添加文件夹
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "AddPackageButton"
			}).linkbutton({plain: true, iconCls: 'explorerAddPackage'}).bind('click', Global.onAddPackage));
			$("#AddPackageButton").tooltip({
				position: "bottom",
				content: "new folder"
			}).addClass("quickmenu");
			//添加文件
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "AddFileButton"
			}).linkbutton({plain: true, iconCls: 'explorerAddFile'}).bind('click', Global.onAddFile));
			$("#AddFileButton").tooltip({
				position: "bottom",
				content: "new file"
			}).addClass("quickmenu");
			//删除
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "RemoveButton"
			}).linkbutton({plain: true, iconCls: 'explorerRemove'}).bind('click', explorerPanel.onRemove));
			$("#RemoveButton").tooltip({
				position: "bottom",
				content: "delete"
			}).addClass("quickmenu");
			//保存
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "SaveButton"
			}).linkbutton({plain: true, iconCls: 'save'}).bind('click', Global.onSaveFile));
			$("#SaveButton").tooltip({
				position: "bottom",
				content: "save file"
			}).addClass("quickmenu");
			//运行
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "RunButton"
			}).linkbutton({plain: true, iconCls: 'run'}).bind('click', Global.onRunProject));
			$("#RunButton").tooltip({
				position: "bottom",
				content: "run"
			}).addClass("quickmenu");
			//调试
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "DebugButton"
			}).linkbutton({plain: true, iconCls: 'debug'}).bind('click', Global.onDebug));
			$("#DebugButton").tooltip({
				position: "bottom",
				content: "debug"
			}).addClass("quickmenu");
			//stepInto
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "StepIntoButton"
			}).linkbutton({plain: true, iconCls: 'stepInto'}).bind('click', Global.onStepInto));
			$("#StepIntoButton").tooltip({
				position: "bottom",
				content: "step into"
			}).linkbutton('disable').unbind('click').addClass("quickmenu");

			//stepOver
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "StepOverButton"
			}).linkbutton({plain: true, iconCls: 'stepOver'}).bind('click', Global.onStepOver));
			$("#StepOverButton").tooltip({
				position: "bottom",
				content: "step over"
			}).linkbutton('disable').unbind('click').addClass("quickmenu");

			//resume
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "ResumeButton"
			}).linkbutton({plain: true, iconCls: 'resume'}).bind('click', Global.onResume));
			$("#ResumeButton").tooltip({
				position: "bottom",
				content: "resume"
			}).linkbutton('disable').unbind('click').addClass("quickmenu");

			//terminate
			toolbar.append($("<a/>").attr({
				"href": "#",
				"id": "TerminateButton"
			}).linkbutton({plain: true, iconCls: 'terminate'}).bind('click', Global.onTerminate));
			$("#TerminateButton").tooltip({
				position: "bottom",
				content: "terminate"
			}).linkbutton('disable').unbind('click').addClass("quickmenu");     

			// download project
			var form=$("<form>");//定义一个form表单
			form.attr({
				"id": "downloadProjectForm",
				"style": "display:none",
				"method": "post",
				"action":"packageExplorer"
			});
			var input1=$("<input>");
			input1.attr({"type":"hidden","name":"operation","value": "downloadProject"});
			var input2=$("<input>");
			input2.attr({"type":"hidden","name":"appname","value": appname});
			var input3=$("<input>");
			input3.attr({"type":"hidden","name":"apptype","value": apptype});
			var input4=$("<input>");
			input4.attr({"type":"hidden","name":"ownername","value": ownername});
			$("#toolbar").append(form);//将表单放置在web中
			form.append(input1, input2, input3, input4);


			return this;
		},

		customMenuItems : function(menuTitle, prependItems, appendItems){
			for(i in this.menuItems){
				if(this.menuItems[i].Menu[0].id==menuTitle+"Menu"){
					var menu = this.menuItems[i].Menu;
					for(i in prependItems){
						menu.prepend(prependItems[prependItems.length-i-1]);
					}
					for(i in appendItems){
						menu.append(appendItems[i]);
					}
				}
			}
		},

};