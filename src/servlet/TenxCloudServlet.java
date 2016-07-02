package servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdbc.TenxcloudJDBC;
import jdbc.UsrJDBC;
import model.User;
import model.UserTenxcloud;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.eclipse.jgit.util.Base64;

import service.usermanger.UserPath;
import utils.ZipUtil;
import utils.javaJson.JSONArray;
import utils.javaJson.JSONException;
import utils.javaJson.JSONObject;
import constants.PathConstant;
import constants.TenxCloudConstant;



public class TenxCloudServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Content-Type", "text/plain;charset=UTF-8");
		
		String operation = request.getParameter("operation");
		String appname = request.getParameter("appname");
		String apptype = request.getParameter("apptype");
		String ownername = request.getParameter("ownername");
		
		String username = ((User) request.getSession().getAttribute("user")).getUsername();
		String userPath = UserPath.getUserPath(ownername, apptype);
		String projectPath = UserPath.getProjectPath(ownername, appname, apptype);
		
		
		System.out.println("TenxCloud : Operation = " + operation + ", apptype = " + apptype);
		String ret = "";
		
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add("Success");
		errorList.add("Init User Failed");
		errorList.add("Create Service Failed");
		errorList.add("Start Service Failed");
		errorList.add("Stop Service Failed");
		errorList.add("Upload Code Failed");
		errorList.add("Unkown Error");
		errorList.add("请先编译项目");
		errorList.add("Delete Service Failed");
		
		int retCode = 6;
		
		if (operation != null && operation.equals("check")) {
			String checkRet = "Conflict";
			System.out.println("Tenxcloud: check user service type : " + username);
			UserTenxcloud user;
			try {
				user = TenxcloudJDBC.findUserByName(username);
				if (user == null) {
					// 数据库中还没有
					user = new UserTenxcloud(username);
					// 插入数据库
					TenxcloudJDBC.insert(user);
				}
				String savedType = user.getServiceType();
				String savedName = user.getServiceName();
				
				// 如果之前没部署过，或者部署的类型名字都不变，没问题
				if (savedType == null || savedName == null || (savedName.equals(appname) && savedType.equals(apptype))) {
					checkRet = "OK";
				}
				
					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response.getWriter().print(checkRet);
			return;
			
		}
		if (operation == null) {
			
			
			
			try {
				// 1: 从数据库读取用户状态
				System.out.println("Tenxcloud: check user " + username);
				UserTenxcloud user;
				user = TenxcloudJDBC.findUserByName(username);
				if (user == null) {
					// 数据库中还没有
					user = new UserTenxcloud(username);
					// 插入数据库
					TenxcloudJDBC.insert(user);
				}
				
				int userStatus = user.getUserStatus().intValue();
				String savedType = user.getServiceType();
				String savedName = user.getServiceName();
				
				// 如果之前部署过，先删除。
				// 进入这里时一定通过了用户的确认
				if (userStatus == 2 && ((savedType == null || savedName == null) || 
										!(savedType.equals(apptype) && savedName.equals(appname)))) {
					DeleteService(savedName, username);
					userStatus = 1;
				}
				
				// 2: 根据用户状态选择操作
				switch (userStatus) {
				
				case 0:
					// 用户未初始化
					retCode = InitUser(username);
					if (retCode != 0)
						break;
					
				
				case 1:
					// 未创建服务
					// 上传代码
					retCode = UploadCode(username, appname, apptype, projectPath);
					if (retCode != 0)
						break;
					// 创建服务
					retCode = CreateService(appname, username, apptype);
					if (retCode != 0)
						break;
					break;
				
				case 2:
					// 已有服务，重新部署
					// 停止服务
					retCode = StopService(appname, username);
					if (retCode != 0)
						break;
					// 上传代码
					retCode = UploadCode(username, appname, apptype, projectPath);
					if (retCode != 0)
						break;
					// 启动服务
					retCode = StartService(appname, username, apptype);
					if (retCode != 0)
						break;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (operation.equals("getstatus")) {
			ret = GetStatus();
		}	
		
		else if (operation.equals("inituser")) {
			retCode = InitUser(username);
		}
		
		/*else if (operation.equals("getserviceinfo")) {
			ret = GetServiceInfo(appname);
		}*/
		
		else if (operation.equals("createservice")) {
			try {
				retCode = CreateService(appname, username, apptype);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (operation.equals("startservice")) {
			try {
				retCode = StartService(appname, username, apptype);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (operation.equals("stopservice")) {
			try {
				retCode = StopService(appname, username);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (operation.equals("upload")) {
			try {
				retCode = UploadCode(username, appname, apptype, projectPath);
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		JSONObject retMsg = new JSONObject();
		try {
			if (retCode == 0) {

				UserTenxcloud user;
				user = TenxcloudJDBC.findUserByName(username);
				ret = "<a href=http://" + user.getServiceAddress() + " target=\"_blank\">" + "http://" + user.getServiceAddress() + "</a>" ;
			}
			else
				ret = errorList.get(retCode);
			retMsg.put("retCode", retCode).put("msg", ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.getWriter().print(retMsg.toString());
		
	}
	
	String GetStatus() {
		System.out.println("TenxCloud : start get Status");
		String ret = "Error";
		String strUrl = TenxCloudConstant.GETSTATUS;   
		String username = TenxCloudConstant.USERNAME;
		String token = TenxCloudConstant.TOKEN;
		// 建立连接   
		URL url;
		try {
			url = new URL(strUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoInput(true); 
			httpConn.setUseCaches(false); 
			httpConn.setRequestMethod("GET");
			//设置认证
			httpConn.setRequestProperty("username", username);
			httpConn.setRequestProperty("Authorization", "token " 	+ token);
			
			int responseCode = httpConn.getResponseCode();   
			if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
				System.out.println("OK");
				ret = "OK";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
  
		return ret;
	}
	
	int InitUser(String username) throws ClientProtocolException, IOException {
		System.out.println("TenxCloud : start Init User");
		int ret = 0;
		
		String email = null;
		User user;
		
		try {
			user = UsrJDBC.findUserByUname(username);
			if (user == null) 
				return 1;
			email = user.getEmail();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (email == null || email.equals(""))
			return 1;
		//构建body，内容是email
		JSONObject body = new JSONObject();
		try {
			body.put("email", email);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(body.toString());
		
		String strUrl = TenxCloudConstant.INITUSER + username;   
		String headerusername = TenxCloudConstant.USERNAME;
		String token = TenxCloudConstant.TOKEN;
		
		// 建立连接   
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(strUrl);
		
		post.setHeader("username", headerusername);
		post.setHeader("Authorization", "token " + token);
		
		StringEntity entity = new StringEntity(body.toString());
		entity.setContentType("application/json"); 
		
		post.setEntity(entity);
		
		HttpResponse  httpResponse = httpClient.execute(post);
		if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			ret = 1;
		}
		
		HttpEntity httpEntity =  httpResponse.getEntity();
		String content = EntityUtils.toString(httpEntity);
		System.out.println("ret: " + content);
		try {
			JSONObject retObj = new JSONObject(content);
			String userToken, usernameTenxcloud;
			if (retObj.has("token")){
				userToken = retObj.getString("token");
				usernameTenxcloud = retObj.getString("username");
			}
			else
				return 1;
			UserTenxcloud userT = TenxcloudJDBC.findUserByName(username);
			if (userT == null) {
				// 数据库中还没有
				userT = new UserTenxcloud(username);
				// 插入数据库
				TenxcloudJDBC.insert(userT);
			}
			userT.setToken(userToken);
			userT.setUsernameTenxcloud(usernameTenxcloud);
			userT.setUserStatus(1);
			TenxcloudJDBC.update(userT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	int	CreateService(String name, String username, String type) throws Exception {
		
		int ret = 0;
		
		System.out.println("TenxCloud : start Create Service");
		
		UserTenxcloud user;
		user = TenxcloudJDBC.findUserByName(username);
		String usernameTenxcloud = user.getUsernameTenxcloud();
		String token = user.getToken();
		
		String strUrl = TenxCloudConstant.SERVICE + name;   
		
		System.out.println(usernameTenxcloud + " : " + token);
		
		// 准备数据
		String body = CreateServiceJson(name, type);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(strUrl);
		
		post.setHeader("username", usernameTenxcloud);
		post.setHeader("Authorization", "token " + token);
		
		StringEntity entity = new StringEntity(body.toString());
		entity.setContentType("application/json"); 
		
		post.setEntity(entity);
		
		HttpResponse  httpResponse = httpClient.execute(post);
		
		
		HttpEntity httpEntity =  httpResponse.getEntity();
		String content = EntityUtils.toString(httpEntity);
		System.out.println("ret: " + content);
		
		int retCode = httpResponse.getStatusLine().getStatusCode();
		if (retCode != HttpStatus.SC_OK) {
			ret = 2;
			
		}
		else {
			// 更新服务地址和用户状态
			JSONObject retObject = new JSONObject(content);	
			String domainName = retObject.getString("default_domain_name") + "/"+name + "/";
			user.setServiceAddress(domainName);
			user.setUserStatus(2);
			user.setServiceType(type);
			user.setServiceName(name);
			TenxcloudJDBC.update(user);
			
			TestService("http://"+domainName);
		}
		
		return ret;
	}
	
	String GetServiceInfo(String name) {
		String ret = "Error";
		System.out.println("TenxCloud : start get Service Info");
		//for debug
		name = "service4pop";
		
		String strUrl = TenxCloudConstant.SERVICE + name;   
		String username = TenxCloudConstant.USERNAME;
		String token = TenxCloudConstant.TOKEN;
		
		URL url;
		try {
			url = new URL(strUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoInput(true); 
			httpConn.setUseCaches(false); 
			httpConn.setRequestMethod("GET");
			//设置认证
			httpConn.setRequestProperty("username", username);
			httpConn.setRequestProperty("Authorization", "token " 	+ token);
			
			int responseCode = httpConn.getResponseCode();   
			if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
				String response = "";
				String readLine;   
				BufferedReader responseReader;     
				responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));   
				while ((readLine = responseReader.readLine()) != null) {   
					response += readLine + "\n";
				}   
				responseReader.close(); 
				ret = response;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		
		return ret;
	}
	
	int StartService(String appname, String username, String apptype) throws Exception {
		int ret = 0;
		System.out.println("TenxCloud : start starting Service");
		
		UserTenxcloud user;
		user = TenxcloudJDBC.findUserByName(username);
		String usernameTenxcloud = user.getUsernameTenxcloud();
		String token = user.getToken();
		
		String strUrl = TenxCloudConstant.SERVICE + appname + "/start"; 
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPut = new HttpPut(strUrl);
		
		httpPut.setHeader("username", usernameTenxcloud);
		httpPut.setHeader("Authorization", "token " + token);
		
		HttpResponse  httpResponse = httpClient.execute(httpPut);
		
		int retCode = httpResponse.getStatusLine().getStatusCode();
		if (retCode != HttpStatus.SC_OK) {
			ret = 3;
		}
		else {
			String type = user.getServiceType();
				if (type == null || !type.equals(apptype)) {
				user.setServiceType(apptype);
				user.setServiceName(appname);
				TenxcloudJDBC.update(user);
			}
		}
		HttpEntity httpEntity =  httpResponse.getEntity();
		String content = EntityUtils.toString(httpEntity);
		System.out.println("ret: " + content);
		
		TestService("http://" + user.getServiceAddress());
		
		return ret;
	}
	
	int StopService(String appname, String username) throws Exception {
		int ret = 0;
		System.out.println("TenxCloud : start starting Service");
	
		UserTenxcloud user;
		user = TenxcloudJDBC.findUserByName(username);
		String usernameTenxcloud = user.getUsernameTenxcloud();
		String token = user.getToken();
		
		String strUrl = TenxCloudConstant.SERVICE + appname + "/stop"; 
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPut = new HttpPut(strUrl);
		
		httpPut.setHeader("username", usernameTenxcloud);
		httpPut.setHeader("Authorization", "token " + token);
		
		HttpResponse  httpResponse = httpClient.execute(httpPut);
		
		int retCode = httpResponse.getStatusLine().getStatusCode();
		if (retCode != HttpStatus.SC_OK) {
			ret = 4;
		}
		
		HttpEntity httpEntity =  httpResponse.getEntity();
		String content = EntityUtils.toString(httpEntity);
		System.out.println("ret: " + content);
		
		return ret;
	}
	
	static int DeleteService(String appname, String username) throws Exception {
		int ret = 0;
		System.out.println("TenxCloud : start deleting Service");
	
		UserTenxcloud user;
		user = TenxcloudJDBC.findUserByName(username);
		String usernameTenxcloud = user.getUsernameTenxcloud();
		String token = user.getToken();
		String serviceAddress = user.getServiceAddress();
		
		String[] parts = serviceAddress.split("/");
		if (appname == null)
			appname = parts[parts.length-1];
		
		System.out.println("delete Service: apname=" + appname);
		
		String strUrl = TenxCloudConstant.SERVICE + appname; 
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete(strUrl);
		
		httpDelete.setHeader("username", usernameTenxcloud);
		httpDelete.setHeader("Authorization", "token " + token);
		
		HttpResponse  httpResponse = httpClient.execute(httpDelete);
		
		int retCode = httpResponse.getStatusLine().getStatusCode();
		if (retCode != HttpStatus.SC_OK) {
			ret = 8;
		}
		else {
			user.setServiceAddress(null);
			user.setUserStatus(1);
			user.setServiceType(null);
			user.setServiceName(null);
			TenxcloudJDBC.update(user);
		}
		HttpEntity httpEntity =  httpResponse.getEntity();
		String content = EntityUtils.toString(httpEntity);
		System.out.println("ret: " + content);
		
		return ret;
	}
	
	int UploadCode(String username, String appname, String apptype, String projectPath) throws Exception{
		int ret = 0;
		
		String volumeName = "code_repo_pop";
		String strUrl = TenxCloudConstant.UPLOAD + volumeName + "/import";   
		String headerusername = TenxCloudConstant.USERNAME;
		String token = TenxCloudConstant.TOKEN;
		
		UserTenxcloud user;
		user = TenxcloudJDBC.findUserByName(username);
		String usernameTenxcloud = user.getUsernameTenxcloud();
		
		//打包项目
		String tempfile;
		String tempdir = PathConstant.TEMP + username + "/";
		if (apptype.equals("javaweb")) {
			File tarFile = new File(projectPath + "target/" + appname + ".war");
			if (!tarFile.exists())
				return 7;
			ZipUtil.compress(projectPath + "target/" + appname + "/", tempdir, "", "");
		}
		else {
			System.out.println(projectPath);
			ZipUtil.compress(projectPath, tempdir, "", "");
		}
		tempfile = tempdir + appname +".zip";
		File file=new File(tempfile);  
		
		// 认证信息
		String username_token = headerusername + ":" + token;
		String authorization = Base64.encodeBytes(username_token.getBytes());
		
		System.out.println("URL: " + strUrl);
		
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost(strUrl);
		
		httpPost.setHeader("authorization", "Basic " + authorization);
		httpPost.setHeader("username", usernameTenxcloud);
		httpPost.setHeader("format", "ext4");
		//if (apptype.equals("javaweb"))
		//	httpPost.setHeader("isunzip", "false");
		//else
			httpPost.setHeader("isunzip", "true");

		MultipartEntity multiEntity = new MultipartEntity();
		
		multiEntity.addPart("file", new FileBody(file));
		
		httpPost.setEntity(multiEntity);

		HttpResponse  httpResponse = httpClient.execute(httpPost);
		
		int retCode = httpResponse.getStatusLine().getStatusCode();
		System.out.println("retCode : " + retCode);
		if (retCode != HttpStatus.SC_OK) {
			ret = 5;
			//return ret;
		}
		
		HttpEntity httpEntity =  httpResponse.getEntity();
		String content = EntityUtils.toString(httpEntity);
		System.out.println("ret: " + content);
		
		return ret;

	}
	
	int TestService(String strUrl) throws IOException {
		
		System.out.println("testing: " + strUrl);
		
		int retryCnt = 0, retCode = 503;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(strUrl);
		HttpResponse httpResponse;
		
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,2000);//连接时间
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,2000);//数据传输时间
		
		System.out.println("Init done");
		try {
			httpResponse = httpClient.execute(httpGet);
			System.out.println("Init done");
			retCode = httpResponse.getStatusLine().getStatusCode();
			System.out.println("Init done");
			System.out.println("retCode = " + retCode + " Trying...");
			
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SocketTimeoutException e1) {
			System.out.println("time out");
		}
		
		
		while (retCode == HttpStatus.SC_SERVICE_UNAVAILABLE && retryCnt < 30) {
			httpClient = new DefaultHttpClient();
			httpGet = new HttpGet(strUrl);
			
			httpResponse = httpClient.execute(httpGet);
			
			retCode = httpResponse.getStatusLine().getStatusCode();
			
			retryCnt++;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			System.out.println("Trying...");
		}
		System.out.println("done");
		return retCode==HttpStatus.SC_OK?0:1;
	}
	
	/*
	 * 根据应用类型生成创建服务的json
	 */
	String CreateServiceJson(String appname, String type) {
		String ret = "{}";
		
		
		JSONObject serviceJson = new JSONObject();
		String imageURL = "";
		
		// 选择URL
		if (type.equals("php")) 
			imageURL = "index.tenxcloud.com/tenxcloud/php:latest";
		else if (type.equals("javaweb"))
			imageURL = "index.tenxcloud.com/tenxcloud/java:latest";
		else if (type.equals("python"))
			imageURL = "index.tenxcloud.com/tenxcloud/python-runtime:latest";
		
		try {
			JSONObject container = new JSONObject();
			
			JSONArray env = new JSONArray().put(new JSONObject().put("name", "ROOT_PASS").put("value", "poppassword"));
			
			// 端口需定制
			JSONArray ports = new JSONArray();
			if (type.equals("php") || type.equals("python"))
				ports.put(new JSONObject().put("port", 22).put("protocol", "TCP"))
											.put(new JSONObject().put("port", 80).put("protocol", "TCP"));
			else if (type.equals("javaweb"))
				ports.put(new JSONObject().put("port", 22).put("protocol", "TCP"))
				  							.put(new JSONObject().put("port", 8080).put("protocol", "TCP"));
			
			JSONObject resources = new JSONObject("{\"limits\": {\"memory\": \"512Mi\"}}");
			
			// mountPath 需定制
			JSONArray volumeMounts = new JSONArray();
			if (type.equals("php") || type.equals("python"))
				volumeMounts.put(new JSONObject().put("name", "volume-name1").put("mountPath", "/app"));
			else if (type.equals("javaweb"))
				volumeMounts.put(new JSONObject().put("name", "volume-name1").put("mountPath", "/tomcat/webapps"));
			
			container.put("image", imageURL).put("env", env).put("ports", ports).put("resources", resources).put("volumeMounts", volumeMounts);
			
			
			JSONArray containers = new JSONArray().put(container);
			
			// port_mapping 需定制
			JSONArray port_mapping = new JSONArray();
			if (type.equals("php") || type.equals("python"))
				port_mapping = new JSONArray().put(new JSONObject().put("container_port", 22).put("protocol", "tcp"))
													.put(new JSONObject().put("container_port", 80).put("protocol", "http"));
			else if (type.equals("javaweb"))
				port_mapping = new JSONArray().put(new JSONObject().put("container_port", 22).put("protocol", "tcp"))
													.put(new JSONObject().put("container_port", 8080).put("protocol", "http"));
			
			// 这里的code-repo-pop是测试用，记得改回下划线
			JSONArray volumes = new JSONArray().put(new JSONObject().put("name", "volume-name1").put("disk_name", "code_repo_pop").put("is_read_only", false));
			boolean sync_timezone = true;
			
			serviceJson.put("name", appname);
			serviceJson.put("target_instance_size", 1);
			serviceJson.put("containers", containers);
			serviceJson.put("port_mapping", port_mapping);
			serviceJson.put("volumes", volumes);
			serviceJson.put("sync_timezone", sync_timezone);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ret = serviceJson.toString();
		
		System.out.println(ret);
		
		return ret;
	}
}
