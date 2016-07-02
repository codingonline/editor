package jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import service.rwfileauth.RWFileAuth;
import model.App;

public class AppJDBC {
	
	public static App findApp(String username, String appname) throws Exception{
		App app = AppJDBC.findAppByProperty("user_name="+convertStr(username)+" and app_name="+convertStr(appname));
		return app;
	}
	
	public static App findAppByProperty(String props) throws Exception{
		List<App> list = findAppsByProperty(props);
		if(list==null||list.size()==0){
			return null;
		}
		App app = list.get(0);
		return app;
	}

	public static List<App> findAppsByProperty(String props) throws Exception{
		String sqlString = "select * from app where " + props + " order by id desc";
		MySQL mySQL = new MySQL();
		ArrayList<HashMap<String, Object>> list = mySQL.execute(sqlString);
		if(list==null) return null;
		return App.getApps(list);
	}
	
	public static String findDevelopers(String filepath, String type,
			String ownername) throws Exception {
		// TODO Auto-generated method stub
		String projectName = filepath.substring(1, filepath.indexOf('/', 1));
		
		String where = " app_name="+convertStr(projectName)+" and app_type="+convertStr(type)+" and owner_name="+convertStr(ownername);
		List<App> apps = findAppsByProperty(where);
		
		String authUserString = RWFileAuth.getAuthorizedUser(filepath, ownername, type).get(0);
		String ret = "[\""+authUserString+"\"";
		Iterator<App> itr = apps.iterator();
		while(itr.hasNext()){
			String usernameString = itr.next().getUserName();
			if(!usernameString.equals(authUserString)){
				ret+=",\""+usernameString+"\"";
			}
		}
		ret+="]";
		return ret;
	}

	public static void update(App a) throws Exception{		
		String sqlString = "update app set "
				+ "app_name=" + convertStr(a.getAppName())   
				+ ", app_type=" + convertStr(a.getAppType())  
				+ ", user_name=" + convertStr(a.getUserName())  
				+ ", owner_name=" + convertStr(a.getOwnerName())  
				+ ", `write`=" + (a.getWrite()==false?"0":"1")  
				+ ", paas_name=" + convertStr(a.getPaasName())  
				+ ", svn_url=" + convertStr(a.getSvnUrl())  
				+ ", git_url=" + convertStr(a.getGitUrl())  
				+ ", domain=" + convertStr(a.getDomain())
				+ ", date=" + convertStr(a.getDate().toString())	
				+ ", import_url=" + convertStr(a.getImportUrl())  
				+ ", save_data=" + (a.getSaveData() ==false?"0":"1")
				+ " where id=" + a.getId();
		new MySQL().execute(sqlString);
	}

	public static void insert(App a) throws Exception{
		String sqlString = "insert into app (app_name, app_type, user_name, owner_name, `write`, paas_name, " 
				+ "svn_url, git_url, domain, date, import_url,save_data) "
				+ "values (" 
				+ convertStr(a.getAppName()) + ", " + convertStr(a.getAppType())  + ", " + convertStr(a.getUserName()) + ", "
				+ convertStr(a.getOwnerName()) + ", " + (a.getWrite()==false?"0":"1") + ", " + convertStr(a.getPaasName()) + ", "
				+ convertStr(a.getSvnUrl()) + ", " + convertStr(a.getGitUrl()) + ", " + convertStr(a.getDomain()) + ", " 
				+ convertStr(a.getDate().toString()) + ", " + convertStr(a.getImportUrl()) + ", " + (a.getSaveData() ==false?"0":"1")
				+ ")";
		new MySQL().execute(sqlString);			
	}
	
	public static void delete(App a) throws Exception{
		String sqlString = "delete from app where app_name=" + convertStr(a.getAppName()) + " and user_name=" + convertStr(a.getUserName());
		new MySQL().execute(sqlString);
	}

	public static String convertStr(Object str){
		if(str==null) return "''";
		else return "'" + str.toString().replaceAll(".*([';]+|(--)+).*", " ") + "'";
	}

	
}
