package service.packageexplorer;

public interface IPackageExplorer {

	public String createProject(String path, String type);
	
	public String removeProject(String path);

	public String createPackage(String path);

	public String deletePackage(String path);

	public String createFile(String path);

	public String deleteFile(String path);

	public String Fs2Json(String path);

	public String updateFile(String path, String source);

	public String retrieveFile(String path);

	public String delBinFolder(String path);
	
	public boolean rename(String path, String name);

	public boolean move(String path, String newDirectory);
}
