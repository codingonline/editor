package cn.edu.pku.sei.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPost{
	public static String post(String urlstr, String arg) throws IOException{
		URL url = new URL(urlstr);
		return post(url, arg);
	}
	
	public static String post(URL url, String arg) throws IOException{
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");

		out.write(arg);       
		out.flush();
		out.close();

		String strLine="";
		String strResponse ="";
		InputStream in =connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while((strLine =reader.readLine()) != null)       {
			strResponse +=strLine +"\n";
		}

		return strResponse;
	}

}
