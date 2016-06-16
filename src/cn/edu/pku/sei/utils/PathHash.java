package cn.edu.pku.sei.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PathHash {
	// key: path, value: username;
	private static HashMap<String, List<String>> loadedFiles 
	= new HashMap<String, List<String>>();
	// key: user, value: path;
	
	public static List<String> getloaded(String path){
		List<String> users = new ArrayList<String>();
		users = loadedFiles.get(path);
		return users;
	}
	
	public static List<String> load(String username, String path){
		List<String> users = loadedFiles.get(path);
		if(users==null)	users = new ArrayList<String>();
		users.remove(username);
		users.add(username);
		loadedFiles.put(path, users);
		return users;
	}
	
	public static void unload(String username, String path){
		List<String> users = new ArrayList<String>();
		users = loadedFiles.get(path);
		if(!users.isEmpty()){
			users.remove(username);
		}
	}
	
	public static void unload(String username){
		List<String> users = new ArrayList<String>();
		// users = loadedFiles.
	}
	
}
