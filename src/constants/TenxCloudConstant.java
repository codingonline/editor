package constants;

import utils.EnvironmentProperty;

public class TenxCloudConstant {
	
	public static final String USERNAME = EnvironmentProperty.readConf("TCUSERNAME");
	
	public static final String TOKEN = EnvironmentProperty.readConf("TCTOKEN");
	
	public static final String GETSTATUS = EnvironmentProperty.readConf("TCGETSTATUS");
	
	public static final String SERVICE = EnvironmentProperty.readConf("TCSERVICE");
	
	public static final String INITUSER = EnvironmentProperty.readConf("TCINITUSER");
	
	public static final String UPLOAD = EnvironmentProperty.readConf("TCUPLOAD");
}
