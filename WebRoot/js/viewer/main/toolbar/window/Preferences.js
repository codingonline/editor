preferences = {
		font_size : 12,
		theme : "ace/theme/eclipse",
		themeHTML: '',
		dialogs: {},

		preInit : function(){
			$.get(Global.url.preferences, {
				apptype: apptype,
				appname: appname,
				ownername: ownername
			}, function(data){
				var jo = JSON.parse(data);
				preferences.font_size = jo.font_size;
				preferences.theme = jo.theme;
				preferences.dialog.flushTable();
				dialog.addDialog(preferences.dialog);
			});
			var preferencesDialog = {
					id: 'preferences',
					title: 'Preferences',
					submitHandler: preferences.aceSettings,
					tableTemplate: '<table id="preferencesTable"> \
						<tr>\
						<td><label for="fontsize">font size</label></td>\
						<td><input type="number" name="fontsize" value="{fontsize}"></td>\
						</tr>\
						<tr>\
						<td><label for="theme">theme</label></td>\
						<td><select name="theme" style="width:100%">\
						{theme}</select>\
						</tr>\
						</table>',
						table: '',
						flushTable: function(){
							var table = new String(this.tableTemplate);
							this.table = table.replace("{fontsize}", preferences.font_size)
							.replace("{theme}", preferences.getThemeHTML());
							$("#preferencesForm").empty();
							$("#preferencesForm").append(this.table);
						},
			};
			preferences.dialog=preferencesDialog;
		},

		aceSettings : function(){
			var tfontsize = $("#preferencesTable").find("input[name='fontsize']").val();
			var ttheme = $("#preferencesTable").find("select[name='theme']").val();
			var tab;
			$.post(Global.url.preferences, {
				apptype: apptype,
				appname: appname,
				ownername: ownername,
				fontsize: tfontsize,
				theme: ttheme
			}, function(data){
				if(data=="success"){
					preferences.theme = ttheme;
					preferences.font_size = parseInt(tfontsize);
					for(i in codePanel.openTabs){
						tab = codePanel.openTabs[i];
						tab.editor.setTheme(preferences.theme);
						tab.editor.setFontSize(preferences.font_size);
					}
					preferences.dialog.flushTable();
				}
			});
		},

		getThemeHTML: function(){
			var themelist = ace.require("ace/ext/themelist");
			var selected = $("<optgroup>").attr("label", "selected"), dark = $("<optgroup>").attr("label", "Dark"), light = $("<optgroup>").attr("label", "Light");
			var themes = themelist.themes;
			for(i in themes){
				if(themes[i].theme==preferences.theme){
					selected.append($("<option>").attr({"value": themes[i].theme, "selected": "selected"}).text(themes[i].name));
				}
				else if(themes[i].isDark){
					dark.append($("<option>").attr("value", themes[i].theme).text(themes[i].name));
				}else{
					light.append($("<option>").attr("value", themes[i].theme).text(themes[i].name));
				}
			}
			return selected[0].outerHTML+light[0].outerHTML+dark[0].outerHTML;
		}
}