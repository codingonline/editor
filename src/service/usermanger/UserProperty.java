package service.usermanger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.App;
import model.User;
import jdbc.AppJDBC;
import jdbc.UsrJDBC;

public class UserProperty {
	
	public static String getUsername(HttpServletRequest req) {
		String name = ((User) req.getSession().getAttribute("user")).getUsername();
		return name;
	}
	
	public static String getUsername(HttpSession session) {
		String name = ((User) session.getAttribute("user")).getUsername();
		return name;
	}
	
	public static String getUserToken(HttpServletRequest req) {
		String token = ((User) req.getSession().getAttribute("user")).getToken();
		return token;
	}
	
	public static String getUserProjectName(HttpServletRequest req){
//		String name = ((App) req.getSession().getAttribute("app")).getAppName();
		String name = req.getParameter("appname");
		return name;
	}
	
	public static String getUserProjectType(HttpServletRequest req){
//		String type = ((App) req.getSession().getAttribute("app")).getAppType();
		String type = req.getParameter("apptype");
		return type;
	}
	
	public static String getProjectToken(HttpServletRequest req){
		String token = null;
		String projectName = getUserProjectName(req);
		String username = getUsername(req);
		if(projectName!=null&&username!=null){
			try {
				
				App app = (AppJDBC.findApp(username, projectName));
				if(app.getOwnerName().equals(app.getUserName())){
					token = getUserToken(req);
				}
				else token = UsrJDBC.findUserByUname(app.getOwnerName()).getToken();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return token;
	}

}
