package cn.edu.pku.sei.utils;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitOperation {

	
	public static String gitClonePrivate(String remoteUrl, String localPath, String username, String password) {
		File dir = new File(localPath);
		if(dir.exists())
			return "Project already exists";
		Git gitRepo = null;
		try {
			gitRepo = Git.cloneRepository().setURI(remoteUrl)
					.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
					.setDirectory(new File(localPath)).call();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		String retMsg = "success";
		if(gitRepo == null)
			retMsg = "Git clone failure";
		return retMsg;
	}
	
	public static String gitCommit(String localPath, String gitUsername, String password) {
		File repository = new File(localPath);
		if(!repository.exists()) {
			return "repository not exist";
		}
		try {
			File gitDir = new File(localPath + File.separator + ".git");
			if(!gitDir.exists()) {
				//Git.init().setDirectory(repository).call();
				return "not git repository";
			}
			Git git = Git.open(repository);
			git.add().addFilepattern(".").call();
			RevCommit revCommit = git.commit().setCommitter(gitUsername, password).setMessage("commit msg").call();
			String version = revCommit.getName();
			System.out.println("commit version: " + version);
		} catch (GitAPIException e) {
			System.out.println("Git API exception");
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		return "success";
	}
	
	public static String gitPush(String localPath, String httpsUrl, String gitUsername, String password) {
		if("success".equals(gitCommit(localPath, gitUsername, password))) {
			File repository = new File(localPath);
			try {
				Git git = Git.open(repository);
				String remoteUrl1 = "https://"+gitUsername+":"+password+"@"+httpsUrl.substring(8);
				Iterable<PushResult> pushResults = git.push().setRemote(remoteUrl1).call();
				for(PushResult pr : pushResults) {
					System.out.println("push result messages: " + pr.getMessages());
				}
				return "success";
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidRemoteException e) {
				System.out.println("called with an invalid remote uri");
			} catch (TransportException e) {
				System.out.println("error occurs with the transport");
			} catch (GitAPIException e) {
				e.printStackTrace();
			}
			return "push failure";
		}
		return "commit failure";
	}
	
	public static void main(String[] args){
		System.out.println(GitOperation.gitClonePrivate("https://code.csdn.net/pkumass/pop-homepage.git", "/data/nfs/repo/javaweb/pkusei/pop-homepage", "wangqiaobj@sina.com", "wangqiaobj@sina.com"));

	}
}
