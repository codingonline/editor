<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="cn.edu.pku.sei.model.*"%>
<%@page import="cn.edu.pku.sei.jdbc.*"%>
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
			request.getRequestDispatcher("editor.jsp").forward(request, response);
		}
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>POP - Online IDE</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
</head>

<body>
</body>
</html>
