package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * eg: copy "/data/template", "/data/repository/00948001/ProjectOne"
 *
 */
public class DirectoryCopy {
		private File rootDirectory, sourRootD, destRootD;// 
		private ArrayList<File> fileArrayList; // ()

		public DirectoryCopy(String source, String des) {
			destRootD = new File(des);
			sourRootD = new File(source);
			rootDirectory = new File(source);
			rootDirectory.mkdir();
			fileArrayList = new ArrayList<File>();
		}

		// ()
		public void initFileArrayList() {
			if (rootDirectory.isDirectory()) {
				// 
				File[] fileList = rootDirectory.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					fileList[i].setReadable(true,false);
					fileList[i].setWritable(true, false);
					fileList[i].setExecutable(true, false);
					if (fileList[i].isFile()) {
						fileArrayList.add(fileList[i]);
					}
					// 
					else if (fileList[i].isDirectory()) {
						fileList[i].mkdir();

						String p1r = sourRootD.getAbsolutePath();

						int p1rl = sourRootD.getAbsolutePath().length();
						String p2r = destRootD.getAbsolutePath();
						String fileFullName = fileList[i].getAbsolutePath();

						String o = p2r.concat(fileFullName.substring(p1rl));
						File f = new java.io.File(o);
						f.mkdirs();
						f.setReadable(true,false);
						f.setWritable(true,false);
						f.setExecutable(true, false);

						rootDirectory = fileList[i];
						initFileArrayList();
					}

				}
			}
		}

		// 
		public void addFiles(File f) {
			fileArrayList.add(f);
		}

		// 
		public ArrayList<File> getFileArrayList() {
			return fileArrayList;
		}

		// 
		public void fileBackup() throws IOException{
			BufferedReader input;// 
			String p1r = sourRootD.getAbsolutePath();
			int p1rl = sourRootD.getAbsolutePath().length();
			String p2r = destRootD.getAbsolutePath();
			
			for (int i = 0; i < fileArrayList.size(); i++) {
				String fileFullName = fileArrayList.get(i).getAbsolutePath();

				input = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileFullName)));
				String o = p2r.concat(fileFullName.substring(p1rl));
				File f = new java.io.File(o);
				if(!f.exists()){
					f.getParentFile().mkdirs();
					f.getParentFile().setReadable(true,false);
					f.getParentFile().setWritable(true,false);
					f.getParentFile().setExecutable(true, false);
					f.createNewFile();
				}
				f.setReadable(true, false);
				f.setWritable(true, false);
				f.setExecutable(true, false);
				java.io.FileOutputStream fout = new java.io.FileOutputStream(f);
				java.io.PrintWriter output = new java.io.PrintWriter(fout);

				int b;
				while ((b = input.read()) != -1) {
					output.write(b);
				}
				output.close();
				input.close();
				fout.close();
			}
		}

		public void copy() throws IOException{
			initFileArrayList();
			fileBackup();
		}
}
