tenxcloud = {
		preInit : function() {

		},
		
		onDeploy : function() {
			if (apptype != "javaweb" && apptype != "php") {
				alert(apptype + " 项目暂不支持部署到时速云");
				return;
			}
			
			var url = Global.url.tenxCloud;
			var param = {
				appname : appname,
				apptype : apptype,
				ownername : ownername,
				operation : "check"
			};
			var success = function(data) {
				if (data != "OK") {
					if (confirm("每个用户只能部署1个应用至时速云。\n您已部署了另一应用，是否替换为当前应用？") == false)
						return false;
				}
				
				consolePanel.append('info', 'Processing', 'Start Deploying');
				url = Global.url.tenxCloud;
				param = {
					appname : appname,
					apptype : apptype,
					ownername : ownername
				};
				var success = function(data) {
					ret = eval('('+data+')');
					if (ret.retCode == 0)
						consolePanel.append('info', 'Success', ret.msg);
					else
						consolePanel.append('info', 'Error', ret.msg);
				};
				$.post(url, param, success);
				
			};
			$.post(url, param, success);
		}
}
		
			