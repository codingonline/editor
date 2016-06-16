stdStream = {
		
		connect_times : 0,
		success : "BUILD SUCCESS",
		
		stdin : function(event){
			if(event.which == "13"){
				var input = this;
				var data = input.value;
				consolePanel.appendConsole("input",data);
				input.value = "";
				
				if(consolePanel.ws!=null){
					console.log("send"+data+'\n')
					consolePanel.ws.send(data+'\n');
				}
			}
		},

		stdout : function(event){
			consolePanel.appendConsole("output",event.data);
			if(apptype=="javaweb"){
				var lines=event.data.split("\n");
				var line;
				for(var i=0; i<lines.length; i++){
					line = lines[i];
					//console.log(line);
					//console.log(line.indexOf(stdStream.success));
					if(apptype=="javaweb"&&line.indexOf(stdStream.success)!=-1){
						var url = Global.url.run;
						var param = {
								appid: appid,
								user: username,
						};
						var success = function(data){
							var d = JSON.parse(data);
							consolePanel.weburl = "http://" + d.domain+':'+d.port;
							consolePanel.append('info', 'Success', 'Project is running at <a href='+consolePanel.weburl+' target="_blank">'+consolePanel.weburl+'</a>');
						}
						$.get(url, param, success);
					}
				}
			}
		},
		
		open : function(){
			stdStream.connect_times = stdStream.connect_times + 1;
			consolePanel.ws = new WebSocket(consolePanel.wsurl);
			consolePanel.ws.onerror = stdStream.error;
			consolePanel.ws.onopen = function(){consolePanel.append("console", "runner", "connect to runner("+consolePanel.wsurl.substring(5)+")")}
			consolePanel.ws.onmessage = stdStream.stdout;
			consolePanel.ws.onclose = function(){consolePanel.append("console", "runner", "disconnect to runner("+consolePanel.wsurl.substring(5)+")")}
		},
		
		error : function(){
			if(stdStream.connect_times<5){
				setTimeout(stdStream.open, 1000);
			}else{
				//consolePanel.append("info", "fail", "connection timeout");
				var url = Global.url.loadFile;
				var param = {
						path: "error.log",
		    			appname: appname,
		    			apptype: apptype,
		    			ownername: ownername,
				};
				var success = function(data){
					consolePanel.appendConsole("error", data);
				}
				$.post(url, param, success);
			}
		},
		
		close : function(event){
			consolePanel.appendConsole("exit", "exit");
		}
}