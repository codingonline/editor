package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Monitor {

	private Integer id;
	private String path;
	private String operation;
	private String appname;
	private String username;
	private String content;
	private Timestamp time;

	public Monitor(HashMap<String, Object> map) {
		this((String) map.get("username"), (String) map.get("appname"),
				(String) map.get("path"), (String) map.get("operation"),
				(String) map.get("content"), (Timestamp) map.get("time"));
	}

	public Monitor(String username, String appname, String path,
			String operation, String content, Timestamp time) {
		this.path = path;
		this.operation = operation;
		this.appname = appname;
		this.username = username;
		this.content = content;
		this.time = time;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String opertaion) {
		this.operation = opertaion;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getContent() {
		return content;
	}

	public Timestamp getTime() {
		return time;
	}

	public static List<Monitor> getMonitors(ArrayList<HashMap<String, Object>> list) {
		List<Monitor> monitors = new ArrayList<Monitor>();
		for(HashMap<String, Object> mu : list){
			monitors.add(new Monitor(mu));
		}
		return monitors;
	}

}
