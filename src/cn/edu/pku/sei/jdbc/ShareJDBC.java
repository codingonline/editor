package cn.edu.pku.sei.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.pku.sei.model.Share;

public class ShareJDBC {
	
	public static void insert(Share s) throws Exception{
		String sql = "insert into share (username, appname, token) values ("
				+ convertStr(s.getUsername()) + ", "
				+ convertStr(s.getAppname()) + ", "
				+ convertStr(s.getToken()) + ")";
		new MySQL().execute(sql);
	}
	
	public static void delete(Share s) throws Exception {
		String sql = "delete from share where token="+convertStr(s.getToken());
		new MySQL().execute(sql);
	}
	
	public static List<Share> findUserShares(String username) throws Exception{
		String sql = "select * from share where username="+convertStr(username);
		MySQL mySQL = new MySQL();
		ArrayList<HashMap<String, Object>> list = mySQL.execute(sql);
		if(list==null) return null;
		return Share.getShares(list);
	}
	
	public static Share findShare(String token) throws Exception{
		String sql = "select * from share where token="+convertStr(token);
		MySQL mySQL = new MySQL();
		ArrayList<HashMap<String, Object>> list = mySQL.execute(sql);
		if(list==null)return null;
		return Share.getShares(list).get(0);
	}
	

	public static String convertStr(Object str){
		if(str==null) return "''";
		else return "'" + str.toString().replaceAll(".*([';]+|(--)+).*", " ") + "'";
	}
	
}
