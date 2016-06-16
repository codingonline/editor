package cn.edu.pku.sei.utils;

import java.io.File;

import cn.edu.pku.sei.utils.json.JsonEntry;
import cn.edu.pku.sei.utils.json.JsonTuple;

public class Fs2Json {
	public static int idInc = 0;

	private static JsonTuple _gen(File p, String pprefix, String projectName) {
		JsonTuple _ret = new JsonTuple();
		if (p.exists()) {
			File[] files = p.listFiles();
			for (int i = 0; i < files.length; i++) {
				String s = files[i].getName();
				if ((files[i].isDirectory() && files[i].getName().startsWith(".")) 
						|| (files[i].getName().startsWith(".") && (!".gitignore".equals(files[i].getName())))
						|| files[i].getName().endsWith("~")) {
					continue;
				}
				JsonEntry e = new JsonEntry();
				if (files[i].isDirectory()) {
					e.setAttribute("name", s);
					e.setAttribute("path", pprefix + s + "/");
					e.setAttribute("type", "package");
					e.setAttribute("iconCls", "package");
					e.setAttribute("project", projectName);
					JsonTuple tt = _gen(files[i], pprefix + s + "/", projectName);
					if (tt.isEmpty()){
						e.setAttribute("children", new JsonTuple());
					} else {
						e.setAttribute("expanded", true);
						e.setAttribute("children", tt);
					}
					_ret.addEntry(e);
				}

				if (files[i].isFile()) {

					String type = s.substring(s.lastIndexOf('.') + 1);
					e.setAttribute("name", s);
					e.setAttribute("path", pprefix + s);
					e.setAttribute("type", type);
					e.setAttribute("leaf", true);
					e.setAttribute("iconCls", type);
					e.setAttribute("project", projectName);
					_ret.addEntry(e);
				}
			}
		}
		return _ret;
	}

	public static JsonTuple gen(File path) throws Exception {
		//System.out.println(path.getName());
		
		//File[] dirs = path.listFiles();
		
		JsonTuple tuple = new JsonTuple();
		//for(int i = dirs.length-1; i>=0; i--) {
			File dir = path;
			JsonEntry e = new JsonEntry();
			if (dir.isDirectory()) {
				String projectName = dir.getName();
				
				e.setAttribute("name", projectName);
				e.setAttribute("path", "");
				e.setAttribute("type", "project");
				e.setAttribute("iconCls", "package");
				e.setAttribute("project", projectName);
				//JsonTuple tt = _gen(new File(dir.getAbsolutePath() + "/" +EnvironmentProperty.readConf("srcDirName")), "/", projectName);
				JsonTuple tt = _gen(new File(dir.getAbsolutePath()), "", projectName);
				if (tt.isEmpty()){
					e.setAttribute("children", new JsonTuple());
				} else {
					e.setAttribute("expanded", true);
					e.setAttribute("children", tt);
				}
				tuple.addEntry(e);
			}
		//}
		
		return tuple;
	}

	public static void main(String[] args) {
		try {
			System.out.println(gen(new File("/data/repository/00748189/source")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
