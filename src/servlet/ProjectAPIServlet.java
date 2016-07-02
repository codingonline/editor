package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.packageexplorer.PackageExplorer;
import service.usermanger.UserPath;
import model.App;
import model.User;
import jdbc.AppJDBC;
import jdbc.UsrJDBC;

public class ProjectAPIServlet extends HttpServlet{

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setHeader("Content-Type", "text/plain;charset=UTF-8");
		String projectName = req.getParameter("projectName");
		String operation = req.getParameter("operation");
		String username;
		String ret = "fail";
		try {
			username = req.getParameter("username");
			PackageExplorer pe = new PackageExplorer();
			if("createProject".equals(operation)){
				String[] type = req.getParameter("projectType").split("_"); // type[0]: project type; type[1]: template type
				String userPath = UserPath.getUserPath(username, type[0]);
				if(AppJDBC.findApp(username, projectName)==null){
					if(type[0].equals("javaweb")){
						if(type.length>1){
							ret = pe.createMvnProject(username, projectName, type[0]+"/"+type[1]);
						}else {
							ret = pe.createMvnProject(username, projectName, type[0]+"/empty");
						}
					}else{
						ret = pe.createProject(userPath+projectName, type[0]);
					}
					App app = new App();
					app.setAppType(type[0]);
					app.setAppName(projectName);
					app.setUserName(username);
					app.setOwnerName(username);
					AppJDBC.insert(app);
				}
			}
			else if ("removeProject".equals(operation)){
				App app = AppJDBC.findApp(username, projectName);
				if(app!=null){
					String projectPath = UserPath.getProjectPath(username, projectName, app.getAppType());
					AppJDBC.delete(app);
					ret = pe.removeProject(projectPath);
				}
			}
			resp.getWriter().println(ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
