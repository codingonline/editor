package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.packageexplorer.PackageExplorer;
import service.usermanger.UserPath;
import utils.javaJson.JSONArray;
import utils.javaJson.JSONException;
import utils.javaJson.JSONObject;
import model.App;
import model.User;
import jdbc.AppJDBC;

public class FileSearchServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Content-Type", "text/plain;charset=UTF-8");
		
		String searchfor = request.getParameter("key");
		String projectName = request.getParameter("appname");
		String username = ((User) request.getSession().getAttribute("user")).getUsername();
		String ownername = request.getParameter("ownername");
		String type = request.getParameter("apptype");
		
		boolean isCaseSens = false;
		if (request.getParameter("isCaseSens") != null && request.getParameter("isCaseSens").equals("true"))
			isCaseSens = true;
		boolean isRegexp = false;
		if (request.getParameter("isRegexp") != null && request.getParameter("isRegexp").equals("true"))
			isRegexp = true;
		
		
		String userPath = UserPath.getUserPath(ownername, type);
		String projectPath = UserPath.getProjectPath(ownername, projectName, type);
		String rootPath = "";
		try {
			System.out.println(projectPath );
			
			if (type.equalsIgnoreCase("javaweb")) {
				projectPath += "src";
				rootPath = "src";
			}
			
			File rootFile = new File(projectPath);
			
			JSONArray searchRes = new JSONArray();
			SearchInPath(rootFile, searchfor, rootPath, projectName, searchRes, isCaseSens, isRegexp);
			System.out.println(searchRes.toString());
			response.getWriter().println(searchRes.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static final FileFilter textFileFilter = new FileFilter() {  
		public boolean accept(File pathname) {  
			boolean ret = true;
			ret &= pathname.canRead();
			return ret;
		}  
	};

	void SearchInPath(File rootFile, String key, String filePath, String project, JSONArray ret, boolean isCaseSens, boolean isRegexp) {
		if (rootFile.isHidden())
			return;
		if (rootFile.isDirectory()) {
			File[] files = rootFile.listFiles(textFileFilter);
			for (File file : files) {  
				SearchInPath(file, key, filePath + '/' + file.getName(), project, ret, isCaseSens, isRegexp);  
			}
		}
		else if (rootFile.isFile()) {
			if (filePath.startsWith("/")) {
				filePath = filePath.substring(1);
			}
			int lineCnt = 0;
			JSONObject fileRet = new JSONObject();
			try {
				BufferedReader br = new BufferedReader(new FileReader(rootFile));
				String s = null;
				int pos = 0;
				boolean hasContent = false;
				JSONArray children = new JSONArray();
				while((s = br.readLine())!=null){
					lineCnt++;
					int flag = 0;
					if (!isCaseSens) 	flag |= Pattern.CASE_INSENSITIVE;
					if (!isRegexp)		flag |= Pattern.LITERAL;
					Matcher  m = Pattern.compile(key, flag).matcher(s);
					while(m.find()) {
						hasContent = true;
						JSONObject json=new JSONObject();
						String markedS = s.substring(0, m.start()) + "<mark>" + m.group() + "</mark>" + s.substring(m.end());
						json.put("text", "Line " + lineCnt + " Column " + (m.start()+1) + " : " + markedS);
						JSONObject attributes = new JSONObject();
						attributes.put("type", "line")
						.put("project", project)
						.put("path", filePath)
						.put("title", rootFile.getName())
						.put("line", lineCnt)
						.put("col", m.start())
						.put("pos", pos + m.start());
						json.put("attributes", attributes);
						children.put(json);
					}
					pos += s.length()+1;
				}
				if (hasContent) {
					fileRet.put("text", filePath);
					JSONObject attributes = new JSONObject();
					attributes.put("type", "filename")
					.put("project", project)
					.put("path", filePath)
					.put("title", rootFile.getName());
					fileRet.put("attributes", attributes);
					fileRet.put("children", children);
				}

			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (fileRet.length() != 0)
				ret.put(fileRet);
		}

	}
}
