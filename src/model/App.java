package model;
// default package

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.DateUtil;
/**
 * App entity. @author MyEclipse Persistence Tools
 */

public class App implements java.io.Serializable {

	// Fields

	private Integer id;
	private String appName;
	private String appType;
	private String userName;
	private String ownerName;
	private Boolean write;
	private String paasName;
	private String svnUrl;
	private String gitUrl;
	private String importUrl;
	private String domain;
	private Timestamp date;
	private Boolean saveData;

	// Constructors

	/** default constructor */
	public App() {
		Timestamp timestamp = DateUtil.getCurrentDate();
		this.appName = "";
		this.appType = "";
		this.userName = "";
		this.ownerName = "";
		this.write = true;
		this.paasName = "";
		this.svnUrl = "";
		this.gitUrl = "";
		this.domain = "";
		this.date = timestamp;
		this.importUrl = "";
		this.saveData = false;
	}

	/** minimal constructor */
	public App(String appName, String appType, String userName,
			String ownerName, Boolean write, Timestamp date) {
		this.appName = appName;
		this.appType = appType;
		this.userName = userName;
		this.ownerName = ownerName;
		this.write = write;
		this.date = date;
	}

	/** full constructor */
	public App(Integer id, String appName, String appType, String userName,
			String ownerName, Boolean write, String paasName, String svnUrl,
			String gitUrl, String domain, Timestamp date, String importUrl, Boolean saveData) {
		this.id = id;
		this.appName = appName;
		this.appType = appType;
		this.userName = userName;
		this.ownerName = ownerName;
		this.write = write;
		this.paasName = paasName;
		this.svnUrl = svnUrl;
		this.gitUrl = gitUrl;
		this.domain = domain;
		this.date = date;
		this.importUrl = importUrl;
		this.saveData = saveData;
	}
	
	public App(HashMap<String, Object> map) {
		this((Integer)map.get("id"), (String)map.get("app_name"), (String)map.get("app_type"), 
				(String)map.get("user_name"), (String)map.get("owner_name"), 
				(Boolean)map.get("write"), 
						(String)map.get("paas_name"), (String)map.get("svn_url"), 
						(String)map.get("git_url"), (String)map.get("domain"), (Timestamp)map.get("date"), 
						(String)map.get("import_url"), (Boolean)map.get("save_data"));
	}
	
	public static List<App> getApps(ArrayList<HashMap<String, Object>> al){
		List<App> apps = new ArrayList<App>();
		for(HashMap<String, Object> aa : al){
			apps.add(new App(aa));
		}
		return apps;
	}

//	public List<App> getUsers(ArrayList<HashMap<String, Object>> al){
//		List<App> apps = new ArrayList<App>();
//		for(HashMap<String, Object> aa : al){
//			apps.add(new App(aa));
//		}
//		return apps;
//	}
	
	// Property accessors
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppType() {
		return this.appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Boolean getWrite() {
		return this.write;
	}

	public void setWrite(Boolean write) {
		this.write = write;
	}

	public String getPaasName() {
		return this.paasName;
	}

	public void setPaasName(String paasName) {
		this.paasName = paasName;
	}

	public String getSvnUrl() {
		return this.svnUrl;
	}

	public void setSvnUrl(String svnUrl) {
		this.svnUrl = svnUrl;
	}

	public String getGitUrl() {
		return this.gitUrl;
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}
	
	public String getImportUrl() {
		return this.importUrl;
	}

	public void setImportUrl(String importUrl) {
		this.importUrl = importUrl;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Timestamp getDate() {
		return this.date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
	
	public Boolean getSaveData() {
		return this.saveData;
	}
	
	public void setSaveData(Boolean saveData) {
		this.saveData = saveData;
	}

}