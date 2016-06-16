function dwrInit() {
	dwr.engine.setErrorHandler(errorHandler);
}

function receiveBuildMessages(message) {
	consolePanel.append('info', 'Build', message);
}

function receiveDeployMessages(message) {
	consolePanel.append('info', 'Deploy', message);
}

function receiveDebugMessages(message) {
	consolePanel.append('info', 'Debug', message);
}

function receiveAlertMsg(title, message) {
	jQuery.messager.alert(title, message);
}

function dwrAlert(appVisitUrl) {
	var msg = "<a target=\"_blank\" href=\""+ appVisitUrl + "\">" + appVisitUrl + "</a>";
	msg += "<br/>[提示] 项目部署需要时间，如果访问异常，请尝试刷新页面";
	jQuery.messager.alert('点击查看结果', msg);
}

function dwrAlertFailure(message) {
	jQuery.messager.alert('Failure', message);
}

function receiveWatchValue(key, value) {
//	var store = Ext.getCmp("Variables").store;
//	var index = store.findExact("name", key);
//	var record = store.getAt(index);
//	record.set("value",value);
}

function listLocals(json) {
//	var store = Ext.getCmp("Locals").store;
//	store.removeAll(false);
//	store.add(eval(json));
}

function contToNoSuspend(message) {
	Global.ideViewer.codePanel.getActiveTab().removeMarker();
	$("#ResumeButton").linkbutton('disable');
	$("#StepOverButton").linkbutton('disable');
	$("#StepIntoButton").linkbutton('disable');
}

function terminate() {
	//Ext.getCmp('terminate').setDisabled(true);
	dwr.engine.setActiveReverseAjax(false);
	Global.ideViewer.codePanel.getActiveTab().removeMarker();
	Global.isDebuging = false;
	Global.isRunning = false;
}

function currentStopLine(project, path, line) {
	var array = path.split('/');
    var title = array[array.length-1];
	Global.ideViewer.onFileSelect(title, path, project, function(){
    	Global.ideViewer.codePanel.getActiveTab().highlightDebugLine(line);
    });
	$("#TerminateButton").linkbutton('enable');
	$("#ResumeButton").linkbutton('enable');
	$("#StepOverButton").linkbutton('enable');
	$("#StepIntoButton").linkbutton('enable');
}
