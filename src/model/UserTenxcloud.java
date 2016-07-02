package model;
// default package

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * UserTenxcloud entity. @author MyEclipse Persistence Tools
 */

public class UserTenxcloud  implements java.io.Serializable {


    // Fields    

     private String username;
     private String usernameTenxcloud;
     private String token;
     private String serviceAddress;
     private Integer userStatus;
     private String serviceType;
     private String serviceName;


    // Constructors

    /** default constructor */
    public UserTenxcloud() {
    }

	/** minimal constructor */
    public UserTenxcloud(String username) {
        this.username = username;
        this.userStatus = 0;
    }
    
    /** full constructor */
    public UserTenxcloud(String username, String usernameTenxcloud, String token, String serviceAddress, Integer userStatus, String serviceType, String serviceName) {
        this.username = username;
        this.usernameTenxcloud = usernameTenxcloud;
        this.token = token;
        this.serviceAddress = serviceAddress;
        this.userStatus = userStatus;
        this.serviceType = serviceType;
        this.serviceName = serviceName;
        
    }

    public UserTenxcloud(HashMap<String, Object> map) {
		this((String)map.get("username"), (String)map.get("username_tenxcloud"), (String)map.get("token"), (String)map.get("service_address"), (Integer)map.get("user_status"), (String)map.get("service_type"), (String)map.get("service_name"));
	}
    
	public static List<UserTenxcloud> getUsers(ArrayList<HashMap<String, Object>> al){
		List<UserTenxcloud> users = new ArrayList<UserTenxcloud>();
		for(HashMap<String, Object> aa : al){
			users.add(new UserTenxcloud(aa));
		}
		return users;
	}
    
    // Property accessors

    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernameTenxcloud() {
        return this.usernameTenxcloud;
    }
    
    public void setUsernameTenxcloud(String usernameTenxcloud) {
        this.usernameTenxcloud = usernameTenxcloud;
    }

    public String getToken() {
        return this.token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    

    public String getServiceAddress() {
        return this.serviceAddress;
    }
    
    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
    

    public Integer getUserStatus() {
        return this.userStatus;
    }
    
    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }
   
    public String getServiceType() {
    	return this.serviceType;
    }
    
    public void setServiceType(String serviceType) {
    	this.serviceType = serviceType;
    }

    public String getServiceName() {
    	return this.serviceName;
    }
    
    public void setServiceName(String serviceName) {
    	this.serviceName = serviceName;
    }
    
}