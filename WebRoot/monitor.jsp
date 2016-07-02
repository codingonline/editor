<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="model.*"%>
<%@page import="jdbc.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String token = request.getParameter("token");
	User u = UsrJDBC.findUserByToken(token);
	if (u == null) {
		response.sendRedirect("http://www.poprogramming.com/login");
	} else {
		request.getSession().setAttribute("user", u);
		String appname = request.getParameter("appname");
		App a = AppJDBC.findApp(u.getUsername(), appname);
		if(a==null){
			response.sendRedirect("http://www.poprogramming.com/console");
		} else{
			request.setAttribute("app", a);
		}
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>POP - Online IDE</title>

<!--Load CSS-->
<link type="text/css" rel="stylesheet" href="./css/IdeViewer.css">
<link type="text/css" rel="stylesheet" href="./css/searchbox.css">
<link rel="stylesheet" type="text/css"
	href="./css/themes/gray/easyui.css">
<link rel="stylesheet" type="text/css" href="./css/themes/icon.css">
<script type="text/javascript">
        appid = '${app.id }';
        appname = '${app.appName }';
        apptype = '${app.appType }';
        ownername = '${app.ownerName }';
        username = '${app.userName }';
        write = '${app.write }'
        paasname = '${app.paasName }';
        svnurl = '${app.svnUrl }';
        giturl = '${app.gitUrl }';
        domain = '${app.domain }';
        importurl = '${app.importUrl }';
        domain = '${app.domain }';
        userid = '${user.id}';

        unamebaidu = '${user.unameBaidu }';
        unamesina = '${user.unameSae }';
        pwdbaidu = '${user.pwdBaidu }';
        pwdsina = '${user.pwdSae }';
    </script>
</head>
<body>

	<!--Main UI Blocks-Code in js/viewer/main-->
	<div id="toolbar"></div>
	<div id="explorerpanel" data-options="tools:[
				{
					iconCls:'collapseall',
					handler:function(){
						$('#treeView').tree('collapseAll');
					}
				}]"></div>
	<div id="codepanel"></div>
	<div id="outlinepanel"></div>
	<div id="consolepanel"></div>
	<!-- Other UI Blocks-Code in js/viewers/others-->
	<!-- Show up when click certain button -->
	<div id="dialogs"></div>
	<div id="commentWindow"></div>
	
	<!--Load JS-->
	<script src="js/lib/jquery/jquery-2.1.0.min.js"></script>
	<script src="js/lib/jquery/jquery.form.min.js"></script>
	<script src="js/lib/jquery/jquery.zclip/jquery.zclip.min.js"></script>
	<script src="js/lib/EasyUI/jquery.easyui.min_1.3.js"></script>
	  
	<script src="js/service/GitCmd.js"></script>
	<script src="js/service/RwFileAuth.js"></script>
    <script src="js/service/PHPMethods.js"></script>
    <script src="js/service/Completer.js"></script>
    <script src="js/service/Declaration.js"></script>
	<script src="js/service/FileSearch.js"></script>
    <script src="js/service/DeclarCall.js"></script>
    <script src="js/service/Hierarchy.js"></script>
    <script src="js/service/StdStream.js"></script>
    <script src="js/service/Terminal.js"></script>
    
	<script src="js/monitor/Global.js"></script>	
	<script src="js/monitor/IdeViewer.js"></script>
	<script src="js/monitor/main/OutlinePanel.js"></script>
	<script src="js/monitor/main/toolbar/Toolbar.js"></script>
	<script src="js/monitor/main/codePanel/CodePanel.js"></script>
	<script src="js/monitor/main/codePanel/CodeTab.js"></script>
	<script src="js/monitor/main/ConsolePanel.js"></script>
	<script src="js/monitor/main/ExplorerPanel.js"></script>
	<script src="js/openservice/OpenServiceAPI.js"></script>
	<script src="js/openservice/OpenServiceUIAPI.js"></script>
	<script src="js/openservice/OpenService.js"></script>

    <script src='dwr/engine.js'></script>
    <script src='dwr/interface/DwrDebugIO.js'></script>
    <script src="js/dwrConf.js"></script>
    
	<script src="js/lib/ace/ace.js" ></script>
	<script src="js/lib/ace/emmet.js" ></script>
	<script src="js/lib/ace/ext-emmet.js" ></script>
	<script src="js/lib/ace/ext-modelist.js"></script>
	<script src="js/lib/ace/ext-themelist.js"></script>
	<script src="js/lib/ace/ext-settings_menu.js"></script>
	<script src="js/lib/ace/ext-language_tools.js"></script>
	
	<!--Test Broswer Version-->
	<script type="text/javascript">
		var version = (function() {
			var ua = navigator.userAgent, tem, M = ua
					.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*([\d\.]+)/i)
					|| [];
			if (/trident/i.test(M[1])) {
				tem = /\brv[ :]+(\d+(\.\d+)?)/g.exec(ua) || [];
				return 'IE ' + (tem[1] || '');
			}
			M = M[2] ? [ M[1], M[2] ] : [ navigator.appName,
					navigator.appVersion, '-?' ];
			if ((tem = ua.match(/version\/([\.\d]+)/i)) != null)
				M[2] = tem[1];
			return M;
		})();
		if (version[0] == 'MSIE' && version[1] <= 8) {
			alert('Please use IE 9.0+, chrome or firefox broswer.');
		}
	</script>
	<script type="text/javascript">
		$(function() {
			ideViewer.InitIdeViewer();
		});
	</script>


</body>
</html>