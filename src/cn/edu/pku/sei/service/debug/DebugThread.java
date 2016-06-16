package cn.edu.pku.sei.service.debug;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import cn.edu.pku.sei.constants.UrlConstant;
import cn.edu.pku.sei.utils.EnvironmentProperty;
import cn.edu.pku.sei.utils.javaJson.JSONException;
import cn.edu.pku.sei.utils.javaJson.JSONObject;

public class DebugThread extends Thread {

	String username;
	public String projectName;
	public String domain;
	public String debugPort;
	
	public Process process;
	public BufferedWriter processWriter;
	public BufferedReader processReader;
	public boolean runReaderAndWriterReady = false;
	
	String jdbAttachAddr = "";
	DwrDebugIO consoleIO;
	
	public static final int RunVMRuntimeInMilli = Integer.valueOf(EnvironmentProperty.readConf("DebugVMRuntimeInMilli"));

	public boolean isJdbRun = false;
	public ConcurrentHashMap<String, String> watchExpressions = new ConcurrentHashMap<String, String>();
	public ConcurrentLinkedQueue<String> expressionQueue = new ConcurrentLinkedQueue<String>();

	private boolean isListingLocal = false;
	private boolean isListingExp = false;
	private boolean isOutput = false;

	public DebugThread(String pn, String username, DwrDebugIO consoleIO,
			String domain, String debugPort) {
		projectName = pn;
		this.username = username;
		this.consoleIO = consoleIO;
		this.domain = domain;
		this.debugPort = debugPort;
	}

	/**
	 * 停止正在运行的进程
	 */
	public void clearAll() {
		try {
			if (process != null) {
				process.destroy();
				System.out.println(username + ": call process.destory()");
				process = null;
			}
			if (processWriter != null)
				processWriter.close();
			if (processReader != null)
				processReader.close();
		} catch (Exception e) {
			StackTraceElement[] es = e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			for (StackTraceElement ee : es)
				sb.append(ee);
			System.out.println(sb);
		}

		isJdbRun = false;
		runReaderAndWriterReady = false;
		consoleIO.sendTerminateToBroswer();
	}

	@Override
	public void run() {
		runProject();
		DebugInterface.clearInstance(username);
	}

	private String runProject() {
		String ret = jdbRun();
		return ret;
	}

	public void jdbCommand(String text) throws Exception {
		if (processWriter != null) {
			processWriter.append(text);
			processWriter.flush();
		}
	}

	public String jdbRun() {
		//String attachAddr = EnvironmentProperty.readConf("DebugAttachAddr");
		String attachAddr = this.domain + ":" + this.debugPort;
		String sourcePath = EnvironmentProperty.readConf("JavawebRepositoryPath") + username + "/" + projectName + "/" + EnvironmentProperty.readConf("MvnSourcePath");
		ProcessBuilder processBuilder = new ProcessBuilder("jdb", "-attach", attachAddr, "-sourcepath", sourcePath);
		processBuilder.redirectErrorStream(true);
		try {
			process = processBuilder.start();
			processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			processWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			runReaderAndWriterReady = true;
			
			String line = null;
			System.out.println(username + ": jdb started.");
			while (processReader != null && (line = processReader.readLine()) != null) {
				System.out.println(username + ": jdb output: " + line);
				if(line.contains("Initializing jdb ...")) {
					synchronized(this) {
						this.notifyAll();
					}
				}
				parseLine(line);
				consoleIO.sendDebugMsg(line + "\n");
			}
			process.waitFor();
		} catch (Exception e) {
			StackTraceElement[] es = e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			for (StackTraceElement ee : es)
				sb.append(ee);
		}
		DebugInterface.clearInstance(username);
		return "ok";
	}

	private void parseLine(String line) {
		line = line.trim();
		if (isListingLocal) {
			isOutput = false;
			String[] arr = line.split(" = ");
			if (arr.length == 2) {
				if (arr[0].contains(" ")) {
					isListingLocal = false;
					isListingExp = true;
					int pos = line.indexOf(" ");
					String subline = line.substring(pos + 1);
					parseLine(subline);
				} else {
					String json = "[{'name':'" + arr[0] + "', 'value':'"
							+ arr[1] + "'}]";
					consoleIO.listLocals(json);
				}
			} else {
				isListingLocal = false;
				parseLine(line);
			}
		} else if (isListingExp) {
			isOutput = false;
			String[] arr = line.split(" = ");
			if (arr.length == 2) {
				String key = expressionQueue.poll();
				String value = arr[1];
				consoleIO.sendWatchValue(key, value);
			} else {
				isListingExp = false;
				parseLine(line);
			}
		} else if (line.startsWith("Breakpoint hit")
				|| line.startsWith("Step completed")) {
			isListingLocal = false;
			isListingExp = false;
			isOutput = false;
			String[] arr = line.split(", ");
			int lastDotPos = arr[1].lastIndexOf(".");
			String file = arr[1].substring(0, lastDotPos).replace('.', '/');
			String lineNo = arr[2].substring(5, arr[2].indexOf(" "));
			consoleIO.sendCurrentStopLine(projectName, EnvironmentProperty.readConf("MvnSourcePath") + file + ".java", Integer.parseInt(lineNo));
			listLocals();
			sendPrintCmd();
		} else if (line.startsWith("The application exited")) {
			isListingLocal = false;
			isListingExp = false;
			isOutput = false;
			consoleIO.sendTerminateToBroswer();
		} else if (line.contains("Method arguments:")
				|| line.startsWith("Local variables:")) {
			isListingLocal = true;
			isListingExp = false;
			isOutput = false;
		} else if (line.startsWith("VM Started: ")) {
			isListingLocal = false;
			isListingExp = false;
			isOutput = true;
		} else if (line.startsWith("http-") && line.endsWith("Nothing suspended.")) {
			isListingLocal = false;
			isListingExp = false;
			isOutput = true;
			consoleIO.sendContToNoSuspend(line);
		} else if (isOutput){
			isListingLocal = false;
			isListingExp = false;
			consoleIO.sendDebugMsg("\n" + line);
		} else if(false) {
			//  when JDB output suspends, print watched expressions
			
		}
		
	}

	private void listLocals() {
		try {
			jdbCommand("locals\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendPrintCmd() {
		Set<String> expressions = watchExpressions.keySet();
		expressionQueue.clear();
		for (String expression : expressions) {
			try {
				expressionQueue.add(expression);
				jdbCommand("print " + expression + "\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 守护线程  用户进程运行超时后关闭进程
	 */
	class DaemonThread extends Thread {
		@Override
		public void run() {
			try {
				sleep(RunVMRuntimeInMilli);
				if (process != null) {
					System.out.println("debug too long, killed:" + username);
					DebugInterface.clearInstance(username);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
