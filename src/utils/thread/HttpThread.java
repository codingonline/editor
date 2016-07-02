package utils.thread;

import java.io.IOException;
import java.net.URL;

import utils.HttpPost;

public class HttpThread implements Runnable{
	
	private URL url;
	private String param;
	
	public HttpThread(URL url, String params){
		this.url=url;
		this.param = params;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			HttpPost.post(url, param);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
