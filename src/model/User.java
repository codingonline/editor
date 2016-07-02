package model;
// default package

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.DateUtil;

/**
 * User entity. @author MyEclipse Persistence Tools
 */
public class User implements java.io.Serializable {

	// Fields

	private Integer id;
	private String username;
	private String password;
	private Timestamp lastLogin;
	private Boolean isSuper;
	private String email;
	private Timestamp dateJoined;
	private String unameBaidu;
	private String unameSina;
	private String unameSae;
	private String unameCode;
	private String pwdBaidu;
	private String pwdSae;
	private Timestamp registerTime;
	private String validateCode;
	private Boolean registerStatus;
	private String token;
	private Boolean isRemembered;

	// Constructors
	
	/** default constructor */
	public User() {
		Timestamp timestamp = DateUtil.getCurrentDate();
		this.username = "";
		this.password = "";
		this.lastLogin = timestamp;
		this.isSuper = false;
		this.email = "";
		this.dateJoined = timestamp;
		this.unameBaidu = "";
		this.unameSina = "";
		this.unameSae = "";
		this.pwdBaidu = "";
		this.pwdSae = "";
		this.unameCode = "";
		this.registerTime = timestamp;
		this.validateCode = "";
		this.registerStatus = false;
		this.token = "";
		this.isRemembered = false;
	}

	/** full constructor */
	public User(Integer id, String username, String password, Timestamp lastLogin,
			Boolean isSuper, String email, Timestamp dateJoined,
			String unameBaidu, String unameSina, String unameSae, String unameCode, String pwdBaidu, String pwdSae,
			Timestamp registerTime, String validateCode,
			Boolean registerStatus, String token, Boolean isRemembered) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.lastLogin = lastLogin;
		this.isSuper = isSuper;
		this.email = email;
		this.dateJoined = dateJoined;
		this.unameBaidu = unameBaidu;
		this.unameSina = unameSina;
		this.unameSae = unameSae;
		this.unameCode = unameCode;
		this.pwdBaidu = pwdBaidu;
		this.pwdSae = pwdSae;
		this.registerTime = registerTime;
		this.validateCode = validateCode;
		this.registerStatus = registerStatus;
		this.token = token;
		this.isRemembered = isRemembered;
	}
	
	public User(HashMap<String, Object> map) {
		this((Integer)map.get("id"), (String)map.get("username"), (String)map.get("password"), 
				(Timestamp)map.get("last_login"),
				(Boolean)map.get("is_super"), 
						(String)map.get("email"), (Timestamp)map.get("date_joined"),
						(String)map.get("uname_baidu"), (String)map.get("uname_sina"), (String)map.get("uname_sae"),
						(String)map.get("uname_code"), (String)map.get("pwd_baidu"), (String)map.get("pwd_sae"), (Timestamp)map.get("register_time"),
						(String)map.get("validate_code"), (Boolean)map.get("register_status"), 
						(String)map.get("token"), (Boolean)map.get("is_remembered"));
	}
	
	public static List<User> getUsers(ArrayList<HashMap<String, Object>> ul){
		List<User> users = new ArrayList<User>();
		for(HashMap<String, Object> uu : ul){
			users.add(new User(uu));
		}
		return users;
	}

	// Property accessors
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Boolean getIsSuper() {
		return this.isSuper;
	}

	public void setIsSuper(Boolean isSuper) {
		this.isSuper = isSuper;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getDateJoined() {
		return this.dateJoined;
	}

	public void setDateJoined(Timestamp dateJoined) {
		this.dateJoined = dateJoined;
	}

	public String getUnameBaidu() {
		return this.unameBaidu;
	}

	public void setUnameBaidu(String unameBaidu) {
		this.unameBaidu = unameBaidu;
	}

	public String getUnameSina() {
		return this.unameSina;
	}

	public void setUnameSina(String unameSina) {
		this.unameSina = unameSina;
	}
	
	public String getUnameSae() {
		return this.unameSae;
	}

	public void setUnameSae(String unameSae) {
		this.unameSae = unameSae;
	}

	public String getUnameCode() {
		return this.unameCode;
	}

	public void setUnameCode(String unameCode) {
		this.unameCode = unameCode;
	}

	public Timestamp getRegisterTime() {
		return this.registerTime;
	}

	public void setRegisterTime(Timestamp registerTime) {
		this.registerTime = registerTime;
	}

	public String getValidateCode() {
		return this.validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

	public Boolean getRegisterStatus() {
		return this.registerStatus;
	}

	public void setRegisterStatus(Boolean registerStatus) {
		this.registerStatus = registerStatus;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Boolean getIsRemembered() {
		return this.isRemembered;
	}

	public void setIsRemembered(Boolean isRemembered) {
		this.isRemembered = isRemembered;
	}

	public String getPwdBaidu() {
		return pwdBaidu;
	}

	public void setPwdBaidu(String pwdBaidu) {
		this.pwdBaidu = pwdBaidu;
	}

	public String getPwdSae() {
		return pwdSae;
	}

	public void setPwdSae(String pwdSae) {
		this.pwdSae = pwdSae;
	}

}