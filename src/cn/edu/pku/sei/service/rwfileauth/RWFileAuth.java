package cn.edu.pku.sei.service.rwfileauth;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.jdbc.MySQL;
import cn.edu.pku.sei.model.RWFile;

public class RWFileAuth {

	public static boolean grant(String actionUsername, String username, String path, String ownername, String apptype){
		boolean success = false;
		if(isAuthorized(actionUsername, path, "grant", ownername, apptype)){
			try {
				RWFileAuthJDBC.update(new RWFile(path, ownername, apptype, username));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			success = true;
		}
		return success;
	}

	public static boolean revoke(String actionUsername, String username, String path, String ownername, String apptype){
		boolean success = false;
		if(isAuthorized(actionUsername, path, "revoke", ownername, apptype)){
			try {
				RWFileAuthJDBC.update(new RWFile(path, ownername, apptype, username));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			success = true;
		}
		return success;
	}

	private static List<RWFile> getRWFileAuth(String appname, String ownername, String apptype) throws Exception{
		String sqlString = MessageFormat.format("select * from rwfileauth where path like {0} and ownername={1} and apptype={2} order by length(path) desc", 
				MySQL.convertStr(appname+"%"), MySQL.convertStr(ownername), MySQL.convertStr(apptype));
		return RWFileAuthJDBC.select(sqlString);
	}

	public static List<String> getAuthorizedUser(String path, String ownername, String apptype) throws Exception{
		List<RWFile> auths = getRWFileAuth(getAppname(path), ownername, apptype);
		List<String> users = new ArrayList<String>();
		for(RWFile auth : auths){
			if(auth.getPath().length()<=path.length()){
				String subpath = path.substring(0, auth.getPath().length());
				if(auth.getPath().equals(subpath)){
					users.add(auth.getUsername());
				}
			}
		}
		return users;
	}

	public static boolean isAuthorized(String username, String rawpath, String action, String ownername, String apptype){
		String path;
		if(rawpath.charAt(0)!='/'){
			path = "/"+rawpath;
		}else{
			path = new String(rawpath);
		}
		try {			
			if("write".equals(action)){
				List<String> userStrings = getAuthorizedUser(path, ownername, apptype);
				if(userStrings.get(0).equals(username)){
					return true;
				}else {
					return false;
				}
			}else if ("grant".equals(action)||"revoke".equals(action)||"create".equals(action)||"delete".equals(action)||"rename".equals(action)) {
				if(path.lastIndexOf('/')==path.length()-1){
					path = path.substring(0, path.length()-1);
				}else{
					path = path.substring(0);
				}
				path = path.substring(0, path.lastIndexOf('/')+1);
				List<String> userStrings = getAuthorizedUser(path, ownername, apptype);
				for(String userString : userStrings){
					if(userString.equals(username)){
						return true;
					}
				}
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static String getAppname(String path){
		int endIndex = path.indexOf("/", 1);
		return path.substring(0, endIndex+1);
	}

	public static void main(String[] args) throws Exception{

	}

}
