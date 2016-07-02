package constants;

import utils.EnvironmentProperty;

public class UrlConstant {
	
	public static final String login =  EnvironmentProperty.readConf("LOGIN");
	
	public static final String console =  EnvironmentProperty.readConf("CONSOLE");
	
	public static final String run =  EnvironmentProperty.readConf("RUN");
	
	public static final String log =  EnvironmentProperty.readConf("LOG");
	
	public static final String build =  EnvironmentProperty.readConf("BUILD");

}
