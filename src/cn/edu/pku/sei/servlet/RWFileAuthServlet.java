package cn.edu.pku.sei.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.sei.jdbc.AppJDBC;
import cn.edu.pku.sei.model.User;
import cn.edu.pku.sei.service.rwfileauth.RWFileAuth;

public class RWFileAuthServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String ret = "";
		String operation = req.getParameter("operation");
		String filepath = req.getParameter("filepath");
		String ownername = req.getParameter("ownername");
		String apptype = req.getParameter("apptype");
		try {
			if (operation.equals("getDevelopers")) {
				ret = AppJDBC.findDevelopers(filepath, apptype, ownername);
			}
			resp.setHeader("Content-Type", "text/plain;charset=UTF-8");
			resp.getWriter().print(ret);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String operation = req.getParameter("operation");
		String filepath = req.getParameter("path");
		String actionUsername = ((User) req.getSession().getAttribute("user")).getUsername();
		String username = req.getParameter("username");
		String ownername = req.getParameter("ownername");
		String apptype = req.getParameter("apptype");
		String retString = "fail";
		if("grant".equals(operation)){	
			if(RWFileAuth.grant(actionUsername, username, filepath, ownername, apptype)){
				retString = "success";
			}
		}else if ("revoke".equals(operation)) {
			if(RWFileAuth.revoke(actionUsername, username, filepath, ownername, apptype)){
				retString = "success";
			}
		}
		resp.setHeader("Content-Type", "text/plain;charset=UTF-8");
		resp.getWriter().print(retString);
	}

}
