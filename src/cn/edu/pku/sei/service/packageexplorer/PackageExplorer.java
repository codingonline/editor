package cn.edu.pku.sei.service.packageexplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cn.edu.pku.sei.constants.PathConstant;
import cn.edu.pku.sei.service.usermanger.UserPath;
import cn.edu.pku.sei.utils.DirectoryCopy;
import cn.edu.pku.sei.utils.Fs2Json;
import cn.edu.pku.sei.utils.SingleFileCopy;
import cn.edu.pku.sei.utils.ZipUtil;
import cn.edu.pku.sei.utils.json.JsonTuple;

public class PackageExplorer implements IPackageExplorer {
	public String createProject(String path, String type) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
			String source =  PathConstant.TEMPLATE+type+"/";
			DirectoryCopy dc = new DirectoryCopy(source, path);
			try {
				dc.copy();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "failed";
			}
			return "success";
		}

		return "fail";
	}

	public String createMvnProject(String userName, String projectName, String projectType) {
		String projectPath = UserPath.getProjectPath(userName, projectName, "javaweb");
		String ret = createProject(projectPath, projectType);
		if(ret.equals("failure"))
			return "failure";
		// modify contents of pom.xml
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(projectPath + "pom.xml"));
			Element root = doc.getDocumentElement();
			if(root.getElementsByTagName("artifactId")!=null)
				root.getElementsByTagName("artifactId").item(0).getFirstChild().setNodeValue(projectName);
			if(root.getElementsByTagName("name")!=null) 
				root.getElementsByTagName("name").item(0).getFirstChild().setNodeValue(projectName);
			if(root.getElementsByTagName("finalName")!=null)
				root.getElementsByTagName("finalName").item(0).getFirstChild().setNodeValue(projectName);

			// output
			TransformerFactory tFactory = TransformerFactory.newInstance();
			try {
				Transformer transformer = tFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(projectPath + "pom.xml")); 
				transformer.transform(source, result);
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
				return "failure";
			} catch (TransformerException e) {
				e.printStackTrace();
				return "failure";
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return "failure";
		} catch (SAXException e) {
			e.printStackTrace();
			return "failure";
		} catch (IOException e) {
			e.printStackTrace();
			return "failure";
		}

		return "success";
	}

	public String createPackage(String path) {
		try {
			File file = new File(path);
			if (file.isDirectory())//
				return "Package already exists";
			if (!file.mkdirs())
				return "Error creating package";
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unknown Error!";	
	}

	public String deletePackage(String path) {
		try {
			File file = new File(path);
			if (!file.isDirectory())
				return "Package doesn't exist";
			if (!_deleteDirectory(file))
				return "Error deleting package";
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unknown Error!";
	}

	private boolean _deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					_deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public String createFile(String path) {
		try {
			File file = new File(path);
			String directoryName = file.getParent();
			File directory = new File(directoryName);


			if (!directory.isDirectory())
				directory.mkdirs();
			if (!file.isFile()) {
				boolean createSuccess = false;
				if(path.endsWith(".jsp")){
					String type = "";
					String templateName = "basic";

					type = templateName+".jsp";
					String source =  PathConstant.TEMPLATE+"/singlefile/"+type;
					File srcFile = new File(source);
					createSuccess=SingleFileCopy.fileChannelCopy(srcFile, file);
				}
				else
					createSuccess=file.createNewFile();

				if (createSuccess) {
					return "success";
				} else {
					return "error";
				}

			} else
				return "File already exists";


		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unknown Error!";
	}

	public String deleteFile(String path) {
		try {
			File file = new File(path);
			if (file.exists() && file.isFile())//
				if (!file.delete())
					return "Error deleting file";
				else
					return "success";
			else
				return "File doesn't exists";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unknown Error!";
	}

	public String Fs2Json(String path) {
		//System.out.println(path);
		try {
			File file = new File(path);
			JsonTuple t = Fs2Json.gen(file);
			setAllRights(file);
			return t.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "error";

		}
	}

	private void setAllRights(File file) {
		// TODO Auto-generated method stub
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			fileList[i].setReadable(true,false);
			fileList[i].setWritable(true, false);
			fileList[i].setExecutable(true, false);
			if(fileList[i].isDirectory()){
				setAllRights(fileList[i]);
			}
		}
	}

	public String updateFile(String path, String source) {
		//		if (source.indexOf("fork") != -1 || source.indexOf("system") != -1) {
		//			return "forbid system and fork"; 
		//		}

		File f = new File(path);
		if (!(f.exists() && f.isFile()))//
			return "Source file doesn't exist";
		else {
			try {
				FileOutputStream fos = new FileOutputStream(f);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				PrintWriter pw = new PrintWriter(osw);
				pw.print(source);
				pw.close();
				osw.close();
				fos.close();

				return "success";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "Unknown Error!";
	}

	public String retrieveFile(String path) {
		File f = new File(path);
		System.out.println("retriveFile: " + path);
		if (!(f.exists() && f.isFile()))
			return "Source file doesn't exist";
		else {
			try {
				StringBuffer o = new StringBuffer("");
				FileInputStream fis = new FileInputStream(f);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader pr = new BufferedReader(isr);
				while (pr.ready()) {
					o.append(pr.readLine() + "\n");
				}
				pr.close();
				isr.close();
				fis.close();
				String s1 = o.toString();
				return s1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "Unknown Error!";
	}

	public String delBinFolder(String path) {
		try {
			File f = new File(path);
			if (!f.isDirectory())
				return "Package doesn't exist";
			if (f.exists()) {
				File[] files = f.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						_deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unknown Error!";
	}


	public String removeProject(String path) {
		if(_deleteDirectory(new File(path)))
			return "success";
		else 
			return "failed";
	}


	public boolean rename(String path, String name) {
		File f = new File(path);
		if (!f.exists())
			return false;
		else {
			String parent = f.getParent();
			if (!f.getParent().endsWith("/"))
				parent += "/";

			f.renameTo(new File(parent + name));
			return true;
		}
	}


	public boolean move(String path, String newDirectory) {
		File f = new File(path);
		if (!f.exists())
			return false;
		else {
			f.renameTo(new File(newDirectory + f.getName()));
			return true;
		}
	}

	public boolean copy(String srcPath, String dstPath) {
		File src = new File(srcPath);
		File dst = new File(dstPath);
		if(dst.isFile())
			dst = dst.getParentFile();
		if(src.isFile()) {
			File dstFile = new File(dst.getAbsolutePath() + File.separator + src.getName());
			if(dstFile.exists() && dstFile.isFile())
				return false;
			return SingleFileCopy.fileChannelCopy(src, dstFile);
		} else {
			File target = new File(dst.getAbsolutePath() + File.separator + src.getName());
			if(target.exists() && target.isDirectory())
				return false;
			DirectoryCopy dc = new DirectoryCopy(srcPath, target.getAbsolutePath());
			try {
				dc.copy();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Directory copy exception");
				return false;
			}
			return true;
		}
	}

	public boolean deployToIaaS(String appname, String apptype, String ownername, String usernameIaaS, String password, String ip, int port) 
			throws JSchException, FileNotFoundException, IOException {

		String projectPath = UserPath.getProjectPath(ownername, appname, apptype);
		
		//打包项目
		String tempfile;
		String tempdir = PathConstant.TEMP + ownername + "/";
		if (apptype.equals("javaweb")) {
			File tarFile = new File(projectPath + "target/" + appname + ".war");
			if (!tarFile.exists())
				return false;
			tempfile = projectPath + "target/" + appname + ".war";
		}
		else {
			System.out.println("IaaS: "+projectPath);
			ZipUtil.compress(projectPath, tempdir, "", "");
			tempfile = tempdir + appname +".zip";
		}
		
		// for debug
//		usernameIaaS = "root";
//		password = "Seforge520";
//		ip = "123.57.145.224";
//		port = 22;

		Session session = null;
		Channel channel = null;

		JSch jsch = new JSch();

		if(port <=0){  
			//连接服务器，采用默认端口  
			session = jsch.getSession(usernameIaaS, ip);  
		}else{  
			//采用指定的端口连接服务器  
			session = jsch.getSession(usernameIaaS, ip ,port);  
		}  

		//如果服务器连接不上，则抛出异常  
		if (session == null) {  
			System.out.println("deploy 2 IaaS: connection failed");
			return false;  
		}  

		session.setPassword(password);//设置密码   
		session.setConfig("StrictHostKeyChecking", "no");  
		//设置登陆超时时间     
		session.connect(30000);

		//创建sftp通信通道
		channel = (Channel) session.openChannel("sftp");
		channel.connect(1000);
		ChannelSftp sftp = (ChannelSftp) channel;

		//进入服务器指定的文件夹
		try {
			sftp.mkdir("POPupload");
		}
		catch (SftpException e) {
			System.out.println("mkdir failed");
		}

		try {
			sftp.cd("POPupload");
			//列出服务器指定的文件列表
			Vector v = sftp.ls("*");
			for(int i=0;i<v.size();i++){
				System.out.println(v.get(i));
			}

			//以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
			sftp.put(tempfile, ".");
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			session.disconnect();
			channel.disconnect();
		}





		/*} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
			channel.disconnect();
		}*/
		return true;
	}
	//	public static void main(String[] args) {
	//		PackageExplorer pe = new PackageExplorer();
	//		String path = null, source = null;
	//		path = "test";
	//		System.out.println(pe.openProject(path));
	//		path += "/src";
	//		System.out.println(pe.Fs2Json(path));
	//		path += "/source/new";
	//		System.out.println(pe.createPackage(path));
	//		System.out.println(pe.deletePackage(path));
	//		path += "/a.cpp";
	//		System.out.println(pe.createFile(path));
	//		System.out.println(pe.deleteFile(path));
	//		System.out.println(pe.createFile(path));
	//		source = "int main(){return 0;}";
	//		System.out.println(pe.updateFile(path, source));
	//		System.out.println(pe.retrieveFile(path));
	//	new PackageExplorer().rename("/home/mass/outputscore.xls", "o.xls");
	//	
}
