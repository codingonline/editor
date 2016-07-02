package service.debug;

import java.util.Hashtable;
import java.util.Iterator;

import utils.EnvironmentProperty;
import utils.javaJson.JSONArray;
import utils.javaJson.JSONException;
import utils.javaJson.JSONObject;

public class DebugInterface {

	public DebugThread debugThread;
	String username;
	public static Hashtable<String, DebugThread> debugThreads = new Hashtable<String, DebugThread>();
	public static int DebugProcessNumber = Integer.valueOf(EnvironmentProperty.readConf("DebugProcessNumber"));

	public static DebugThread getInstance(String key) {
		if (debugThreads.containsKey(key)) {
			return debugThreads.get(key);
		}
		return null;
	}

	public static void clearInstance(String username) {
		DebugThread debugThread = debugThreads.get(username);
		if (debugThread != null) {
			debugThread.clearAll();
		}
		debugThreads.remove(username);
	}

	/**
	 * servlet直接调用此函数，检查 1. 是否当前进程池已满; 2. 当前用户是否正在运行程序。
	 * @param username 用户名
	 * @param projectName 工程名
	 * @return 	"ok" : 正常运行
	 * 			"running" : 用户已经在运行一个程序
	 * 			"too many concurrent..." : 进程池已满，稍等
	 */
	public static String newDebugProcess(String username, String projectName, String args, 
			DwrDebugIO consoleIO, String domain, String debugPort) {
		if (debugThreads.size() >= DebugProcessNumber)
			return "too many concurrently running process, wait 30s";
		if (DebugInterface.getInstance(username) != null) {
			clearInstance(username);
		}
		
		String result = newThreads(username, projectName, consoleIO, domain, debugPort);
		try {
			JSONObject jo = new JSONObject(args);
			JSONArray breakpoints = (JSONArray) jo.get("breakpoints");
			addBreakpoints(username, breakpoints.toString());
			
			JSONArray watchExpressions = (JSONArray)jo.get("watchExpressions");
			for (int i = 0; i < watchExpressions.length(); i++) {
				addWatchExpression(username, watchExpressions.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	private static String newThreads(String username, String projectName, DwrDebugIO consoleIO, 
			String domain, String debugPort) {
		DebugThread debugThread = new DebugThread(projectName, username, consoleIO, domain, debugPort);
		// JDB connection started
		debugThread.start();
		debugThreads.put(username, debugThread);
		try {
			synchronized(debugThread) {
				debugThread.wait();
			}
			System.out.println("Main thread wait() returns");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "ok";
	}
	
	public static void addBreakpoints(String username, String breakpoints) {
		DebugThread debugThread = DebugInterface.getInstance(username);
		try {
			JSONArray ja = new JSONArray(breakpoints);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = (JSONObject) ja.get(i);
				for (Iterator<String> iter=jo.keys(); iter.hasNext();) {
					String file = iter.next();
					int line = jo.getInt(file);
					//file = file.substring(4, file.length() - 5);
					debugThread.jdbCommand("stop at "+file+":"+line+"\n");
				}
			}
			
			if (!debugThread.isJdbRun) {
				debugThread.jdbCommand("run\n");
				debugThread.isJdbRun = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String addBreakpoint(String username, String pos) {
		DebugThread debugThread = DebugInterface.getInstance(username);
		try {
			debugThread.jdbCommand("stop at "+pos+"\n");
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "false";
	}
	
	public static String removeBreakpoint(String username, String pos) {
		DebugThread debugThread = DebugInterface.getInstance(username);
		
		try {
			debugThread.jdbCommand("clear "+pos+"\n");
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "false";
	}
	
	public static void step(String username) {
		if(DebugInterface.getInstance(username) != null) {
			DebugThread debugThread = DebugInterface.getInstance(username);
			try {
				debugThread.jdbCommand("step\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void next(String username) {
		if(DebugInterface.getInstance(username) != null) {
			DebugThread debugThread = DebugInterface.getInstance(username);
			try {
				debugThread.jdbCommand("next\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void resume(String username) {
		if(DebugInterface.getInstance(username) != null) {
			DebugThread debugThread = DebugInterface.getInstance(username);
			try {
				debugThread.jdbCommand("cont\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String addWatchExpression(String username, String watchExpression) {
		DebugThread debugThread = DebugInterface.getInstance(username);
		if (debugThread != null) {
			debugThread.watchExpressions.put(watchExpression, "");
			try {
					debugThread.expressionQueue.add(watchExpression);
					//debugThread.jdbCommand("-data-evaluate-expression " + watchExpression + "\n");
					return "true";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return "false";
	}
	
	public static void removeWatchExpression(String username, String watchExpression) {
		DebugThread debugThread = DebugInterface.getInstance(username);
		if (debugThread != null) {
			debugThread.watchExpressions.remove(watchExpression);
		}
	}
	
	
}
