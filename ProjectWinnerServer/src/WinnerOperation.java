/*
Papadakis Fragkiskos
*/
import java.rmi.*;
import java.util.HashSet;

public interface WinnerOperation extends Remote { //interface gia epikoinwnia tou BingoServer me ton WinnerServer

    public HashSet<String> sendWinner(Player s) throws RemoteException;
    
    public HashSet<String> askList() throws RemoteException;
}
