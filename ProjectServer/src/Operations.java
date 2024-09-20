/*
Papadakis Fragkiskos
*/
import java.rmi.*;
import java.util.HashSet;

public interface Operations extends Remote { //interface gia epikoinwnia tou Client me ton BingoServer
    //leitourgies pou xreiazetai o BingoServer gia oli tin leitourgia me ton Client 
    public boolean register(Player passwd) throws RemoteException;
    
    public boolean login(Player usr) throws RemoteException;
    
    public void joinGame(Listener p) throws RemoteException;
    
    public void removePlayer(Player p) throws RemoteException;
    
    public void removeListenerPlayer(Listener p) throws RemoteException;
    
    public boolean isBingo(int[][] del,Player p) throws RemoteException;
    
    public int[][] deltio() throws RemoteException;
    
    public HashSet<String> sendtoClient() throws RemoteException;
}
