dialog = {
		

		afterInit: function(){
			for(i in this){
				if( typeof this[i] != 'function'){
					this.create($('#dialogs'), this[i]);
				}
			}
		},
		
		addDialog: function(){
			for(i in arguments){
				this.create($('#dialogs'), arguments[i]);
			}
		},

		create : function(parent, element){
			var btns = [];
			if(element.submitHandler!=undefined){
				btns.unshift({
					text:element.submitText?element.submitText:'save',
					handler: element.submitHandler
				});
			}
			if(element.handlers!=null){
				var handler;
				for(i in element.handlers){
					handler = element.handlers[i];
					btns.unshift({
						text: handler.text,
						handler: handler.handler
					});
				}
			}
			

			parent.append($('<div/>').attr({
				'id' : element.id+'Dialog',
			}).css({
				'margin-left':'auto',
				'margin-right':'auto'
			}));
			$('#'+element.id+'Dialog').append($('<form/>').attr({
				'id' : element.id+'Form',
				'method' : 'post'
			}).css({
				'margin-left':'auto',
				'margin-right':'auto'
			}));
			$('#'+element.id+'Form').html(element.table);
			$('#'+element.id+'Dialog').dialog({
				title : element.title,
				cache : false,
				buttons : btns,
			});
			$('#'+element.id+'Dialog').dialog('close');
		},

		createProject :{
			id: 'createProject' ,
			title: 'New Project',
			submitHandler: function() {
				consolePanel.append('info', 'Processing', 'Creating a new project...');
				var newappname = $('#createProjectTable').find('input[name="appname"]').val();
				var newapptype = $('#createProjectTable').find('select[name="apptype"]').val();
				$.post(Global.url.addProject, {appname:newappname, apptype: newapptype}, function(data, status){
					if(data=='fail'){
						consolePanel.append('info', 'Error', 'Creating new project failed');
					}else{
						var url = window.location.href.replace(appname, $('#createProjectTable').find("input[name='appname']").val());
						consolePanel.append('info', 'Success', 'Click <a href='+url+' target="_blank">here</a> to open project in new Window');
					}
				});
				$('#createProjectDialog').dialog('close');
			},
			table: '<table id="createProjectTable"> \
				<tr>\
				<td><label for="appname">Name: </label></td>\
				<td><input type="text" name="appname" /></td>\
				</tr>\
				<tr>\
				<td><label for="apptype">Type: </label></td>\
				<td><select name="apptype" style="width:100%">\
				<option value="php">php</option>\
				<option value="python">python</option>\
				<option value="javaweb">java</option></select>\
				</td>\
				</tr>\
				</table>'
		},

		importfromBAE:{
			id: 'importfromBAE',
			title: 'Import project from BAE',
			submitHandler: function(){
				consolePanel.append('info', 'Processing', 'Import project from BAE');
				var newappname = $('#importfromBAETable').find('input[name="appname"]').val();
				var newapptype = $('#importfromBAETable').find('select[name="apptype"]').val();
				var svngit = $('#importfromBAETable').find('input[name="SVNGIT"]').val();
				var unamebaidu = $('#importfromBAETable').find('input[name="unameBaidu"]').val();
				var password = $('#importfromBAETable').find('input[name="password"]').val();
				$.post(Global.url.importFromBAE, {appname:newappname, apptype: newapptype,
					svngit:svngit, unamebaidu:unamebaidu, password:password}, function(data, status){
						if(data=='success'){
							var url = window.location.href.replace(appname, $('#importfromBAETable').find("input[name='appname']").val());
							consolePanel.append('info', 'Success', 'Click <a href='+url+' target="_blank">here</a> to open project in new Window');
						}else{
							consolePanel.append('info', 'Error', 'Import new project from BAE failed');
						}
					});
				$('#importfromBAEDialog').dialog('close');
			},
			table: '<table id="importfromBAETable"> \
				<tr>\
				<td><label for="appname">Project Name: </label></td>\
				<td><input type="text" name="appname" /></td>\
				</tr>\
				<tr>\
				<td><label for="apptype">Project Type: </label></td>\
				<td><select name="apptype" style="width:100%">\
				<option value="php">php</option>\
				<option value="python">python</option></select>\
				</tr>\
				<tr>\
				<td><label for="SVNGIT">SVN/GIT url: </label></td>\
				<td><input type="text" name="SVNGIT" /></td>\
				</tr>\
				<tr>\
				<td><label for="unameBaidu">Baidu username: </label></td>\
				<td><input type="text" name="unameBaidu"/></td>\
				</tr>\
				<tr>\
				<td><label for="password">Baidu password: </label></td>\
				<td><input type="password" name="password"/></td>\
				</tr>\
				\
				</table>'
		},

		importfromSAE :{
			id: 'importfromSAE',
			title: 'Import project from SAE',
			submitHandler: function(){
				consolePanel.append('info', 'Processing', 'Import project from SAE');
				var table = $('#importfromSAETable');
				var newappname = table.find('input[name="appname"]').val();
				var newapptype = table.find('select[name="apptype"]').val();
				var svn = table.find('input[name="SVN"]').val();
				var domain = table.find('input[name="domain"]').val()
				var unamesina = table.find('input[name="unameSina"]').val();
				var password = table.find('input[name="password"]').val();
				$.post(Global.url.importFromSAE, {appname:newappname, apptype: newapptype,
					svn:svn, unamesina:unamesina, password:password, domain:domain}, function(data, status){
						if(data=='success'){
							var url = window.location.href.replace(appname, $('#importfromSAETable').find("input[name='appname']").val());
							consolePanel.append('info', 'Success', 'Click <a href='+url+' target="_blank">here</a> to open project in new Window');
						}else{
							consolePanel.append('info', 'Error', 'Import new project from SAE failed');
						}
					});
				$('#importfromSAEDialog').dialog('close');
			},
			table:'<table id="importfromSAETable"> \
				<tr>\
				<td><label for="appname">Project Name: </label></td>\
				<td><input type="text" name="appname" /></td>\
				</tr>\
				<tr>\
				<td><label for="apptype">Project Type: </label></td>\
				<td><select name="apptype" style="width:100%">\
				<option value="php">php</option>\
				<option value="python">python</option></select>\
				</tr>\
				<tr>\
				<td><label for="SVN">SVN url: </label></td>\
				<td><input type="text" name="SVN" /></td>\
				</tr>\
				<tr>\
				<td><label for="domain">domain url: </label></td>\
				<td><input type="text" name="domain" /></td>\
				</tr>\
				<tr>\
				<td><label for="unameSina">Sina username: </label></td>\
				<td><input type="text" name="unameSina"/></td>\
				</tr>\
				<tr>\
				<td><label for="password">Sina password: </label></td>\
				<td><input type="password" name="password"/></td>\
				</tr>\
				\
				</table>' 
		},

		importfromLocal: {
			id: 'importfromLocal',
			title: 'upload file from local zip file',
			submitHandler: function(){
				var path = explorerPanel.currentPath();
				var form  = $("#importfromLocalForm");
				var filename = form.find('input[type="file"]')[0].value;
				var reg=new RegExp("\.jar$");     
				if(reg.test(filename)&&path==""){
					path += "src/main/webapp/WEB-INF/lib/";
				}
				var options = {
						type: 'post',
						url: Global.url.uploadFile+
						'&appname='+appname+'&apptype='+apptype+'&ownername='+ownername+'&path='+path,
						success: function(data){
							$('#importfromLocalDialog').dialog('close');
							Global.refreshExplorer();
							console.log(data);
							if(data.indexOf("success")>=0)
								consolePanel.append('info', 'Success', 'File uploaded');
							else
								consolePanel.append('info', 'Error', 'Uploading file fail');
						},
						error: function(){
							$('#importfromLocalDialog').dialog('close');
							consolePanel.append('info', 'Error', 'Uploading file fail');
						}
				}
				consolePanel.append('info', 'Processing', 'Uploading file...');
				form.ajaxSubmit(options);
			},
			table: '<table id="importfromLocalTable"> \
				<tr>\
				<td><input type="file" name="file" /></td>\
				</tr>\
				</table>' 
		},

		deployProject: {
			id: 'deployProject',
			title: 'Deploy Project',
			submitHandler: function(){
				consolePanel.append('info', 'Processing', 'Deploying '+appname+'...');
				var table = $('#deployProjectTable');
				var uname = table.find('input[name="uname"]').val();
				var password = table.find('input[name="password"]').val();
				var param = {
						appname: appname,
						apptype: apptype,
						uname: uname,
						password: password,
						giturl: giturl,
						svnurl: svnurl,
				};
				$.post(Global.url.deploy, param, function(data,status){
					$('#deployProjectDialog').dialog('close');
					if(data=='success') {
						if(paasname=='bae')
							consolePanel.append('info', 'Success', 
							'Project deployed to bae. Click <a href="http://console.bce.baidu.com/bae/" target="_blank">here</a> to publish the it.');
						else if(paasname=='sae'){
							if(domain!=""){
								consolePanel.append('info', 'Success', 
										'Project deployed to sae. See at <a target="_blank" href="'+domain+'">'+domain+'</a>');
							}
							else {
								consolePanel.append('info', 'Success', 
								'Project deployed to sae.');
							}
						}
					}else {
						consolePanel.append('info', 'Error', 
						'Deploy project failed.');
					}
				});
			},
			table: '<table id="deployProjectTable"> \
				<tr>\
				<td><label for="git">Git/SVN HTTPS: </label></td>\
				<td><input type="text" name="git"/></td>\
				</tr>\
				<tr>\
				<td><label for="uname">username: </label></td>\
				<td><input type="text" name="uname"/></td>\
				</tr>\
				<tr>\
				<td><label for="password">password: </label></td>\
				<td><input type="password" name="password"/></td>\
				</tr>\
				</table>'
		},
		
		deployProjectIaaS: {
			id: 'deployToIaaS',
			title: 'Deploy to IaaS',
			submitText : 'Deploy',
			submitHandler: function(){
				consolePanel.append('info', 'Processing', 'Deploying '+appname+' to IaaS...');
				var table = $('#deployToIaaSTable');
				var ip = table.find('input[name="ip"]').val();
				var port = table.find('input[name="port"]').val();
				var uname = table.find('input[name="uname"]').val();
				var password = table.find('input[name="password"]').val();
				var param = {
						appname: appname,
						apptype: apptype,
						ownername : ownername,
						uname: uname,
						password: password,
						ip : ip,
						port : port
				};
				$.post(Global.url.deployToIaaS, param, function(data,status){
					$('#deployToIaaSDialog').dialog('close');
					if(data=='success') {
						consolePanel.append('info', 'Success', 'Project deployed to IaaS.');
					}else {
						consolePanel.append('info', 'Error', 
						'Deploy project failed.');
					}
				});
			},
			table: '<table id="deployToIaaSTable"> \
				<tr>\
				<td><label for="git">IP:</label></td>\
				<td><input type="text" name="ip"/></td>\
				</tr>\
				<tr>\
				<td><label for="port">Port: </label></td>\
				<td><input type="text" name="port"/></td>\
				</tr>\
				<tr>\
				<td><label for="uname">Login as: </label></td>\
				<td><input type="text" name="uname"/></td>\
				</tr>\
				<tr>\
				<td><label for="password">Password: </label></td>\
				<td><input type="password" name="password"/></td>\
				</tr>\
				</table>'
		},
		
		saveProject :{
			id: 'saveProject',
			title: 'Your account in POP',
			submitHandler: function(){
				var username = $("#saveProjectTable").find("input[name='uname']").val();
				var password = $("#saveProjectTable").find("input[name='password']").val();
				var param = {
						username: username,
						password: password,
						appname: appname
				};
				$.post(Global.url.saveApp, param, function(data){
					$('#saveProjectDialog').dialog('close');
					window.location.href = data;
				});
			},
			table: '<table id="saveProjectTable"> \
				<tr>\
				<td><label for="uname">username: </label></td>\
				<td><input type="text" name="uname"/></td>\
				</tr>\
				<tr>\
				<td><label for="password">password: </label></td>\
				<td><input type="password" name="password"/></td>\
				</tr>\
				</table>'
		},

		developers: {
			id: 'developers',
			title: 'Add developer',
			onAddDeveloper: function(){
				if(ownername==username){
					var newDev = $('#developersTable').find('input[name="uname"]').val();
					$.post(Global.url.addDeveloper, {
						newDev: newDev,
						appname: appname,
					}, function(data){
						if(data=="success"){
							$('#developersForm').find('select[name="developers"]')
							.append("<option value='"+newDev+"'>"+newDev+"</option>");
							consolePanel.append('info', 'Success', newDev+" added to project.");
							rwFileAuth.devs.push(newDev);
						}else{
							consolePanel.append('info', 'Error', newDev+" added to project.");
						}
					});
				}else{
					consolePanel.append('info', 'Error', "Only project owner("+ownername+") can add other developer");
				}
			},
			onDelDeveloper: function(){
				if(ownername==username){
					var delDev = $('#developersTable').find('select[name="developers"]').val();
					if(delDev!=ownername){
						$.post(Global.url.delDeveloper, {
							delDev: delDev,
							appname: appname,
						}, function(data){
							if(data=="success"){
								$('#developersForm').find('option[value='+delDev+']').remove();
								consolePanel.append('info', 'Success', delDev+" delete from project.");
								rwFileAuth.removeDev(delDev);
							}else{
								consolePanel.append('info', 'Error', delDev+" delete from project.");
							}
						});
					}else{
						consolePanel.append('info', 'Error', "Can't delete project owner("+ownername+")");
					}
				}else{
					consolePanel.append('info', 'Error', "Only project owner("+ownername+") can delete other developer");
				}
			},
			table: '<table id="developersTable"> \
				<tr>\
				<td><label for="uname">username: </label></td>\
				<td><input type="text" name="uname" placeholder="new developer"/></td>\
				<td><input type="button" onclick="dialog.developers.onAddDeveloper()" value="add" style="width:100%"/></td>\
				<tr>\
				<tr>\
				<td><label for="developers">developers: </label></td>\
				<td><select name="developers" style="width:100%">\
				</select></td>\
				<td><input type="button" onclick="dialog.developers.onDelDeveloper()" value="delete" style="width:100%"/></td>\
				</tr>\
				</table>',
				flushTable: function(){
					$('#developersForm').find('select[name="developers"]').empty()
					.append(rwFileAuth.getDevsHTML());
				}
		},

		rwAuth :{
			id: 'rwAuth' ,
			title: 'Set write authorization',
			tableTemplate: '<table id="rwAuthTable"> \
				<tr>\
				<td><label for="filepath">filepath</label></td>\
				<td><span name="filepath">{filepath}</span></td>\
				</tr>\
				<td><label for="writer">current writer</label></td>\
				<td><span name="writer">{writer}</span></td>\
				<td><input type="button" onclick="rwFileAuth.onrwRevoke()" value="revoke" style="width:100%"/>\
				</tr>\
				<tr>\
				<td><label for="developers">change write auth: </label></td>\
				<td><select name="developers" style="width:100%">\
				{developers}</select>\
				<td><input type="button" onclick="rwFileAuth.onrwGrant()" value="grant" style="width:100%"/>\
				</table>',
				table: '',
				flushTable: function(writer){
					$("#rwAuthDialog").remove();
					var node = explorerPanel.treeView.tree('getSelected');
					var filepath = '/'+appname+'/'+node.attributes.path;
					var table = new String(dialog.rwAuth.tableTemplate);
					dialog.rwAuth.table = table.replace("{filepath}", filepath)
					.replace("{writer}", writer)
					.replace("{developers}", rwFileAuth.getDevsHTML());
					dialog.create($('#dialogs'), dialog.rwAuth);
				},

		},
		
		addPlugin :{
			id: 'addPlugin' ,
			title: 'Install Plugin',
			tableTemplate:
				'<table id="addPluginTable" class="table"> \
				<tr><th>Plugins</th><th>Descripton</th></tr>\
				<tr>\
				<td width=\'200px\'>\
				<ul id="addPluginTreeView">\
				<li data-options="state:\'closed\'"><span>code_analysis</span>\
				<ul>{code_ana}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>code_management</span>\
				<ul>{code_mng}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>compile</span>\
				<ul>{compile}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>model</span>\
				<ul>{model}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>test</span>\
				<ul>{test}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>other</span>\
				<ul>{other}\
				</ul></li>\
				</ul>\
				</td>\
				<td width=\'300px\'><p id = \'description\'></p></td>\
				</tr>\
				</table>'
				
				,
			
				table:'',
				serviceType:['code_ana','code_mng','compile','model','test','other'],
				sevindex :'',
				submitHandler: function(){
					var sevindex = dialog.addPlugin.sevindex;
					$.post(openService.openServiceAPIURL, {
						action : 'addservice',
						serviceid : openService.unauthedServices[sevindex].id,
						userid : userid,
					}, function(data, status) {
						if (status == 'success') {
							
							consolePanel.append('info','Success', 'Add plugin '+openService.unauthedServices[sevindex].serviceName+' success.');
							window.location.reload();
						}
						else{
							consolePanel.append('info','Error', 'Add plugin '+openService.unauthedServices[sevindex].serviceName+' fail.');
						}
						
					});
					
				},
				
				
				flushTable: function(){
					$("#addPluginDialog").remove();
					
					//var node = explorerPanel.treeView.tree('getSelected');
					//var filepath = '/'+appname+'/'+node.attributes.path;
					var sevtype = dialog.addPlugin.serviceType;
					var table = new String(dialog.addPlugin.tableTemplate);
					var sevlist = openService.unauthedServices;
					var sevhtml = ['','','','','','']
					for (i in sevlist) 
						for(j in sevtype)
						if(sevlist[i].serviceType == sevtype[j]){
						
							sevhtml[j]+='<li><span>'+sevlist[i].serviceName+'</span></li>';
							
							break;
						}
				
					for(j in sevhtml){
						table = table.replace("{"+sevtype[j]+"}", sevhtml[j]);
					}
					dialog.addPlugin.table  = table;
					//consolePanel.appendInfo(openService.unauthedServices);
		
					dialog.create($('#dialogs'), dialog.addPlugin);
					
					$('#addPluginTreeView').tree(
							{
								onClick : function(node) {
									var index = openService.unauthSevIndex(node.text);
									dialog.addPlugin.sevindex = index;
									
									var description= openService.unauthedServices[index].description;
									$('#description').html(description);
								
								}
							});
					/*
					$('#addPluginTable li').bind('click',function(){  
						$('#addPluginTable').find("li").removeClass("selected");
						$(this).addClass("selected");
						var index = openService.unauthSevIndex($(this).html());
						dialog.addPlugin.sevindex = index;
						
						var description= openService.unauthedServices[index].description;
						$('#description').html(description);
					});  
					*/
				},

		},
		
		
		delPlugin :{
			id: 'delPlugin' ,
			title: 'Uninstall Plugin',
			tableTemplate: 
				'<table id="delPluginTable" > \
				<tr>\
				<td width=\'200px\'><dl>\
				<ul id="delPluginTreeView">\
				<li data-options="state:\'closed\'"><span>code_analysis</span>\
				<ul>{code_ana}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>code_management</span>\
				<ul>{code_mng}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>compile</span>\
				<ul>{compile}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>model</span>\
				<ul>{model}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>test</span>\
				<ul>{test}\
				</ul></li>\
				<li data-options="state:\'closed\'"><span>other</span>\
				<ul>{other}\
				</ul></li>\
				</ul>\
				</td>\
				</tr>\
				</table>'
				,
			
				table:'',
				serviceType:['code_ana','code_mng','compile','model','test','other'],
				sevindex :'',
				submitHandler: function(){
					var sevindex = dialog.delPlugin.sevindex;
					$.post(openService.openServiceAPIURL, {
						action : 'delservice',
						serviceid : openService.authedServices[sevindex].id,
						userid : userid,
					}, function(data, status) {
						if (status == 'success') {
							
							consolePanel.append('info','Success', 'Delete plugin '+openService.authedServices[sevindex].serviceName+' success.');
							window.location.reload();
						}
						else{
							consolePanel.append('info','Error', 'Delete plugin '+openService.authedServices[sevindex].serviceName+' fail.');
						}
						
					});
					
				},
				
				
				flushTable: function(){
					$("#delPluginDialog").remove();
					//var node = explorerPanel.treeView.tree('getSelected');
					//var filepath = '/'+appname+'/'+node.attributes.path;
					var sevtype = dialog.delPlugin.serviceType;
					var table = new String(dialog.delPlugin.tableTemplate);
					var sevlist = openService.authedServices;
					var sevhtml = ['','','','','','']
					for (i in sevlist) 
						for(j in sevtype)
						if(sevlist[i].serviceType == sevtype[j]){
						
							sevhtml[j]+='<li>'+sevlist[i].serviceName+'</li>';
							
							break;
						}
				
					for(j in sevhtml){
						table = table.replace("{"+sevtype[j]+"}", sevhtml[j]);
					}
					dialog.delPlugin.table  = table;
					//consolePanel.appendInfo(openService.unauthedServices);
		
					dialog.create($('#dialogs'), dialog.delPlugin);
					$('#delPluginTreeView').tree(
							{
								onClick : function(node) {
									var index = openService.authSevIndex(node.text);
									dialog.delPlugin.sevindex = index;
								
								}
							});
					/*
					$('#delPluginTable li').bind('click',function(){  
						$('#delPluginTable').find("li").removeClass("selected");
						$(this).addClass("selected");
						var index = openService.authSevIndex($(this).html());
						dialog.delPlugin.sevindex = index;
						
					
					});  
					*/
				},

		},
		

//		:{
//		id: ,
//		title: ,
//		submitHandler: 
//		table: 
//		},
}