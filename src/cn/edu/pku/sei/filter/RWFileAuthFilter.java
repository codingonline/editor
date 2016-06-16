package cn.edu.pku.sei.filter;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.sei.model.RWFile;
import cn.edu.pku.sei.model.User;
import cn.edu.pku.sei.service.rwfileauth.RWFileAuth;
import cn.edu.pku.sei.service.rwfileauth.RWFileAuthJDBC;

public class RWFileAuthFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest req = (HttpServletRequest) arg0;  
		HttpServletResponse resp = (HttpServletResponse) arg1;   
		String operation = req.getParameter("operation");
		String projectpath = "/"+req.getParameter("appname");
		String username = ((User) req.getSession().getAttribute("user")).getUsername();
		String ownername = req.getParameter("ownername");
		String apptype = req.getParameter("apptype");
		String filePath = req.getParameter("path");
		String retString = "success";
		if(filePath!=null){
			if(filePath.startsWith("/")){
				filePath = projectpath + req.getParameter("path");
			}else{
				filePath = projectpath + "/" + req.getParameter("path");
			} 
		}
		if(operation.equals("createPackage")||operation.equals("createFile")){
			if(!RWFileAuth.isAuthorized(username, filePath, "create", ownername, apptype)){
				retString = "fail";
			}
		}
		else if (operation.equals("deletePackage")||operation.equals("deleteFile")){
			if(!RWFileAuth.isAuthorized(username, filePath, "delete", ownername, apptype)){
				retString = "fail";
			}else{
				try {
					RWFileAuthJDBC.delete(new RWFile(filePath, ownername, apptype, username));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if (operation.equals("updateFile")||operation.equals("uploadFile")){
			if(!RWFileAuth.isAuthorized(username, filePath, "write", ownername, apptype)){
				retString = "fail";
			}
		}
		else if (operation.equals("renameFile")){
			File f = new File(filePath);
			String parent = f.getParent();
			if (!f.getParent().endsWith("/"))
				parent += "/";
			String newfilePath = parent+req.getParameter("name");
			if(!RWFileAuth.isAuthorized(username, filePath, "rename", ownername, apptype)){
				retString = "fail";
			}else {
				try {
					RWFileAuthJDBC.update(new RWFile(filePath, ownername, apptype, username), new RWFile(newfilePath, ownername, apptype, username));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if (operation.equals("moveFile")) {
			String newPath = req.getParameter("newPath");
			if (!( newPath == null || newPath.length()==0)){
				if(!RWFileAuth.isAuthorized(username, newPath, "create", ownername, apptype)&&!RWFileAuth.isAuthorized(username, filePath, "delete", ownername, apptype)){
					//retString = "auth fail";
				}
			}
		}
		else if (operation.equals("copyFile")) {
			
		}
		else{
			retString = "success";
			//retString = "auth fail";
			System.out.println("filePath: "+filePath);
		}
		
		if(retString.equals("fail")){
			resp.setHeader("Content-Type", "text/plain;charset=UTF-8");
			resp.getWriter().print("Please check your file("+filePath+") auth");
		}else{
			arg2.doFilter(arg0, arg1);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
