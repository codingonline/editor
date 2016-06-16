package cn.edu.pku.sei.utils;

import java.io.UnsupportedEncodingException;  
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  
import java.util.Random;
  
public class MD5Util {  
     
	public static final String SALT = "POP-Register-Email-CreatBy-w2qiao";

    public static byte[] encode2bytes(String source) {  
        byte[] result = null;  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.reset();  
            md.update(source.getBytes("UTF-8"));  
            result = md.digest();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
          
        return result;  
    }  
      
    public static String encode2hex(String source) {  
        byte[] data = encode2bytes(source);  
  
        StringBuffer hexString = new StringBuffer();  
        for (int i = 0; i < data.length; i++) {  
            String hex = Integer.toHexString(0xff & data[i]);  
              
            if (hex.length() == 1) {  
                hexString.append('0');  
            }  
              
            hexString.append(hex);  
        }  
          
        return hexString.toString();  
    }  
 
    public static boolean validate(String unknown , String okHex) {  
        return okHex.equals(encode2hex(unknown));  
    }  
    
    public static String getToken(){
    	Random rand = new Random();
		Long tt = System.currentTimeMillis()+rand.nextInt(99999);		
		String token = MD5Util.encode2hex(tt.toString());
		return token;
    }
      
}  
