gitCmd = {
		
		dialogs : {},
		
		afterInit: function(){
			var commit = {
					id: 'gitCommit',
					title: "git commit",
					submitHandler: gitCmd.gitCommit,
					table: '<table id="gitCommitTable"> \
						<tr>\
						<td><label for="message">commit message: </label></td>\
						<td><input type="text" name="message"/></td>\
						</tr>\
						</table>'
			};
			var push = {
					id: 'gitPush',
					title: 'git push',
					submitHandler: gitCmd.gitPush,
					table: '<table id="gitPushTable"> \
						<tr>\
						<td><label for="remote">remote(HTTPS): </label></td>\
						<td><input type="text" name="remote"/></td>\
						</tr>\
						<tr>\
						<td><label for="uname">username: </label></td>\
						<td><input type="text" name="uname"/></td>\
						</tr>\
						<tr>\
						<td><label for="password">password: </label></td>\
						<td><input type="password" name="password"/></td>\
						</tr>\
						</table>',
			};
			dialog.addDialog(commit, push);
			
			var branchAdd = {
					id: 'gitBranchAdd',
					title: 'new branch',
					submitHandler: gitCmd.gitBranchAdd,
					table: '<table id="gitBranchAddTable"> \
						<tr>\
						<td><label for="newBranchName">new branch name</label></td>\
						<td><input type="text" name="newBranchName"/></td>\
						</tr>\
						</table>',
			}
			dialog.addDialog(branchAdd);
			
			var branchRename = {
					id: 'gitBranchRename',
					title: 'rename branch',
					submitHandler: gitCmd.gitBranchRename,
					table: '<table id="gitBranchRenameTable"> \
						<tr>\
						<td><label for="newBranchName">new branch name</label></td>\
						<td><input type="text" name="newBranchName"/></td>\
						</tr>\
						</table>',
			}
			dialog.addDialog(branchRename);
			gitCmd.dialogs['branchRename'] = branchRename;
			
			var branch = {
					id: 'gitBranch',
					title: 'git branch',
					handlers: [{text: 'Checkout', handler: gitCmd.gitCheckout }, 
					          {text: 'Create', handler: function(){$("#gitBranchAddDialog").dialog("open")}}, 
					          {text: 'Delete', handler: gitCmd.gitBranchDel},
					          {text: 'Rename', handler: function(){$("#gitBranchRenameDialog").dialog("open");
					          var oldBranchName = $("#gitBranchTable").find("select[name='branch']").val();
					          $("#gitBranchRenameTable").find("input[name='newBranchName']").val(oldBranchName)}}],
					tableTemplate: '<table id="gitBranchTable"> \
						<tr>\
						<td><label for="branch">branch </label></td>\
						<td><select style="width:100%" name="branch">{branches}</select></td>\
						</tr>\
						</table>',
					table: '<table id="gitBranchTable"> \
						<tr>\
						<td><label for="branch">branch </label></td>\
						<td><select style="width:100%" name="branch">\
						<option value="refs/heads/master" selected="selected">refs/heads/master</option></td>\
						</select></tr>\
						</table>',
					flushTable: function(data){
						console.log(data);
						var table = new String(this.tableTemplate);
						this.table = table.replace("{branches}", data);
						$("#gitBranchForm").empty();
						$("#gitBranchForm").append(this.table);
					}
			};
			gitCmd.dialogs['branch'] = branch;
			dialog.addDialog(branch);
			
		},

		gitInit: function(){
			gitCmd.post("init");
		},

		gitStatus: function(){
			gitCmd.post("status");
		},

		gitAdd: function(){
			gitCmd.post("add");
		},

		gitCommit: function(){
			var msg = $("#gitCommitTable").find("input[name='message']").val();
			gitCmd.post('commit', {message: msg});
			$('#gitCommitDialog').dialog('close');
		},
		
		gitPush: function(){
			var table = $('#gitPushTable');
			var uname = table.find('input[name="uname"]').val();
			var password = table.find('input[name="password"]').val();
			var remote = table.find('input[name="remote"]').val();
			gitCmd.print('Push '+appname+' to '+remote);
			gitCmd.post("push", {remote: remote, username: uname, password: password})
		},
		
		gitBranchQuery: function(){
			var param = {
					cmd: "branch",
					ownername: ownername,
					type: apptype,
					appname: appname,
				};
				$.post("gitcmd", param, function(data){
					var jo = JSON.parse(data);
					console.log(jo);
					var head = $("<optgroup>").attr("label", "HEAD"), local = $("<optgroup>").attr("label", "local"), remote = $("<optgroup>").attr("label", "remote");
					var branches = JSON.parse(jo.msg), branch;
					for(i in branches){
						branch = branches[i];
						if(branch.attr=="HEAD"){
							head.append($("<option>").attr({"value": branch.name, "selected": "selected"}).text(branch.name));
						}else if(branch.attr=="local"){
							local.append($("<option>").attr("value", branch.name).text(branch.name));
						}else if(branch.attr=='remote'){
							remote.append($("<option>").attr("value", branch.name).text(branch.name));
						}
					}
					gitCmd.dialogs.branch.flushTable(head[0].outerHTML+local[0].outerHTML+remote[0].outerHTML)
				});
		},
		
		gitBranchAdd: function(){
			var newBranchName = $('#gitBranchAddTable').find('input[name="newBranchName"]').val();
			gitCmd.post("branchadd", {newBranchName: newBranchName}, function(){
				$("#gitBranchAddDialog").dialog("close");
				gitCmd.gitBranchQuery();
			});	
		},
		
		gitBranchDel: function(){
			var delBranchName = $("#gitBranchTable").find("select[name='branch']").val();
			gitCmd.post("branchdel", {delBranchName: delBranchName}, function(){
				gitCmd.gitBranchQuery();
			});
		},
		
		gitBranchRename: function(oldname){
			var oldBranchName = $("#gitBranchTable").find("select[name='branch']").val();
			var newBranchName = $('#gitBranchRenameTable').find('input[name="newBranchName"]').val();
			gitCmd.post("branchrename", {oldBranchName: oldBranchName, newBranchName: newBranchName}, function(){
				$("#gitBranchRenameDialog").dialog("close");
				gitCmd.gitBranchQuery();
			});
		},
		
		gitCheckout: function(){
			var branchName = $("#gitBranchTable").find("select[name='branch']").val();
			gitCmd.post("checkout", {branchName: branchName}, function(){
				Global.refreshExplorer();
				gitCmd.gitBranchQuery();
				var tab;
				for(i in codePanel.openTabs){
					tab = codePanel.openTabs[i];
					$("#codearea").tabs('close', tab.tabName);
				}
			});
		},

		post: function(cmd, options, callback){
			var param = $.extend({
				cmd: cmd,
				ownername: ownername,
				type: apptype,
				appname: appname,
			}, options)
			$.post("gitcmd", param, function(data){
				var jo = JSON.parse(data);
				gitCmd.print(jo.msg);
				if(callback!=null&&callback!=undefined){callback()}
			});
		},

		print: function(msg){
			consolePanel.append('info', "git", msg);
		},


}