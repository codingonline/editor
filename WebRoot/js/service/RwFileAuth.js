rwFileAuth = {

		devs: [],
		
		afterInit: function(){
			$.get(Global.url.getDevelopers, {
				filepath: '/'+appname+'/',
				apptype: apptype,
				ownername: ownername
			}, function(data){
				var devs = JSON.parse(data);
				rwFileAuth.devs = devs;
				dialog.developers.flushTable();
			});
		},
		
		removeDev: function(dev){
			var devs = rwFileAuth.devs;
			for(i in devs){
				if(devs[i]==dev){
					devs.splice(i, 1);
				}
			}
		},
		
		getDevsHTML: function(){
			var devs = rwFileAuth.devs, devsHTML = '';
			for(var i=0; i<devs.length; ++i){
				devsHTML += "<option value='"+devs[i]+"'>"+devs[i]+"</option>";
        	}
			return devsHTML;
		},

		onRWAuthDialog: function() {
			var node = explorerPanel.treeView.tree('getSelected');
			var filepath = '/'+appname+'/'+node.attributes.path;
			$.get(Global.url.getDevelopers, {
				filepath: filepath,
				apptype: apptype,
				ownername: ownername
	    	}, function(data){
	    		var devs = JSON.parse(data);
	    		dialog.rwAuth.flushTable(devs[0]);
				$("#rwAuthDialog").dialog("open");
	    	});
		},

		onrwGrant: function(){
			rwFileAuth.onrwAuth("grant");
		},

		onrwRevoke: function(){
			rwFileAuth.onrwAuth("revoke");
		},

		onrwAuth: function(action){
			var url, oldwriter, newwriter;
			if(action=="grant"){
				url = Global.url.grant;
				newwriter = $('#rwAuthTable').find('select[name="developers"]').val();
			}else if(action=="revoke"){
				url = Global.url.revoke;
				newwriter = username;
			}
			var filepath = $('#rwAuthTable').find('span[name="filepath"]').text();
			var oldwriter = $('#rwAuthTable').find('span[name="writer"]').text();
			$.post(url, {
				apptype: apptype,
				ownername: ownername,
				path: filepath,
				username: newwriter,
			}, function(data){
				if(data=="success"){
					var amsg = new Object();
					amsg['filepath'] = filepath;
					amsg['oldwriter'] = oldwriter;
					amsg['newwriter'] = newwriter;
					codePanel.authws.send(JSON.stringify(amsg));
					if(action=="grant"){
						$('#rwAuthTable').find('span[name="writer"]').text(developer);
					}else if(action=="revoke"){
						$('#rwAuthTable').find('span[name="writer"]').text(username);
					}
				}
			});
		}

}