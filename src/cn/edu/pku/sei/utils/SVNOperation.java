package cn.edu.pku.sei.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.service.usermanger.UserPath;
import cn.edu.pku.sei.service.usermanger.UserProperty;

public class SVNOperation {
	
	public static boolean checkout(String repoAddress, String projectPath, String username, String password) {
		List<String> command = new ArrayList<String>();
		// svn 使用checkout
		command.add("svn");
		command.add("checkout");
		command.add(repoAddress);
		command.add(projectPath);
		command.add("--username");
		command.add(username);
		command.add("--password");
		command.add(password);
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process p;
		int ret = 1;
		try {
			p = pb.start();
			BufferedReader processReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = processReader.readLine()) != null) {
				System.out.println(line);
			}
			ret = p.waitFor();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		if (ret == 0)
			return true;
		else
			return false;
	}
	
	public static boolean commit(String projectPath, String username, String password) {
		System.out.println("start commit");
		
		List<String> command = new ArrayList<String>();
		command.add("svn");
		command.add("commit");
		command.add("-m");
		command.add("from pop");
		command.add("--username");
		command.add(username);
		command.add("--password");
		command.add(password);
	
		ProcessBuilder pb = new ProcessBuilder(command); 
		pb.redirectErrorStream(true);
		
		File dir=new File(projectPath);
		pb.directory(dir);
		Process p;
		int ret = 1;
		try {
			p = pb.start();
			System.out.println("????");
			BufferedReader processReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			
			while ((line = processReader.readLine()) != null) {
				System.out.println(line);
			}
			
			ret = p.waitFor();
			System.out.println("ret: " + ret);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ret == 0)
			return true;
		else
			return false;
	}
	
	
	public static boolean addAll(String projectPath, String username, String password) {
		
		List<String> command = new ArrayList<String>();
		
		System.out.println("start add all");
		
		command.add("/bin/bash");
		command.add("-c");
		command.add("svn add * --force --username "+username+" --password "+password);
	
		ProcessBuilder pb = new ProcessBuilder(command); 
		File dir=new File(projectPath);
		pb.directory(dir);
		Process p;
		int ret = 1;
		try {
			p = pb.start();
			ret = p.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ret == 0)
			return true;
		else 
			return false;
		 
	}
	
	public static void update(String projectsrcPath, String username, String password) {
		List<String> command = new ArrayList<String>();
		command.add("svn");
		command.add("update");
		command.add("--username");
		command.add(username);
		command.add("--password");
		command.add(password);
	
		ProcessBuilder pb = new ProcessBuilder(command); 
		File dir=new File(projectsrcPath);
		pb.directory(dir);
		Process p;
		try {
			p = pb.start();
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
