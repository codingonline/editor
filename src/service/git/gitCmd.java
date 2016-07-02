package service.git;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.directwebremoting.json.types.JsonObject;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.simple.JSONObject;
import org.omg.PortableInterceptor.SUCCESSFUL;

import utils.javaJson.JSONArray;


public class gitCmd {

	public static String init(String path){	
		String retString = null;
		File filepath = new File(path);
		try (Git git = Git.init().setDirectory(filepath).call()) {
			retString = "init git repo success";
			System.out.println("Having repository: " + git.getRepository().getDirectory());
			return retString;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "init git repo fail";
		} 
	}

	@SuppressWarnings("finally")
	public static String status(String path){
		File filepath = new File(path);
		String retString = null;
		try(Git git = Git.open(filepath)){
			Status status = git.status().call();
			if(status.isClean()){
				retString = "working directory clean";
			}else{
				retString = "\nAdded: " + status.getAdded().toString()
						+ "\nChanged: " + status.getChanged().toString()
						+ "\nConflicting: " + status.getConflicting().toString()
						+ "\nConflictingStageState: " + status.getConflictingStageState().toString()
						+ "\nIgnoredNotInIndex: " + status.getIgnoredNotInIndex().toString()
						+ "\nMissing: " + status.getMissing().toString()
						+ "\nModified: " + status.getModified().toString()
						+ "\nRemoved: " + status.getRemoved().toString()
						+ "\nUntracked: " + status.getUntracked().toString()
						+ "\nUntrackedFolders: " + status.getUntrackedFolders().toString()
						+ "\nUncommittedChanges" + status.getUncommittedChanges().toString();

			}
			System.out.println(retString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			return retString;
		}

	}

	public static String add(String path) {
		// TODO Auto-generated method stub
		File filepath = new File(path);
		try(Git git = Git.open(filepath)){
			git.add().addFilepattern(".").call();
			return "git add success\n"+status(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "git add fail\n"+status(path);
		} 
	}

	public static String commit(String projectPath, String name, String email, String msg) {
		// TODO Auto-generated method stub
		File filepath = new File(projectPath);
		try(Git git = Git.open(filepath)){
			git.commit()
			.setMessage(msg)
			.setCommitter(name, email)
			.call();
			return "git commit success\n" + status(projectPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "git commit fail\n" + status(projectPath);
		}
	}
	
	public static String push(String projectPath, String name, String password, String remote){
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(name, password);
		File filepath = new File(projectPath);
		try(Git git = Git.open(filepath)){
			git.push().setRemote(remote).setCredentialsProvider(cp)
				.setForce(true)
				.setPushAll().call();
			return "git push success";
		} catch (Exception e) {
			return "git push fail";
		}
	}
	
	public static String branch(String projectPath){
		File filepath = new File(projectPath);
		JSONArray jArray= new JSONArray();
		JSONObject local, remote, head;
		try(Git git = Git.open(filepath)){
			// HEAD
			head = new JSONObject();
			Ref cRef = git.getRepository().getRef(Constants.HEAD);
			head.put("name", cRef.getTarget().getName());
			head.put("attr", "HEAD");
			jArray.put(head);
			// local
			List<Ref> call = git.branchList().call();
            for (Ref ref : call) {
            	local = new JSONObject();
            	local.put("name", ref.getName());
            	local.put("attr", "local");
    			jArray.put(local);
            	//System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
            }
            // remote 
            call = git.branchList().setListMode(ListMode.REMOTE).call();
            for (Ref ref : call) {
            	remote = new JSONObject();
            	remote.put("name", ref.getName());
            	remote.put("attr", "remote");
    			jArray.put(remote);
            	//System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
            }
			return jArray.toString();
		} catch (Exception e) {
			return "git branch fail";
		}
	}
	
	public static String branchAdd(String projectPath, String name){
		File filepath = new File(projectPath);
		try(Git git = Git.open(filepath)){
			git.branchCreate()
	        .setName(name)
	        .call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "add new branch fail";
		}
		return "add new branch success";
	}
	
	public static String branchDel(String projectPath, String name){
		File filepath = new File(projectPath);
		try(Git git = Git.open(filepath)){
			git.branchDelete()
	        .setBranchNames(name)
	        .call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "delete branch "+name+" fail";
		}
		return "delete branch "+name+" success";
	}
	
	public static String branchRename(String projectPath, String oldname, String newname){
		File filepath = new File(projectPath);
		String newname1 = null;
		if(newname.startsWith("refs/heads/")){
			newname1 = newname.substring(11);
		}
		if(newname1!=null){
			try(Git git = Git.open(filepath)){
				git.branchRename()
		        .setOldName(oldname).setNewName(newname1)
		        .call();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return "rename branch "+oldname+" to "+newname1+" fail";
			}
		}
		return "rename branch "+oldname+" to "+newname+" success";
	}
	
	public static String gitCheckout(String projectPath, String name){
		File filepath = new File(projectPath);
		try(Git git = Git.open(filepath)){
			// 本地分支
			if(name.startsWith("refs/heads/")){
				git.checkout().setName(name).call();
			}else if (name.startsWith("refs/remotes/")) {
				git.checkout().setCreateBranch(true).setName(name.substring(13)).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "checkout branch "+name+" fail";
		}
		return "checkout branch "+name+" success";
	}
	
	public static void main(String[] args){
		String fString = "/home/wq/Workspaces/MyEclipse 10/pop2016-editor";
		File filepath = new File(fString);
//		String name = "refs/heads/test";
//		try(Git git = Git.open(filepath)){
//			git.checkout().setName(name).call();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("checkout branch "+name+" fail");
//		}
		System.out.println(branch(fString));
	}
	
}
