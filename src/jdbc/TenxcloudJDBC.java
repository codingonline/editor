package jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.App;
import model.UserTenxcloud;

public class TenxcloudJDBC {
	
	public static UserTenxcloud findUserByName(String name)  throws Exception{
		UserTenxcloud user = TenxcloudJDBC.findUserByProperty("username="+convertStr(name));
		return user;
	}
	
	public static UserTenxcloud findUserByProperty(String props) throws Exception{
		List<UserTenxcloud> list = findUsersByProperty(props);
		if(list==null||list.size()==0){
			return null;
		}
		UserTenxcloud user= list.get(0);
		return user;
	}
	
	public static List<UserTenxcloud> findUsersByProperty(String props) throws Exception{
		String sqlString = "select * from user_tenxcloud where " + props + " order by username desc";
		MySQL mySQL = new MySQL();
		ArrayList<HashMap<String, Object>> list = mySQL.execute(sqlString);
		if(list == null) 
			return null;
		return UserTenxcloud.getUsers(list);
	}
	
	public static void update(UserTenxcloud u) throws Exception{		
		String sqlString = "update user_tenxcloud set "
				+ "username_tenxcloud=" + convertStr(u.getUsernameTenxcloud())   
				+ ", token=" + convertStr(u.getToken())  
				+ ", service_address=" + convertStr(u.getServiceAddress())  
				+ ", user_status=" + convertStr(u.getUserStatus()) 
				+ ", service_type=" + convertStr(u.getServiceType())
				+ ", service_name=" + convertStr(u.getServiceName())
				+ " where username=" + convertStr(u.getUsername());
		new MySQL().execute(sqlString);
	}

	public static void insert(UserTenxcloud u) throws Exception{
		String sqlString = "insert into user_tenxcloud (username, username_tenxcloud, token, service_address, user_status) "
				+ "values (" 
				+ convertStr(u.getUsername()) + ", " 
				+ convertStr(u.getUsernameTenxcloud())  + ", " 
				+ convertStr(u.getToken()) + ", "
				+ convertStr(u.getServiceAddress()) + ", "
				+ convertStr(u.getUserStatus()) + ", "
				+ convertStr(u.getServiceType()) + ", "
				+ convertStr(u.getServiceName())
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
