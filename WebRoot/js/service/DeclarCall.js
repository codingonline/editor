var declarcall = {
		openDeclaration: function(){
			var tab = codePanel.getActiveTab();
			var editor = tab.editor;
    		var session = tab.editor.session; 

    		var selection = tab.editor.selection;
    		if(text==""){
				text = session.getTextRange(selection.selectAWord());
			}
    		var text = session.getTextRange(selection.getRange());
			console.log(text);
			console.log(selection.getRange());

    		pos = editor.getCursorPosition();
    		declaration.getDeclaration(session, pos);


		},
		
		callHierarchy: function(){
			var tab = codePanel.getActiveTab();
			var editor = tab.editor;
    		var session = tab.editor.session; 
    		pos = editor.getCursorPosition();
    		hierarchy.getCallHierarchy(session, pos);
		}
}