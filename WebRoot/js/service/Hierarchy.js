var hierarchy = {
	declarfile:'',
	declarpos :'',
	token :'',
	searchresults :'',
	declarline :'',
	getCallHierarchy : function(session, pos) {
		hierarchy.cursorline = pos.row+1;
		var lines = session.getLines(0, pos.row - 1);
		var cnt = 0;
		for ( var i in lines) {
			cnt += lines[i].length + 1;
		}
		cnt += pos.column;
		var path = Global.ideViewer.codePanel.getActiveTab().path;
		var code = session.getValue();
		hierarchy.getDeclaration(path, cnt, code,function(){
			var token = hierarchy.token;
			if (token == null ||token == "")
				return;
			hierarchy.searchInProject(token,hierarchy.declarfile,hierarchy.declarpos);
		});
		
		
		
	},
	
	getDeclaration : function(path, pos, code,callbackFunction){
		var url = Global.url.declaration;
		var param;
		if (code == null ||code == ""){
			param = {
					position : pos,
					path : path,
					appname : appname,
					username : username
				};	
		}
		else{
			param = {
					code : code,
					position : pos,
					path : path,
					appname : appname,
					username : username
				};	
		}
			
			
			
		var success = function(data) {
			var json = data;
			if(json.success){
				hierarchy.token = json.token;
				var file = json.declarationFile ;
				if(file == "LOCAL")
					file = path;
				hierarchy.declarfile = file;
				hierarchy.declarpos = json.declarationStartPosition;
				hierarchy.declarline = json.row1+1;
				if (callbackFunction && typeof(callbackFunction)=="function") {
					callbackFunction();
				}
				
			}
		}

		$.ajax({
			type : "post",
			async : false,
			url : url,
			data : param,
			dataType : "json",
			//jsonp : "jsonpCallback",
			success : success
		});
	},
	
	searchInProject : function(str,file,pos){
			consolePanel.clearCallHierarchy();
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
				
				var searchresults = $.parseJSON(data);
				var res = [];
				var i=0;
				
				while(i<searchresults.length){
					var j=0;
					var children = searchresults[i].children;
					var callhierarchys = [];
					
					while(j<children.length){
						var child = children[j];
						hierarchy.getDeclaration(child.attributes.path,child.attributes.pos+1);
						if(child.attributes.line == hierarchy.declarline ||  hierarchy.declarfile != file || hierarchy.declarpos != pos){
							searchresults[i].children.splice(j,1);
							j--;
						}
						j++;
					}
					if(searchresults[i].children.length == 0){
						delete searchresults.splice(i,1);
						i--;
					}
					i++;
				}
				consolePanel.showCallHierarchy(searchresults);
			}
			$.post(url, param, success);
	}

};
