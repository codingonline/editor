package servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdbc.AppJDBC;
import jdbc.MySQL;
import model.App;
import model.User;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import service.packageexplorer.PackageExplorer;
import service.usermanger.UserPath;
import utils.EnvironmentProperty;
import utils.HttpPost;
import utils.ZipUtil;
import utils.thread.HttpThread;
import constants.PathConstant;

public class PackageExplorerServlet extends HttpServlet {

	private static final long serialVersionUID = 8949803303667801816L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		PackageExplorer pe = new PackageExplorer();
		try {
			String operation = request.getParameter("operation");
			String filePath = request.getParameter("path");
			String projectName = request.getParameter("appname");
			String username = ((User) request.getSession().getAttribute("user")).getUsername();
			String ownername = request.getParameter("ownername");
			String type = request.getParameter("apptype");

			String userPath = UserPath.getUserPath(ownername, type);
			String projectPath = UserPath.getProjectPath(ownername, projectName, type);
			String path = filePath == null ? null : UserPath.getFilePath(projectPath, filePath);
			

			String ret = "";
			if (operation.equals("createProject")){
				if(AppJDBC.findApp(username, projectName)==null){
					if("javaweb".equals(type)){
						ret = pe.createMvnProject(username, projectName, "javaweb");
					}else{
						userPath = UserPath.getUserPath(username, type);
						ret = pe.createProject(userPath+projectName, type);
					}
					App app = new App();
					app.setAppType(type);app.setAppName(projectName);
					app.setUserName(username);app.setOwnerName(username);
					AppJDBC.insert(app);
				}else{
					ret = "fail";
				}
			} else if (operation.equals("removeProject")){
				if(!ownername.equals(username)){
					App app = AppJDBC.findApp(username, projectName);
					if(app!=null){
						AppJDBC.delete(app);
						ret = pe.removeProject(projectPath);
					}else{
						ret = "fail";
					}
				}else{
					String sqlString = MessageFormat.format("delete from app where app_name={0} and owner_name={1}", MySQL.convertStr(projectName), MySQL.convertStr(ownername));
					new MySQL().execute(sqlString);
					ret = pe.removeProject(projectPath);
				}
			}
			else if(operation.equals("uploadFile")){
				ret = uploadFile(request, response, username, path, pe, ownername, projectName);
//				if("success".equals(ret)&&filePath.endsWith(".jar")){
//					//HttpPost.post(EnvironmentProperty.readConf("JARPARSER"), MessageFormat.format("operation=userjar&username={0}&appname={1}", ownername, projectName));
//					HttpThread ht = new HttpThread(new URL(EnvironmentProperty.readConf("JARPARSER")), MessageFormat.format("operation=userjar&username={0}&appname={1}", ownername, projectName));
//					new Thread(ht).start();
//				}
			} else if(operation.equals("downloadProject")){
				downloadProject(request, response, username, projectName, projectPath, pe);
				return;
			} 
			else if (operation.equals("createPackage"))
				ret = pe.createPackage(path);
			else if (operation.equals("deletePackage"))
				ret = pe.deletePackage(path);
			else if (operation.equals("createFile"))
				ret = pe.createFile(path);
			else if (operation.equals("deleteFile"))
				ret = pe.deleteFile(path);
			else if (operation.equals("Fs2Json"))
				ret = pe.Fs2Json(projectPath);
			else if (operation.equals("updateFile")) {
				String source = request.getParameter("code");
				ret = pe.updateFile(path, source);
				if("success".equals(ret)&&"pom.xml".equals(filePath)){
					//HttpPost.post(EnvironmentProperty.readConf("JARPARSER"), MessageFormat.format("operation=mvn&username={0}&appname={1}", ownername, projectName));
					HttpThread ht = new HttpThread(new URL(EnvironmentProperty.readConf("JARPARSER")), MessageFormat.format("operation=mvn&username={0}&appname={1}", ownername, projectName));
					new Thread(ht).start();
				}
			} else if (operation.equals("retrieveFile")){
				ret = pe.retrieveFile(path);
			} else if (operation.equals("renameFile")) {
				String name = request.getParameter("name");
				if (name != null && name.length() != 0)
					pe.rename(path, name);
			} else if (operation.equals("moveFile")) {
				String newPath = request.getParameter("newPath");
				if (!( newPath == null || newPath.length()==0)){
					String desPath = projectPath + newPath;
					pe.move(path, desPath);
				}
			} else if(operation.equals("copyFile")) {
				String src = request.getParameter("src");
				String dst = request.getParameter("dst");
				if(src != null && dst != null) {
					String srcPath = UserPath.getFilePath(projectPath, src);
					String dstPath = UserPath.getFilePath(projectPath, dst);
					if(pe.copy(srcPath, dstPath))
						ret = "success";
					else
						ret = "Copy failure";
				}
			}
			else if (operation.equals("deployToIaaS")) {
				System.out.println("deploy 2 IaaS");
				String uname = request.getParameter("uname");
				String password = request.getParameter("password");
				String ip = request.getParameter("ip");
				int port = Integer.parseInt(request.getParameter("port"));
				if(!pe.deployToIaaS(projectName, type, username, uname, password, ip, port))
					ret = "failed";
				else
					ret = "success";
			}
			else {
				ret = "Operation doesn't exist";
			}
			response.setHeader("Content-Type", "text/plain;charset=UTF-8");
			response.getWriter().print(ret);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void downloadProject(HttpServletRequest request,
			HttpServletResponse response, String username, String projectName, String projectPath, PackageExplorer pe) throws FileNotFoundException, IOException{
		String tempdir = PathConstant.TEMP+username+"/";
		String tempfile = tempdir+projectName+".zip";
		ZipUtil.compress(projectPath, tempdir, "utf-8", "download");
		File file=new File(tempfile);  
		if(file.exists()){  
			response.setContentType("application/zip");  
			response.addHeader("Content-Disposition", "attachment;filename="+projectName+".zip");  
			byte[] buffer=new byte[1024];  
			FileInputStream fis=null;  
			BufferedInputStream bis=null;  
			try{  
				fis=new FileInputStream(file);  
				bis=new BufferedInputStream(fis);  
				OutputStream os=response.getOutputStream();  
				int i=bis.read(buffer);  
				while(i!=-1){  
					os.write(buffer,0,i);  
					i=bis.read(buffer);  
				}  
			}catch(IOException e){  
				e.printStackTrace();
			}finally{  
				if(bis!=null){  
					bis.close();  
				}  
				if(fis!=null){  
					fis.close();  
				} 
				pe.deletePackage(tempdir);
			}  
		}else{  
			System.out.println("project does not exit!");  
		}  
	}

	private String uploadFile(HttpServletRequest request,
			HttpServletResponse response, String username, String path, PackageExplorer pe, String ownername, String projectname) throws Exception{
		String tempdir = PathConstant.TEMP+username+"/";
		String filename;
		File temp = new File(tempdir);
		if(!temp.exists())temp.mkdirs();
		DiskFileItemFactory diskFactory = new DiskFileItemFactory();  
		PrintWriter pw = response.getWriter();
		diskFactory.setSizeThreshold(4 * 1024);  
		diskFactory.setRepository(temp);  
		ServletFileUpload upload = new ServletFileUpload(diskFactory);  
		upload.setSizeMax(100 * 1024 * 1024);  
		List fileItems = upload.parseRequest(request);  
		Iterator iter = fileItems.iterator();  
		while(iter.hasNext())  
		{  
			FileItem item = (FileItem)iter.next();  
			if(item.isFormField())  
			{  
				processFormField(item, pw);  
			}else{  
				filename = item.getName();         
				System.out.println("完整的文件名：" + filename);  
				int index = filename.lastIndexOf("\\");  
				filename = filename.substring(index + 1, filename.length());
				if(filename.endsWith(".zip")){
					filename = processUploadFile(item, pw, tempdir);  
					ZipUtil.decompress(tempdir+filename, path, "utf-8");
					if(ZipUtil.contains(tempdir+filename, "utf-8", "jar")){
//						HttpPost.post(EnvironmentProperty.readConf("JARPARSER"), MessageFormat.format("operation=userjar&username={0}&appname={1}", ownername, projectname));
						HttpThread ht = new HttpThread(new URL(EnvironmentProperty.readConf("JARPARSER")), MessageFormat.format("operation=userjar&username={0}&appname={1}", ownername, projectname));
						new Thread(ht).start();
						System.out.println("operation=userjar&username={0}&appname={1}");
					}
				}else {
					filename = processUploadFile(item, pw, path); 
					if(filename.endsWith(".jar")){
						HttpThread ht = new HttpThread(new URL(EnvironmentProperty.readConf("JARPARSER")), MessageFormat.format("operation=userjar&username={0}&appname={1}", ownername, projectname));
						new Thread(ht).start();
						System.out.println("operation=userjar&username={0}&appname={1}");
					}
				}
			}  
		}
		pe.deletePackage(tempdir);
		return "success";
	}

	// 处理上传的文件  
	private String processUploadFile(FileItem item, PrintWriter pw, String filePath)  
			throws Exception  
			{  
		// 此时的文件名包含了完整的路径，得注意加工一下  
		String filename = item.getName();         
		System.out.println("完整的文件名：" + filename);  
		int index = filename.lastIndexOf("\\");  
		filename = filename.substring(index + 1, filename.length());  

		long fileSize = item.getSize();  

		if("".equals(filename) && fileSize == 0)  
		{             
			System.out.println("文件名为空 ...");  
			return null;  
		}  

		File pathFile = new File(filePath);
		if(!pathFile.exists())pathFile.mkdirs();
		File uploadFile = new File(filePath + "/" + filename);  
		item.write(uploadFile);  
		pw.println(filename + " 文件保存完毕 ...");  
		pw.println("文件大小为 ：" + fileSize + "\r\n");
		return filename;
			}  

	// 处理表单内容  
	private void processFormField(FileItem item, PrintWriter pw)  
			throws Exception  
			{  
		String name = item.getFieldName();  
		String value = item.getString();          
		pw.println(name + " : " + value + "\r\n");  
			}  

}
