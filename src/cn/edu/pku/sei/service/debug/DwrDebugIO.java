package cn.edu.pku.sei.service.debug;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.directwebremoting.Browser;
import org.directwebremoting.ScriptSessions;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import cn.edu.pku.sei.constants.UrlConstant;
import cn.edu.pku.sei.service.usermanger.UserProperty;
import cn.edu.pku.sei.utils.EnvironmentProperty;
import cn.edu.pku.sei.utils.javaJson.JSONException;
import cn.edu.pku.sei.utils.javaJson.JSONObject;

public class DwrDebugIO {

	String scriptSessionId;
	
	public String debugCmd(String cmd, String projectName, String arg, String appid) {
		
		WebContext wc = WebContextFactory.get();
		HttpSession session = wc.getSession();
		scriptSessionId = wc.getScriptSession().getId();
		String username = UserProperty.getUsername(session);
		
		if (cmd.equals("start")) {
			String buildRet = "{ status: \"success\", msg: \"buildInfo\" }";
//			String buildRet = requestBuild(username, projectName);
			if(!buildRet.isEmpty()) {
				try {
					JSONObject jo = new JSONObject(buildRet);
					if("success".equals(jo.get("status").toString())) {
							// debugRet = {"domain": "123.57.2.1", "code": "0", "debugport": 3001, "sshport": 4005, "dockerid": "6c5806f84bdb", "port": "2001"}
							String debugRet = requestDebug(username, appid);
							System.out.println("Request for debug return json: " + debugRet);
							JSONObject debugRetJson = new JSONObject(debugRet);
							if("0".equals(debugRetJson.getString("code"))) {
								String domain = debugRetJson.getString("domain");
								String debugPort = debugRetJson.getString("debugport");
								String runPort = debugRetJson.getString("port");
								String dockerId = debugRetJson.getString("dockerid");
								session.setAttribute("dockerid", dockerId);
								DebugInterface.newDebugProcess(username, projectName, arg, this, domain, debugPort);
								String runUrl = "http://" + domain + ":" + runPort;
								sendAlertMsg("DebugInfo", "Please visit here to start debug: <a href=\"" + runUrl + "\" target=\"_blank\">" + runUrl + "</a>");
							} else {
								sendDebugMsg("Request for debug failed : " + debugRetJson.getString("msg"));
							}
					} else {
						String buildMsg = jo.get("msg").toString();
						sendBuildMsg(buildMsg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else if (cmd.equals("stop")) {
			DebugInterface.clearInstance(username);
			// request to stop the debug container
			String dockerId = session.getAttribute("dockerid").toString();
			System.out.println("debug stop dockerid : " + dockerId);
			// ret = {"msg": "success", "code": 1}
			requestStopDebugContainer(dockerId);
		} else if (cmd.equals("addBreakpoint")) {
			return DebugInterface.addBreakpoint(username, arg);
		} else if (cmd.equals("removeBreakpoint")) {
			return DebugInterface.removeBreakpoint(username, arg);
		} else if (cmd.equals("step")) {
			DebugInterface.step(username);
		} else if (cmd.equals("next")) {
			DebugInterface.next(username);
		} else if (cmd.equals("resume")) {
			DebugInterface.resume(username);
		} else if (cmd.equals("watch")) {
			DebugInterface.addWatchExpression(username, arg);
		} else if (cmd.equals("removeWatch")) {
			DebugInterface.removeWatchExpression(username, arg);
		}
		return "";
	}
	
	public void sendBuildMsg(final String text) {
		Browser.withSession(scriptSessionId, new Runnable() {
			public void run() {
				ScriptSessions.addFunctionCall("receiveBuildMessages", text);
			}
		});
	}
	
	public void sendDeployMsg(final String text) {
		Browser.withSession(scriptSessionId, new Runnable() {
			public void run() {
				ScriptSessions.addFunctionCall("receiveDeployMessages", text);
			}
		});
	}
	
	public void sendDebugMsg(final String text) {
		Browser.withSession(scriptSessionId, new Runnable() {
			public void run() {
				ScriptSessions.addFunctionCall("receiveDebugMessages", text);
			}
		});
	}
	
	public void sendAlertMsg(final String title, final String text) {
		Browser.withSession(scriptSessionId, new Runnable() {
			public void run() {
				ScriptSessions.addFunctionCall("receiveAlertMsg", title, text);
			}
		});
	}
	
	public void sendContToNoSuspend(final String text) {
		Browser.withSession(scriptSessionId, new Runnable() {
			public void run() {
				ScriptSessions.addFunctionCall("contToNoSuspend", text);
			}
		});
	}
	
	public void sendTerminateToBroswer() {
		Browser.withSession(scriptSessionId, new Runnable() {
			public void run() {
				ScriptSessions.addFunctionCall("terminate");
			}
		});
	}
	
	public void sendWatchValue(final String key, final String value) {
		Browser.withSession(scriptSessionId, new Runnable() {
			public void run() {
				ScriptSessions.addFunctionCall("receiveWatchValue", key, value);
			}
		});
	}
	
	public void listLocals(final String json) {
		Browser.withSession(scriptSessionId, new Runnable() {
			public void run() {
				ScriptSessions.addFunctionCall("listLocals", json);
			}
		});
	}
	
	public void sendCurrentStopLine(final String project, final String path, final int row) {
		Browser.withSession(scriptSessionId, new Runnable() {
			public void run() {
				ScriptSessions.addFunctionCall("currentStopLine", project, path, row);
			}
		});
	}
	
	/**
	 * Request building service to build the project
	 * @param username
	 * @param projectName
	 * @return building info in JSON
	 */
	private String requestBuild(String username, String projectName) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(UrlConstant.build);
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("username", username));
		nvps.add(new BasicNameValuePair("appname", projectName));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			String reply = "", line = "";
			while ((line = br.readLine()) != null) {
				reply += line;
			}
			return reply;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "request build exception";
	}
	
	private String requestDebug(String username, String appid) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(EnvironmentProperty.readConf("RequestDebugUrl") + "run");
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//		nvps.add(new BasicNameValuePair("action", "run"));
//		nvps.add(new BasicNameValuePair("type", "javaweb-debug"));
//		String path = "/"+username+"/"+projectName+"/target/"+projectName+"/";
//		nvps.add(new BasicNameValuePair("path", path));
		
		nvps.add(new BasicNameValuePair("appid", appid));
		nvps.add(new BasicNameValuePair("user", username));
		nvps.add(new BasicNameValuePair("mode", "debug"));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			String reply = "", line = "";
			while ((line = br.readLine()) != null) {
				reply += line;
			}
			return reply;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "request debug exception";
	}
	
	private String requestStopDebugContainer(String dockerId) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(EnvironmentProperty.readConf("RequestDebugUrl") + "stop");
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("dockerid", dockerId));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			String reply = "", line = "";
			while ((line = br.readLine()) != null) {
				reply += line;
			}
			return reply;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "request stop debug container exception";
	}
	
}
