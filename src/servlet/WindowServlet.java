package servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import service.usermanger.UserPath;

public class WindowServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = req.getParameter("operation");
		String ownername = req.getParameter("ownername");
		String appname = req.getParameter("appname");
		String type = req.getParameter("apptype");
		String projectPath = UserPath.getProjectPath(ownername, appname, type);
		Path path = FileSystems.getDefault().getPath(projectPath, ".settings", ".ace");
		if("ace_setting".equals(action)){
			if(Files.notExists(path)){
				Files.createDirectory(path.getParent());
				Files.createFile(path);
				try(OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE)){
					JSONObject jObject = new JSONObject();
					jObject.put("font_size", 12);
					jObject.put("theme", "ace/ext/eclipse");
					out.write(jObject.toString().getBytes());
				}
			}
			String aceSetting = new String(Files.readAllBytes(path));
			JSONObject jo = (JSONObject) JSONValue.parse(aceSetting);
			resp.setHeader("Content-Type", "text/plain;charset=UTF-8");
			resp.getWriter().print(jo.toJSONString());
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = req.getParameter("operation");
		String ownername = req.getParameter("ownername");
		String appname = req.getParameter("appname");
		String type = req.getParameter("apptype");
		String projectPath = UserPath.getProjectPath(ownername, appname, type);
		Path path = FileSystems.getDefault().getPath(projectPath, ".settings", ".ace");
		if("ace_setting".equals(action)){
			Files.delete(path);
			try(OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE)){
				JSONObject jObject = new JSONObject();
				jObject.put("font_size", Integer.parseInt(req.getParameter("fontsize")));
				jObject.put("theme", req.getParameter("theme"));
				out.write(jObject.toString().getBytes());
				resp.getWriter().print("success");
			}
		}
	}
	
	public static void main(String args[]) throws IOException, ParseException{
		Path path = FileSystems.getDefault().getPath("/data/nfs/repo/php/pkusei/demo", ".settings", ".ace");
		String aceSetting = new String(Files.readAllBytes(path));
		System.out.println(aceSetting);
		JSONParser parser=new JSONParser();
		JSONObject jObject = (JSONObject) parser.parse(aceSetting);
	}
	
}
