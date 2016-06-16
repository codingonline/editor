openService= {

		openServiceAPIURL: "http://123.57.32.46:8080/openServiceAPI?",
		scriptURL: "http://123.57.32.46:8080/plugins/",
		authedServices:[],
		unauthedServices:[],
		pluginNum:0,
		afterInit: function(){
			$.get(openService.openServiceAPIURL, {
				action : 'services',
				userid : userid
			}, function(data) {
				var toolbar = $('#toolbar');
				toolbar.append($("<div/>").attr("id", "ToolsMenu"));
				var sevlist =$.parseJSON(data);
				openService.authedServices = sevlist;
				dialog.delPlugin.flushTable();
				for (i in openService.authedServices) {
					var serviceName = openService.authedServices[i].serviceName;
					var ownerName = openService.authedServices[i].ownerName;
					jQuery.getScript(openService.scriptURL+ownerName+"/"+serviceName+"/"+serviceName+".js")
					 .done(function() {	
						 openService.pluginNum++;
						 if(openService.pluginNum== openService.authedServices.length){
							 openService.initToolbarButton();
						 }
							 
						 
						
					});
					
				}
				if(openService.authedServices.length==0)
					openService.initToolbarButton();
				 
				
				
			});
			$.get(openService.openServiceAPIURL, {
				action : 'unauthedservices',
				userid : userid
			}, function(data) {
				openService.unauthedServices = $.parseJSON(data);
				dialog.addPlugin.flushTable();
				
			});
			
			
			
		},
		
		authSevIndex: function (sevname) {
			for (var i = 0; i < openService.authedServices.length; i++) {
				var sev = openService.authedServices[i];
				if (sev.serviceName == sevname) {
					return i;
				}
			}
			return openService.authedServices.length;
		},
		
		unauthSevIndex:function (sevname) {
			for (var i = 0; i < openService.unauthedServices.length; i++) {
				var sev = openService.unauthedServices[i];
				if (sev.serviceName == sevname) {
					return i;
				}
			}
			return openService.unauthedServices.length;
		},
		
		getClassesPath: function(){
			return "/data/workspace/javaweb/"+ownername+"/"+appname+"/target/classes/";
		},
		
		initWorkSpace: function(userName, serviceName){
			$.post(openService.openServiceAPIURL, {
				action: 'initWorkSpace',
				apptype: apptype,
				appowner: ownername,
				appname: appname,
				serviceowner: userName,
				servicename: serviceName
			}, function(data){
				
			
			});
		},
		
		initToolbarButton: function(){
			
			var ToolsMB= $("#ToolsMB");
			 var ToolsMenu = $("#ToolsMenu");
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
			 ToolsMenu.append(findbugs,checkstyle,$("<div class='menu-sep'></div>"),addPlugin,delPlugin);
			 ToolsMB.menubutton({
					menu: "#ToolsMenu"
				});
			
		},
		
		
}