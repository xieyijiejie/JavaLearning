package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by lisa.wei on 2017/1/4.
 */
public interface log extends Remote {
	public List<String> read(String filepath) throws RemoteException;
	public String [] getFiles(String filepath) throws RemoteException;
}
