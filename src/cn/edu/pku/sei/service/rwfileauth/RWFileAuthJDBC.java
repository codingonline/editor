package cn.edu.pku.sei.service.rwfileauth;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.pku.sei.jdbc.MySQL;
import cn.edu.pku.sei.model.RWFile;

public class RWFileAuthJDBC {

	public static void insert(RWFile auth) throws Exception{
		String sqlString = MessageFormat.format("insert into rwfileauth(path, username, apptype, ownername) values ({0}, {1}, {2}, {3})", 
				MySQL.convertStr(auth.getPath()), MySQL.convertStr(auth.getUsername()), MySQL.convertStr(auth.getApptype()), MySQL.convertStr(auth.getOwnername()));
		new MySQL().execute(sqlString);
	}
	
	// 如果数据库中不存在，则插入新纪录,如存在path的记录，则更新username
	public static void update(RWFile auth) throws Exception{
		String sqlString = MessageFormat.format("select * from rwfileauth where path={0} and apptype={1} and ownername={2}", 
				MySQL.convertStr(auth.getPath()), MySQL.convertStr(auth.getApptype()), MySQL.convertStr(auth.getOwnername()));
		List<RWFile> rwFiles = select(sqlString);
		if(rwFiles==null){
			insert(auth);
		}else {
			sqlString = MessageFormat.format("update rwfileauth set username={0} where path={1} and apptype={2} and ownername={3}", 
					MySQL.convertStr(auth.getUsername()), MySQL.convertStr(auth.getPath()), MySQL.convertStr(auth.getApptype()), MySQL.convertStr(auth.getOwnername()));
			new MySQL().execute(sqlString);
		}
	}
	
	public static void update(RWFile oldauth, RWFile newauth) throws Exception{
		String sqlString = MessageFormat.format("select * from rwfileauth where path={0} and apptype={1} and ownername={2}", 
				MySQL.convertStr(oldauth.getPath()), MySQL.convertStr(oldauth.getApptype()), MySQL.convertStr(oldauth.getOwnername()));
		List<RWFile> rwFiles = select(sqlString);
		if(rwFiles!=null){
			sqlString = MessageFormat.format("select * from rwfileauth where path={0} and apptype={1} and ownername={2}", 
					MySQL.convertStr(newauth.getPath()), MySQL.convertStr(newauth.getApptype()), MySQL.convertStr(newauth.getOwnername()));
			rwFiles = select(sqlString);
			if(rwFiles==null){
				sqlString = MessageFormat.format("update rwfileauth set path={0} where path={1} and apptype={2} and ownername={3}", 
						MySQL.convertStr(newauth.getPath()), MySQL.convertStr(oldauth.getPath()), MySQL.convertStr(oldauth.getApptype()), MySQL.convertStr(oldauth.getOwnername()));
				new MySQL().execute(sqlString);
			}
		}
	}
	
	public static List<RWFile> select(String sqlString) throws Exception{
		ArrayList<HashMap<String, Object>> list = new MySQL().execute(sqlString);
		if(list==null) return null;
		else return RWFile.getRWFileAuths(list);
	}
	
	public static void delete(RWFile rwFile) throws Exception{
		String sqlString = MessageFormat.format("delete from rwfileauth where path={0} and apptype={1} and ownername={2}", MySQL.convertStr(rwFile.getPath()), MySQL.convertStr(rwFile.getApptype()), MySQL.convertStr(rwFile.getOwnername()));
		new MySQL().execute(sqlString);
	}
	
}
