javaWeb = {
		
		buildTip : null,
		text : "building project, please wait",
		dotNum : 0,
		
		preInit: function(){
			var runMenu = $("#RunMenu");
			var buildProject = $("<div/>").attr("id", "buildProject").html("build project").bind('click', function(){javaWeb.onBuildRunProject(false)});
			var buildRunProject = $("<div/>").attr("id", "buildRunProject").html("build and run project").bind('click', function(){javaWeb.onBuildRunProject(true)});
			var mvnrepo = $("<div/>").attr("id", "mvnrepo").html("open maven repository").bind('click', function(){
				window.open("http://mvnrepository.com/", "_blank");
			});
			topBar.customMenuItems("Run", [mvnrepo, buildProject], [buildRunProject]);
		},
		
		afterInit: function(){
			$("#RunButton").after($("<a/>").attr({
				"href": "#",
				"id": "BuildRunButton"
			}).linkbutton({plain: true, iconCls: 'buildRun'}).bind('click', this.onBuildRunProject));
			$("#BuildRunButton").tooltip({
				position: "bottom",
				content: "build and run"
			}).addClass("quickmenu");
			var div = $("<div>").attr("id", "console_building").append($("<pre>"));
			$("#console_output").after(div);
		},
		
		onBuildRunProject: function(isRun){
			Global.onSaveAll();
			var url = Global.url.build;
			var param = {
					appname: appname,
					apptype: apptype,
					ownername: ownername
			};
			var success = function(data){
//				data = $.parseJSON(data);
//				if(data.status=="success"){
//					consolePanel.append('info', 'Success', 'Project build success');
//					if(isRun){Global.onRunProject();}
//				}else{
//					consolePanel.append('info', 'Error', 'Project build Failure');
//				}
				//consolePanel.append('console', "build", data.msg);
			}
			consolePanel.append('info', 'Processing', 'Building project...');
			$.post(url, param, success);
			var buildws = new WebSocket(Global.url.buildws + '/' + ownername + '/' + appname);
			buildws.onerror = function(event) {
				consolePanel.appendInfo('Error', 'Project build Failure');
			};
			buildws.onopen = function(event) {
				var building = $("#console_building").find("pre");
				building.text("building project, please wait");
				javaWeb.buildTip = setInterval(javaWeb.stateTipChange, 1000);
			};
			buildws.onmessage = function(event) {
				var info = event.data.replace(new RegExp("\n", 'gm'), '<br/>');
				consolePanel.append('console', 'Building', info);
			};
			if(isRun){
				buildws.onclose = function(event) {
					javaWeb.buildComplete();
					Global.onRunProject();
				}
			}else{
				buildws.onclose = function(event) {
					javaWeb.buildComplete();
				}
			}
		},

		stateTipChange : function(){
			var num = javaWeb.dotNum + 1;
			num = num % 10;
			var div = $("#console_building").find("pre");
			var dots = "";
			for(var i=0; i<num; i++){
				dots = dots + ".";
			}
			div.text(javaWeb.text+dots);
			javaWeb.dotNum = num;
		},
		
		buildComplete : function(){
			console.log("buildws close");
			clearInterval(javaWeb.buildTip);
			console.log(this.isRun);
			$("#console_building").find("pre").text("Build Complete");
		}
}