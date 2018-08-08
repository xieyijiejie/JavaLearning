package rmi;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisa.wei on 2017/1/4.
 */
public class logProcess extends UnicastRemoteObject implements log {
	public logProcess() throws RemoteException {
	}

	public List<String> read (String filepath) throws RemoteException {
		List<String> content = new ArrayList<>();
		try {
			File file = new File(filepath);
            if (!file.isDirectory()) {
            	InputStream is = new FileInputStream(filepath);
            	content = IOUtils.readLines(is);
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return content;
	}
	
	public String[] getFiles (String filepath) throws RemoteException {
		File file = new File(filepath);
        if (file.isDirectory()) {
        	String[] filelist = file.list();
        	return filelist;
        }
		return null;
	}
	
//	public static void main(String[] args) throws FileNotFoundException, IOException {
//		List<String> content = new ArrayList<>();
//		logProcess logProcess = new logProcess();
//		content = logProcess.read("D:/intellJcode/log/22_janus_log/");
//		JSONObject obj = new JSONObject();
////			content.forEach(o -> {
////				System.out.println(o);
////			});
//		System.out.println(content.size());
//	}
}
