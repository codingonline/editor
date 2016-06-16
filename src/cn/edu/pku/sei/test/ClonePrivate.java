package cn.edu.pku.sei.test;

import cn.edu.pku.sei.servlet.DeployServlet;

public class ClonePrivate {
	public static void main(String[] args){
		System.out.println(remoteUrl("https://git.duapp.com/appid9475c43rb6", "w2qiao", "pkumass"));
//		DeployServlet.gitClonePrivate("https://w2qiao:pkumass@git.duapp.com/appid9475c43rb6", 
//				"/data/repo/php/pkusei/gitdemo");
	}
	
	public static String remoteUrl(String url, String username, String password){
		//https://git.duapp.com/appid9475c43rb6 
		String remoteUrl = "https://"+username+":"+password+"@"+url.substring(8);
		return remoteUrl;
		
	}
}
