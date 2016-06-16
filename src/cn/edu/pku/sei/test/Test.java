package cn.edu.pku.sei.test;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cn.edu.pku.sei.constants.TenxCloudConstant;
import cn.edu.pku.sei.jdbc.TenxcloudJDBC;
import cn.edu.pku.sei.model.UserTenxcloud;
import cn.edu.pku.sei.service.packageexplorer.PackageExplorer;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Test {
	
    
	public static void main(String[] args) throws Exception {
		/*JsonGenerator jsonGenerator = null;
	    ObjectMapper objectMapper = null;

	    objectMapper = new ObjectMapper();
	    try {
	        jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		// TODO Auto-generated method stub
		PathHash.load("pkusei", "file1");
		PathHash.load("pkusei", "file2");
		PathHash.load("ckcz123", "file2");
		PathHash.load("guest", "file3");
		List<String> users = PathHash.load("ckcz123", "file3");
		String ret;
		ret = objectMapper.writeValueAsString(users);
		System.out.println(ret);*/
		
		PackageExplorer pe = new PackageExplorer();
		pe.deployToIaaS("!23", "123", "123", "123", "123", "123", 123);
		
		
	}
	
	
}
