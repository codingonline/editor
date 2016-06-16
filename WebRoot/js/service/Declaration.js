var declaration = {
	getDeclaration : function(session, pos) {
		var url = Global.url.declaration;
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
			username : username
		};		
		var success = function(data) {
			var json = data;
			if(json.success){
				var path = json.declarationFile;
				var row1 = json.row1;
				var column1 = json.column1;
				var row2 = json.row2;
				var column2 = json.column2;
				if(path == "LOCAL"){
					Global.ideViewer.codePanel.getActiveTab().addSelectionMarker(row1,column1,row2,column2);
				}
				else{
					var title = path.substring(path.lastIndexOf('/')+1,path.length);
					var project = Global.ideViewer.codePanel.getActiveTab().project;
					Global.ideViewer.onFileSelect(title, path, project,function(){
						Global.ideViewer.codePanel.getActiveTab().addSelectionMarker(row1,column1,row2,column2);
			    	});
				}
			}
		}

		$.ajax({
			type : "post",
			async : false,
			url : url,
			data : param,
			dataType : "json",
			success : success
		});
	}
};
