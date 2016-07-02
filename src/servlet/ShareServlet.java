package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.usermanger.UserPath;
import utils.DirectoryCopy;
import utils.MD5Util;
import model.App;
import model.Share;
import model.User;
import jdbc.AppJDBC;
import jdbc.ShareJDBC;
import jdbc.UsrJDBC;
import constants.PathConstant;

public class ShareServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String token = req.getRequestURI().substring(7);
		String username = "guest";
		if(req.getSession().getAttribute("user")!=null){
			username = ((User) req.getSession().getAttribute("user")).getUsername();
		}
		try{
			User u = UsrJDBC.findUserByUname(username);
			Share s = ShareJDBC.findShare(token);
			App a = AppJDBC.findApp(s.getUsername(), s.getAppname());
			String sharePath = PathConstant.SHARE+a.getAppName()+"_"+s.getToken();
			
			a.setAppName(a.getAppName()+"_"+MD5Util.getToken().substring(0, 8));
			a.setUserName(username);
			a.setOwnerName(username);
			AppJDBC.insert(a);
			String projectPath = UserPath.getProjectPath(username, a.getAppName(), a.getAppType());
			DirectoryCopy dc = new DirectoryCopy(sharePath, projectPath);
			try {
				dc.copy();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resp.sendRedirect("/?token="+u.getToken()+"&appname="+a.getAppName());
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String operation = req.getParameter("operation");
		String appname = req.getParameter("appname");
		String username = ((User) req.getSession().getAttribute("user")).getUsername();		
		String ret = "fail";
		try{
			App a = AppJDBC.findApp(username, appname);
			if("genShareUrl".equals(operation)){
				// only app owner can share the app files
				if(a.getOwnerName().equals(username)){
					String token = MD5Util.getToken().substring(0, 8);
					String sharePath = PathConstant.SHARE+appname+"_"+token;
					String projectPath = UserPath.getProjectPath(a.getOwnerName(), appname, a.getAppType());
					DirectoryCopy dc = new DirectoryCopy(projectPath, sharePath);
					try {
						dc.copy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Share s = new Share(username, appname, token);
					ShareJDBC.insert(s);
					ret = "/share/"+token;
				}
			}else if("saveApp".equals(operation)){
				username = req.getParameter("username");
				String password = req.getParameter("password");
				User u = UsrJDBC.findUserByUname(username);
				if(u.getPassword().equals(MD5Util.encode2hex(password+MD5Util.SALT))){
					String sourcePath = UserPath.getProjectPath("guest", appname, a.getAppType());
					String destPath = UserPath.getProjectPath(username, appname, a.getAppType());
					DirectoryCopy dc = new DirectoryCopy(sourcePath, destPath);
					try {
						dc.copy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					App aa = new App();
					aa.setOwnerName(username);
					aa.setUserName(username);
					aa.setAppType(a.getAppType());
					aa.setAppName(a.getAppName());
					AppJDBC.insert(aa);
					String token = UsrJDBC.findUserByUname(username).getToken();
					ret = "/?token="+token+"&appname="+appname;
				}
			}else if ("addDeveloper".equals(operation)) {
				String newDev = req.getParameter("newDev");
				a.setUserName(newDev);
				AppJDBC.insert(a);
				ret = "success";
			}else if ("delDeveloper".equals(operation)) {
				String delDev = req.getParameter("delDev");
				a.setUserName(delDev);
				AppJDBC.delete(a);
				ret = "success";
			}
			resp.setHeader("Content-Type", "text/plain;charset=UTF-8");
			resp.getWriter().write(ret);
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
