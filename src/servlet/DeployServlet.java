package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import service.usermanger.UserPath;
import service.usermanger.UserProperty;
import utils.EnvironmentProperty;
import utils.GitOperation;
import utils.HttpPost;
import utils.SVNOperation;
import model.App;
import model.User;
import jdbc.AppJDBC;
import constants.UrlConstant;

public class DeployServlet extends HttpServlet {

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
		HttpSession session = req.getSession();
		String operation = req.getParameter("operation");
		PrintWriter pw = resp.getWriter();
		String username = ((User) req.getSession().getAttribute("user")).getUsername();
		String projectName = req.getParameter("appname");
		String projectType = req.getParameter("apptype");
		
		try{
			if("run".equals(operation)){
				String msg = applyForRunner(req.getParameterMap());
				System.out.println(msg);
				pw.append(msg);
				pw.close();
			}else if("debug".equals(operation)){
//				String appid = req.getParameter("appid");
//				String msg = "{\"domain\": \"123.56.106.160\", \"code\": \"0\", \"debugport\": 7080, \"sshport\": 4005, \"dockerid\": \"6c5806f84bdb\", \"port\": \"9080\"}";
				System.out.println(projectName);
				String msg = applyForDebugger(username, projectName);
				System.out.println(msg);
				pw.append(msg);
				pw.close();
			}else if("stopdebug".equals(operation)){
				String urlstr= EnvironmentProperty.readConf("StopDebugUrl");
				String arg = "action=delete&dockerid=" + req.getParameter("dockerid");
				System.out.println("debug stop dockerid : " + req.getParameter("dockerid"));
				// ret = {"msg": "success", "code": 1}
				String ret = HttpPost.post(urlstr, arg);
				System.out.println("debug stop result: " + ret);
			}else if("singleRun".equals(operation)){
				String path = req.getParameter("path");
				String ownername = req.getParameter("ownername");
				String msg = applyForSingleRun(ownername, projectName, path);
				System.out.println(msg);
				pw.append(msg);
				pw.close();
			}else if("log".equals(operation)){
				String token = UserProperty.getProjectToken(req);			
				String msg = applyForLogger(username, projectName, token, projectType);
				pw.append(msg);
				pw.close();
			}else if("build".equals(operation)){
				String ownername = req.getParameter("ownername");
				String msg = applyForBuilder(ownername, projectName);
				pw.append(msg);
				pw.close();
			}else if("importFromBAE".equals(operation)){
				String svngit = req.getParameter("svngit");
				String unamebaidu = req.getParameter("unamebaidu");
				String password = req.getParameter("password");

				String projectPath = UserPath.getProjectPath(username, projectName, projectType);
				String ret = "fail";
				if(AppJDBC.findApp(username, projectName)==null&&svngit!=""
						&&unamebaidu!=""&&password!=""){
					App a = new App();
					a.setAppName(projectName);
					a.setAppType(projectType);
					a.setOwnerName(username);
					a.setUserName(username);
					a.setPaasName("bae");
					a.setWrite(true);
					if(svngit.matches("https://svn.*")){
						a.setSvnUrl(svngit);
						if(SVNOperation.checkout(svngit, projectPath, unamebaidu, password)){
							ret = "success";
						}
					}else if(svngit.matches("https://git.*")){
						a.setGitUrl(svngit);
						ret = GitOperation.gitClonePrivate(svngit, projectPath, unamebaidu, password);
					}
					pw.append(ret);
					if("success".equals(ret)){
						AppJDBC.insert(a);
					}
					pw.close();
				} 
			} else if("importFromSAE".equals(operation)){
				String svngit = req.getParameter("svn");
				String unamesina = req.getParameter("unamesina");
				String password = req.getParameter("password");
				String domain = req.getParameter("domain");
				String projectPath = UserPath.getProjectPath(username, projectName, projectType);
				String ret = "fail";
				if(AppJDBC.findApp(username, projectName)==null&&svngit!=""
						&&unamesina!=""&&password!=""){
					App a = new App();
					a.setAppName(projectName);
					a.setAppType(projectType);
					a.setOwnerName(username);
					a.setUserName(username);
					a.setPaasName("sae");
					a.setDomain(domain);
					a.setWrite(true);
					if(svngit.matches("https://svn.*")){
						a.setSvnUrl(svngit);
						if(SVNOperation.checkout(svngit, projectPath, unamesina, password)){
							ret = "success";
						}
					}else if(svngit.matches("https://git.*")){
						//a.setGitUrl(svngit);
						//ret = GitOperation.gitClonePrivate(svngit, projectPath, unamebaidu, password);
					}
					pw.append(ret);
					if("success".equals(ret)) {
						AppJDBC.insert(a);
					}
					pw.close();
				}
			} else if("deploy".equals(operation)){
				String uname = req.getParameter("uname");
				String password = req.getParameter("password");
				String giturl = req.getParameter("giturl");
				String svnurl = req.getParameter("svnurl");
				String projectPath = UserPath.getProjectPath(username, projectName, projectType);
				String ret = "fail";
				if(giturl!=null&&giturl.matches("https://git.*")){
					ret = GitOperation.gitPush(projectPath, giturl, uname, password);
				}
				if(svnurl!=null&&svnurl.matches("https://svn.*")&&SVNOperation.commit(projectPath, uname, password)){
					ret = "success";
				}
				pw.append(ret);
				pw.close();
			}
		} catch(Exception e){
			e.printStackTrace();
		}

	}

	private String applyForRunner(String username, String appid){
		try{
			//String arg="type="+type+"&user="+username+"&token="+token+"&appname="+projectName;
			String arg = "appid="+appid+"&user="+username;
			System.out.println(arg);
			String strResponse = HttpPost.post(UrlConstant.run, arg);
			return strResponse;
		} catch(Exception e){
			e.printStackTrace();
			return "fail";
		}
	}
	
	private String applyForDebugger(String username, String projectName){
		String url = EnvironmentProperty.readConf("RequestDebugUrl");
		String path = "/" + username + "/" + projectName; 
		String arg = "action=run&type=javaweb-debug&path=" + path;
		try {
			String strResponse = HttpPost.post(url, arg);
			return strResponse;
		} catch (IOException e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	private String applyForSingleRun(String username, String projectName, String extra){
		String url = EnvironmentProperty.readConf("RequestSingleRunUrl");
		String path = "/" + username + "/" + projectName;
		String arg = "action=run&type=javaweb-file&path=" + path + "&extra=/" + extra;
		System.out.println(arg);
		try {
			String strResponse = HttpPost.post(url, arg);
			return strResponse;
		} catch (IOException e) {
			e.printStackTrace();
			return "fail";
		}
	}

	private String applyForRunner(Map<String, String[]> param){
		try{
			//String arg="type="+type+"&user="+username+"&token="+token+"&appname="+projectName;
			String arg = "";
			for(String key:param.keySet()){
				arg = arg+key+"="+param.get(key)[0]+"&";
			}
			arg=arg.substring(0, arg.length()-1);
			System.out.println(arg);
			String strResponse = HttpPost.post(UrlConstant.run, arg);
			return strResponse;
		} catch(Exception e){
			e.printStackTrace();
			return "fail";
		}
	}
	
	private String applyForLogger(String username, String projectName, String token, String type){
		try{
			String arg="type="+type+"&user="+username+"&token="+token+"&appname="+projectName;
			System.out.println(arg);
			String strResponse = HttpPost.post(UrlConstant.log, arg);		
			return strResponse;
		} catch(Exception e){
			e.printStackTrace();
			return "fail";
		}
	}
	
	private String applyForBuilder(String username, String projectName){
		try{
			String arg="type=java&username="+username+"&appname="+projectName;
			System.out.println(arg);
			String strResponse = HttpPost.post(UrlConstant.build, arg);
			return strResponse;
		} catch(Exception e){
			e.printStackTrace();
			return "fail";
		}
	}
}
