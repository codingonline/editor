package cn.edu.pku.sei.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.AcceptPendingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import cn.edu.pku.sei.model.User;
import cn.edu.pku.sei.service.git.gitCmd;
import cn.edu.pku.sei.service.usermanger.UserPath;

public class GitCmdServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONObject jObject= new JSONObject();
		String retString = "";
		String cmdString = req.getParameter("cmd");
		String ownername = req.getParameter("ownername");
		String appname = req.getParameter("appname");
		String type = req.getParameter("type");
		String projectPath = UserPath.getProjectPath(ownername, appname, type);
		if("status".equals(cmdString)){
			jObject.put("msg", gitCmd.status(projectPath));
		}else if("init".equals(cmdString)){
			jObject.put("msg", gitCmd.init(projectPath));
		}else if("add".equals(cmdString)){
			jObject.put("msg", gitCmd.add(projectPath));
		}else if ("commit".equals(cmdString)) {
			String msg = req.getParameter("message");
			User user = (User) req.getSession().getAttribute("user");
			String name = user.getUsername();
			String email = user.getEmail();
			jObject.put("msg", gitCmd.commit(projectPath, name, email, msg));
		}else if ("push".equals(cmdString)) {
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			String remote = req.getParameter("remote");
			jObject.put("msg", gitCmd.push(projectPath, username, password, remote));
		}else if("branch".equals(cmdString)) {
			jObject.put("msg", gitCmd.branch(projectPath));
		}else if ("branchadd".equals(cmdString)) {
			String newBranchName = req.getParameter("newBranchName");
			jObject.put("msg", gitCmd.branchAdd(projectPath, newBranchName));
		}else if ("branchdel".equals(cmdString)) {
			String delBranchName = req.getParameter("delBranchName");
			jObject.put("msg", gitCmd.branchDel(projectPath, delBranchName));
		}else if ("branchrename".equals(cmdString)) {
			String oldBranchName = req.getParameter("oldBranchName");
			String newBranchName = req.getParameter("newBranchName");
			jObject.put("msg", gitCmd.branchRename(projectPath, oldBranchName, newBranchName));
		}else if ("checkout".equals(cmdString)) {
			String branchName = req.getParameter("branchName");
			jObject.put("msg", gitCmd.gitCheckout(projectPath, branchName));
		}

		try(PrintWriter writer = resp.getWriter()){
			resp.setHeader("Content-Type", "text/plain;charset=UTF-8");
			writer.print(jObject.toJSONString());
		}
	}

}
