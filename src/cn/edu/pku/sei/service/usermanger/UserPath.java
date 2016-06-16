package cn.edu.pku.sei.service.usermanger;

import cn.edu.pku.sei.constants.PathConstant;


public class UserPath {
	public final static String repositoryPath = PathConstant.REPO;
	
	public final static String tempPath = PathConstant.TEMP;
	
	public static String getTempPath(String username, String projectName){
		return tempPath + "/" + username + "/";
	}
	
	public static String getRepositoryPath() {
		return repositoryPath;
	}
	
	public static String getUserPath(String username, String projectType) {
		return repositoryPath  + projectType + "/" + username + "/";
	}
	
	public static String getProjectPath(String username, String projectName, String projectType) {
		return getUserPath(username, projectType) + projectName + "/";
	}
	
	public static String getFilePath(String projectPath, String filepath){
		if(filepath.startsWith("/")){
			filepath = filepath.substring(1);
		}
		return projectPath+filepath;
	}
	
}
