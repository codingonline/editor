package servlet;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.javaJson.JSONArray;
import utils.javaJson.JSONObject;
import model.App;
import model.Monitor;
import jdbc.AppJDBC;
import jdbc.MonitorJDBC;

public class MonitorServlet extends HttpServlet {
	private static final long serialVersionUID = 741435430776531975L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Content-Type", "text/plain;charset=UTF-8");
		String monitorOperation = request.getParameter("operation");
		String username = request.getParameter("username");
		String appname = request.getParameter("appname");
		if (monitorOperation.equals("saveData")) {
			String json = request.getParameter("json");
			JSONArray ja;
			String operation, content, path;
			Timestamp time;
			try {
				ja = new JSONArray(json);
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = (JSONObject) ja.get(i);
					path = jo.getString("path");
					content = (jo.getJSONObject("data")).toString();
					content = content.replaceAll("\"", "\\\\\"");
					content = content.replaceAll("\'", "\\\\\'");
					operation = jo.getString("operation");
					time = new Timestamp(jo.getLong("time"));
					Monitor m = new Monitor(username, appname, path, operation,
							content, time);
					MonitorJDBC.insert(m);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (monitorOperation.equals("startReplay")) {
			String path = request.getParameter("path");
			String datajson = "[";
			String closejson = "[";
			String ret;
			try {
				List<Monitor> list = MonitorJDBC.find(username, appname, path);
				for (Monitor m : list) {
					if (!m.getOperation().equals("close")
							&& !m.getOperation().equals("save")
							&& !m.getOperation().equals("edit"))
						continue;
					String content = m.getContent();
					content = content.replaceAll("\\n", "\\\\\\n");
					String json = "{\"operation\":\"" + m.getOperation()
							+ "\",\"content\":" + content + ",\"time\":\""
							+ m.getTime() + "\"},";
					if (m.getOperation().equals("close"))
						closejson += json;
					datajson += json;
				}
				if (datajson.length() > 1)
					datajson = datajson.substring(0, datajson.length() - 1);
				if (closejson.length() > 1)
					closejson = closejson.substring(0, closejson.length() - 1);
				ret = "[" + datajson + "]," + closejson + "]]";
				System.out.println(ret);

				PrintWriter pw = response.getWriter();
				pw.append(ret);
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (monitorOperation.equals("setSaveData")) {
			try {
				App a = AppJDBC.findApp(username, appname);
				if (a.getSaveData()) {
					a.setSaveData(false);
				}
				else {
					a.setSaveData(true);
				}
				AppJDBC.update(a);
				PrintWriter pw = response.getWriter();
				pw.append(a.getSaveData().toString());
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}