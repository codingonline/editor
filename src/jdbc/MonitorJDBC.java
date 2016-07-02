package jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Monitor;

public class MonitorJDBC {

	public static void insert(Monitor m) throws Exception {
		String sqlString = "insert into monitor (username, appname, path, operation, content, time) "
				+ "values ('"
				+ m.getUsername()
				+ "', '"
				+ m.getAppname()
				+ "', '"
				+ m.getPath()
				+ "', '"
				+ m.getOperation()
				+ "', '"
				+ m.getContent() + "', '" + m.getTime().toString() + "')";
		new MySQL().execute(sqlString);
	}

	public static List<Monitor> find(String username, String appname, String path) throws Exception {
		String sqlString = "select * from monitor where username='" + username
				+ "' and appname='" + appname + "' and path='" + path + "'";
		ArrayList<HashMap<String, Object>> list = new MySQL().execute(sqlString);
		if (list == null)
			return null;
		return Monitor.getMonitors(list);
	}

}
