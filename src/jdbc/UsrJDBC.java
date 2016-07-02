package jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.User;

public class UsrJDBC {
	
	public static User findUserByToken(String token) throws Exception{
		User u = findUserByProperty("token='"+token+"'");
		return u;
	}
	
	public static User findUserByUname(String username) throws Exception{
		User u = findUserByProperty("username='"+username+"'");
		return u;
	}
	
	public static User findUserByProperty(String props) throws Exception{
		List<User> list = findUsersByProperty(props);
		if(list==null||list.size()==0){
			return null;
		}
		User user = list.get(0);
		return user;
	}

	public static List<User> findUsersByProperty(String props) throws Exception{
		String sqlString = "select * from user where " + props;
		MySQL mySQL = new MySQL();
		ArrayList<HashMap<String, Object>> list = mySQL.execute(sqlString);
		if(list==null) return null;
		return User.getUsers(list);
	}

	public static void update(User u) throws Exception{		
		String sqlString = "update user set "
				+ "username=" + convertStr(u.getUsername())   
				+ ", password=" + convertStr(u.getPassword())  
				+ ", last_login=" + convertStr(u.getLastLogin().toString())  
				+ ", is_super=" + (u.getIsSuper()==false?"0":"1")  
				+ ", email=" + convertStr(u.getEmail())  
				+ ", date_joined=" + convertStr(u.getDateJoined())  
				+ ", uname_baidu=" + convertStr(u.getUnameBaidu())  
				+ ", uname_sina=" + convertStr(u.getUnameSina())  
				+ ", uname_sae=" + convertStr(u.getUnameSae())  
				+ ", uname_code=" + convertStr(u.getUnameCode())
				+ ", pwd_baidu=" +  convertStr(u.getPwdBaidu())
				+ ", pwd_sae=" +  convertStr(u.getPwdSae())
				+ ", register_time=" + convertStr(u.getRegisterTime())
				+ ", validate_code=" + convertStr(u.getValidateCode())
				+ ", register_status=" + (u.getRegisterStatus()==false?"0":"1")
				+ ", token=" + convertStr(u.getToken())
				+ ", is_remembered=" + (u.getIsRemembered()==false?"0":"1") 
				+ " where id=" + u.getId();
		new MySQL().execute(sqlString);
	}

	public static void insert(User u) throws Exception{
		String sqlString = "insert into user (username, password, last_login, is_super, email, date_joined, " 
				+ "uname_baidu, uname_sina, uname_sae, uname_code, pwd_baidu, pwd_sae, register_time, validate_code, register_status, token, is_remembered) "
				+ "values (" 
				+ convertStr(u.getUsername()) + ", " + convertStr(u.getPassword()) + ", " + convertStr(u.getLastLogin()) + ", "
				+ (u.getIsSuper()==false?"0":"1") + ", " + convertStr(u.getEmail()) + ", " + convertStr(u.getDateJoined()) + ", "
				+ convertStr(u.getUnameBaidu()) + ", " + convertStr(u.getUnameSina()) + ", " + convertStr(u.getUnameCode()) + ", " 
				+ convertStr(u.getRegisterTime()) + ", " + convertStr(u.getValidateCode()) + ", " + (u.getRegisterStatus()==false?"0":"1") + ", "
				+ convertStr(u.getToken()) + ", " + (u.getIsRemembered()==false?"0":"1")
				+ ")";
		new MySQL().execute(sqlString);			
	}

	public static String convertStr(Object str){
		if(str==null) return "''";
		else return "'" + str.toString().replaceAll(".*([';]+|(--)+).*", " ") + "'";
	}
	
}
