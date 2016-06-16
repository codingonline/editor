package cn.edu.pku.sei.utils.json;


public class StrV extends AbsV{
	String s;
	public StrV(String ss){
		s = ss;
	}
	public String toString(){
		if(true){
			String sss = add_escapes(s);
			return "\""+sss+"\"";
		}
		//if(s.('"') == 0)
		//	System.err.println(s.replace('"', "\\"+"\""));
		String _ret=s.replaceAll("\"", "\\\\\"");
		return "\""+_ret+"\"";
	}
	static String add_escapes(String str) {
	      StringBuffer retval = new StringBuffer();
	      char ch;
	      for (int i = 0; i < str.length(); i++) {
	        switch (str.charAt(i))
	        {
	           case 0 :
	              continue;
	           case '\b':
	              retval.append("\\b");
	              continue;
	           case '\t':
	              retval.append("\\t");
	              continue;
	           case '\n':
	              retval.append("\\n");
	              continue;
	           case '\f':
	              retval.append("\\f");
	              continue;
	           case '\r':
	              retval.append("\\r");
	              continue;
	           case '\"':
	              retval.append("\\\"");
	              continue;
	           case '\'':
	              retval.append("\\\'");
	              continue;
	           case '\\':
	              retval.append("\\\\");
	              continue;
	           default:
	              if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
	                 String s = "0000" + Integer.toString(ch, 16);
	                 retval.append("\\u" + s.substring(s.length() - 4, s.length()));
	              } else {
	                 retval.append(ch);
	              }
	              continue;
	        }
	      }
	      return retval.toString();
	   }

	
}