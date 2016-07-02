package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.DateUtil;

public class Share {

	private Integer id;
	private String username;
	private String appname;
	private Timestamp time;
	private String token;
	
	public Share(String username, String appname, String token){
		this.username = username;
		this.appname = appname;
		this.token = token;
	}
	
	public Share(Integer id, String username, String appname, Timestamp time, String token){
		this.id = id;
		this.username = username;
		this.appname = appname;
		this.time = time;
		this.token = token;
	}
	
	public Share(HashMap<String, Object> map) {
		this((Integer)map.get("id"), (String)map.get("username"), (String)map.get("appname"),
				(Timestamp)map.get("time"), (String)map.get("token"));
	}
	
	public static List<Share> getShares(ArrayList<HashMap<String, Object>> sl){
		List<Share> shares = new ArrayList<Share>();
		for(HashMap<String, Object> ss : sl){
			shares.add(new Share(ss));
		}
		return shares;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
